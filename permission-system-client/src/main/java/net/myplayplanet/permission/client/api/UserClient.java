package net.myplayplanet.permission.client.api;

import net.myplayplanet.permission.core.dto.UserDto;
import net.myplayplanet.permission.core.dto.effective.EffectiveUserModelDto;
import net.myplayplanet.permission.core.dto.effective.ExtensiveEffectiveUserModelDto;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserClient extends PermissionClientMarker{

    Mono<ExtensiveEffectiveUserModelDto> getExtensiveEffectiveUserModelDto(UUID uuid);

    Mono<EffectiveUserModelDto> getEffectiveUserModelDto(UUID uuid);

    Mono<UserDto> addRole(UUID uuid, Long roleId);

    Mono<UserDto> removeRole(UUID uuid, Long roleId);

    Flux<UserDto> getAll();
}
