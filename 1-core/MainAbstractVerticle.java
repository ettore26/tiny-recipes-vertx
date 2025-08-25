///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.vertx:vertx-core:5.0.3

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;

public class MainAbstractVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> promise) throws Exception {
        Handler<HttpServerRequest> httpRequestHandler =
            (req) -> req.response()
                        .putHeader("Content-Type", "text/plain")
                        .end("Hello from Vert.x!");

        vertx.createHttpServer()
             .requestHandler(httpRequestHandler)
             .listen(8888)
             .onSuccess(server -> System.out.println("HTTP server started on port " + server.actualPort()))
             .onFailure(throwable -> throwable.printStackTrace());

        promise.complete();
    }

    public static void main(String[] args) {
        Vertx.vertx()
             .deployVerticle(new MainAbstractVerticle());
    }
}
