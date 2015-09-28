package io.rhiot.demo

import io.rhiot.steroids.camel.Route
import org.apache.camel.builder.RouteBuilder

import static org.apache.camel.model.dataformat.JsonLibrary.Jackson

@Route
class WiFiRoutes extends RouteBuilder {

    @Override
    void configure() {
        restConfiguration().component('netty4-http').host('0.0.0.0').port(8080)

        rest('/wifi').get().route().to('kura-wifi:*/*').marshal().json(Jackson)
    }

}