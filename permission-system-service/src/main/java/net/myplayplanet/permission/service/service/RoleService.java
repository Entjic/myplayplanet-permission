package net.myplayplanet.permission.service.service;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
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

    public Role save(Role role) {
        if (isInvalid(role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The role has the same permission set as GRANTED and DENIED at the same time. The first offender is: " + findFirstInvalid(role));
        }
        return this.roleRepository.save(role);
    }

    private boolean isInvalid(Role role) {
        return findFirstInvalid(role) != null;
    }

    private Permission findFirstInvalid(Role role) {

        for (final Permission granted : role.getGranted()) {
            for (final Permission denied : role.getDenied()) {
                if (granted.getUuid().equals(denied.getUuid())) {
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

        role.getGranted().remove(permission);
        role.getDenied().remove(permission);

        if (permissionValue.equals(PermissionValue.GRANTED)) {
            role.getGranted().add(permission);
        }
        if (permissionValue.equals(PermissionValue.DENIED)) {
            role.getDenied().add(permission);
        }
        return this.save(role);
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
                set.add(createExtensivePermissionDto(permission.getUuid(), PermissionValue.GRANTED, role));
            }
            for (Permission permission : role.getDenied()) {
                set.add(createExtensivePermissionDto(permission.getUuid(), PermissionValue.DENIED, role));
            }
        }

        return set;

    }

    private ExtensivePermissionDto createExtensivePermissionDto(UUID key, PermissionValue permissionValue, Role role) {
        return new ExtensivePermissionDto(new PermissionDto(key, permissionValue),
                PermissionOrigin.ROLE, entityMapper.roleToRoleDto(role));
    }

    public PermissionSet getEffectivePermissions(Set<Role> roles) {
        List<Role> sorted = this.sortByWeight(roles);

        Collections.reverse(sorted);

        PermissionSet set = new PermissionSet();

        for (Role role : sorted) {
            for (Permission permission : role.getGranted()) {
                if (set.contains(permission.getUuid())) continue;
                set.add(permission.getUuid(), PermissionValue.GRANTED);
            }
            for (Permission permission : role.getDenied()) {
                if (set.contains(permission.getUuid())) continue;
                set.add(permission.getUuid(), PermissionValue.DENIED);
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
