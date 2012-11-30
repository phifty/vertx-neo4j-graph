package me.phifty.graph.neo4j;

import me.phifty.graph.Handler;
import me.phifty.graph.Relationships;
import org.neo4j.graphdb.*;
import org.neo4j.tooling.GlobalGraphOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Neo4jRelationships implements Relationships {

  private GraphDatabaseService graphDatabaseService;

  private Finder finder;

  public Neo4jRelationships(GraphDatabaseService graphDatabaseService, Finder finder) {
    this.graphDatabaseService = graphDatabaseService;
    this.finder = finder;
  }

  @Override
  public void create(Object fromId, Object toId, String name, Map<String, Object> properties, Handler<Object> handler) {
    Transaction transaction = graphDatabaseService.beginTx();
    try {
      Node fromNode = finder.getNode(fromId);
      Node toNode = finder.getNode(toId);
      RelationshipType relationshipType = DynamicRelationshipType.forName(name);

      Relationship relationship = fromNode.createRelationshipTo(toNode, relationshipType);
      PropertyHandler.setProperties(relationship, properties);
      transaction.success();

      handler.handle(relationship.getId());
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
      Relationship relationship = finder.getRelationship(id);

      PropertyHandler.setProperties(relationship, properties);
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
      Relationship relationship = finder.getRelationship(id);
      if (relationship == null) {
        handler.handle(null);
      } else {
        handler.handle(PropertyHandler.getProperties(relationship));
      }
    } catch (Exception exception) {
      handler.exception(exception);
    }
  }

  @Override
  public void fetchAllOfNode(Object nodeId, Handler<Iterable<Map<String, Object>>> handler) {
    Node node = finder.getNode(nodeId);
    if (node == null) {
      handler.handle(new ArrayList<Map<String, Object>>());
    } else {
      final Iterable<Relationship> relationships = node.getRelationships();
      handler.handle(new Iterable<Map<String, Object>>() {
        @Override
        public Iterator<Map<String, Object>> iterator() {
          final Iterator<Relationship> relationshipsIterator = relationships.iterator();
          return new Iterator<Map<String, Object>>() {
            @Override
            public boolean hasNext() {
              return relationshipsIterator.hasNext();
            }

            @Override
            public Map<String, Object> next() {
              return PropertyHandler.getProperties(relationshipsIterator.next());
            }

            @Override
            public void remove() {
              relationshipsIterator.remove();
            }
          };
        }
      });
    }
  }

  @Override
  public void remove(Object id, Handler<Boolean> handler) {
    Transaction transaction = graphDatabaseService.beginTx();
    try {
      Relationship relationship = finder.getRelationship(id);
      relationship.delete();
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
      for (Relationship relationship : globalGraphOperations.getAllRelationships()) {
        relationship.delete();
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
