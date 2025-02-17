package net.myplayplanet.permission.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.service.dto.PermissionDto;
import net.myplayplanet.permission.service.dto.UserDto;
import net.myplayplanet.permission.service.dto.effective.EffectiveUserModelDto;
import net.myplayplanet.permission.service.dto.effective.ExtensiveEffectiveUserModelDto;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.model.User;
import net.myplayplanet.permission.service.service.PermissionService;
import net.myplayplanet.permission.service.service.RoleService;
import net.myplayplanet.permission.service.service.ScopeService;
import net.myplayplanet.permission.service.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user/")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final ScopeService scopeService;
    private final PermissionService permissionService;

    private final EntityMapper entityMapper;

    @GetMapping("{scope}/effective/extensive/{uuid}")
    public ExtensiveEffectiveUserModelDto getExtensiveEffectiveUserModelDto(@PathVariable Long scope, @PathVariable UUID uuid) {
        return userService.getExtensiveEffectiveUserModel(userService.findUserOrThrow(
                this.scopeService.findScopeOrThrow(scope), uuid));
    }

    @GetMapping("{scope}/effective/{uuid}")
    public EffectiveUserModelDto getEffectiveUserModelDto(@PathVariable Long scope, @PathVariable UUID uuid) {
        return userService.getEffectiveUserModel(userService.findUserOrThrow(
                this.scopeService.findScopeOrThrow(scope), uuid));
    }

    @Operation(operationId = "getAllUsersByScope")
    @GetMapping("{scope}")
    public Set<UserDto> getAll(@PathVariable Long scope) {
        return this.entityMapper.usersToUserDtos(this.userService.getAll(
                this.scopeService.findScopeOrThrow(scope)));
    }

    @PostMapping("{scope}/role/add/{roleId}/user/{uuid}")
    public UserDto addRole(@PathVariable Long scope, @PathVariable UUID uuid, @PathVariable Long roleId) {
        User user = this.userService.findOrCreateUser(
                this.scopeService.findScopeOrThrow(scope), uuid);
        Role role = this.roleService.findOrThrow(roleId);
        return this.entityMapper.userToUserDto(this.userService.addRole(user, role));
    }

    @PostMapping("{scope}/role/remove/{roleId}/user/{uuid}")
    public UserDto removeRole(@PathVariable Long scope, @PathVariable UUID uuid, @PathVariable Long roleId) {
        User user = this.userService.findUserOrThrow(
                this.scopeService.findScopeOrThrow(scope), uuid);
        Role role = this.roleService.findOrThrow(roleId);
        return this.entityMapper.userToUserDto(this.userService.removeRole(user, role));
    }

    @GetMapping("user/{uuid}/scopes")
    public Set<Long> scopesByUser(@PathVariable UUID uuid) {
        return this.userService.findUsersByUUID(uuid)
                .stream().map(User::getScope)
                .map(this.entityMapper::map)
                .collect(Collectors.toSet());
    }

    @PostMapping("{scope}/user/{uuid}/permission")
    public UserDto setUserSpecificPermission(@PathVariable Long scope, @PathVariable UUID uuid, @RequestBody PermissionDto permissionDto) {
        User user = this.userService.findUserOrThrow(this.scopeService.findScopeOrThrow(scope), uuid);
        Permission permission = this.permissionService.findPermissionOrThrow(permissionDto.getKey());
        return this.entityMapper.userToUserDto(this.userService.setUserSpecificPermission(user, permission, permissionDto.getPermissionValue()));
    }

    @PostMapping("{scope}/user/{uuid}/permissions")
    public UserDto setUserSpecificPermissions(@PathVariable Long scope, @PathVariable UUID uuid, @RequestBody Set<PermissionDto> permissionDtos) {
        User user = this.userService.findUserOrThrow(this.scopeService.findScopeOrThrow(scope), uuid);
        for (final PermissionDto permissionDto : permissionDtos) {
            Permission permission = this.permissionService.findPermissionOrThrow(permissionDto.getKey());
            user = this.userService.setUserSpecificPermission(user, permission, permissionDto.getPermissionValue());
        }
        return this.entityMapper.userToUserDto(user);
    }

    @DeleteMapping("{scope}/user/{uuid}/permissions")
    public UserDto clearUserSpecificPermissions(@PathVariable Long scope, @PathVariable UUID uuid) {
        User user = this.userService.findUserOrThrow(this.scopeService.findScopeOrThrow(scope), uuid);
        return this.entityMapper.userToUserDto(this.userService.clearUserSpecificPermissions(user));
    }

    @DeleteMapping("{scope}/user/{uuid}")
    public UserDto deleteUserFromScope(@PathVariable Long scope, @PathVariable UUID uuid) {
        User user = this.userService.findUserOrThrow(this.scopeService.findScopeOrThrow(scope), uuid);
        this.userService.delete(user);
        return this.entityMapper.userToUserDto(user);
    }

    @GetMapping("{scope}/weight/user/{uuid}")
    public Integer getWeight(@PathVariable final Long scope,
                             @PathVariable final UUID uuid) {
        User user = this.userService.findUserOrThrow(this.scopeService.findScopeOrThrow(scope), uuid);

        return user.getRoles().stream()
                .map(Role::getWeight)
                .max(Integer::compareTo)
                .orElse(0);
    }

    @GetMapping("{scope}/user/{uuid}/known")
    public Boolean isKnown(@PathVariable final Long scope, @PathVariable UUID uuid) {
        return this.userService.findUser(this.scopeService.findScopeOrThrow(scope), uuid).isPresent();
    }


}
