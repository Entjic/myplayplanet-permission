package net.myplayplanet.permission.client.implementation;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.myplayplanet.permission.client.api.PermissionClient;
import net.myplayplanet.permission.core.dto.PermissionDisplayDto;
import net.myplayplanet.permission.core.dto.PermissionInfoDto;
import net.myplayplanet.permission.core.enums.DeletionMode;
import net.myplayplanet.services.rest.base.client.crud.DebugLevel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

final class PermissionRestClient extends AbstractPermissionClient implements PermissionClient {


    @Inject
    private PermissionRestClient(@Named("permissionBaseUrl") String baseUrl) {
        super(baseUrl, "", DebugLevel.SIMPLE);
    }


    @Override
    public Mono<PermissionInfoDto> save(PermissionInfoDto permissionDto) {
        return super.post(uriBuilder -> uriBuilder.path(super.base).path("save/").build(),
                permissionDto, PermissionInfoDto.class);
    }

    @Override
    public Mono<PermissionInfoDto> updatePermission(PermissionInfoDto permissionInfoDto) {
        return super.post(uriBuilder -> uriBuilder.path(super.base).path("update/").build(),
                permissionInfoDto, PermissionInfoDto.class);
    }

    @Override
    public Flux<UUID> deletePermission(UUID uuid, DeletionMode mode) {
        return super.deleteFlux(uriBuilder -> uriBuilder
                .path(super.base).path("delete/{uuid}/mode/{mode}/")
                .build(uuid, mode), UUID.class);
    }

    @Override
    public Mono<Boolean> hasPermission(Long scope, UUID user, UUID permission) {
        return super.get(uriBuilder -> uriBuilder.path(super.base)
                .path("{scope}/has/{user}/permission/{permission}/")
                .build(scope, user, permission), Boolean.class);
    }

    @Override
    public Flux<PermissionDisplayDto> getAll() {
        return super.getFlux(uriBuilder -> uriBuilder.path(super.base)
                .path("all/").build(), PermissionDisplayDto.class);
    }

    @Override
    public Mono<PermissionDisplayDto> getById(UUID uuid) {
        return super.get(uriBuilder -> uriBuilder.path(super.base)
                .path("{id}/")
                .build(),
                PermissionDisplayDto.class);
    }
}
