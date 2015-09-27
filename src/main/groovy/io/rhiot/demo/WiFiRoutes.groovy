package io.rhiot.demo

import org.apache.camel.builder.RouteBuilder

import static org.apache.camel.model.dataformat.JsonLibrary.Jackson

class WiFiRoutes extends RouteBuilder {

    @Override
    void configure() {
        rest('/wifi').route().to('kura-wifi:*/*').marshal().json(Jackson)
    }

}