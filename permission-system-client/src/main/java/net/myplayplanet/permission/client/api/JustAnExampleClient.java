package net.myplayplanet.permission.client.api;

import reactor.core.publisher.Mono;

public interface JustAnExampleClient extends ExampleMarkerInterface {

    Mono<Integer> example();

}
