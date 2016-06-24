package org.openpcf.neo4vertx.examples;

import java.io.IOException;

/**
 * Demo verticle that will startup a neo4j in cluster master mode. The additional http server on port 8082 can be used to count data within the neo4j. This
 * verticle should be used in conjunction with the {@see ClusterDemoSlaveVerticle}.
 *
 * @author https://github.com/Jotschi[Johannes Schüth]
 */
public class ClusterDemoMasterVerticle extends AbstractHttpDemoVerticle {

    public static final int HTTP_PORT = 8082;

    /**
     * Start this verticle and do not terminate the main process.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        deployVerticle(ClusterDemoMasterVerticle.class, null);
        keepProcessAlive();
    }

    @Override
    public void start() throws Exception {

        logger.info("Starting " + getClass().getName());

        // Start the neo4vertx verticle that will provide neo4j support
        deployNeo4Vertx("neo4vertx_cluster_master.json");
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

    @Override
    public void stop() {
        logger.info("Stopped " + getClass().getName());
    }

}