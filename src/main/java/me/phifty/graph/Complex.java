package me.phifty.graph;

import java.util.Map;
import java.util.Set;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public interface Complex {

  public void fetchAllRelatedNodes(Object nodeId, String name, String direction, Handler<Iterable<Map<String, Object>>> handler);

  public void resetNodeRelationships(Object nodeId, String name, Set<Object> targetIds, Handler<ComplexResetNodeRelationshipsResult> handler);

}
