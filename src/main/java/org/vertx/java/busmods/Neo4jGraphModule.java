package org.vertx.java.busmods;

import me.phifty.graph.Graph;
import me.phifty.graph.neo4j.Neo4jGraph;
import org.vertx.java.busmods.json.JsonConfiguration;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

import java.util.Map;

public class Neo4jGraphModule extends Verticle {

  private Configuration configuration;
  private Graph database;

  @Override
  public void start() throws Exception {
    initializeConfiguration();
    initializeDatabase();
    registerStoreNodeHandler();
    registerFetchNodeHandler();
    registerRemoveNodeHandler();
    registerStoreRelationshipHandler();
    registerFetchRelationshipHandler();
    registerRemoveRelationshipHandler();
    registerClearHandler();
  }

  @Override
  public void stop() throws Exception {
    database.shutdown();
    super.stop();
  }

  private void initializeConfiguration() {
    configuration = new JsonConfiguration(container.getConfig());
  }

  private void initializeDatabase() {
    database = new Neo4jGraph(configuration.getPath());
  }

  private void registerStoreNodeHandler() {
    final Graph database = this.database;
    vertx.eventBus().registerHandler(configuration.getBaseAddress() + ".nodes.store", new Handler<Message<JsonObject>>() {
      @Override
      public void handle(final Message<JsonObject> message) {
        Map<String, Object> body = message.body.toMap();
        Map<String, Object> properties = body.containsKey("properties") ? (Map<String, Object>)body.get("properties") : null;

        if (body.containsKey("id")) {
          long id = Long.parseLong(body.get("id").toString());

          try {
            database.nodes().update(id, properties, new me.phifty.graph.Handler<Boolean>() {
              @Override
              public void handle(Boolean value) {
                message.reply(doneMessage(true));
              }

              @Override
              public void exception(Exception exception) {
                message.reply(failMessage(exception));
              }
            });
          } catch (Exception exception) {
            message.reply(failMessage(exception));
          }
        } else {
          try {
            database.nodes().create(properties, new me.phifty.graph.Handler<Long>() {
              @Override
              public void handle(Long id) {
                message.reply(idMessage(id));
              }

              @Override
              public void exception(Exception exception) {
                message.reply(failMessage(exception));
              }
            });
          } catch (Exception exception) {
            message.reply(failMessage(exception));
          }
        }
      }
    });
  }

  private void registerFetchNodeHandler() {
    final Graph database = this.database;
    vertx.eventBus().registerHandler(configuration.getBaseAddress() + ".nodes.fetch", new Handler<Message<JsonObject>>() {
      @Override
      public void handle(final Message<JsonObject> message) {
        long id = message.body.getLong("id");

        try {
          database.nodes().fetch(id, new me.phifty.graph.Handler<Map<String, Object>>() {
            @Override
            public void handle(Map<String, Object> node) {
              message.reply(node == null ? null : nodeMessage(node));
            }

            @Override
            public void exception(Exception exception) {
              message.reply(failMessage(exception));
            }
          });
        } catch (Exception exception) {
          message.reply(failMessage(exception));
        }
      }
    });
  }

  private void registerRemoveNodeHandler() {
    vertx.eventBus().registerHandler(configuration.getBaseAddress() + ".nodes.remove", new Handler<Message<JsonObject>>() {
      @Override
      public void handle(final Message<JsonObject> message) {
        long id = message.body.getLong("id");

        try {
          database.nodes().remove(id, new me.phifty.graph.Handler<Boolean>() {
            @Override
            public void handle(Boolean value) {
              message.reply(doneMessage(true));
            }

            @Override
            public void exception(Exception exception) {
              message.reply(failMessage(exception));
            }
          });
        } catch (Exception exception) {
          message.reply(failMessage(exception));
        }
      }
    });
  }

  private void registerStoreRelationshipHandler() {
    final Graph database = this.database;
    vertx.eventBus().registerHandler(configuration.getBaseAddress() + ".relationships.store", new Handler<Message<JsonObject>>() {
      @Override
      public void handle(final Message<JsonObject> message) {
        Map<String, Object> body = message.body.toMap();
        Map<String, Object> properties = body.containsKey("properties") ? (Map<String, Object>)body.get("properties") : null;

        if (body.containsKey("id")) {
          long id = Long.parseLong(body.get("id").toString());

          try {
            database.relationships().update(id, properties, new me.phifty.graph.Handler<Boolean>() {
              @Override
              public void handle(Boolean value) {
                message.reply(doneMessage(true));
              }

              @Override
              public void exception(Exception exception) {
                message.reply(failMessage(exception));
              }
            });
          } catch (Exception exception) {
            message.reply(failMessage(exception));
          }
        } else {
          Long fromId = message.body.getLong("from");
          Long toId = message.body.getLong("to");
          String name = message.body.getString("name");
          try {
            database.relationships().create(fromId, toId, name, properties, new me.phifty.graph.Handler<Long>() {
              @Override
              public void handle(Long id) {
                message.reply(idMessage(id));
              }

              @Override
              public void exception(Exception exception) {
                message.reply(failMessage(exception));
              }
            });
          } catch (Exception exception) {
            message.reply(failMessage(exception));
          }
        }
      }
    });
  }

  private void registerFetchRelationshipHandler() {
    final Graph database = this.database;
    vertx.eventBus().registerHandler(configuration.getBaseAddress() + ".relationships.fetch", new Handler<Message<JsonObject>>() {
      @Override
      public void handle(final Message<JsonObject> message) {
        long id = message.body.getLong("id");

        try {
          database.relationships().fetch(id, new me.phifty.graph.Handler<Map<String, Object>>() {
            @Override
            public void handle(Map<String, Object> node) {
              message.reply(node == null ? null : nodeMessage(node));
            }

            @Override
            public void exception(Exception exception) {
              message.reply(failMessage(exception));
            }
          });
        } catch (Exception exception) {
          message.reply(failMessage(exception));
        }
      }
    });
  }

  private void registerRemoveRelationshipHandler() {
    vertx.eventBus().registerHandler(configuration.getBaseAddress() + ".relationships.remove", new Handler<Message<JsonObject>>() {
      @Override
      public void handle(final Message<JsonObject> message) {
        long id = message.body.getLong("id");

        try {
          database.relationships().remove(id, new me.phifty.graph.Handler<Boolean>() {
            @Override
            public void handle(Boolean value) {
              message.reply(doneMessage(true));
            }

            @Override
            public void exception(Exception exception) {
              message.reply(failMessage(exception));
            }
          });
        } catch (Exception exception) {
          message.reply(failMessage(exception));
        }
      }
    });
  }

  private void registerClearHandler() {
    vertx.eventBus().registerHandler(configuration.getBaseAddress() + ".clear", new Handler<Message<JsonObject>>() {
      @Override
      public void handle(final Message<JsonObject> message) {
        try {
          database.clear(new me.phifty.graph.Handler<Boolean>() {
            @Override
            public void handle(Boolean value) {
              message.reply(doneMessage(true));
            }

            @Override
            public void exception(Exception exception) {
              message.reply(failMessage(exception));
            }
          });
        } catch (Exception exception) {
          message.reply(failMessage(exception));
        }
      }
    });
  }

  private JsonObject doneMessage(Boolean done) {
    JsonObject message = new JsonObject();
    message.putBoolean("done", done);
    return message;
  }

  private JsonObject idMessage(long id) {
    JsonObject message = new JsonObject();
    message.putNumber("id", id);
    return message;
  }

  private JsonObject nodeMessage(Map<String, Object> properties) {
    return new JsonObject(properties);
  }

  private JsonObject failMessage(Exception exception) {
    JsonObject message = new JsonObject();
    message.putString("exception", exception.toString());
    return message;
  }

}
