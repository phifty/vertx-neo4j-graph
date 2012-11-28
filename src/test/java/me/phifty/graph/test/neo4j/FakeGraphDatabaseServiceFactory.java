package me.phifty.graph.test.neo4j;

import me.phifty.graph.neo4j.GraphDatabaseServiceFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class FakeGraphDatabaseServiceFactory implements GraphDatabaseServiceFactory {

  @Override
  public GraphDatabaseService create(String path) {
    return new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase();
  }

}
