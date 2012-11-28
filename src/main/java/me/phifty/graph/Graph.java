package me.phifty.graph;

import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public interface Graph {

  public void createNode(Map<String, Object> properties, Handler<Long> handler);

  public void updateNode(long id, Map<String, Object> properties, Handler<Boolean> handler);

  public void fetchNode(long id, Handler<Map<String, Object>> handler);

  public void removeNode(long id, Handler<Boolean> handler);

  public void clear(Handler<Boolean> handler);

  public void shutdown();

}
