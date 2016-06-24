package org.openpcf.neo4vertx.neo4j.service;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.graph.neo4j.Neo4VertxConfiguration;

import org.neo4j.graphdb.GraphDatabaseService;

/**
 * The Graph interface.
 *
 * @author https://github.com/phifty[Philipp Brüll]
 * @author https://github.com/rubin55[Rubin Simons]
 */
public interface GraphService {

    public void initialize(Neo4VertxConfiguration configuration) throws Exception;

    public GraphDatabaseService getGraphDatabaseService();

    public JsonObject query(JsonObject request) throws Exception;

    public void shutdown();
}
