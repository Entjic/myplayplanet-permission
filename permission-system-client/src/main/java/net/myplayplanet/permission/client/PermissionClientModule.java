package net.myplayplanet.permission.client;

import net.myplayplanet.di.ImplementationInterfaceRegisterModule;
import net.myplayplanet.permission.client.api.PermissionClient;

public class PermissionClientModule extends ImplementationInterfaceRegisterModule<PermissionClient> {

    private final static String[] packages = {
            "net.myplayplanet.permission.client.implementation"
    };

    public PermissionClientModule(ClassLoader classLoader, boolean asSingleton) {
        super(PermissionClient.class, classLoader, asSingleton, packages);
    }

    public PermissionClientModule(boolean asSingleton) {
        super(PermissionClient.class, asSingleton, packages);
    }
}
