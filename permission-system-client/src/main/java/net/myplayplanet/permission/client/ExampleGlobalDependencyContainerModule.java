package net.myplayplanet.permission.client;

import com.google.inject.AbstractModule;
import net.myplayplanet.permission.di.GlobalDependencyContainerModule;

import java.util.List;

public class ExampleGlobalDependencyContainerModule implements GlobalDependencyContainerModule {

    @Override
    public List<AbstractModule> getModulesToRegister() {
        return List.of(new ExampleClientModule(true));
    }
}
