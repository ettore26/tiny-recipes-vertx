# Tiny Recipes Vert.x

A collection of tiny single-file recipes in Vert.x.
It uses JBang to manage dependencies and easily run the single files.

## Examples

### Core
- **Abstract Verticle** - Simple HTTP server using AbstractVerticle

### Web Route
- **Web Routes** - HTTP server with routing using VerticleBase

### Event Bus
- **Sender** - Sends messages via event bus with clustering
- **Receiver** - Receives messages via event bus with clustering

### MQTT
- **MQTT Server** - Simple MQTT broker implementation

## Running

Each example can be run directly with JBang:

```bash
jbang 1-core/MainAbstractVerticle.java
jbang 2-web-route/MainVerticleBase.java
jbang 4-mqtt/MainMqttServerVerticle.java
```

For event bus examples, run sender and receiver in separate terminals:
```bash
jbang 3-event-bus/cluster/SenderVerticle.java
jbang 3-event-bus/cluster/ReceiverVerticle.java
```

## Requirements

- Java 11+
- JBang

HTTP servers run on port 8888, MQTT server on default port 1883.
