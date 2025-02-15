package net.myplayplanet.permission.service.service;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.permission.service.dto.PermissionDto;
import net.myplayplanet.permission.service.dto.effective.ExtensivePermissionDto;
import net.myplayplanet.permission.service.dto.enums.PermissionOrigin;
import net.myplayplanet.permission.service.dto.enums.PermissionValue;
import net.myplayplanet.permission.service.dto.model.ExtensivePermissionSet;
import net.myplayplanet.permission.service.dto.model.PermissionSet;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.model.Scope;
import net.myplayplanet.permission.service.repository.RoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final EntityMapper entityMapper;
    private final RoleRepository roleRepository;

    public Role findOrThrow(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Collection<Role> getAll() {
        return this.roleRepository.findAll();
    }

    public Set<Long> getAll(Scope scope) {
        return this.roleRepository.findAllByScope(scope)
                .stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
    }

    public Role getByName(Scope scope, String name) {
        return getByNameOptional(scope, name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Optional<Role> getByNameOptional(Scope scope, String name) {
        return this.roleRepository.findAllByScope(scope)
                .stream()
                .filter(role -> role.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public Role save(Role role) {
        if (isInvalid(role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The role has the same permission set as GRANTED and DENIED at the same time. The first offender is: " + findFirstInvalid(role));
        }
        return this.roleRepository.save(role);
    }

    public Role alterOrCreate(Role role) {
        if (role.getId() == null) {
            Optional<Role> existing = this.getByNameOptional(role.getScope(), role.getName());
            return existing.map(value -> alter(value, role)).orElseGet(() -> save(role));
        }
        Optional<Role> byId = this.roleRepository.findById(role.getId());
        if (byId.isEmpty()) return save(role);
        Role current = byId.get();
        log.info("Overriding role {} with {}", current, role);
        return alter(current, role);
    }

    private Role alter(Role current, Role alter) {
        current.setWeight(alter.getWeight());

        for (final Permission permission : alter.getGranted()) {
            setPermission(current, permission, PermissionValue.GRANTED);
        }

        for (final Permission permission : alter.getDenied()) {
            setPermission(current, permission, PermissionValue.DENIED);
        }

        current.setName(alter.getName());
        current.setEditable(alter.getEditable());
        current.setScope(alter.getScope());
        return this.roleRepository.save(current);

    }

    private boolean isInvalid(Role role) {
        return findFirstInvalid(role) != null;
    }

    private Permission findFirstInvalid(Role role) {

        for (final Permission granted : role.getGranted()) {
            for (final Permission denied : role.getDenied()) {
                if (granted.getKey().equals(denied.getKey())) {
                    return granted;
                }
            }
        }
        return null;
    }


    public Role changeWeight(long id, int weight) {
        Role role = this.findOrThrow(id);
        role.setWeight(weight);
        return this.save(role);
    }

    public Role setPermission(long id, Permission permission, PermissionValue permissionValue) {
        Role role = this.findOrThrow(id);
        this.setPermission(role, permission, permissionValue);
        return this.save(role);
    }

    private void setPermission(Role role, Permission permission, PermissionValue permissionValue) {
        role.getGranted().remove(permission);
        role.getDenied().remove(permission);

        if (permissionValue.equals(PermissionValue.GRANTED)) {
            role.getGranted().add(permission);
        }
        if (permissionValue.equals(PermissionValue.DENIED)) {
            role.getDenied().add(permission);
        }
    }

    public Long delete(long id) {
        Role role = this.findOrThrow(id);
        this.roleRepository.delete(role);
        return id;
    }


    public ExtensivePermissionSet getExtensivEffectivePermissions(Set<Role> roles) {
        List<Role> sorted = Lists.reverse(this.sortByWeight(roles));

        ExtensivePermissionSet set = new ExtensivePermissionSet();

        for (Role role : sorted) {
            for (Permission permission : role.getGranted()) {
                set.add(createExtensivePermissionDto(permission.getKey(), PermissionValue.GRANTED, role));
            }
            for (Permission permission : role.getDenied()) {
                set.add(createExtensivePermissionDto(permission.getKey(), PermissionValue.DENIED, role));
            }
        }

        return set;

    }

    private ExtensivePermissionDto createExtensivePermissionDto(String key, PermissionValue permissionValue, Role role) {
        return new ExtensivePermissionDto(new PermissionDto(key, permissionValue),
                PermissionOrigin.ROLE, entityMapper.roleToRoleDto(role));
    }

    public PermissionSet getEffectivePermissions(Set<Role> roles, Long scope) {
        List<Role> sorted = this.sortByWeight(roles);

        Collections.reverse(sorted);

        PermissionSet set = new PermissionSet(scope);

        for (Role role : sorted) {
            for (Permission permission : role.getGranted()) {
                if (set.contains(permission.getKey())) continue;
                set.add(permission.getKey(), PermissionValue.GRANTED);
            }
            for (Permission permission : role.getDenied()) {
                if (set.contains(permission.getKey())) continue;
                set.add(permission.getKey(), PermissionValue.DENIED);
            }
        }

        return set;
    }

    private List<Role> sortByWeight(Set<Role> set) {
        List<Role> roles = new ArrayList<>(set);

        roles.sort(Comparator.comparingInt(Role::getWeight));

        return roles;
    }

}
