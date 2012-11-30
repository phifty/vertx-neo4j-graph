package me.phifty.graph.neo4j;

import me.phifty.graph.Graph;
import me.phifty.graph.Handler;
import me.phifty.graph.Nodes;
import me.phifty.graph.Relationships;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Neo4jGraph implements Graph {

  private GraphDatabaseService graphDatabaseService;
  private Nodes nodes;
  private Relationships relationships;

  public Neo4jGraph(String path) {
    this(path, null, null, new Neo4jGraphDatabaseServiceFactory());
  }

  public Neo4jGraph(String path, String alternateNodeIdField) {
    this(path, alternateNodeIdField, null, new Neo4jGraphDatabaseServiceFactory());
  }

  public Neo4jGraph(String path, String alternateNodeIdField, String alternateRelationshipIdField) {
    this(path, alternateNodeIdField, alternateRelationshipIdField, new Neo4jGraphDatabaseServiceFactory());
  }

  public Neo4jGraph(
    String path,
    String alternateNodeIdField,
    String alternateRelationshipIdField,
    GraphDatabaseServiceFactory graphDatabaseServiceFactory) {

    graphDatabaseService = graphDatabaseServiceFactory.create(path);

    Finder finder = new Finder(graphDatabaseService, alternateNodeIdField, alternateRelationshipIdField);
    nodes = new Neo4jNodes(graphDatabaseService, finder);
    relationships = new Neo4jRelationships(graphDatabaseService, finder);
  }

  @Override
  public Nodes nodes() {
    return nodes;
  }

  @Override
  public Relationships relationships() {
    return relationships;
  }

  @Override
  public void clear(final Handler<Boolean> handler) {
    relationships().clear(new Handler<Boolean>() {
      @Override
      public void handle(final Boolean relationshipsDeleted) {
        nodes().clear(new Handler<Boolean>() {
          @Override
          public void handle(Boolean nodesDeleted) {
            handler.handle(nodesDeleted && relationshipsDeleted);
          }

          @Override
          public void exception(Exception exception) {
            handler.exception(exception);
          }
        });
      }

      @Override
      public void exception(Exception exception) {
        handler.exception(exception);
      }
    });
  }

  @Override
  public void shutdown() {
    graphDatabaseService.shutdown();
  }

}
