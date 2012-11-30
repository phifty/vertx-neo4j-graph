package org.vertx.java.busmods;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Messages {

  public static JsonObject done(Boolean done) {
    JsonObject message = new JsonObject();
    message.putBoolean("done", done);
    return message;
  }

  public static JsonObject fail(Exception exception) {
    JsonObject message = new JsonObject();
    message.putString("exception", exception.toString());
    return message;
  }

  public static JsonObject id(Object id) {
    JsonObject message = new JsonObject();
    message.putNumber("id", (Long)id);
    return message;
  }

  public static JsonObject ids(Iterable<Object> value, String field) {
    JsonObject message = new JsonObject();
    JsonArray ids = new JsonArray();
    for (Object id : value) {
      ids.add(id);
    }
    message.putArray(field, ids);
    return message;
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

}
