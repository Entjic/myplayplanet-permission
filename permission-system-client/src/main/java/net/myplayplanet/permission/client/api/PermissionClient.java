package net.myplayplanet.permission.client.api;

import net.myplayplanet.permission.core.dto.PermissionDisplayDto;
import net.myplayplanet.permission.core.dto.PermissionDto;
import net.myplayplanet.permission.core.dto.PermissionInfoDto;
import net.myplayplanet.permission.core.enums.DeletionMode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

public interface PermissionClient extends PermissionClientMarker{

    Mono<PermissionInfoDto> save(PermissionInfoDto permissionDto);

    Mono<PermissionInfoDto> updatePermission(PermissionInfoDto permissionInfoDto);

    Flux<UUID> deletePermission(UUID uuid, DeletionMode mode);

    Mono<Boolean> hasPermission(Long scope, UUID user, UUID permission);

    Flux<PermissionDisplayDto> getAll();

    Mono<PermissionDisplayDto> getById(UUID uuid);
}
