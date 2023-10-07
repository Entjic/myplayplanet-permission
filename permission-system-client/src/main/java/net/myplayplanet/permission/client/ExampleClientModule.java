package net.myplayplanet.permission.client;

import net.myplayplanet.permission.di.ImplementationInterfaceRegisterModule;
import net.myplayplanet.permission.client.api.ExampleMarkerInterface;

public class ExampleClientModule extends ImplementationInterfaceRegisterModule<ExampleMarkerInterface> {

    private final static String[] packages = {
            "net.myplayplanet.permission.example.client.implementation"
    };

    public ExampleClientModule(ClassLoader classLoader, boolean asSingleton) {
        super(ExampleMarkerInterface.class, classLoader, asSingleton, packages);
    }

    public ExampleClientModule(boolean asSingleton) {
        super(ExampleMarkerInterface.class, asSingleton, packages);
    }
}
