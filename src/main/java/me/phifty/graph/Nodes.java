package me.phifty.graph;

import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public interface Nodes {

  public void create(Map<String, Object> properties, Handler<Object> handler);

  public void update(Object id, Map<String, Object> properties, Handler<Boolean> handler);

  public void fetch(Object id, Handler<Map<String, Object>> handler);

  public void remove(Object id, Handler<Boolean> handler);

  public void clear(Handler<Boolean> handler);

}
