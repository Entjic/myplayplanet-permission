package net.myplayplanet.permission.bukkit;

import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.permission.api.PermissionClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
public class PermissionEvaluator {

    private final PermissionClient permissionClient;
    private final Long scope;

    public PermissionEvaluator(@Named("permissionScope") Long scope,
                               PermissionClient permissionClient) {
        this.permissionClient = permissionClient;
        this.scope = scope;

    }

    public Mono<Boolean> hasPermission(UUID user, String permission) {
        return permissionClient.hasPermission(this.scope, user, permission)
                .onErrorResume(throwable -> {
                    log.error("Error whilst checking if user {} has permission {}, " +
                            "defaulting back to FALSE", user, permission, throwable);
                    return Mono.just(Boolean.FALSE);
                });
    }

}
