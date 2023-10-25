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
    public Mono<ExtensiveEffectiveUserModelDto>
    getExtensiveEffectiveUserModelDto(Long scope,
                                      UUID uuid) {
        return super.get(uriBuilder -> uriBuilder.path(super.base)
                        .path("{scope}/effective/extensive/{uuid}/").build(scope, uuid),
                ExtensiveEffectiveUserModelDto.class);
    }

    @Override
    public Mono<EffectiveUserModelDto> getEffectiveUserModelDto(Long scope, UUID uuid) {
        return super.get(uriBuilder -> uriBuilder.path(super.base)
                        .path("{scope}/effective/{uuid}/").build(scope, uuid),
                EffectiveUserModelDto.class);
    }

    @Override
    public Mono<UserDto> addRole(Long scope, UUID uuid, Long roleId) {
        return super.post(uriBuilder -> uriBuilder.path(super.base)
                        .path("{scope}/role/add/{roleId}/user/{uuid}/")
                        .build(scope, uuid, roleId),
                UserDto.class);
    }

    @Override
    public Mono<UserDto> removeRole(Long scope, UUID uuid, Long roleId) {
        return super.post(uriBuilder -> uriBuilder.path(super.base)
                .path("{scope}/role/remove/{roleId}/user/{uuid}/")
                .build(scope, uuid, roleId), UserDto.class);
    }

    @Override
    public Flux<UserDto> getAll(Long scope) {
        return super.getFlux(uriBuilder -> uriBuilder.path(super.base).path("{scope}/all/")
                .build(scope), UserDto.class);
    }
}
