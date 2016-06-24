package org.openpcf.neo4vertx.examples;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;

/**
 * Abstract verticle for all java demos
 * 
 * @author https://github.com/Jotschi[Johannes Schüth]
 */
abstract class AbstractHttpDemoVerticle extends AbstractDemoVerticle {

    public static final int DEFAULT_HTTP_PORT = 8080;

    /**
     * @param storeHandler
     * @param countHandler
     */
    protected void startHttpServer(
            Handler<AsyncResult<Message<JsonObject>>> storeHandler,
            Handler<AsyncResult<Message<JsonObject>>> countHandler) {
        startHttpServer(DEFAULT_HTTP_PORT, storeHandler, countHandler, null);
    }

    /**
     * @param port
     * @param storeHandler
     * @param countHandler
     */
    protected void startHttpServer(int port,
            Handler<AsyncResult<Message<JsonObject>>> storeHandler,
            Handler<AsyncResult<Message<JsonObject>>> countHandler) {
        startHttpServer(port, storeHandler, countHandler, null);
    }

    /**
     * Start a simple http server that will store a node into the graph and
     * count the nodes in the graph. Handlers can be used to handle the replies.
     * 
     * @param port
     *            Http port of the http server
     * @param storeHandler
     * @param countHandler
     * @param requestHandler
     */
    // START SNIPPET: startHttpServer
    protected void startHttpServer(int port,
            Handler<AsyncResult<Message<JsonObject>>> storeHandler,
            Handler<AsyncResult<Message<JsonObject>>> countHandler,
            Runnable requestHandler) {
        HttpServerOptions options = new HttpServerOptions();
        options.setPort(port);
        vertx.createHttpServer(options).requestHandler(request -> {
            if (storeHandler != null) {
                storeNode(storeHandler);
            }
            if (countHandler != null) {
                countData(countHandler);
            }
            if (requestHandler != null) {
                new Thread(requestHandler).start();
            }
            request.response().headers().set("Content-Type", "text/plain");
            request.response().end("Request handled");
        }).listen();
        logger.info("Started " + getClass().getName());

    }

    // END SNIPPET: startHttpServer

}
