package me.phifty.graph.neo4j;

import me.phifty.graph.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Neo4jGraph implements Graph {

  private GraphDatabaseService graphDatabaseService;
  private Nodes nodes;
  private Relationships relationships;
  private Complex complex;

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
    complex = new Neo4jComplex(graphDatabaseService, finder);
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
  public Complex complex() {
    return complex;
  }

  @Override
  public void clear(final Handler<Boolean> handler) throws Exception {
    GlobalGraphOperations globalGraphOperations = GlobalGraphOperations.at(graphDatabaseService);
    Transaction transaction = graphDatabaseService.beginTx();
    try {
      for (Relationship relationship : globalGraphOperations.getAllRelationships()) {
        relationship.delete();
      }

      for (Node node : globalGraphOperations.getAllNodes()) {
        node.delete();
      }

      transaction.success();
      handler.handle(true);
    } catch (Exception exception) {
      transaction.failure();
      throw exception;
    } finally {
      transaction.finish();
    }
  }

  @Override
  public void shutdown() {
    graphDatabaseService.shutdown();
  }

}
