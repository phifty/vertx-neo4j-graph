package me.phifty.graph.neo4j;

import me.phifty.graph.Graph;
import me.phifty.graph.Handler;
import me.phifty.graph.Nodes;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

import java.util.HashMap;
import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Neo4jGraph implements Graph {

  private GraphDatabaseService graphDatabaseService;
  private Nodes nodes;

  public Neo4jGraph(String path) {
    this(path, new Neo4jGraphDatabaseServiceFactory());
  }

  public Neo4jGraph(String path, GraphDatabaseServiceFactory graphDatabaseServiceFactory) {
    graphDatabaseService = graphDatabaseServiceFactory.create(path);

    nodes = new Neo4jNodes(graphDatabaseService);
  }

  @Override
  public Nodes nodes() {
    return nodes;
  }

  @Override
  public void clear(Handler<Boolean> handler) {
    nodes().clear(handler);
  }

  @Override
  public void shutdown() {
    graphDatabaseService.shutdown();
  }

}
