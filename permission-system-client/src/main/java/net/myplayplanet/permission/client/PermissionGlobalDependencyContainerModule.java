package net.myplayplanet.permission.client;

import com.google.inject.AbstractModule;
import net.myplayplanet.di.GlobalDependencyContainerModule;

import java.util.List;

public class PermissionGlobalDependencyContainerModule implements GlobalDependencyContainerModule {

    @Override
    public List<AbstractModule> getModulesToRegister() {
        return List.of(new PermissionClientModule(true));
    }
}
