package me.phifty.graph.neo4j;

import me.phifty.graph.Handler;
import me.phifty.graph.Identifier;
import me.phifty.graph.Relationships;
import org.neo4j.graphdb.*;
import org.neo4j.tooling.GlobalGraphOperations;

import java.util.HashMap;
import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Neo4jRelationships implements Relationships {

  private GraphDatabaseService graphDatabaseService;

  private Map<String, RelationshipType> relationshipTypeCache = new HashMap<>();

  public Neo4jRelationships(GraphDatabaseService graphDatabaseService) {
    this.graphDatabaseService = graphDatabaseService;
  }

  @Override
  public void create(Identifier fromId, Identifier toId, String name, Map<String, Object> properties, Handler<Identifier> handler) {
    Transaction transaction = graphDatabaseService.beginTx();
    try {
      Node fromNode = getNode(fromId);
      Node toNode = getNode(toId);
      RelationshipType relationshipType = getRelationshipTypeFor(name);

      Relationship relationship = fromNode.createRelationshipTo(toNode, relationshipType);
      PropertyHandler.setProperties(relationship, properties);
      transaction.success();

      handler.handle(new Identifier(relationship.getId()));
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
      Relationship relationship = getRelationship(id);

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
  public void fetch(Identifier id, Handler<Map<String, Object>> handler) {
    try {
      Relationship relationship = getRelationship(id);
      handler.handle(PropertyHandler.getProperties(relationship));
    } catch (NotFoundException exception) {
      handler.handle(null);
    }
  }

  @Override
  public void remove(Identifier id, Handler<Boolean> handler) {
    Transaction transaction = graphDatabaseService.beginTx();
    try {
      Relationship relationship = getRelationship(id);
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

  private Node getNode(Identifier id) {
    return graphDatabaseService.getNodeById((Long)id.getId());
  }

  private Relationship getRelationship(Identifier id) {
    return graphDatabaseService.getRelationshipById((Long) id.getId());
  }

  private RelationshipType getRelationshipTypeFor(String name) {
    if (relationshipTypeCache.containsKey(name)) {
      return relationshipTypeCache.get(name);
    } else {
      RelationshipType relationshipType = new DynamicRelationshipType(name);
      relationshipTypeCache.put(name, relationshipType);
      return relationshipType;
    }
  }

  private class DynamicRelationshipType implements RelationshipType {

    private String name;

    DynamicRelationshipType(String name) {
      this.name = name;
    }

    @Override
    public String name() {
      return name;
    }

  }

}
