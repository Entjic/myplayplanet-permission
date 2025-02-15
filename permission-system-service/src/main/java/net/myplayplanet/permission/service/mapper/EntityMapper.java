package net.myplayplanet.permission.service.mapper;

import net.myplayplanet.permission.service.dto.*;
import net.myplayplanet.permission.service.dto.enums.PermissionValue;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.model.Scope;
import net.myplayplanet.permission.service.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    Set<Permission> permissionDtosToPermissions(Set<PermissionDto> permissionDtoSet);

    default Permission permissionDtoToPermission(PermissionDto permissionDto) {
        Permission permission = new Permission();

        String key = permissionDto.getKey();
        permission.setKey(key);
        return permission;
    }

    default Long map(Scope scope) {
        return scope.getId();
    }

    default Scope map(Long scope) {
        return new Scope(scope, null);
    }


    PermissionDto permissionToPermissionDto(Permission permission, PermissionValue permissionValue);

    @Mapping(source = "permission.parent.key", target = "parent")
    PermissionDisplayDto permissionToPermissionDisplayDto(Permission permission);

    Set<PermissionDisplayDto> permissionsToPermissionDisplayDtos(Collection<Permission> permissions);

    default Set<String> mapPermissionsToKeys(Set<Permission> permissions) {
        return permissions.stream().map(Permission::getKey).collect(Collectors.toSet());
    }

    default RoleDto roleToRoleDto(Role role) {
        RoleDto roleDto = new RoleDto();
        Set<PermissionDto> permissions = new HashSet<>();

        for (Permission permission : role.getGranted()) {
            permissions.add(permissionToPermissionDto(permission, PermissionValue.GRANTED));
        }

        for (Permission permission : role.getDenied()) {
            permissions.add(permissionToPermissionDto(permission, PermissionValue.DENIED));
        }

        roleDto.setKey(role.getId());
        roleDto.setWeight(role.getWeight());
        roleDto.setPermissions(permissions);
        roleDto.setDescription(role.getDescription());

        return roleDto;
    }

    Set<RoleDto> rolesToRoleDtos(Set<Role> roles);

    default Role roleDtoToRole(RoleDto roleDto, Scope scope) {
        final Set<Permission> grantedPermissions = new HashSet<>();
        final Set<Permission> deniedPermissions = new HashSet<>();

        for (PermissionDto permission : roleDto.getPermissions()) {
            Permission obj = new Permission(permission.getKey());
            if (permission.getPermissionValue().equals(PermissionValue.GRANTED)) {
                grantedPermissions.add(obj);
            }
            if (permission.getPermissionValue().equals(PermissionValue.DENIED)) {
                deniedPermissions.add(obj);
            }
        }

        return new Role(roleDto.getKey(),
                scope,
                roleDto.getName(),
                roleDto.getWeight(),
                grantedPermissions,
                deniedPermissions,
                roleDto.getEditable(),
                roleDto.getDescription());
    }

    default UserDto userToUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setUuid(user.getUuid());
        userDto.setRoles(this.rolesToRoleDtos(user.getRoles()));

        Set<PermissionDto> permissions = new HashSet<>();

        for (Permission permission : user.getGranted()) {
            permissions.add(this.permissionToPermissionDto(permission, PermissionValue.GRANTED));
        }

        for (Permission permission : user.getDenied()) {
            permissions.add(this.permissionToPermissionDto(permission, PermissionValue.DENIED));
        }

        userDto.setExplicitPermissions(permissions);

        return userDto;
    }

    Set<UserDto> usersToUserDtos(Set<User> users);

    @Mapping(source = "permission.parent.key", target = "parent")
    PermissionInfoDto permissionToPermissionInfoDto(Permission permission);

    ScopeDto mapScopeToScopeDto(Scope scope);

    Set<ScopeDto> mapScopesToScopeDtos(Collection<Scope> scopes);

    RoleDisplayDto roleToRoleDisplayDto(Role role);

    Set<RoleDisplayDto> rolesToRoleDisplayDto(Collection<Role> roles);
}
