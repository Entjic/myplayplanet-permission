package net.myplayplanet.permission.client.implementation;

import net.myplayplanet.permission.services.rest.base.client.crud.AbstractBaseRestClient;
import net.myplayplanet.permission.services.rest.base.client.crud.DebugLevel;

abstract class AbstractExampleClient extends AbstractBaseRestClient {

    private static final String SUB_DIRECTORY = "api/v1/wargear_team/";

    protected final String base;


    public AbstractExampleClient(String baseUrl, String base, DebugLevel debug) {
        super(baseUrl, debug);
        this.base = SUB_DIRECTORY + base;
    }

    public AbstractExampleClient(String baseUrl, String base) {
        super(baseUrl);
        this.base = SUB_DIRECTORY + base;
    }
}
