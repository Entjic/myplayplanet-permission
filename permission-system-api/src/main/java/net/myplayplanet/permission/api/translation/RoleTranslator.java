package net.myplayplanet.permission.api.translation;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.client.api.RoleClient;
import net.myplayplanet.permission.client.api.ScopeClient;
import net.myplayplanet.permission.core.dto.RoleDisplayDto;
import net.myplayplanet.permission.core.dto.ScopeDto;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RoleTranslator {

    private final RoleClient roleClient;
    private final ScopeClient scopeClient;

    public static String defaultRoleGrantedKey(){
        return "global.permission.default.role_granted";
    }

    public static String defaultRoleRevokedKey() {
        return "global.permission.default.role_revoked";
    }

    public Mono<String> generateNameKey(Long scope, Long id) {
        return this.gatherData(scope, id)
                .map(names -> "global.permission.scope." + names.getT1().strip().toLowerCase()
                        + ".rank." + names.getT2().strip().toLowerCase() + ".name");
    }

    public Mono<String> generateTitleKey(Long scope, Long id) {
        return this.gatherData(scope, id)
                .map(names -> "global.permission.scope." + names.getT1().strip().toLowerCase()
                        + ".rank." + names.getT2().strip().toLowerCase() + ".description");
    }

    public Mono<String> generateRoleGrantedKey(Long scope, Long id) {
        return this.gatherData(scope, id)
                .map(names -> "global.permission.scope." + names.getT1().strip().toLowerCase()
                        + ".rank." + names.getT2().strip().toLowerCase() + ".role_granted");
    }

    public Mono<String> generateRoleRevokedKey(Long scope, Long id) {
        return this.gatherData(scope, id)
                .map(names -> "global.permission.scope." + names.getT1().strip().toLowerCase()
                        + ".rank." + names.getT2().strip().toLowerCase() + ".role_revoked");
    }

    private Mono<Tuple2<String, String>> gatherData(Long scope, Long roleId) {
        return Mono.zip(this.scopeClient.getById(scope).map(ScopeDto::getName),
                this.roleClient.getById(roleId).map(RoleDisplayDto::getName));
    }
}
