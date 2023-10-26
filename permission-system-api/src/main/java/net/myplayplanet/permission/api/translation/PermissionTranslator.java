package net.myplayplanet.permission.api.translation;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.client.api.PermissionClient;
import net.myplayplanet.permission.client.api.ScopeClient;
import net.myplayplanet.permission.core.dto.PermissionDisplayDto;
import net.myplayplanet.permission.core.dto.ScopeDto;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.inject.Inject;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PermissionTranslator {

    private final PermissionClient permissionClient;
    private final ScopeClient scopeClient;

    public static String defaultMissingPermissionKey(){
        return "global.permission.default.missing_permission";
    }

    public Mono<String> generateNameKey(Long scope, UUID uuid) {
        return this.gatherData(scope, uuid)
                .map(names -> "global.permission.scope." + names.getT1().strip().toLowerCase()
                        + ".permission." + names.getT2().strip().toLowerCase() + ".name");
    }

    public Mono<String> generateTitleKey(Long scope, UUID uuid) {
        return this.gatherData(scope, uuid)
                .map(names -> "global.permission.scope." + names.getT1().strip().toLowerCase()
                        + ".permission." + names.getT2().strip().toLowerCase() + ".description");
    }

    public Mono<String> generateMissingPermissionKey(Long scope, UUID uuid) {
        return this.gatherData(scope, uuid)
                .map(names -> "global.permission.scope." + names.getT1().strip().toLowerCase()
                        + ".permission." + names.getT2().strip().toLowerCase() + ".missing_permission");
    }


    private Mono<Tuple2<String, String>> gatherData(Long scope, UUID uuid) {
        return Mono.zip(this.scopeClient.getById(scope).map(ScopeDto::getName),
                this.permissionClient.getById(uuid).map(PermissionDisplayDto::getName));

    }
}
