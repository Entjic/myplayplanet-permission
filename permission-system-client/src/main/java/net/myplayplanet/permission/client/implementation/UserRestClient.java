package net.myplayplanet.permission.client.implementation;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.myplayplanet.permission.client.api.UserClient;
import net.myplayplanet.permission.core.dto.UserDto;
import net.myplayplanet.permission.core.dto.effective.EffectiveUserModelDto;
import net.myplayplanet.permission.core.dto.effective.ExtensiveEffectiveUserModelDto;
import net.myplayplanet.services.rest.base.client.crud.DebugLevel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

final class UserRestClient extends AbstractPermissionClient implements UserClient {

    @Inject
    private UserRestClient(@Named("permissionBaseUrl") String baseUrl) {
        super(baseUrl, "user/", DebugLevel.SIMPLE);
    }

    @Override
    public Mono<ExtensiveEffectiveUserModelDto> getExtensiveEffectiveUserModelDto(UUID uuid) {
        return super.get(uriBuilder -> uriBuilder.path(super.base)
                        .path("effective/extensive/{uuid}").build(uuid),
                ExtensiveEffectiveUserModelDto.class);
    }

    @Override
    public Mono<EffectiveUserModelDto> getEffectiveUserModelDto(UUID uuid) {
        return super.get(uriBuilder -> uriBuilder.path(super.base)
                .path("effective/{uuid}").build(uuid), EffectiveUserModelDto.class);
    }

    @Override
    public Mono<UserDto> addRole(UUID uuid, Long roleId) {
        return super.post(uriBuilder -> uriBuilder.path(super.base)
                .path("role/add/{roleId}/user/{uuid}")
                .build(uuid, roleId),
                UserDto.class);
    }

    @Override
    public Mono<UserDto> removeRole(UUID uuid, Long roleId) {
        return super.post(uriBuilder -> uriBuilder.path(super.base)
                .path("role/remove/{roleId}/user/{uuid}")
                .build(uuid, roleId), UserDto.class);
    }

    @Override
    public Flux<UserDto> getAll() {
        return super.getFlux(uriBuilder -> uriBuilder.path(super.base).path("all/")
                .build(), UserDto.class);
    }
}
