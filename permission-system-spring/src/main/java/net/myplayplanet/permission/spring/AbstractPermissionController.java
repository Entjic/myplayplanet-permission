package net.myplayplanet.permission.spring;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.api.PermissionClient;
import net.myplayplanet.permission.api.RoleClient;
import net.myplayplanet.permission.api.UserClient;
import net.myplayplanet.permission.model.PermissionDto;
import net.myplayplanet.permission.model.RoleDto;
import net.myplayplanet.permission.model.RoleRenameDto;
import net.myplayplanet.permission.model.UserDto;
import net.myplayplanet.security.annotation.AuthenticatedSelf;
import net.myplayplanet.security.annotation.IdentityType;
import net.myplayplanet.security.annotation.access_restriction.Authenticated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

// used to guarantee similar interface across all implementations for frontend

@RestController
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public abstract class AbstractPermissionController {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final RoleClient roleClient;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final UserClient userClient;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final PermissionClient permissionClient;

    // role controller

    @GetMapping("role/{scope}/all")
    public Flux<RoleDto> getAllRolesByScope(@PathVariable Long scope) {
        return getAllRolesByScopeDefer(scope);
    }

    protected Flux<RoleDto> getAllRolesByScopeDefer(Long scope) {
        return this.roleClient.getAllRolesByScope(scope);
    }

    @Authenticated
    @PostMapping("role/{scope}/rename")
    public Mono<RoleDto> renameRole(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long scope, @RequestBody RoleRenameDto roleDto) {
        return renameRoleDefer(self, scope, roleDto);
    }

    protected abstract Mono<RoleDto> renameRoleDefer(UUID self, Long scope, RoleRenameDto roleDto);


    @Authenticated
    @PostMapping("role/{scope}/create-empty")
    public Mono<RoleDto> createEmptyRole(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long scope, @RequestBody RoleRenameDto roleDto) {
        return createEmptyRoleDefer(self, scope, roleDto);
    }

    protected abstract Mono<RoleDto> createEmptyRoleDefer(UUID self, Long scope, RoleRenameDto roleDto);

    @Authenticated
    @DeleteMapping("role/{id}")
    public Mono<RoleDto> deleteRole(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long id) {
        return deleteRoleDefer(self, id);
    }

    protected abstract Mono<RoleDto> deleteRoleDefer(UUID self, Long id);

    // scope controller

    // user controller

    // TODO: 17.02.2025 maybe maybe we also want authentication here
    @GetMapping("user/{scope}")
    public Flux<UserDto> getUsersByScope(@PathVariable Long scope) {
        return getUsersByScopeDefer(scope);
    }

    protected Flux<UserDto> getUsersByScopeDefer(Long scope) {
        return this.userClient.getAllUsersByScope(scope);
    }

    @Authenticated
    @PostMapping("user/{scope}/role/add/{roleId}/user/{uuid}")
    public Mono<UserDto> addRoleToUser(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long scope, @PathVariable UUID uuid, @PathVariable Long roleId) {
        return addRoleToUserDefer(self, scope, uuid, roleId);
    }

    protected abstract Mono<UserDto> addRoleToUserDefer(UUID self, Long scope, UUID uuid, Long roleId);

    @Authenticated
    @PostMapping("user/{scope}/role/remove/{roleId}/user/{uuid}")
    public Mono<UserDto> removeRole(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long scope, @PathVariable UUID uuid, @PathVariable Long roleId) {
        return removeRoleToUserDefer(self, scope, uuid, roleId);
    }

    protected abstract Mono<UserDto> removeRoleToUserDefer(UUID self, Long scope, UUID uuid, Long roleId);


    @PostMapping("user/{scope}/user/{uuid}/permission")
    public Mono<UserDto> setUserSpecificPermission(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long scope, @PathVariable UUID uuid, @RequestBody PermissionDto permissionDto) {
        return setUserSpecificPermissionDefer(self, scope, uuid, permissionDto);
    }

    protected abstract Mono<UserDto> setUserSpecificPermissionDefer(UUID self, Long scope, UUID uuid, PermissionDto permissionDto);

    @PostMapping("user/{scope}/user/{uuid}/permissions")
    public Mono<UserDto> setUserSpecificPermissions(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long scope, @PathVariable UUID uuid, @RequestBody Set<PermissionDto> permissionDtos) {
        return setUserSpecificPermissionsDefer(self, scope, uuid, permissionDtos);
    }

    protected abstract Mono<UserDto> setUserSpecificPermissionsDefer(UUID self, Long scope, UUID uuid, Set<PermissionDto> permissionDtos);


    @DeleteMapping("user/{scope}/user/{uuid}")
    public Mono<UserDto> deleteUserFromScope(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long scope, @PathVariable UUID uuid) {
        return deleteUserFromScopeDefer(self, scope, uuid);
    }

    protected abstract Mono<UserDto> deleteUserFromScopeDefer(UUID self, Long scope, @PathVariable UUID uuid);

    // permission controller

    @GetMapping("permission/scope/{scope}")
    public Flux<String> getAllPermissionsByScope(@PathVariable Long scope) {
        return getAllPermissionsByScopeDefer(scope);
    }

    protected Flux<String> getAllPermissionsByScopeDefer(Long scope) {
        return this.permissionClient.getAllPermissionsByScope(scope).flatMapMany(Flux::fromIterable);
    }

    @GetMapping("{scope}/has/{user}/permission/{permission}")
    public Mono<Boolean> hasPermission(@PathVariable Long scope,
                                       @PathVariable UUID user,
                                       @PathVariable String permission) {
        return hasPermissionDefer(scope, user, permission);
    }

    protected Mono<Boolean> hasPermissionDefer(Long scope, UUID user, String permission) {
        return this.permissionClient.hasPermission(scope, user, permission);
    }


}
