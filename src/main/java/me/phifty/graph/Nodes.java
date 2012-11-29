package me.phifty.graph;

import me.phifty.graph.Handler;

import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public interface Nodes {

  public void create(Map<String, Object> properties, Handler<Long> handler);

  public void update(long id, Map<String, Object> properties, Handler<Boolean> handler);

  public void fetch(long id, Handler<Map<String, Object>> handler);

  public void remove(long id, Handler<Boolean> handler);

  public void clear(Handler<Boolean> handler);

}
