package me.phifty.graph.test.neo4j;

import me.phifty.graph.Graph;
import me.phifty.graph.neo4j.Neo4jGraph;
import me.phifty.graph.test.FakeHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Neo4jGraphTest {

  private static String NODE_ID_FIELD = "id";
  private static String RELATIONSHIP_ID_FIELD = "id";

  private FakeHandler<Object> idHandler = new FakeHandler<>();
  private FakeHandler<Boolean> doneHandler = new FakeHandler<>();
  private FakeHandler<Map<String, Object>> nodeHandler = new FakeHandler<>();
  private FakeHandler<Map<String, Object>> relationshipHandler = new FakeHandler<>();
  private Map<String, Object> properties;
  private Graph graph;

  @Before
  public void setUp() throws Exception {
    graph = new Neo4jGraph("/tmp/test", NODE_ID_FIELD, RELATIONSHIP_ID_FIELD, new FakeGraphDatabaseServiceFactory());
  }

  @After
  public void tearDown() {
    idHandler.reset();
    doneHandler.reset();
    nodeHandler.reset();
    graph.shutdown();
  }

  @Test
  public void testCreateNode() {
    properties = testNode();

    graph.nodes().create(properties, idHandler);
    Assert.assertNotNull(idHandler.getValue());

    graph.nodes().fetch(currentNodeId(), nodeHandler);
    assertTestNode(nodeHandler.getValue());
  }

  @Test
  public void testUpdateNode() {
    Object id = addTestNode();

    properties = updatedTestNode();
    graph.nodes().update(id, properties, doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.nodes().fetch(currentNodeId(), nodeHandler);
    assertUpdatedTestNode(nodeHandler.getValue());
  }

  @Test
  public void testFetchNode() {
    Object id = addTestNode();

    graph.nodes().fetch(id, nodeHandler);
    assertTestNode(nodeHandler.getValue());
  }

  @Test
  public void testRemoveNode() {
    Object id = addTestNode();

    graph.nodes().remove(id, doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.nodes().fetch(id, nodeHandler);
    Assert.assertNull(nodeHandler.getValue());
  }

  @Test
  public void testCreateRelationship() {
    Object fromId = addTestNode();
    Object toId = addTestNode();
    properties = testRelationship();

    graph.relationships().create(fromId, toId, "connected", properties, idHandler);
    Assert.assertNotNull(idHandler.getValue());

    graph.relationships().fetch(currentRelationshipId(), relationshipHandler);
    assertTestRelationship(relationshipHandler.getValue());
  }

  @Test
  public void testUpdateRelationship() {
    Object id = addTestRelationship();

    properties = updatedTestRelationship();
    graph.relationships().update(id, properties, doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.relationships().fetch(currentRelationshipId(), relationshipHandler);
    assertUpdatedTestRelationship(relationshipHandler.getValue());
  }

  @Test
  public void testFetchRelationship() {
    Object id = addTestRelationship();

    graph.relationships().fetch(id, relationshipHandler);
    assertTestRelationship(relationshipHandler.getValue());
  }

  @Test
  public void testRemoveRelationship() {
    Object id = addTestRelationship();

    graph.relationships().remove(id, doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.relationships().fetch(id, relationshipHandler);
    Assert.assertNull(relationshipHandler.getValue());
  }

  @Test
  public void testClear() {
    Object nodeId = addTestNode();
    Object relationshipId = addTestRelationship();

    graph.clear(doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.nodes().fetch(nodeId, nodeHandler);
    Assert.assertNull(nodeHandler.getValue());

    graph.relationships().fetch(relationshipId, relationshipHandler);
    Assert.assertNull(relationshipHandler.getValue());
  }

  private Object addTestNode() {
    properties = testNode();
    graph.nodes().create(properties, idHandler);
    return currentNodeId();
  }

  private Object addTestRelationship() {
    Object fromId = addTestNode();
    Object toId = addTestNode();
    properties = testRelationship();
    graph.relationships().create(fromId, toId, "connected", properties, idHandler);
    return currentRelationshipId();
  }

  private Object currentNodeId() {
    return NODE_ID_FIELD == null ? idHandler.getValue() : properties.get(NODE_ID_FIELD);
  }

  private Object currentRelationshipId() {
    return RELATIONSHIP_ID_FIELD == null ? idHandler.getValue() : properties.get(RELATIONSHIP_ID_FIELD);
  }

  private void assertTestNode(Map<String, Object> properties) {
    Assert.assertEquals("test node", properties.get("content"));
  }

  private void assertUpdatedTestNode(Map<String, Object> properties) {
    Assert.assertEquals("updated test node", properties.get("content"));
  }

  private void assertTestRelationship(Map<String, Object> properties) {
    Assert.assertEquals("test relationship", properties.get("content"));
  }

  private void assertUpdatedTestRelationship(Map<String, Object> properties) {
    Assert.assertEquals("updated test relationship", properties.get("content"));
  }

  private Map<String, Object> testNode() {
    Map<String, Object> result = new HashMap<>();
    if (NODE_ID_FIELD != null) {
      result.put(NODE_ID_FIELD, UUID.randomUUID().toString());
    }
    result.put("content", "test node");
    return result;
  }

  private Map<String, Object> updatedTestNode() {
    Map<String, Object> result = new HashMap<>();
    if (NODE_ID_FIELD != null) {
      result.put(NODE_ID_FIELD, UUID.randomUUID().toString());
    }
    result.put("content", "updated test node");
    return result;
  }

  private Map<String, Object> testRelationship() {
    Map<String, Object> result = new HashMap<>();
    if (RELATIONSHIP_ID_FIELD != null) {
      result.put(RELATIONSHIP_ID_FIELD, UUID.randomUUID().toString());
    }
    result.put("content", "test relationship");
    return result;
  }

  private Map<String, Object> updatedTestRelationship() {
    Map<String, Object> result = new HashMap<>();
    if (RELATIONSHIP_ID_FIELD != null) {
      result.put(RELATIONSHIP_ID_FIELD, UUID.randomUUID().toString());
    }
    result.put("content", "updated test relationship");
    return result;
  }

}
