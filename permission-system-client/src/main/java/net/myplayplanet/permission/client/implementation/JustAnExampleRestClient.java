package net.myplayplanet.permission.client.implementation;

import com.google.inject.name.Named;
import net.myplayplanet.permission.client.api.JustAnExampleClient;
import net.myplayplanet.permission.services.rest.base.client.crud.DebugLevel;
import reactor.core.publisher.Mono;

public class JustAnExampleRestClient extends AbstractExampleClient implements JustAnExampleClient {


    public JustAnExampleRestClient(@Named("exampleBaseUrl") String baseUrl) {
        super(baseUrl, "just/an/example/", DebugLevel.SIMPLE);
    }

    @Override
    public Mono<Integer> example() {
        return Mono.just(4);
    }
}
