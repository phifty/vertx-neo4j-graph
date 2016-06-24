package org.openpcf.neo4vertx.examples;

import java.io.IOException;

/**
 * Demo verticle that will startup a neo4j instance in cluster slave mode. The verticle will also run a http server on port 8081 which will store data for each
 * http request.
 *
 * @author https://github.com/Jotschi[Johannes Schüth]
 */
public class ClusterDemoSlaveVerticle extends AbstractHttpDemoVerticle {

    public static final int HTTP_PORT = 8081;

    /**
     * Start this verticle and do not terminate the main process.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        deployVerticle(ClusterDemoSlaveVerticle.class, null);
        keepProcessAlive();
    }

    @Override
    public void start() throws Exception {

        logger.info("Starting " + getClass().getName());

        // Start the neo4vertx verticle that will provide neo4j support
        deployNeo4Vertx("neo4vertx_cluster_slave.json");
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