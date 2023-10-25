package net.myplayplanet.permission.api.registration;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.permission.client.api.RoleClient;
import net.myplayplanet.permission.core.dto.RoleDto;
import reactor.core.publisher.Mono;

@Slf4j
public class RoleRegistrar implements Registrar<RoleDto> {

    private final Long scope;
    private final RoleClient roleClient;

    @Inject
    public RoleRegistrar(@Named("permissionScope") Long scope, RoleClient roleClient) {
        this.scope = scope;
        this.roleClient = roleClient;
    }

    @Override
    public void register(RoleDto type) {
        this.roleClient.createRole(this.scope, type)
                .onErrorResume(throwable -> {
                    log.error("Error whilst registering default role.", throwable);
                    return Mono.empty();
                })
                .subscribe();
    }
}
