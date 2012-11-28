package me.phifty.graph.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public interface GraphDatabaseServiceFactory {

  public GraphDatabaseService create(String path);

}
