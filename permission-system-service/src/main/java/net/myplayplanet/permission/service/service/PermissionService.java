package net.myplayplanet.permission.service.service;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.service.dto.enums.DeletionMode;
import net.myplayplanet.permission.service.dto.model.PermissionSet;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.model.Scope;
import net.myplayplanet.permission.service.model.User;
import net.myplayplanet.permission.service.repository.PermissionRepository;
import net.myplayplanet.permission.service.repository.ScopeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Transactional
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final EntityMapper entityMapper;
    private final UserService userService;
    private final ScopeService scopeService;
    private final ScopeRepository scopeRepository;

    public Permission findPermissionOrThrow(String key) {
        return this.permissionRepository.findByKey(key).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Collection<Permission> getAllByScope(Long scopeId) {

        Scope scope = this.scopeService.findScopeOrThrow(scopeId);
        return scope.getPermissions();
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

    public Permission saveNewPermission(String key, String parent) {
        if (permissionRepository.existsByKey(key))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There already exists a permission with the specified key");
        return this.saveOrUpdatePermission(key, parent);
    }

    public Permission updateExistingPermission(String key, String parent) {
        this.assertPermissionExistsByKey(key);
        return this.saveOrUpdatePermission(key, parent);
    }

    private void assertPermissionExistsByKey(String key) {
        if (!permissionRepository.existsByKey(key))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There already exists a permission with the specified key");
    }

    private Permission saveOrUpdatePermission(String key, String parent) {
        Permission permission = this.saveOrUpdatePermission(key);

        if (parent == null) return permission;

        Permission newParent = this.findPermissionOrThrow(parent);
        this.linkPermissions(newParent, permission);

        return this.findPermissionOrThrow(key);
    }

    private Permission saveOrUpdatePermission(String key) {
        if (permissionRepository.existsByKey(key)) {
            Permission permission = this.findPermissionOrThrow(key);
            Permission parent = permission.getParent();
            this.unlinkPermissions(parent, permission);
            permission.setKey(key);
            return permissionRepository.save(permission);
        }
        Permission permission = new Permission(key);
        return permissionRepository.save(permission);
    }

    private void unlinkPermissions(Permission parent, Permission child) {
        if (parent != null && child != null) {
            parent.getChildren().remove(child);
            permissionRepository.save(parent);
        }
        if (child != null && child.getParent() != null) {
            child.setParent(null);
            permissionRepository.save(child);
        }
    }

    private void linkPermissions(Permission parent, Permission child) {
        if (parent != null && child != null) {
            parent.getChildren().add(child);
            permissionRepository.save(parent);
            child.setParent(parent);
            permissionRepository.save(child);
        }

    }

    public Set<String> delete(Set<String> keys, DeletionMode mode) {
        Set<String> output = new HashSet<>();
        switch (mode) {
            case SHALLOW -> {
                for (String key : keys) {
                    output.add(this.deleteShallowNaiv(key));
                }
            }
            case RECURSIVE -> {
                for (String key : keys) {
                    this.permissionRepository.findByKey(key).ifPresent(permission -> {
                        output.addAll(this.deleteRecursive(key));
                    });
                }
            }
            case INTELLIGENT -> {
                for (String key : keys) {
                    output.add(this.deleteShallowIntelligent(key));
                }
            }
        }
        return output;
    }

    // rips a hole in tree
    public String deleteShallowNaiv(String key) {
        Permission permission = this.findPermissionOrThrow(key);
        for (Permission child : permission.getChildren()) {
            child.setParent(null);
            this.permissionRepository.save(child);
        }
        this.permissionRepository.delete(permission);
        return key;
    }

    // makes sure the tree stays complete
    public String deleteShallowIntelligent(String key) {
        Permission permission = this.findPermissionOrThrow(key);

        if (permission.getParent() == null) {
            return this.deleteShallowNaiv(key);
        }
        Permission parent = permission.getParent();
        for (Permission child : permission.getChildren()) {
            child.setParent(parent);
            this.permissionRepository.save(child);
        }
        parent.getChildren().addAll(permission.getChildren());
        this.permissionRepository.save(parent);
        this.permissionRepository.delete(permission);
        return key;
    }

    // deletes every sub permission, therefore the tree stays complete
    private Set<String> deleteRecursive(String key) {
        Set<Permission> open = new HashSet<>();
        Permission root = this.findPermissionOrThrow(key);
        open.add(root);

        return delete(open);
    }

    private Set<String> delete(Set<Permission> open) {
        Set<String> closed = new HashSet<>();
        Permission current;
        while (!open.isEmpty()) {
            current = open.iterator().next();
            open.addAll(current.getChildren());
            String key = current.getKey();
            this.permissionRepository.delete(current);
            closed.add(key);
        }

        return closed;
    }

    public void addToScope(String key, Long scopeId) {
        Scope scope = this.scopeService.findScopeOrThrow(scopeId);
        Permission permission = this.findPermissionOrThrow(key);
        if (scope.getPermissions().contains(permission)) return;
        scope.getPermissions().add(permission);
        this.scopeRepository.save(scope);
    }

}
