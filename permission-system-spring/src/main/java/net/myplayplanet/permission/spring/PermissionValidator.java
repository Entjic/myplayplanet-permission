package net.myplayplanet.permission.spring;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.permission.api.PermissionClient;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
@NoArgsConstructor
public class PermissionValidator {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private PermissionClient permissionClient;

    @EventListener(ApplicationReadyEvent.class)
    public void process(ApplicationReadyEvent event) {
        ApplicationContext springContext = event.getApplicationContext();
        AutowireCapableBeanFactory factory = springContext.getAutowireCapableBeanFactory();
        this.permissionClient = factory.createBean(PermissionClient.class);
    }

    public Mono<Void> assertPermissionAsync(UUID user, PermissionType type, Long scope) {
        return this.permissionClient.hasPermission(scope, user, type.permissionKey()).onErrorResume(throwable -> {
            log.error("Error checking permission {} for user {} in scope {}", type.permissionKey(), user, scope, throwable);
            return Mono.just(false);
        }).flatMap(hasPermission -> {
            if (hasPermission) return Mono.empty();
            return Mono.error(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        }).then();
    }

    public void assertPermission(UUID user, PermissionType type, Long scope) {
        this.assertPermissionAsync(user, type, scope).block();
    }

}
