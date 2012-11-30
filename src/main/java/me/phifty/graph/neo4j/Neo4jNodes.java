package me.phifty.graph.neo4j;

import me.phifty.graph.Handler;
import me.phifty.graph.Nodes;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Neo4jNodes implements Nodes {

  private GraphDatabaseService graphDatabaseService;
  private Finder finder;

  public Neo4jNodes(GraphDatabaseService graphDatabaseService, Finder finder) {
    this.graphDatabaseService = graphDatabaseService;
    this.finder = finder;
  }

  @Override
  public void create(Map<String, Object> properties, Handler<Object> handler) {
    Transaction transaction = graphDatabaseService.beginTx();
    try {
      Node node = graphDatabaseService.createNode();
      PropertyHandler.setProperties(node, properties);
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
  public void update(Object id, Map<String, Object> properties, Handler<Boolean> handler) {
    Transaction transaction = graphDatabaseService.beginTx();
    try {
      Node node = finder.getNode(id);
      PropertyHandler.setProperties(node, properties);
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
  public void fetch(Object id, Handler<Map<String, Object>> handler) {
    try {
      Node node = finder.getNode(id);
      if (node == null) {
        handler.handle(null);
      } else {
        handler.handle(PropertyHandler.getProperties(node));
      }
    } catch (Exception exception) {
      handler.exception(exception);
    }
  }

  @Override
  public void remove(Object id, Handler<Boolean> handler) {
    Transaction transaction = graphDatabaseService.beginTx();
    try {
      Node node = finder.getNode(id);
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

}
