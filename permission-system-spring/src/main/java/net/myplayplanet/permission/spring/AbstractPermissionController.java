package net.myplayplanet.permission.spring;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.api.PermissionClient;
import net.myplayplanet.permission.api.RoleClient;
import net.myplayplanet.permission.api.UserClient;
import net.myplayplanet.permission.model.*;
import net.myplayplanet.security.annotation.AuthenticatedSelf;
import net.myplayplanet.security.annotation.IdentityType;
import net.myplayplanet.security.annotation.access_restriction.Authenticated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
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

    @Authenticated
    @GetMapping("role/{scope}/all")
    public Flux<RoleDto> getAllRolesByScope(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long scope) {
        return this.hasReadPermission(self, scope)
                .flatMapMany(aBoolean -> {
                    if (!aBoolean)
                        return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing read permissions!"));
                    return Mono.just(scope);
                }).flatMap(this.roleClient::getAllRolesByScope)
                .flatMap(this::postGetAllRolesByScope);
    }

    protected Mono<RoleDto> postGetAllRolesByScope(RoleDto roleDto) {
        return Mono.just(roleDto);
    }


    @Authenticated
    @GetMapping("{roleId}")
    public Mono<CompleteRoleDto> getRoleById(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long roleId) {
        return this.getRoleClient().getRoleById(roleId)
                .flatMap(completeRoleDto -> this.hasReadPermission(self, completeRoleDto.getId())
                        .flatMap(aBoolean -> {
                            if (!aBoolean)
                                return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing read permission!"));
                            return Mono.just(completeRoleDto);
                        }))
                .flatMap(this::postGetRoleById);
    }

    protected Mono<CompleteRoleDto> postGetRoleById(CompleteRoleDto roleDto) {
        return Mono.just(roleDto);
    }

    @Authenticated
    @PostMapping("role/rename")
    public Mono<RoleDto> renameRole(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @RequestBody RoleRenameDto roleDto) {
        return this.roleClient.getRoleById(roleDto.getId())
                .map(completeRoleDto -> completeRoleDto.getScope().getId())
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is null")))
                .flatMap(aLong -> hasEditRolePermission(self, aLong))
                .flatMap(aBoolean -> {
                    if (!aBoolean)
                        return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing edit permission!"));
                    return Mono.just(roleDto);
                }).flatMap(this.roleClient::renameRole)
                .flatMap(result -> this.postRenameRole(self, roleDto, result));
    }

    protected Mono<RoleDto> postRenameRole(UUID self, RoleRenameDto renameDto, RoleDto result) {
        return Mono.just(result);
    }

    @Authenticated
    @PostMapping("role/{scope}/create-empty")
    public Mono<RoleDto> createEmptyRole(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long scope, @RequestBody RoleRenameDto roleDto) {
        return this.hasEditRolePermission(self, scope)
                .flatMap(aBoolean -> {
                    if (!aBoolean)
                        return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing edit permission!"));
                    return Mono.just(roleDto);
                })
                .flatMap(roleRenameDto -> this.getRoleClient().createEmptyRole(scope, roleDto))
                .flatMap(this::postCreateEmptyRole);
    }

    protected Mono<RoleDto> postCreateEmptyRole(RoleDto roleDto) {
        return Mono.just(roleDto);
    }

    @Authenticated
    @DeleteMapping("role/{id}")
    public Mono<RoleDto> deleteRole(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long id) {
        return this.roleClient.getRoleById(id)
                .map(completeRoleDto -> completeRoleDto.getScope().getId())
                .flatMap(aLong -> this.hasEditRolePermission(self, aLong))
                .flatMap(aBoolean -> {
                    if (!aBoolean)
                        return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing edit permission!"));
                    return Mono.just(id);
                })
                .flatMap(this.roleClient::deleteRole)
                .flatMap(this::postDeleteRole);
    }

    protected Mono<RoleDto> postDeleteRole(RoleDto roleDto) {
        return Mono.just(roleDto);
    }

    @Authenticated
    @PostMapping("role/sort")
    public Flux<RoleDisplayDto> sort(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @RequestBody List<Long> roles) {
        return this.roleClient.getRoleById(roles.getFirst()).mapNotNull(completeRoleDto -> Objects.requireNonNull(completeRoleDto.getScope()).getId())
                .flatMap(aLong -> hasEditRolePermission(self, aLong))
                .flatMap(aBoolean -> {
                    if (!aBoolean) return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
                    return Mono.just(roles);
                }).flatMapMany(this.roleClient::sort)
                .flatMap(this::postSort);
    }

    protected Mono<RoleDisplayDto> postSort(RoleDisplayDto roleDto) {
        return Mono.just(roleDto);
    }

    protected abstract Mono<Boolean> hasEditRolePermission(UUID uuid, Long scope);

    protected abstract Mono<Boolean> hasReadPermission(UUID uuid, Long scope);
    // scope controller

    // user controller

    // TODO: 17.02.2025 maybe maybe we also want authentication here
    @GetMapping("user/{scope}")
    public Flux<UserDto> getUsersByScope(@PathVariable Long scope) {
        return this.userClient.getAllUsersByScope(scope)
                .flatMap(userDto -> this.postGetUserByScope(scope, userDto));
    }

    protected Mono<UserDto> postGetUserByScope(Long scope, UserDto result) {
        return Mono.just(result);
    }


    @Authenticated
    @PostMapping("user/{scope}/role/add/{roleId}/user/{uuid}")
    public Mono<UserDto> addRoleToUser(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long scope, @PathVariable UUID uuid, @PathVariable Long roleId) {
        return validateUserHasUserSetRolePermission(self, scope, roleId).then(Mono.just(uuid))
                .flatMap(target -> validateTargetUserExistsInScope(scope, target, roleId))
                .flatMap(aLong -> this.getUserClient().addRole(scope, uuid, aLong))
                .flatMap(userDto -> this.postAddRoleToUser(self, scope, uuid, roleId, userDto));
    }

    protected Mono<UserDto> postAddRoleToUser(UUID self, Long scope, UUID target, Long roleId, UserDto result) {
        return Mono.just(result);
    }

    // roleId is target role, if target role is higher than own role throw error
    private Mono<Void> validateUserHasUserEditPermissions(UUID self, Long scope, Long roleId) {
        return this.hasEditUserPermission(self, scope)
                .then(Mono.just(roleId))
                .flatMap(aLong -> this.getRoleClient().getRoleById(aLong))
                .flatMap(roleDisplayDto -> this.getUserClient().getWeight(scope, self)
                        .flatMap(selfWeight -> {
                            if (roleDisplayDto.getWeight() >= selfWeight)
                                return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Target rank is higher than own rank"));
                            return Mono.empty();
                        })).then();
    }

    private Mono<Void> validateUserHasUserSetRolePermission(UUID self, Long scope, Long roleId) {
        return this.hasUserSetRolePermission(self, scope)
                .then(Mono.just(roleId))
                .flatMap(aLong -> this.getRoleClient().getRoleById(aLong))
                .flatMap(roleDisplayDto -> this.getUserClient().getWeight(scope, self)
                        .flatMap(selfWeight -> {
                            if (roleDisplayDto.getWeight() >= selfWeight)
                                return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Target rank is higher than own rank"));
                            return Mono.empty();
                        })).then();
    }

    protected abstract Mono<Boolean> hasUserSetRolePermission(UUID self, Long scope);

    private Mono<Void> validateUserHasUserDeletePermissions(UUID self, Long scope, Long roleId) {
        return this.hasDeleteUserPermission(self, scope)
                .then(Mono.just(roleId))
                .flatMap(aLong -> this.getRoleClient().getRoleById(aLong))
                .flatMap(roleDisplayDto -> this.getUserClient().getWeight(scope, self)
                        .flatMap(selfWeight -> {
                            if (roleDisplayDto.getWeight() >= selfWeight)
                                return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Target rank is higher than own rank"));
                            return Mono.empty();
                        })).then();

    }

    private Mono<Long> validateTargetUserExistsInScope(Long scope, UUID user, Long roleId) {
        return this.getUserClient().isKnown(scope, user)
                .flatMap(isKnown -> { // TODO: 18.02.2025 implement context: is it possible to just add other people to scope or do they have to accept invite
                    if (!isKnown)
                        return Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not known to this scope"));
                    return Mono.empty();
                })
                .then(Mono.just(roleId));
    }

    protected abstract Mono<Boolean> hasEditUserPermission(UUID uuid, Long scope);


    @Authenticated
    @PostMapping("user/{scope}/role/remove/{roleId}/user/{uuid}")
    public Mono<UserDto> removeRoleFromUser(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long scope, @PathVariable UUID uuid, @PathVariable Long roleId) {
        return this.validateTargetUserExistsInScope(scope, uuid, roleId)
                .flatMap(id -> this.validateUserHasUserSetRolePermission(self, scope, roleId).then(Mono.just(id)))
                .flatMap(aLong -> this.getUserClient().removeRole(scope, uuid, aLong));
    }

    @PostMapping("user/{scope}/user/{uuid}/permission")
    public Mono<UserDto> setUserSpecificPermission(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long scope, @PathVariable UUID uuid, @RequestBody PermissionDto permissionDto) {
        return this.findHighestRank(scope, uuid)
                .flatMap(roleId -> this.validateUserHasUserEditPermissions(self, scope, roleId))
                .then(Mono.just(uuid))
                .flatMap(unused -> this.userClient.setUserSpecificPermission(scope, uuid, permissionDto))
                .flatMap(userDto -> this.postSetUserSpecificPermission(self, scope, uuid, permissionDto, userDto));
    }

    protected Mono<UserDto> postSetUserSpecificPermission(UUID self, Long scope, UUID target, PermissionDto permission, UserDto result) {
        return Mono.just(result);
    }

    @GetMapping("user/{scope}/user/{uuid}/highest")
    public Mono<Long> findHighestUserRank(@PathVariable Long scope, @PathVariable UUID uuid) {
        return this.findHighestRank(scope, uuid);
    }

    private Mono<Long> findHighestRank(Long scope, UUID user) {
        return this.userClient.getUserRoles(scope, user).collectList().mapNotNull(roleDisplayDtos -> {
            int max = Integer.MIN_VALUE;
            RoleDisplayDto maxDto = null;
            for (RoleDisplayDto roleDisplayDto : roleDisplayDtos) {
                if (roleDisplayDto.getWeight() > max) {
                    max = roleDisplayDto.getWeight();
                    maxDto = roleDisplayDto;
                }
            }
            return maxDto;
        }).map(RoleDisplayDto::getId);
    }

    @PostMapping("user/{scope}/user/{uuid}/permissions")
    public Mono<UserDto> setUserSpecificPermissions(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long scope, @PathVariable UUID uuid, @RequestBody Set<PermissionDto> permissionDtos) {
        return this.findHighestRank(scope, uuid)
                .flatMap(aLong -> this.validateUserHasUserEditPermissions(self, scope, aLong))
                .then(Mono.just(uuid))
                .flatMap(unused -> this.userClient.setUserSpecificPermissions(scope, uuid, permissionDtos))
                .flatMap(result -> this.postSetUserSpecificPermissions(self, scope, uuid, permissionDtos, result));
    }

    protected Mono<UserDto> postSetUserSpecificPermissions(UUID self, Long scope, UUID target, Set<PermissionDto> permissionDtos, UserDto result) {
        return Mono.just(result);
    }

    @DeleteMapping("user/{scope}/user/{uuid}")
    public Mono<UserDto> deleteUserFromScope(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long scope, @PathVariable UUID uuid) {
        return this.findHighestRank(scope, uuid)
                .flatMap(id -> this.validateUserHasUserDeletePermissions(self, scope, id))
                .then(Mono.just(uuid))
                .flatMap(target -> this.userClient.deleteUserFromScope(scope, uuid));
    }

    protected abstract Mono<Boolean> hasDeleteUserPermission(UUID user, Long scope);

    // permission controller

    @Authenticated
    @GetMapping("permission/scope/{scope}")
    public Flux<String> getAllPermissionsByScope(@AuthenticatedSelf(IdentityType.MINECRAFT_UUID) UUID self, @PathVariable Long scope) {
        return this.hasReadPermission(self, scope)
                .flatMap(aBoolean -> {
                    if (!aBoolean)
                        return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing read permissions!"));
                    return Mono.just(scope);
                })
                .flatMap(this.permissionClient::getAllPermissionsByScope)
                .flatMapMany(Flux::fromIterable);
    }

    @GetMapping("{scope}/has/{user}/permission/{permission}")
    public Mono<Boolean> hasPermission(@PathVariable Long scope,
                                       @PathVariable UUID user,
                                       @PathVariable String permission) {
        return this.permissionClient.hasPermission(scope, user, permission);
    }


}
