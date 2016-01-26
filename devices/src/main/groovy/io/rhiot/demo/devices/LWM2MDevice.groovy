package io.rhiot.demo.devices

import static io.rhiot.utils.leshan.client.LeshanClientTemplate.createVirtualLeshanClientTemplate
import static java.util.concurrent.TimeUnit.SECONDS
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic

class LWM2MDevice {

    def leshan = createVirtualLeshanClientTemplate(randomAlphabetic(10))

    def start() {
        leshan.connect()
        while (true) {
            println "I'm LWM2M device!"
            SECONDS.sleep(1)
        }
    }

    public static void main(String[] args) {
        new LWM2MDevice().start()
    }

}