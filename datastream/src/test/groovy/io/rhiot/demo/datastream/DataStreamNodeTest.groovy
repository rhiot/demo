/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.demo.datastream

import com.google.common.truth.Truth
import com.mongodb.BasicDBObject
import com.mongodb.Mongo
import io.rhiot.bootstrap.Bootstrap
import io.rhiot.datastream.engine.test.DataStreamTest
import io.rhiot.mongodb.EmbeddedMongo
import io.rhiot.steroids.camel.CamelBootInitializer
import io.vertx.core.json.Json
import org.junit.AfterClass
import org.junit.Test

import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.amqp

class DataStreamNodeTest extends DataStreamTest {

    static def mongo = new EmbeddedMongo().start()

    @Override
    protected void beforeDataStreamStarted() {
        System.setProperty('mongo_host', 'localhost')
        System.setProperty('mongo_port', mongo.port() + '')
    }

    @Override
    protected void afterDataStreamStarted() {
        Bootstrap.bootstrap = dataStream
    }

    @AfterClass
    static void afterClass() {
        mongo.stop()
    }

    @Test
    void smokeTestMongoSparkTask() {
        def mongoClient = new Mongo('localhost', mongo.port())
        mongoClient.getDB('cloudlet_document').getCollection('GpsCoordinates').save(new BasicDBObject([client: 'foo']))

        def encodedResult = CamelBootInitializer.camelContext().createProducerTemplate().requestBody(amqp('spark.execute.gpsCoordinatesRdd.clientCoordinatesCount'), Json.encode([payload: 'foo']), String.class)
        def result = Json.decodeValue(encodedResult, Map.class).payload
        Truth.assertThat(result).isEqualTo(1)
    }

}
