package me.phifty.graph.neo4j;

import me.phifty.graph.Complex;
import me.phifty.graph.Handler;
import org.neo4j.graphdb.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
  public void fetchAllRelatedNodes(Object nodeId, String relationshipName, String directionName, final Handler<Iterable<Map<String, Object>>> handler) {
    try {
      final Node node = finder.getNode(nodeId);
      RelationshipType relationshipType = DynamicRelationshipType.forName(relationshipName);
      Direction direction = Direction.valueOf(directionName.toUpperCase());

      final Iterable<Relationship> relationships = node.getRelationships(relationshipType, direction);
      handler.handle(new Iterable<Map<String, Object>>() {
        @Override
        public Iterator<Map<String, Object>> iterator() {
          final Iterator<Relationship> iterator = relationships.iterator();
          return new Iterator<Map<String, Object>>() {
            @Override
            public boolean hasNext() {
              return iterator.hasNext();
            }

            @Override
            public Map<String, Object> next() {
              Relationship relationship = iterator.next();
              Node targetNode = relationship.getOtherNode(node);
              return PropertyHandler.getProperties(targetNode);
            }

            @Override
            public void remove() {
              iterator.remove();
            }
          };
        }
      });
    } catch (Exception exception) {
      handler.exception(exception);
    }
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
