package me.phifty.graph.neo4j;

import me.phifty.graph.Handler;
import me.phifty.graph.Nodes;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

import java.util.HashMap;
import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Neo4jNodes implements Nodes {

  private GraphDatabaseService graphDatabaseService;

  public Neo4jNodes(GraphDatabaseService graphDatabaseService) {
    this.graphDatabaseService = graphDatabaseService;
  }

  @Override
  public void create(Map<String, Object> properties, Handler<Long> handler) {
    Transaction transaction = graphDatabaseService.beginTx();
    try {
      Node node = graphDatabaseService.createNode();
      setProperties(node, properties);
      transaction.success();
      handler.handle(node.getId());
    } catch (Exception exception) {
      transaction.failure();
      handler.exception(exception);
    } finally {
      transaction.finish();
    }
  }

  @Override
  public void update(long id, Map<String, Object> properties, Handler<Boolean> handler) {
    Transaction transaction = graphDatabaseService.beginTx();
    try {
      Node node = graphDatabaseService.getNodeById(id);
      setProperties(node, properties);
      transaction.success();
      handler.handle(true);
    } catch (Exception exception) {
      transaction.failure();
      handler.exception(exception);
    } finally {
      transaction.finish();
    }
  }

  @Override
  public void fetch(long id, Handler<Map<String, Object>> handler) {
    try {
      Node node = graphDatabaseService.getNodeById(id);
      handler.handle(getProperties(node));
    } catch (NotFoundException exception) {
      handler.handle(null);
    } catch (Exception exception) {
      handler.exception(exception);
    }
  }

  @Override
  public void remove(long id, Handler<Boolean> handler) {
    Transaction transaction = graphDatabaseService.beginTx();
    try {
      Node node = graphDatabaseService.getNodeById(id);
      node.delete();
      transaction.success();
      handler.handle(true);
    } catch (Exception exception) {
      transaction.failure();
      handler.exception(exception);
    } finally {
      transaction.finish();
    }
  }

  @Override
  public void clear(Handler<Boolean> handler) {
    Transaction transaction = graphDatabaseService.beginTx();
    try {
      GlobalGraphOperations globalGraphOperations = GlobalGraphOperations.at(graphDatabaseService);
      for (Node node : globalGraphOperations.getAllNodes()) {
        node.delete();
      }
      transaction.success();
      handler.handle(true);
    } catch (Exception exception) {
      transaction.failure();
      handler.exception(exception);
    } finally {
      transaction.finish();
    }
  }

  private void setProperties(Node node, Map<String, Object> properties) {
    for (Map.Entry<String, Object> property : properties.entrySet()) {
      node.setProperty(property.getKey(), property.getValue());
    }
  }

  private Map<String, Object> getProperties(Node node) {
    Map<String, Object> result = new HashMap<>();
    for (String key : node.getPropertyKeys()) {
      result.put(key, node.getProperty(key));
    }
    return result;
  }

}
