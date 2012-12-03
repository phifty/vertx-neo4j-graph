package org.vertx.java.busmods.graph.neo4j;

import java.io.File;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class DefaultConfiguration implements Configuration {

  @Override
  public String getBaseAddress() {
    return "neo4j-graph";
  }

  @Override
  public String getPath() {
    return System.getProperty("user.home") + File.pathSeparator + "graph";
  }

  @Override
  public String getAlternateNodeIdField() {
    return null;
  }

  @Override
  public String getAlternateRelationshipIdField() {
    return null;
  }

}
