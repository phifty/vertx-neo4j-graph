package org.openpcf.neo4vertx.example;

import static io.vertx.core.http.HttpMethod.GET;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

/**
 * Demo verticle which will create a new node and count the existing nodes each
 * time a request is being received on port 8080.
 *
 * @author https://github.com/Jotschi[Johannes Schüth]
 */
public class DemoVerticle extends AbstractVerticle {

    protected static Logger log = LoggerFactory.getLogger(DemoVerticle.class);
    
    private static final int HTTP_PORT = 8080;

    public static final String DEFAULT_ADDRESS = "graph.cypher.query";

    private Router apiRouter;

    // START SNIPPET: start

    @Override
    public void start() throws Exception {
        log.info("Starting demo verticle.");

        HttpServer server = vertx.createHttpServer(new HttpServerOptions()
                .setPort(HTTP_PORT));

        // Setup the routing logic in oder to handle different rest requests
        Router router = Router.router(vertx);
        apiRouter = Router.router(vertx);
        router.mountSubRouter("/api/v1/", apiRouter);

        addInsertHandler();
        addFlowerHandler();
        addCountHandler();
        addClearHandler();

        server.requestHandler(router::accept);
        server.listen();

    }

    // END SNIPPET: start

    private void addClearHandler() {
        String query = "MATCH (n)-[r]-() DELETE n, r";
        Route route = apiRouter.route("/demo/clear");
        route.method(GET).handler(rc -> {
            JsonObject obj = new JsonObject();
            obj.put("query", query);
            log.info("Sending query: " + obj.getString("query"));
            vertx.eventBus().send(DEFAULT_ADDRESS, obj, ch -> {
                rc.response().end(ch.result().body().toString());
            });
        });
    }

    private void addInsertHandler() {
        long now = System.currentTimeMillis();
        String query = "CREATE (testentry:ingredient" + now + " {time : '"
                + now + "'})";

        Route route = apiRouter.route("/demo/insert");
        route.method(GET).handler(rc -> {
            JsonObject obj = new JsonObject();
            obj.put("query", query);
            log.info("Sending query: " + obj.getString("query"));
            vertx.eventBus().send(DEFAULT_ADDRESS, obj, rh -> {
                rc.response().end(rh.result().body().toString());
            });
        });
    }

    private void addCountHandler() {
        Route route = apiRouter.route("/demo/count");
        route.method(GET).handler(rc -> {
            JsonObject obj = new JsonObject();
            obj.put("query", "MATCH (n) RETURN count(*);");
            log.info("Sending query: " + obj.getString("query"));
            vertx.eventBus().send(DEFAULT_ADDRESS, obj, ch -> {
                rc.response().end(ch.result().body().toString());
            });
        });
    }

    /**
     * Add a route for /demo/addFlower. A request to
     * http://localhost:8080/api/v1/demo/addFlower will create a graph in the
     * form of a flower in the neo4j database.
     */
    private void addFlowerHandler() {
        // https://twitter.com/mesirii/status/558222864495550465
        String query = "CREATE (r) FOREACH(x in range(1,9)|CREATE (r)-[:` `]->()-[:` `]->()) WITH r MATCH (n) CREATE()<-[:` `]-(n)-[:` `]->()";
        Route route = apiRouter.route("/demo/addFlower");
        route.method(GET).handler(rc -> {
            JsonObject obj = new JsonObject();
            obj.put("query", query);
            log.info("Sending query: " + obj.getString("query"));
            vertx.eventBus().send(DEFAULT_ADDRESS, obj, ch -> {
                rc.response().end("<a href=\"http://localhost:7474/browser\">Open the neo4j browser and list all nodes</a>");
            });
        });

    }

    @Override
    public void stop() {
        log.info("Stopped demo verticle.");
    }

    /**
     * Send event to store a node
     */
    // START SNIPPET: storeNode
    protected void storeNode(
            Handler<AsyncResult<Message<JsonObject>>> replyHandler) {
        JsonObject obj = new JsonObject();
        String query = "CREATE (testentry:dummyNode"
                + System.currentTimeMillis() + " {time : '"
                + System.currentTimeMillis() + "'})";
        obj.put("query", query);
        log.info("Sending query: " + query);
        vertx.eventBus().send(DEFAULT_ADDRESS, obj, replyHandler);
    }

    // END SNIPPET: storeNode

    /**
     * Send an event to the event bus which contains an query which counts the
     * nodes in the graph.
     * 
     * @param countHandler
     */
    // START SNIPPET: countData
    protected void countData(
            Handler<AsyncResult<Message<JsonObject>>> countHandler) {

        JsonObject obj = new JsonObject();
        String query = "MATCH (n) RETURN count(*);";
        obj.put("query", query);
        log.info("Sending query: " + query);
        EventBus eb = vertx.eventBus();

        eb.send(DEFAULT_ADDRESS, obj, countHandler);
    }

    // END SNIPPET: countData

}
