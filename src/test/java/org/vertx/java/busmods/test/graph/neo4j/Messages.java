package org.vertx.java.busmods.test.graph.neo4j;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Messages {

  public static JsonObject id(long id) {
    JsonObject message = new JsonObject();
    message.putNumber("id", id);
    return message;
  }

  public static JsonObject createNode(String content) {
    JsonObject message = new JsonObject();

    JsonObject properties = new JsonObject();
    properties.putString("content", content);

    message.putObject("properties", properties);

    return message;
  }

  public static JsonObject updateNode(long id, String content) {
    JsonObject message = new JsonObject();

    JsonObject properties = new JsonObject();
    properties.putString("content", content);

    message.putNumber("id", id);
    message.putObject("properties", properties);

    return message;
  }

  public static JsonObject createRelationship(long fromId, long toId, String content) {
    JsonObject message = new JsonObject();

    JsonObject properties = new JsonObject();
    properties.putString("content", content);

    message.putNumber("from", fromId);
    message.putNumber("to", toId);
    message.putString("name", "connected");
    message.putObject("properties", properties);

    return message;
  }

  public static JsonObject updateRelationship(long id, String content) {
    JsonObject message = new JsonObject();

    JsonObject properties = new JsonObject();
    properties.putString("content", content);

    message.putNumber("id", id);
    message.putObject("properties", properties);

    return message;
  }

  public static JsonObject fetchIncomingRelationshipsOfNode(long id) {
    JsonObject message = new JsonObject();

    message.putNumber("node_id", id);
    message.putString("name", "connected");
    message.putString("direction", "incoming");

    return message;
  }

  public static JsonObject fetchRelatedNodes(long id) {
    return fetchRelatedNodes(id, "outgoing");
  }

  public static JsonObject fetchRelatedNodes(long id, String direction) {
    JsonObject message = new JsonObject();

    message.putNumber("node_id", id);
    message.putString("name", "connected");
    message.putString("direction", direction);

    return message;
  }

  public static JsonObject resettingNodeRelationships(long id, long[] targetIds) {
    JsonObject message = new JsonObject();

    JsonArray ids = new JsonArray();
    for (long targetId : targetIds) {
      ids.add(targetId);
    }

    message.putNumber("node_id", id);
    message.putString("name", "connected");
    message.putArray("target_ids", ids);

    return message;
  }

}
