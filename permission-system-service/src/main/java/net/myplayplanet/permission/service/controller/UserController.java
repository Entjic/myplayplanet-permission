package net.myplayplanet.permission.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.service.dto.effective.EffectiveUserModelDto;
import net.myplayplanet.permission.service.dto.effective.ExtensiveEffectiveUserModelDto;
import net.myplayplanet.permission.service.dto.UserDto;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.model.User;
import net.myplayplanet.permission.service.service.RoleService;
import net.myplayplanet.permission.service.service.ScopeService;
import net.myplayplanet.permission.service.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/permission/user/")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final ScopeService scopeService;

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
    @GetMapping("{scope}/all")
    public Set<UserDto> getAll(@PathVariable Long scope) {
        return this.entityMapper.usersToUserDtos(this.userService.getAll(
                this.scopeService.findScopeOrThrow(scope)));
    }

    @PostMapping("{scope}/role/add/{roleId}/user/{uuid}")
    public UserDto addRole(@PathVariable Long scope, @PathVariable UUID uuid, @PathVariable Long roleId) {
        User user = this.userService.findUserOrThrow(
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


}
