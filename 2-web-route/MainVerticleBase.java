///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.vertx:vertx-core:5.0.3
//DEPS io.vertx:vertx-web:5.0.3

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticleBase extends VerticleBase {

    @Override
    public Future<?> start() {
        Router router = Router.router(vertx);
        router.route()
              .method(HttpMethod.GET)
              .path("/api/1")
              .handler(this::requestHandler);
        router.route()
              .method(HttpMethod.PUT)
              .path("/api/1")
              .handler(this::requestHandler);

        router.get("/api/2").handler(this::requestHandler);
        router.put("/api/2").handler(this::requestHandler);

        return vertx.createHttpServer()
                    .requestHandler(router)
                    .listen(8888)
                    .onSuccess(server -> System.out.println("HTTP server started on port " + server.actualPort()))
                    .onFailure(throwable -> throwable.printStackTrace());
    }

    private void requestHandler(RoutingContext context) {
        String address = context.request().connection().remoteAddress().toString();
        MultiMap queryParams = context.queryParams();
        String name = queryParams.contains("name") ? queryParams.get("name") : "unknown";

        JsonObject jsonObject = new JsonObject()
                                        .put("name", name)
                                        .put("address", address)
                                        .put("message", "Hello " + name + " connected from " + address);
        context.json(jsonObject);
    }

    public static void main(String[] args) {
        Vertx.vertx()
             .deployVerticle(new MainVerticleBase());
    }
}
