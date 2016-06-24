package io.vertx.ext.graph.neo4j;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.lang.reflect.Constructor;

import org.openpcf.neo4vertx.neo4j.service.ClusterHAGraphService;
import org.openpcf.neo4vertx.neo4j.service.EmbeddedGraphService;
import org.openpcf.neo4vertx.neo4j.service.GraphService;
import org.openpcf.neo4vertx.neo4j.service.RemoteGraphService;

/**
 * The Neo4jGraphModule object.
 *
 * @author https://github.com/phifty[Philipp Brüll]
 * @author https://github.com/rubin55[Rubin Simons]
 * @author https://github.com/fraik[Freek Alleman]
 */
public class Neo4jGraphVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(Neo4jGraphVerticle.class);

    public final static String DEFAULT_MODE = "default";
    public final static String CLUSTER_MODE = "cluster";
    public final static String REMOTE_MODE = "remote";
    public final static String GUI_MODE = "gui";

    private Neo4VertxConfiguration configuration;
    private static GraphService service;
    private MessageConsumer<JsonObject> queryMessageConsumer;

    @Override
    public void start() throws Exception {
        if (service != null) {
            throw new Exception("Verticle has already been initiated. Only a single instance of this verticle is allowed.");
        }
        initializeConfiguration();
        initializeDatabase();
        registerQueryHandler();
    }

    @Override
    public void stop() throws Exception {
        if (service != null) {
            service.shutdown();
            service = null;
        }
        if (queryMessageConsumer != null) {
            queryMessageConsumer.unregister();
        }
    }

    public static GraphService getService() throws Exception {
        if (service != null) {
            return service;
        } else {
            throw new Exception("Database not yet initalized.");
        }
    }

    /**
     * Set the database service. This is useful when you want to inject your own database service for testing purposes.
     * 
     * @param service
     */
    public static void setService(GraphService service) {
        Neo4jGraphVerticle.service = service;
    }

    private void initializeConfiguration() {

        // No configuration was set. Use default settings
        if (config().size() == 0) {
            config().put(Neo4VertxConfiguration.MODE_KEY, "default");
            config().put(Neo4VertxConfiguration.PATH_KEY, "path");
            config().put(Neo4VertxConfiguration.BASE_ADDR_KEY, "neo4j-graph");
        }
        configuration = new Neo4VertxConfiguration(config());
    }

    private void initializeDatabase() throws Exception {
        final String mode = configuration.getMode();

        switch (mode) {
        case DEFAULT_MODE:
            service = new EmbeddedGraphService(configuration);
            break;
        case CLUSTER_MODE:
            service = new ClusterHAGraphService(configuration);
            break;
        case GUI_MODE:
            String clazzName = "org.openpcf.neo4vertx.neo4j.service.GuiGraphService";
            try {
                @SuppressWarnings("unchecked")
                Class<? extends GraphService> c = (Class<? extends GraphService>) Class.forName(clazzName);
                Constructor<? extends GraphService> constructor = c.getConstructor(Neo4VertxConfiguration.class);
                service = constructor.newInstance(configuration);
            } catch (ClassNotFoundException e) {
                logger.error("Could not initalize gui mode. Please add the neo4vertx-gui-extension to your classpath first.");
                throw e;
            }
            break;
        case REMOTE_MODE:
            service = new RemoteGraphService(configuration);
            break;
        default:
            throw new Exception("Invalid mode " + mode + " specified");
        }

    }

    private void registerQueryHandler() {
        EventBus eb = vertx.eventBus();

        JsonObject reply = new JsonObject();
        reply.put("type", "reply message");

        queryMessageConsumer = eb.<JsonObject> consumer(configuration.getBaseAddress() + ".cypher.query").handler(msg -> {
            if (msg.replyAddress() != null) {
                try {
                    msg.reply(service.query(msg.body()));
                } catch (Exception e) {
                    logger.error("Error druing query handling:" + msg.body(), e);
                    e.printStackTrace();
                    msg.fail(1, "Query failed: " + e.getMessage());
                }
            }
        });

    }
}
