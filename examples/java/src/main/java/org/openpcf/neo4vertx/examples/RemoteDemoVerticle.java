package org.openpcf.neo4vertx.examples;

import java.io.IOException;

/**
 * Demo verticle that will behave like a rest client. No embedded neo4j server will be started. Instead the request to the verticle will be passed on to the
 * rest api url which has been specified in the configuration file for this verticle.
 * 
 * @author https://github.com/Jotschi[Johannes Schüth]
 */
public class RemoteDemoVerticle extends AbstractHttpDemoVerticle {

    public static final int HTTP_PORT = 8083;

    /**
     * Start this verticle and do not terminate the main process.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        deployVerticle(RemoteDemoVerticle.class, null);
        keepProcessAlive();
    }

    @Override
    public void start() throws Exception {

        logger.info("Starting " + getClass().getName());

        // Start the neo4vertx verticle that will provide neo4j support
        deployNeo4Vertx("neo4vertx_remote.json");
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
