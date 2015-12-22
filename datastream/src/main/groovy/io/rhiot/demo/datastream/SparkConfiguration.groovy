package io.rhiot.demo.datastream

import io.rhiot.bootstrap.Bootstrap
import io.rhiot.bootstrap.BootstrapAware
import io.rhiot.bootstrap.classpath.Bean
import io.rhiot.bootstrap.classpath.Named
import org.apache.camel.component.spark.SparkMongos
import org.apache.camel.component.spark.Sparks
import org.apache.camel.component.spark.annotations.AnnotatedRddCallback
import org.apache.camel.component.spark.annotations.RddCallback
import org.apache.spark.api.java.AbstractJavaRDDLike
import org.apache.spark.api.java.JavaPairRDD
import org.apache.spark.api.java.JavaRDDLike
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.api.java.function.Function
import org.bson.BSONObject
import scala.Tuple2

import static org.apache.camel.component.spark.annotations.AnnotatedRddCallback.annotatedRddCallback

class SparkConfiguration {

    @Bean
    @Named(name = 'gpsCoordinatesRdd')
    JavaRDDLike gpsCoordinatesRdd() {
        def mongoPort = System.getProperty('mongo_port')
        def mongoHost = System.getProperty('mongo_host')
        SparkMongos.mongoRdd(Bootstrap.bootstrap.beanRegistry().bean(JavaSparkContext.class).get(), mongoHost == null ? 'mongodb' : mongoHost, mongoPort == null ? 27017 : mongoPort.toInteger(), 'cloudlet_document', 'GpsCoordinates')
    }

    @Bean
    @Named(name = 'clientCoordinatesCount')
    org.apache.camel.component.spark.RddCallback clientCoordinatesCount() {
        annotatedRddCallback(new ClientCoordinatesCount())
    }

    static class ClientCoordinatesCount {
        @RddCallback
        int clientCoordinatesCount(JavaPairRDD<Object, BSONObject> rdd, String client) {
            rdd.filter(new ClientFilter(client)).count()
        }
    }

    static class ClientFilter implements Function<Tuple2<Object, BSONObject>, Boolean> {

        String client

        ClientFilter(String client) {
            this.client = client
        }

        @Override
        Boolean call(Tuple2<Object, BSONObject> v1) throws Exception {
            return v1._2().get('client').equals(client)
        }
    }

}
