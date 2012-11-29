package me.phifty.graph;

import me.phifty.graph.Handler;

import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public interface Nodes {

  public void create(Map<String, Object> properties, Handler<Identifier> handler);

  public void update(Identifier id, Map<String, Object> properties, Handler<Boolean> handler);

  public void fetch(Identifier id, Handler<Map<String, Object>> handler);

  public void remove(Identifier id, Handler<Boolean> handler);

  public void clear(Handler<Boolean> handler);

}
