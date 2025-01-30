package net.myplayplanet.permission.bukkit.translation;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.api.PermissionClient;
import net.myplayplanet.permission.api.ScopeClient;
import net.myplayplanet.permission.model.PermissionDisplayDto;
import net.myplayplanet.permission.model.ScopeDto;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.UUID;

@RequiredArgsConstructor
public class PermissionTranslator {

    private final PermissionClient permissionClient;
    private final ScopeClient scopeClient;

    public static String defaultMissingPermissionKey() {
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
        return Mono.zip(this.scopeClient.getScopeById(scope).mapNotNull(ScopeDto::getName),
                this.permissionClient.getPermissionById(uuid).mapNotNull(PermissionDisplayDto::getName));

    }
}
