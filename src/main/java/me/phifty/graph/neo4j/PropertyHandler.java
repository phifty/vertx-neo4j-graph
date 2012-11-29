package me.phifty.graph.neo4j;

import org.neo4j.graphdb.PropertyContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class PropertyHandler {

  public static void setProperties(PropertyContainer propertyContainer, Map<String, Object> properties) {
    for (Map.Entry<String, Object> property : properties.entrySet()) {
      propertyContainer.setProperty(property.getKey(), property.getValue());
    }
  }

  public static Map<String, Object> getProperties(PropertyContainer propertyContainer) {
    Map<String, Object> result = new HashMap<>();
    for (String key : propertyContainer.getPropertyKeys()) {
      result.put(key, propertyContainer.getProperty(key));
    }
    return result;
  }

}
