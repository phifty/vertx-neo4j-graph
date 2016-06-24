package org.openpcf.neo4vertx.examples;

import io.vertx.ext.graph.neo4j.Neo4jGraphVerticle;

import java.io.IOException;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * Demo verticle that shows how it is possible to directly utilize the neo4j api for graph manipulation.
 *
 * @author https://github.com/Jotschi[Johannes Schüth]
 */
public class AdvancedDemoVerticle extends AbstractHttpDemoVerticle {

    public static final int HTTP_PORT = 8080;

    /**
     * Start this verticle and do not terminate the main process.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        deployVerticle(AdvancedDemoVerticle.class, null);
        keepProcessAlive();
    }

    @Override
    public void start() throws Exception {

        logger.info("Starting " + getClass().getName());

        // Start the neo4vertx verticle that will provide neo4j support
        deployNeo4Vertx("neo4vertx_default.json");

        // Request handler for http requests
        Runnable requestHandler = () -> {
            try {
                logger.info("Handling request.");
                GraphDatabaseService db = Neo4jGraphVerticle.getService().getGraphDatabaseService();
                try (Transaction tx = db.beginTx()) {
                    Node node = db.createNode(DynamicLabel.label("Dummy Node"));
                    node.setProperty("message", "Hello, ");
                    logger.info("Storing node");
                    tx.success();
                }
            } catch (Exception e) {
                throw new RuntimeException("Could not insert node.");
            }
        };

        // Start the http server and register the reply handler.
        // In this case no reply handler is specified for the store event.
        // This will effectively disable the store action.
        startHttpServer(HTTP_PORT, null, countReply -> {
            if (countReply.failed()) {
                logger.error("Counting failed", countReply.cause());
            } else {
                logger.info("Counted nodes");
                logger.info("Reply count:" + countReply.result().body());
            }
        }, requestHandler);

    }
}