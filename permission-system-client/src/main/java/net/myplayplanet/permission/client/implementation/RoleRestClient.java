package net.myplayplanet.permission.client.implementation;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.myplayplanet.permission.client.api.RoleClient;
import net.myplayplanet.permission.core.dto.RoleDto;
import net.myplayplanet.permission.core.enums.PermissionValue;
import net.myplayplanet.services.rest.base.client.crud.DebugLevel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

final class RoleRestClient extends AbstractPermissionClient implements RoleClient {

    @Inject
    private RoleRestClient(@Named("permissionBaseUrl") String baseUrl) {
        super(baseUrl, "role/", DebugLevel.SIMPLE);
    }


    @Override
    public Mono<RoleDto> createRole(RoleDto roleDto) {
        return super.post(uriBuilder -> uriBuilder.path(super.base).path("create/").build(),
                roleDto, RoleDto.class);
    }

    @Override
    public Mono<Long> deleteRole(Long id) {
        return super.delete(uriBuilder -> uriBuilder.path(super.base).path("delete/{id}")
                .build(id), Long.class);
    }

    @Override
    public Mono<RoleDto> changeWeight(Long id, Integer weight) {
        return super.post(uriBuilder -> uriBuilder.path(super.base).path("{id}/weight/{weight}")
                .build(id, weight), RoleDto.class);
    }

    @Override
    public Mono<RoleDto> setPermission(Long id, UUID uuid, PermissionValue value) {
        return super.post(uriBuilder -> uriBuilder.path(super.base).path("{id}/permission/{uuid}/{value}")
                .build(id, uuid, value), RoleDto.class);
    }

    @Override
    public Flux<Long> getAllRoleIds() {
        return super.getFlux(uriBuilder -> uriBuilder.path(super.base).path("all/").build(), Long.class);
    }
}
