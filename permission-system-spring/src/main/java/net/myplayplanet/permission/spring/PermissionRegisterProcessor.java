package net.myplayplanet.permission.spring;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.permission.api.PermissionClient;
import net.myplayplanet.permission.api.RoleClient;
import net.myplayplanet.permission.api.ScopeClient;
import net.myplayplanet.permission.model.PermissionInfoDto;
import net.myplayplanet.permission.model.RoleDto;
import net.myplayplanet.permission.model.ScopeDto;
import net.myplayplanet.services.rest.base.core.exception.CustomErrorResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Component
public class PermissionRegisterProcessor {
    private final Collection<PermissionAutoRegister> autoRegisters;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final ScopeClient scopeClient;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final PermissionClient permissionClient;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final RoleClient roleClient;

    @PostConstruct
    public void process() {
        for (final PermissionAutoRegister autoRegister : autoRegisters) {
            createDefaults(autoRegister).subscribe();
        }
    }


    public Mono<Void> createDefaults(PermissionAutoRegister autoRegister) {
        ScopeDto scope = autoRegister.permissionScope();
        if (scope == null) throw new IllegalStateException("Scope is null");

        Mono<ScopeDto> scopeMono = this.scopeClient.getOrCreateScope(scope)
                .onErrorResume(throwable -> {
                    log.error("Failed to get or create scope for {}", scope, throwable);
                    return Mono.empty();
                });


        for (final PermissionInfoDto permission : autoRegister.permissions()) {
            scopeMono = scopeMono.flatMap(scopeDto -> savePermission(permission, scope.getId()).then(Mono.just(scopeDto)));
        }

        for (final RoleDto defaultRole : autoRegister.defaultRoles()) {
            scopeMono = scopeMono.flatMap(scopeDto -> saveRole(defaultRole, scopeDto)
                    .then(Mono.just(scopeDto)));
        }

        return scopeMono.doOnSuccess(scopeDto -> log.info("Successfully initialized permission things")).then();
    }

    private Mono<PermissionInfoDto> savePermission(final PermissionInfoDto permission, final Long scope) {
        return this.permissionClient.createPermission(permission)
                .onErrorResume(throwable -> {
                    if (!(throwable instanceof CustomErrorResponse customErrorResponse))
                        return Mono.error(throwable);

                    if (customErrorResponse.getStatus() == 409) return Mono.just(permission)
                            .flatMap(permissionInfoDto -> this.permissionClient.addPermissionToScope(permissionInfoDto.getKey(), scope))
                            .then(Mono.just(permission));
                    return Mono.error(throwable);
                })
                .doOnSuccess(permissionInfoDto -> {
                    log.info("Initialized permission {}", permissionInfoDto);
                });

    }

    private Mono<RoleDto> saveRole(final RoleDto defaultRole, final ScopeDto scopeDto) {
        return this.roleClient.alterOrCreateRole(scopeDto.getId(), defaultRole)
                .doOnSuccess(role -> {
                    log.info("Initialized role {}", role);
                });

    }

}
