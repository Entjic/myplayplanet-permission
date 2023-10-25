package net.myplayplanet.permission.api;

import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.permission.client.api.PermissionClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
public class PermissionEvaluator {

    private final PermissionClient permissionClient;
    private final Long scope;

    public PermissionEvaluator(@Named("permissionScope") Long scope,
                               PermissionClient permissionClient) {
        this.scope = scope;
        this.permissionClient = permissionClient;
    }

    public Mono<Boolean> hasPermission(UUID user, UUID permission) {
        return permissionClient.hasPermission(this.scope, user, permission)
                .onErrorResume(throwable -> {
                    log.error("Error whilst checking if player has permission, " +
                            "defaulting back to FALSE", throwable);
                    return Mono.just(Boolean.FALSE);
                });
    }

}
