package org.vertx.java.busmods.graph.neo4j;

import me.phifty.graph.ComplexResetNodeRelationshipsResult;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;
import java.util.Set;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Messages {

  public static JsonObject done(Boolean done) {
    JsonObject message = new JsonObject();
    message.putBoolean("done", done);
    return message;
  }

  public static JsonObject id(Object id) {
    JsonObject message = new JsonObject();
    message.putNumber("id", (Long)id);
    return message;
  }

  public static JsonArray idArray(Iterable<Object> ids) {
    JsonArray result = new JsonArray();
    for (Object id : ids) {
      result.add(id);
    }
    return result;
  }

  public static JsonObject nodes(Iterable<Map<String, Object>> value) {
    return propertiesArray(value, "nodes");
  }

  public static JsonObject relationships(Iterable<Map<String, Object>> value) {
    return propertiesArray(value, "relationships");
  }

  public static JsonObject propertiesArray(Iterable<Map<String, Object>> value, String field) {
    JsonObject message = new JsonObject();
    JsonArray nodes = new JsonArray();
    for (Map<String, Object> node : value) {
      nodes.add(properties(node));
    }
    message.putArray(field, nodes);
    return message;
  }

  public static JsonObject properties(Map<String, Object> properties) {
    return new JsonObject(properties);
  }

  public static JsonObject resetNodeRelationships(ComplexResetNodeRelationshipsResult complexResetNodeRelationshipsResult) {
    JsonObject message = new JsonObject();

    message.putArray("added_node_ids", idArray(complexResetNodeRelationshipsResult.addedNodeIds));
    message.putArray("removed_node_ids", idArray(complexResetNodeRelationshipsResult.removedNodeIds));
    message.putArray("not_found_node_ids", idArray(complexResetNodeRelationshipsResult.notFoundNodeIds));

    return message;
  }

}
