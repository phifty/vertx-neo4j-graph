package me.phifty.graph;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Identifier {

  private Object id;
  private String field;

  public Identifier(Object id) {
    this(id, null);
  }

  public Identifier(Object id, String field) {
    this.id = id;
    this.field = field;
  }

  public Object getId() {
    return id;
  }

  public String getField() {
    return field;
  }

  public boolean hasField() {
    return field != null;
  }

}
