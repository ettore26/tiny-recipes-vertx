///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.vertx:vertx-core:5.0.3
//DEPS io.vertx:vertx-hazelcast:5.0.3

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class ReceiverVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> promise) throws Exception {
        vertx.eventBus().<String>consumer("message.address", (Handler<Message<String>>) message -> {
            final String msg = "Received message: " + message.body();
            System.out.println(msg);
            message.reply(msg);
        });
    }

    public static void main(String[] args) {
        ClusterManager clusterManager = new HazelcastClusterManager();

        Vertx.builder()
             .withClusterManager(clusterManager)
             .buildClustered()
             .onSuccess(vertx -> vertx.deployVerticle(new ReceiverVerticle()));
    }
}
