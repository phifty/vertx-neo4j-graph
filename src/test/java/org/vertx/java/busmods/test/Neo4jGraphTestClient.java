package org.vertx.java.busmods.test;

import org.vertx.java.busmods.Neo4jGraphModule;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.framework.TestClientBase;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Neo4jGraphTestClient extends TestClientBase {

  private long testNodeId;

  @Override
  public void start() {
    super.start();

    JsonObject configuration = new JsonObject();
    configuration.putString("base_address", "test.neo4j-graph");
    configuration.putString("path", "/tmp/graph");

    container.deployVerticle(Neo4jGraphModule.class.getName(), configuration, 1, new Handler<String>() {
      @Override
      public void handle(String deploymentId) {
        tu.appReady();
      }
    });
  }

  public void testCreateNode() {
    vertx.eventBus().send("test.neo4j-graph.nodes.store", generateCreateNodeMessage("test node"), new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        checkForException(message);
        fetchTestNode(new Handler<String>() {
          @Override
          public void handle(String content) {
            try {
              tu.azzert("test node".equals(content), "should store the right node");
            } finally {
              clearAll(new Handler<Boolean>() {
                @Override
                public void handle(Boolean done) {
                  tu.testComplete();
                }
              });
            }
          }
        });
      }
    });
  }

  public void testUpdateNode() {
    addTestNode(new Handler<Long>() {
      @Override
      public void handle(Long id) {
        vertx.eventBus().send("test.neo4j-graph.nodes.store", generateUpdateNodeMessage(id, "updated test node"), new Handler<Message<JsonObject>>() {
          @Override
          public void handle(Message<JsonObject> message) {
            checkForException(message);
            fetchTestNode(new Handler<String>() {
              @Override
              public void handle(String content) {
                try {
                  tu.azzert("updated test node".equals(content), "should store the right node");
                } finally {
                  clearAll(new Handler<Boolean>() {
                    @Override
                    public void handle(Boolean done) {
                      tu.testComplete();
                    }
                  });
                }
              }
            });
          }
        });
      }
    });
  }

  public void testFetchNode() {
    addTestNode(new Handler<Long>() {
      @Override
      public void handle(Long id) {
        vertx.eventBus().send("test.neo4j-graph.nodes.fetch", generateFetchNodeMessage(id), new Handler<Message<JsonObject>>() {
          @Override
          public void handle(Message<JsonObject> message) {
            try {
              checkForException(message);
              tu.azzert("test node".equals(message.body.getString("content")), "should respond the right content");
            } finally {
              clearAll(new Handler<Boolean>() {
                @Override
                public void handle(Boolean done) {
                  tu.testComplete();
                }
              });
            }
          }
        });
      }
    });
  }

  public void testRemoveNode() {
    addTestNode(new Handler<Long>() {
      @Override
      public void handle(Long id) {
        vertx.eventBus().send("test.neo4j-graph.nodes.remove", generateRemoveNodeMessage(id), new Handler<Message<JsonObject>>() {
          @Override
          public void handle(Message<JsonObject> message) {
            fetchTestNode(new Handler<String>() {
              @Override
              public void handle(String content) {
                try {
                  tu.azzert(content == null, "should remove the content");
                } finally {
                  tu.testComplete();
                }
              }
            });
          }
        });
      }
    });
  }

  public void testClear() {
    addTestNode(new Handler<Long>() {
      @Override
      public void handle(Long id) {
        vertx.eventBus().send("test.neo4j-graph.clear", null, new Handler<Message<JsonObject>>() {
          @Override
          public void handle(Message<JsonObject> message) {
            checkForException(message);
            fetchTestNode(new Handler<String>() {
              @Override
              public void handle(String content) {
                try {
                  tu.azzert(content == null, "should clear all data");
                } finally {
                  tu.testComplete();
                }
              }
            });
          }
        });
      }
    });
  }

  private void checkForException(Message<JsonObject> message) {
    if (message.body.getString("exception") != null) {
      tu.exception(new Exception(message.body.getString("exception")), "received exception message");
    }
  }

  private void addTestNode(final Handler<Long> handler) {
    vertx.eventBus().send("test.neo4j-graph.nodes.store", generateCreateNodeMessage("test node"), new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        testNodeId = message.body.getLong("id");
        handler.handle(testNodeId);
      }
    });
  }

  private void fetchTestNode(final Handler<String> handler) {
    vertx.eventBus().send("test.neo4j-graph.nodes.fetch", generateFetchNodeMessage(testNodeId), new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        handler.handle(message.body == null ? null : message.body.getString("content"));
      }
    });
  }

  private void clearAll(Handler<Boolean> handler) {
    sendAndReceiveDoneMessage("test.neo4j-graph.clear", null, handler);
  }

  private void sendAndReceiveDoneMessage(String address, JsonObject message, final Handler<Boolean> handler) {
    vertx.eventBus().send(address, message, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        handler.handle(message.body.getBoolean("done"));
      }
    });
  }

  private JsonObject generateCreateNodeMessage(String content) {
    JsonObject message = new JsonObject();
    JsonObject properties = new JsonObject();
    properties.putString("content", content);
    message.putObject("properties", properties);
    return message;
  }

  private JsonObject generateUpdateNodeMessage(long id, String content) {
    JsonObject message = new JsonObject();
    JsonObject properties = new JsonObject();
    properties.putString("content", content);
    message.putNumber("id", id);
    message.putObject("properties", properties);
    return message;
  }

  private JsonObject generateFetchNodeMessage(long id) {
    JsonObject message = new JsonObject();
    message.putNumber("id", id);
    return message;
  }

  private JsonObject generateRemoveNodeMessage(long id) {
    JsonObject message = new JsonObject();
    message.putNumber("id", id);
    return message;
  }

}
