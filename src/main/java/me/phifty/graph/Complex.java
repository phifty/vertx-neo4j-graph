package me.phifty.graph;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public interface Complex {

  public void resetNodeRelationships(Object nodeId, String name, Iterable<Object> targetIds, Handler<Iterable<Object>> handler);

}
