package me.phifty.graph.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Neo4jGraphDatabaseServiceFactory implements GraphDatabaseServiceFactory {

  @Override
  public GraphDatabaseService create(String path) {
    GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(path);
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

}
