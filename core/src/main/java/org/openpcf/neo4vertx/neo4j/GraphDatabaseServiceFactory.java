package org.openpcf.neo4vertx.neo4j;

import io.vertx.ext.graph.neo4j.Neo4VertxConfiguration;

import org.neo4j.graphdb.GraphDatabaseService;

/**
 * The GraphDatabaseServiceFactory interface.
 *
 * @author https://github.com/phifty[Philipp Brüll]
 * @author https://github.com/Jotschi[Johannes Schüth]
 */
public interface GraphDatabaseServiceFactory {

    /**
     * Create a new database service.
     *
     * @param path basepath for the neo4j data directory.
     * @return neo4j service.
     */
    public GraphDatabaseService create(String path);

    /**
     * Create and configure the service.
     *
     * @param configuration configuration that should be used for service creation.
     * @return neo4j service.
     */
    public GraphDatabaseService create(Neo4VertxConfiguration configuration);

}
