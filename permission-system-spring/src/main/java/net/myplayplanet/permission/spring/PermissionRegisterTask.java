package net.myplayplanet.permission.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.permission.api.PermissionClient;
import net.myplayplanet.permission.api.RoleClient;
import net.myplayplanet.permission.api.ScopeClient;
import net.myplayplanet.permission.model.PermissionInfoDto;
import net.myplayplanet.permission.model.RoleDto;
import net.myplayplanet.permission.model.ScopeDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class PermissionRegisterTask {

    private final PermissionClient permissionClient;
    private final ScopeClient scopeClient;
    private final RoleClient roleClient;

    public Mono<ScopeDto> createDefaults(PermissionAutoRegister autoRegister) {

        ScopeDto scope = new ScopeDto();
        scope.name(autoRegister.scopeName());

        return this.scopeClient.getOrCreateScope(scope)
                .onErrorResume(throwable -> {
                    log.error("Failed to get or create scope for {}", scope, throwable);
                    return Mono.empty();
                }).flatMap(scopeDto -> savePermissions(autoRegister, scopeDto))
                .flatMap(scopeDto -> saveRoles(autoRegister, scopeDto))
                .doOnSuccess(scopeDto -> log.info("Successfully initialized permission things"));
    }

    private Mono<ScopeDto> saveRoles(final PermissionAutoRegister autoRegister, final ScopeDto scopeDto) {
        return Flux.fromIterable(autoRegister.defaultRoles())
                .flatMap(roleDto -> saveRole(roleDto, scopeDto.getId()))
                .then(Mono.just(scopeDto));
    }

    private Mono<ScopeDto> savePermissions(final PermissionAutoRegister autoRegister, final ScopeDto scopeDto) {
        return Flux.fromIterable(autoRegister.permissions())
                .flatMap(permissionInfoDto -> savePermission(permissionInfoDto, scopeDto.getId()))
                .then(Mono.just(scopeDto));
    }

    private Mono<PermissionInfoDto> savePermission(final PermissionInfoDto permission, final Long scope) {
        return this.permissionClient.createPermission(permission)
                .onErrorResume(throwable -> {
                    if (!(throwable instanceof WebClientResponseException webClientResponseException))
                        return Mono.error(throwable);

                    if (webClientResponseException.getStatusCode().equals(HttpStatus.CONFLICT))
                        return Mono.just(permission);
                    return Mono.error(throwable);
                })
                .flatMap(permissionInfoDto -> this.permissionClient.addPermissionToScope(permissionInfoDto.getKey(), scope)
                        .then(Mono.just(permissionInfoDto)))
                .doOnSuccess(permissionInfoDto -> log.info("Initialized permission {}", permissionInfoDto));

    }

    private Mono<RoleDto> saveRole(final RoleDto defaultRole, final Long scope) {
        return this.roleClient.updateRole(scope, defaultRole)
                .doOnSuccess(role -> {
                    log.info("Initialized role {}", role);
                });

    }


}
