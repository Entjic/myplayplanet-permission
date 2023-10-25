package net.myplayplanet.permission.client.implementation;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.myplayplanet.permission.client.api.ScopeClient;
import net.myplayplanet.permission.core.dto.ScopeDto;
import net.myplayplanet.services.rest.base.client.crud.DebugLevel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


final class ScopeRestClient extends AbstractPermissionClient implements ScopeClient {

    @Inject
    private ScopeRestClient(@Named("permissionBaseUrl") String baseUrl) {
        super(baseUrl, "scope/", DebugLevel.SIMPLE);
    }

    @Override
    public Mono<ScopeDto> getById(Long scope) {
        return super.get(uriBuilder -> uriBuilder.path(super.base)
                        .path("{id}/").build(scope),
                ScopeDto.class);
    }

    @Override
    public Mono<ScopeDto> create(String name) {
        return super.post(uriBuilder -> uriBuilder.path(super.base)
                .path("create/{name}/")
                .build(name),
                ScopeDto.class);
    }

    @Override
    public Mono<ScopeDto> rename(Long scope, String name) {
        return super.post(uriBuilder -> uriBuilder.path(super.base)
                .path("rename/{id}/name/{name}/")
                .build(scope, name),
                ScopeDto.class);
    }

    @Override
    public Mono<ScopeDto> delete(Long scope) {
        return super.delete(uriBuilder -> uriBuilder.path(super.base)
                .path("{id}/")
                .build(scope),
                ScopeDto.class);
    }

    @Override
    public Flux<Long> getAllIds() {
        return super.getFlux(uriBuilder -> uriBuilder.path(super.base)
                .path("all/id/")
                .build(), Long.class);
    }

    @Override
    public Flux<ScopeDto> getAll() {
        return super.getFlux(uriBuilder -> uriBuilder.path(super.base)
                .path("all/")
                .build(),
                ScopeDto.class);
    }
}
