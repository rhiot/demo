package io.rhiot.demo.gateway

import io.rhiot.component.gpsd.ClientGpsCoordinates
import io.rhiot.gateway.test.GatewayTest
import org.apache.camel.component.mock.MockEndpoint
import org.junit.Ignore
import org.junit.Test

import static com.google.common.io.Files.createTempDir
import static io.rhiot.steroids.camel.CamelBootInitializer.camelContext
import static io.rhiot.utils.Properties.setStringProperty

class DemoGatewayTest extends GatewayTest {

    static def gpsCoordinatesStore = createTempDir()

    @Override
    protected void doBefore() {
        setStringProperty('log_appender', 'STDOUT')

        setStringProperty('gps_endpoint', 'seda:gps')
        setStringProperty("gps_store_directory", gpsCoordinatesStore.getAbsolutePath());
        setStringProperty('gps_cloudlet_endpoint', 'mock:gps')
    }

    @Ignore('We need to mock Wifi enriching.')
    @Test
    void shouldCollectAndSendGpsCoordinates() {
        // Given
        def coordinates = new ClientGpsCoordinates(new Date(), 10.0, 20.0)
        def dataStreamConsumerMock = camelContext().getEndpoint('mock:gps', MockEndpoint.class)
        dataStreamConsumerMock.reset()
        dataStreamConsumerMock.setExpectedMessageCount(1)

        // When
        camelContext().createProducerTemplate().sendBody('seda:gps', coordinates)

        // Then
        dataStreamConsumerMock.assertIsSatisfied()
    }

}
