package net.myplayplanet.permission.client.api;

import net.myplayplanet.permission.core.dto.ScopeDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ScopeClient extends PermissionClientMarker{

    Mono<ScopeDto> getById(Long scope);

    Mono<ScopeDto> create(String name);

    Mono<ScopeDto> rename(Long scope, String name);

    Mono<ScopeDto> delete(Long scope);

    Flux<Long> getAllIds();

    Flux<ScopeDto> getAll();

}
