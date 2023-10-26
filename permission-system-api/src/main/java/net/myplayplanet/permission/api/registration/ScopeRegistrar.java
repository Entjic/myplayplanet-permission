package net.myplayplanet.permission.api.registration;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.permission.client.api.ScopeClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ScopeRegistrar implements Registrar<String>{

    private final ScopeClient scopeClient;
    @Override
    public void register(String name) {
        this.scopeClient.create(name)
                .onErrorResume(throwable -> {
                    log.error("Error whilst registering scope.", throwable);
                    return Mono.empty();
                });

    }
}
