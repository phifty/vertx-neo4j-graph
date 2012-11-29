package me.phifty.graph;

import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public interface Graph {

  public Nodes nodes();

  public void clear(Handler<Boolean> handler);

  public void shutdown();

}
