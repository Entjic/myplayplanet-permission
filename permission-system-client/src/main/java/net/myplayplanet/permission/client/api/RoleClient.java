package net.myplayplanet.permission.client.api;

import net.myplayplanet.permission.core.dto.RoleDisplayDto;
import net.myplayplanet.permission.core.dto.RoleDto;
import net.myplayplanet.permission.core.enums.PermissionValue;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RoleClient extends PermissionClientMarker{

    Mono<RoleDto> createRole(Long scope, RoleDto roleDto);

    Mono<Long> deleteRole(Long id);

    Mono<RoleDto> changeWeight(Long id, Integer weight);

    Mono<RoleDto> setPermission(Long id, UUID uuid, PermissionValue value);

    Flux<RoleDisplayDto> getAllRolesByScope(Long scope);

    Flux<Long> getAllRoleIds();

    Mono<RoleDisplayDto> getById(Long id);
}
