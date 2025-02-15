package net.myplayplanet.permission.bukkit.translation;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.api.PermissionClient;
import net.myplayplanet.permission.api.ScopeClient;

@RequiredArgsConstructor
public class PermissionTranslator {

    private final PermissionClient permissionClient;
    private final ScopeClient scopeClient;

    public static String defaultMissingPermissionKey() {
        return "global.permission.default.missing_permission";
    }

}
