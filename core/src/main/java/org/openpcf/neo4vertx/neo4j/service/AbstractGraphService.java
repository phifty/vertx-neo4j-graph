package org.openpcf.neo4vertx.neo4j.service;

import io.vertx.ext.graph.neo4j.Neo4VertxConfiguration;

public abstract class AbstractGraphService implements GraphService {

    public AbstractGraphService(Neo4VertxConfiguration configuration) throws Exception {
        initialize(configuration);
    }
}
