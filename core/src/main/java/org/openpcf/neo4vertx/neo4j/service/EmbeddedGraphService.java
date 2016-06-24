package org.openpcf.neo4vertx.neo4j.service;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.graph.neo4j.Neo4VertxConfiguration;

import org.neo4j.graphdb.GraphDatabaseService;
import org.openpcf.neo4vertx.neo4j.Neo4jGraphDatabaseServiceFactory;

import scala.NotImplementedError;

public class EmbeddedGraphService extends AbstractGraphService {

    public EmbeddedGraphService(Neo4VertxConfiguration configuration) throws Exception {
        super(configuration);
    }

    protected GraphDatabaseService graphDatabaseService;

    @Override
    public GraphDatabaseService getGraphDatabaseService() {
        return graphDatabaseService;
    }

    @Override
    public void shutdown() {
        if (graphDatabaseService != null) {
            graphDatabaseService.shutdown();
        }
    }

    @Override
    public void initialize(Neo4VertxConfiguration configuration) throws Exception {
        graphDatabaseService = new Neo4jGraphDatabaseServiceFactory().create(configuration);
    }

    @Override
    public JsonObject query(JsonObject request) throws Exception {
        throw new NotImplementedError("This functionality is currently not supported in this mode. Add the neo4vertx gui extension to enable it.");
    }

}
