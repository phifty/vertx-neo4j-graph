package org.openpcf.neo4vertx.neo4j;

import io.vertx.ext.graph.neo4j.Neo4VertxConfiguration;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;


/**
 * The Neo4jGraphDatabaseServiceFactory object.
 *
 * @author https://github.com/phifty[Philipp Brüll]
 * @author https://github.com/rubin55[Rubin Simons]
 */
public class Neo4jGraphDatabaseServiceFactory implements GraphDatabaseServiceFactory {

    @Override
    public GraphDatabaseService create(String path) {
        GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(path).newGraphDatabase();
        registerShutdownHook(graphDatabaseService);
        return graphDatabaseService;
    }

    private void registerShutdownHook(final GraphDatabaseService graphDatabaseService) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDatabaseService.shutdown();
            }
        });
    }

    @Override
    public GraphDatabaseService create(Neo4VertxConfiguration configuration) {
        return create(configuration.getPath());
    }

}
