package org.vertx.java.busmods.graph.neo4j;

import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public interface Configuration {

  public String getBaseAddress();

  public String getPath();

  public String getAlternateNodeIdField();

  public String getAlternateRelationshipIdField();

}
