package me.phifty.graph.neo4j;

import me.phifty.graph.Handler;
import me.phifty.graph.Identifier;
import me.phifty.graph.Nodes;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

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
  public void create(Map<String, Object> properties, Handler<Identifier> handler) {
    Transaction transaction = graphDatabaseService.beginTx();
    try {
      Node node = graphDatabaseService.createNode();
      PropertyHandler.setProperties(node, properties);
      transaction.success();
      handler.handle(new Identifier(node.getId()));
    } catch (Exception exception) {
      transaction.failure();
      handler.exception(exception);
    } finally {
      transaction.finish();
    }
  }

  @Override
  public void update(Identifier id, Map<String, Object> properties, Handler<Boolean> handler) {
    Transaction transaction = graphDatabaseService.beginTx();
    try {
      Node node = getNode(id);
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
  public void fetch(Identifier id, Handler<Map<String, Object>> handler) {
    try {
      Node node = getNode(id);
      handler.handle(PropertyHandler.getProperties(node));
    } catch (NotFoundException exception) {
      handler.handle(null);
    } catch (Exception exception) {
      handler.exception(exception);
    }
  }

  @Override
  public void remove(Identifier id, Handler<Boolean> handler) {
    Transaction transaction = graphDatabaseService.beginTx();
    try {
      Node node = getNode(id);
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

  private Node getNode(Identifier id) {
    return graphDatabaseService.getNodeById((Long)id.getId());
  }

}
