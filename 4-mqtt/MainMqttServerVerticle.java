///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.vertx:vertx-core:5.0.3
//DEPS io.vertx:vertx-mqtt:5.0.3
//DEPS org.slf4j:slf4j-api:2.0.7
//DEPS org.slf4j:slf4j-simple:2.0.7

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.messages.MqttPublishMessage;
import io.vertx.mqtt.messages.MqttSubscribeMessage;

public class MainMqttServerVerticle extends VerticleBase {

    private final Map<String, Set<MqttEndpoint>> topicSubscriptions = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(MainMqttServerVerticle.class);

    @Override
    public Future<?> start() {
        return MqttServer.create(vertx)
                .endpointHandler(this::mqttEnpointHandler)
                .listen()
                .onSuccess(server -> log.info("MQTT server started on port [{}]", server.actualPort()))
                .onFailure(throwable -> throwable.printStackTrace());
    }

    private void mqttEnpointHandler(MqttEndpoint endpoint) {
        endpoint.subscribeHandler(subscribe -> mqttSubscribeHandler(endpoint, subscribe));
        endpoint.publishHandler(message -> mqttPublishHandler(message));
        endpoint.disconnectHandler(v -> {
            log.info("MQTT client [{}] disconnected.", endpoint.clientIdentifier());
        });
        endpoint.accept(false);
    }

    private void mqttPublishHandler(MqttPublishMessage message) {
        log.info("Received message [{}] on topic [{}]", message.payload().toString(), message.topicName());

        Set<MqttEndpoint> subscribers = topicSubscriptions.get(message.topicName());
        if (subscribers != null) {
            for (MqttEndpoint subscriber : subscribers) {
                if (subscriber.isConnected()) {
                    subscriber.publish(message.topicName(), message.payload(), message.qosLevel(), message.isDup(),
                            message.isRetain());
                }
            }
        }
    }

    private void mqttSubscribeHandler(MqttEndpoint endpoint, MqttSubscribeMessage subscribe) {
        List<MqttQoS> grantedQosLevels = new ArrayList<>();
        for (var s : subscribe.topicSubscriptions()) {
            log.info("Subscription request for [{}] from client [{}]", s.topicName(), endpoint.clientIdentifier());

            topicSubscriptions.computeIfAbsent(s.topicName(), k -> new HashSet<>())
                    .add(endpoint);
            grantedQosLevels.add(s.qualityOfService());
        }
        endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);
    }

    public static void main(String[] args) {
        Vertx.vertx()
             .deployVerticle(new MainMqttServerVerticle());
    }
}
