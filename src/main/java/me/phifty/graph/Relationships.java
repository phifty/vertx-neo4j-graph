package me.phifty.graph;

import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public interface Relationships {

  public void create(Identifier fromId, Identifier toId, String name, Map<String, Object> properties, Handler<Identifier> handler);

  public void update(Identifier id, Map<String, Object> properties, Handler<Boolean> handler);

  public void fetch(Identifier id, Handler<Map<String, Object>> handler);

  public void remove(Identifier id, Handler<Boolean> handler);

  public void clear(Handler<Boolean> handler);

}
