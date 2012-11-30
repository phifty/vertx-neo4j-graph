package me.phifty.graph.neo4j;

import org.neo4j.graphdb.RelationshipType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class DynamicRelationshipType implements RelationshipType {

  private String name;

  DynamicRelationshipType(String name) {
    this.name = name;
  }

  @Override
  public String name() {
    return name;
  }

  private static Map<String, RelationshipType> relationshipTypeCache = new HashMap<>();

  public static RelationshipType forName(String name) {
    if (relationshipTypeCache.containsKey(name)) {
      return relationshipTypeCache.get(name);
    } else {
      RelationshipType relationshipType = new DynamicRelationshipType(name);
      relationshipTypeCache.put(name, relationshipType);
      return relationshipType;
    }
  }

}
