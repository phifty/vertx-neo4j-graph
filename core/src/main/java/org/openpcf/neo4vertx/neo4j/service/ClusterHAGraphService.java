package org.openpcf.neo4vertx.neo4j.service;

import io.vertx.ext.graph.neo4j.Neo4VertxConfiguration;

import org.openpcf.neo4vertx.neo4j.Neo4jGraphDatabaseHAServiceFactory;

public class ClusterHAGraphService extends EmbeddedGraphService {

    public ClusterHAGraphService(Neo4VertxConfiguration configuration) throws Exception {
        super(configuration);
    }

    @Override
    public void initialize(Neo4VertxConfiguration configuration) throws Exception {
        graphDatabaseService = new Neo4jGraphDatabaseHAServiceFactory().create(configuration);
    }
}
