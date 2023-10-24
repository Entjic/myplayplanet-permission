package net.myplayplanet.permission.client.api;

import net.myplayplanet.permission.core.enums.DeletionMode;
import org.openapitools.client.model.PermissionInfoDto;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

public interface PermissionClient extends PermissionClientMarker{

    Mono<PermissionInfoDto> save(PermissionInfoDto permissionDto);

    Mono<PermissionInfoDto> updatePermission(PermissionInfoDto permissionInfoDto);

    Flux<UUID> deletePermission(UUID uuid, DeletionMode mode);

    Mono<Boolean> hasPermission(UUID user, UUID permission);

    Flux<UUID> getAll();
}
