package org.openpcf.neo4vertx.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.graph.neo4j.Neo4jGraphVerticle;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.commons.io.IOUtils;

public class Runner {

    private static Logger log = LoggerFactory.getLogger(Runner.class);

    private static Vertx vertx = Vertx.vertx();

    /**
     * Start the verticles and do not terminate the main process.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // Start the neo4vertx verticle that will provide neo4j support
        deployNeo4Vertx("neo4vertx.json");
        deployVerticle(DemoVerticle.class, null);
        keepProcessAlive();
    }

    /**
     * Deploy verticle from local classpath
     *
     * @param clazz
     * @throws MalformedURLException
     */
    private static void deployVerticle(Class<? extends AbstractVerticle> clazz,
            JsonObject config) throws MalformedURLException {
        vertx.deployVerticle(
                clazz.getCanonicalName(),
                new DeploymentOptions().setConfig(config),
                handler -> {
                    if (handler.succeeded()) {
                        log.info("Verticle {" + clazz + "} deployed");
                    } else {
                        log.info("Error: " + handler.result(), handler.cause());
                    }
                });
    }

    /**
     * Start the main neo4vertx verticle and use the configuration from the
     * given file.
     * 
     * @param configFileName
     * @throws IOException
     */
    private static void deployNeo4Vertx(String configFileName)
            throws IOException {
        InputStream is = Runner.class.getResourceAsStream(configFileName);
        JsonObject config = new JsonObject(IOUtils.toString(is));
        vertx.deployVerticle(Neo4jGraphVerticle.class.getCanonicalName(),
                new DeploymentOptions().setConfig(config), dh -> {
                    log.info("Neo4Vertx deployed");
                });
    }

    private static void keepProcessAlive() throws IOException {
        System.in.read();
        System.exit(0);
    }

}
