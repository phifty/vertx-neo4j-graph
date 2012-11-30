package me.phifty.graph.neo4j;

import me.phifty.graph.Complex;
import me.phifty.graph.Handler;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Neo4jComplex implements Complex {

  private final GraphDatabaseService graphDatabaseService;
  private final Finder finder;

  public Neo4jComplex(GraphDatabaseService graphDatabaseService, Finder finder) {
    this.graphDatabaseService = graphDatabaseService;
    this.finder = finder;
  }

  @Override
  public void resetNodeRelationships(Object nodeId, String name, Iterable<Object> targetIds, Handler<Iterable<Object>> handler) {
    List<Object> notFoundNodeIds = new ArrayList<>();
    Transaction transaction = graphDatabaseService.beginTx();
    try {
      Node node = finder.getNode(nodeId);

      isolateNode(node);
      for (Object targetId : targetIds) {
        Node targetNode = finder.getNode(targetId);
        if (targetNode == null) {
          notFoundNodeIds.add(targetId);
        } else {
          node.createRelationshipTo(targetNode, DynamicRelationshipType.forName(name));
        }
      }

      transaction.success();
      handler.handle(notFoundNodeIds);
    } catch (Exception exception) {
      transaction.failure();
      handler.exception(exception);
    } finally {
      transaction.finish();
    }
  }

  private void isolateNode(Node node) {
    for (Relationship relationship : node.getRelationships()) {
      relationship.delete();
    }
  }

}
