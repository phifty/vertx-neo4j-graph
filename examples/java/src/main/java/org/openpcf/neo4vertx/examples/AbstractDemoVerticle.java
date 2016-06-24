package org.openpcf.neo4vertx.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.graph.neo4j.Neo4jGraphVerticle;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.commons.io.IOUtils;

/**
 * Abstract verticle for all java demos
 * 
 * @author https://github.com/Jotschi[Johannes Schüth]
 */
abstract class AbstractDemoVerticle extends AbstractVerticle {

    protected static Logger logger = LoggerFactory
            .getLogger(AbstractDemoVerticle.class);


    public static Vertx mainVertx = Vertx.vertx();

    public static final String DEFAULT_ADDRESS = "graph.cypher.query";

    /**
     * Deploy verticle from local classpath
     *
     * @param clazz
     * @throws MalformedURLException
     */
    public static void deployVerticle(Class<? extends AbstractVerticle> clazz,
            JsonObject config) throws MalformedURLException {
        mainVertx.deployVerticle(
                clazz.getCanonicalName(),
                new DeploymentOptions().setConfig(config),
                handler -> {
                    if (handler.succeeded()) {
                        logger.info("Verticle {" + clazz + "} deployed");
                    } else {
                        logger.info("Error: " + handler.result(),
                                handler.cause());
                    }
                });
    }

    public static void keepProcessAlive() throws IOException {
        System.in.read();
        System.exit(0);
    }

    public void undeployVerticle(String deploymentId) {
        vertx.undeploy(deploymentId);
    }

    @Override
    public void stop() {
        logger.info("Stopped demo verticle.");
    }

    @Override
    public void start() throws Exception {
        logger.info("Starting demo verticle.");
    }

    /**
     * Start the main neo4vertx verticle and use the configuration from the
     * given file.
     * 
     * @param configFileName
     * @throws IOException
     */
    public static void deployNeo4Vertx(String configFileName)
            throws IOException {
        InputStream is = AbstractDemoVerticle.class
                .getResourceAsStream(configFileName);
        JsonObject config = new JsonObject(IOUtils.toString(is));
        mainVertx.deployVerticle(Neo4jGraphVerticle.class.getCanonicalName(),
                new DeploymentOptions().setConfig(config), dh -> {
                    logger.info("Neo4Vertx deployed");
                });
    }

    // END SNIPPET: startHttpServer

    /**
     * Send event to store a node
     */
    // START SNIPPET: storeNode
    protected void storeNode(
            Handler<AsyncResult<Message<JsonObject>>> replyHandler) {
        JsonObject obj = new JsonObject();
        String query = "CREATE (testentry:ingredient"
                + System.currentTimeMillis() + " {time : '"
                + System.currentTimeMillis() + "'})";
        obj.put("query", query);
        logger.info("Sending query: " + query);
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
        logger.info("Sending query: " + query);
        EventBus eb = vertx.eventBus();

        eb.send(DEFAULT_ADDRESS, obj, countHandler);
    }
    // END SNIPPET: countData

}
