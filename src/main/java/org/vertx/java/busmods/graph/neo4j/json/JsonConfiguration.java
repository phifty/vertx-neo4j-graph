package org.vertx.java.busmods.graph.neo4j.json;

import org.vertx.java.busmods.graph.neo4j.Configuration;
import org.vertx.java.busmods.graph.neo4j.DefaultConfiguration;
import org.vertx.java.core.json.JsonObject;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class JsonConfiguration implements Configuration {

  private JsonObject object;
  private Configuration defaultConfiguration;

  public JsonConfiguration(JsonObject object) {
    this.object = object;
    this.defaultConfiguration = new DefaultConfiguration();
  }

  @Override
  public String getBaseAddress() {
    return object.getString("base_address", defaultConfiguration.getBaseAddress());
  }

  @Override
  public String getPath() {
    return object.getString("path", defaultConfiguration.getPath());
  }

  @Override
  public String getAlternateNodeIdField() {
    return object.getString("alternate_node_id_field", defaultConfiguration.getAlternateNodeIdField());
  }

  @Override
  public String getAlternateRelationshipIdField() {
    return object.getString("alternate_relationship_id_field", defaultConfiguration.getAlternateRelationshipIdField());
  }

}
