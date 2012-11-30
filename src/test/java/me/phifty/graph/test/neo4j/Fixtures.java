package me.phifty.graph.test.neo4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Fixtures {

  public static String NODE_ID_FIELD = "id";
  public static String RELATIONSHIP_ID_FIELD = "id";

  public static Map<String, Object> testNode() {
    Map<String, Object> result = new HashMap<>();
    if (NODE_ID_FIELD != null) {
      result.put(NODE_ID_FIELD, UUID.randomUUID().toString());
    }
    result.put("content", "test node");
    return result;
  }

  public static Map<String, Object> updatedTestNode() {
    Map<String, Object> result = new HashMap<>();
    if (NODE_ID_FIELD != null) {
      result.put(NODE_ID_FIELD, UUID.randomUUID().toString());
    }
    result.put("content", "updated test node");
    return result;
  }

  public static Map<String, Object> testRelationship() {
    Map<String, Object> result = new HashMap<>();
    if (RELATIONSHIP_ID_FIELD != null) {
      result.put(RELATIONSHIP_ID_FIELD, UUID.randomUUID().toString());
    }
    result.put("content", "test relationship");
    return result;
  }

  public static Map<String, Object> updatedTestRelationship() {
    Map<String, Object> result = new HashMap<>();
    if (RELATIONSHIP_ID_FIELD != null) {
      result.put(RELATIONSHIP_ID_FIELD, UUID.randomUUID().toString());
    }
    result.put("content", "updated test relationship");
    return result;
  }

}
