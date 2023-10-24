package net.myplayplanet.permission.service.service;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.core.enums.DeletionMode;
import net.myplayplanet.permission.core.model.PermissionSet;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.model.User;
import net.myplayplanet.permission.service.repository.PermissionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final EntityMapper entityMapper;
    private final UserService userService;

    public Permission findPermissionOrThrow(UUID uuid) {
        return this.permissionRepository.findById(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Set<UUID> getAll(){
        return this.permissionRepository.findAll().stream().map(Permission::getUuid).collect(Collectors.toSet());
    }

    public Boolean hasPermission(User user, Permission permission) {
        PermissionSet permissionSet = this.userService.calcPermissionSet(user);
        return hasPermission(permissionSet, permission);
    }

    private Boolean hasPermission(PermissionSet set, Permission permission) {
        Set<Permission> requiredSet = collectPermissionsThatSatisfyPermission(permission);
        Set<Permission> actualSet = this.entityMapper.permissionDtosToPermissions(set);

        for (Permission required : requiredSet) {
            if (actualSet.contains(required)) return true;
        }
        return false;
    }

    private Set<Permission> collectPermissionsThatSatisfyPermission(Permission permission) {
        Set<Permission> set = new HashSet<>();
        Permission current = permission;
        while (current != null) {
            set.add(current);
            current = current.getParent();
        }
        return set;
    }

    public Permission saveNewPermission(UUID uuid, String name, String description, UUID parent){
        if(permissionRepository.existsById(uuid)) throw new ResponseStatusException(HttpStatus.CONFLICT, "There already exists a permission with the specified uuid");
        return this.saveOrUpdatePermission(uuid, name, description, parent);
    }

    public Permission updateExistingPermission(UUID uuid, String name, String description, UUID parent){
        this.findPermissionOrThrow(uuid);
        return this.saveOrUpdatePermission(uuid, name, description, parent);
    }

    private Permission saveOrUpdatePermission(UUID uuid, String name, String description, UUID parent) {
        Permission permission = this.saveOrUpdatePermission(uuid, name, description);

        if(parent == null) return permission;

        Permission newParent = this.findPermissionOrThrow(parent);
        this.linkPermissions(newParent, permission);

        return this.findPermissionOrThrow(uuid);
    }

    private Permission saveOrUpdatePermission(UUID uuid, String name, String description) {
        if (permissionRepository.existsById(uuid)) {
            Permission permission = this.findPermissionOrThrow(uuid);
            Permission parent = permission.getParent();
            this.unlinkPermissions(parent, permission);
            permission.setName(name);
            permission.setDescription(description);
            return permissionRepository.save(permission);
        }
        Permission permission = new Permission(uuid, name, description, null, new HashSet<>());
        return permissionRepository.save(permission);
    }

    private void unlinkPermissions(Permission parent, Permission child) {
        if(parent != null && child != null){
            parent.getChildren().remove(child);
            permissionRepository.save(parent);
        }
        if(child != null && child.getParent() != null){
            child.setParent(null);
            permissionRepository.save(child);
        }
    }

    private void linkPermissions(Permission parent, Permission child){
        if(parent != null && child != null){
            parent.getChildren().add(child);
            permissionRepository.save(parent);
            child.setParent(parent);
            permissionRepository.save(child);
        }

    }

    public Set<UUID> delete(Set<UUID> uuids, DeletionMode mode) {
        Set<UUID> output = new HashSet<>();
        switch (mode) {
            case SHALLOW -> {
                for (UUID uuid : uuids) {
                    output.add(this.deleteShallowNaiv(uuid));
                }
            }
            case RECURSIVE -> {
                for (UUID uuid : uuids) {
                    this.permissionRepository.findById(uuid).ifPresent(permission -> {
                        output.addAll(this.deleteRecursive(uuid));
                    });
                }
            }
            case INTELLIGENT -> {
                for (UUID uuid : uuids) {
                    output.add(this.deleteShallowIntelligent(uuid));
                }
            }
        }
        return output;
    }

    // rips a hole in tree
    public UUID deleteShallowNaiv(UUID uuid) {
        Permission permission = this.findPermissionOrThrow(uuid);
        for (Permission child : permission.getChildren()) {
            child.setParent(null);
            this.permissionRepository.save(child);
        }
        this.permissionRepository.delete(permission);
        return uuid;
    }

    // makes sure the tree stays complete
    public UUID deleteShallowIntelligent(UUID uuid) {
        Permission permission = this.findPermissionOrThrow(uuid);

        if (permission.getParent() == null) {
            return this.deleteShallowNaiv(uuid);
        }
        Permission parent = permission.getParent();
        for (Permission child : permission.getChildren()) {
            child.setParent(parent);
            this.permissionRepository.save(child);
        }
        parent.getChildren().addAll(permission.getChildren());
        this.permissionRepository.save(parent);
        this.permissionRepository.delete(permission);
        return uuid;
    }

    // deletes every sub permission, therefore the tree stays complete
    private Set<UUID> deleteRecursive(UUID uuid) {
        Set<Permission> open = new HashSet<>();
        Permission root = this.findPermissionOrThrow(uuid);
        open.add(root);

        return delete(open);
    }

    private Set<UUID> delete(Set<Permission> open) {
        Set<UUID> closed = new HashSet<>();
        Permission current;
        while (!open.isEmpty()) {
            current = open.iterator().next();
            open.addAll(current.getChildren());
            UUID uuid = current.getUuid();
            this.permissionRepository.delete(current);
            closed.add(uuid);
        }

        return closed;
    }

}
