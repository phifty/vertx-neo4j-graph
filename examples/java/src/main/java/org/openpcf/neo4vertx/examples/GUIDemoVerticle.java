package org.openpcf.neo4vertx.examples;

import java.io.IOException;

/**
 * Demo verticle that will startup a embedded neo4j database. The neo4j database will also startup a jetty server with the remote remote api. Queries to the
 * neo4j database will be routed to the embedded database. You can use this verticle in conjunction with the remote demo verticle which will connect to the rest
 * api on port 7474 of this process.
 * 
 * @author https://github.com/Jotschi[Johannes Schüth]
 */
public class GUIDemoVerticle extends AbstractHttpDemoVerticle {

    public static final int HTTP_PORT = 8084;

    /**
     * Start this verticle and do not terminate the main process.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        deployVerticle(GUIDemoVerticle.class, null);
        keepProcessAlive();
    }

    @Override
    public void start() throws Exception {

        logger.info("Starting " + getClass().getName());

        // Start the neo4vertx verticle that will provide neo4j support
        deployNeo4Vertx("neo4vertx_gui.json");
        startHttpServer(HTTP_PORT, insertReply -> {
            if (insertReply.failed()) {
                logger.error("Storing failed", insertReply.cause());
            } else {
                logger.info("Inserted node");
                logger.info("Reply store: " + insertReply.result().body());
            }
        }, countReply -> {
            if (countReply.failed()) {
                logger.error("Counting failed", countReply.cause());
            } else {
                logger.info("Counted nodes");
                logger.info("Reply count:" + countReply.result().body());
            }
        });
    }

}