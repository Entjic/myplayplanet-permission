package net.myplayplanet.permission.bukkit;

import com.google.inject.Injector;
import com.google.inject.name.Names;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.di.DependencyContainer;
import net.myplayplanet.permission.api.PermissionClient;
import org.bukkit.plugin.java.JavaPlugin;

@Slf4j
public class PermissionSystem extends JavaPlugin {

    @Override
    public void onEnable() {
        log.info("Enabling Permission System");
        Injector injector = DependencyContainer.getInstance()
                .createChildInjector(binder -> binder.bind(String.class).annotatedWith(Names.named("permissionScope"))
                        .toInstance("development"));

        PermissionClient permissionApi = injector.getInstance(PermissionClient.class);

        permissionApi.getAllPermissions().doOnNext(permissionDisplayDto -> {
            log.info(permissionDisplayDto.getName());
        }).subscribe();
    }

    @Override
    public void onDisable() {
        log.error("Disabling Permission System");
    }
}
