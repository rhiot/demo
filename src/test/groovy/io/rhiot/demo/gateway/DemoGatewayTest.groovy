package io.rhiot.demo.gateway

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

        setStringProperty("gps_store_directory", gpsCoordinatesStore.getAbsolutePath());
        setStringProperty('gps_cloudlet_endpoint', 'mock:gps')
    }

    @Test
    void shouldCollectAndSendGpsCoordinates() {
        // Given
        def dataStreamConsumerMock = camelContext().getEndpoint('mock:gps', MockEndpoint.class)
        dataStreamConsumerMock.reset()
        dataStreamConsumerMock.setExpectedMessageCount(1)

        // Then
        dataStreamConsumerMock.assertIsSatisfied()
    }

}
