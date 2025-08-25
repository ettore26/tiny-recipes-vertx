///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.vertx:vertx-core:5.0.3
//DEPS io.vertx:vertx-hazelcast:5.0.3

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class SenderVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> promise) throws Exception {

        vertx.eventBus().<String>request("message.address", "Hello from SenderVerticle!")
                .onComplete(ar -> {
                    if (ar.succeeded()) {
                        System.out.println("Received reply: " + ar.result().body());
                    } else {
                        System.out.println("No reply received");
                    }
                });
    }

    public static void main(String[] args) {
        ClusterManager clusterManager = new HazelcastClusterManager();

        Vertx.builder()
             .withClusterManager(clusterManager)
             .buildClustered()
             .onSuccess(vertx -> vertx.deployVerticle(new SenderVerticle()));
    }
}
