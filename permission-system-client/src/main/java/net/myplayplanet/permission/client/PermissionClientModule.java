package net.myplayplanet.permission.client;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.di.ImplementationInterfaceRegisterModule;

@Slf4j
public class PermissionClientModule extends ImplementationInterfaceRegisterModule<IPermissionClient> {

    private static final String[] packageName = new String[]{
            "net.myplayplanet.permission.api",
            "net.myplayplanet.permission.internal"
    };

    public PermissionClientModule(final ClassLoader loader,
                                  final boolean asSingleton) {
        super(IPermissionClient.class, loader, asSingleton, packageName);
    }

    public PermissionClientModule() {
        super(IPermissionClient.class, true, packageName);
    }
}
