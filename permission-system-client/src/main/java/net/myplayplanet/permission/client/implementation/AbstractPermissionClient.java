package net.myplayplanet.permission.client.implementation;

import net.myplayplanet.services.rest.base.client.crud.AbstractBaseRestClient;
import net.myplayplanet.services.rest.base.client.crud.DebugLevel;

abstract class AbstractPermissionClient extends AbstractBaseRestClient {

    private static final String SUB_DIRECTORY = "api/v1/permission/";

    protected final String base;


    public AbstractPermissionClient(String baseUrl, String base, DebugLevel debug) {
        super(baseUrl, debug);
        this.base = SUB_DIRECTORY + base;
    }

    public AbstractPermissionClient(String baseUrl, String base) {
        super(baseUrl);
        this.base = SUB_DIRECTORY + base;
    }
}
