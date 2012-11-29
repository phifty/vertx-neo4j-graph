package me.phifty.graph.test.neo4j;

import me.phifty.graph.Graph;
import me.phifty.graph.Identifier;
import me.phifty.graph.neo4j.Neo4jGraph;
import me.phifty.graph.test.FakeHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Neo4jGraphTest {

  private FakeHandler<Identifier> idHandler = new FakeHandler<Identifier>();
  private FakeHandler<Boolean> doneHandler = new FakeHandler<>();
  private FakeHandler<Map<String, Object>> nodeHandler = new FakeHandler<>();
  private FakeHandler<Map<String, Object>> relationshipHandler = new FakeHandler<>();
  private Graph graph;

  @Before
  public void setUp() throws Exception {
    graph = new Neo4jGraph("/tmp/test", new FakeGraphDatabaseServiceFactory());
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
    graph.nodes().create(testNode(), idHandler);
    Assert.assertNotNull(idHandler.getValue().getId());

    graph.nodes().fetch(idHandler.getValue(), nodeHandler);
    assertTestNode(nodeHandler.getValue());
  }

  @Test
  public void testUpdateNode() {
    Identifier id = addTestNode();

    graph.nodes().update(id, updatedTestNode(), doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.nodes().fetch(id, nodeHandler);
    assertUpdatedTestNode(nodeHandler.getValue());
  }

  @Test
  public void testFetchNode() {
    Identifier id = addTestNode();

    graph.nodes().fetch(id, nodeHandler);
    assertTestNode(nodeHandler.getValue());
  }

  @Test
  public void testRemoveNode() {
    Identifier id = addTestNode();

    graph.nodes().remove(id, doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.nodes().fetch(id, nodeHandler);
    Assert.assertNull(nodeHandler.getValue());
  }

  @Test
  public void testCreateRelationship() {
    Identifier fromId = addTestNode();
    Identifier toId = addTestNode();

    graph.relationships().create(fromId, toId, "connected", testRelationship(), idHandler);
    Assert.assertNotNull(idHandler.getValue().getId());

    graph.relationships().fetch(idHandler.getValue(), relationshipHandler);
    assertTestRelationship(relationshipHandler.getValue());
  }

  @Test
  public void testUpdateRelationship() {
    Identifier id = addTestRelationship();

    graph.relationships().update(id, updatedTestRelationship(), doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.relationships().fetch(id, relationshipHandler);
    assertUpdatedTestRelationship(relationshipHandler.getValue());
  }

  @Test
  public void testFetchRelationship() {
    Identifier id = addTestRelationship();

    graph.relationships().fetch(id, relationshipHandler);
    assertTestRelationship(relationshipHandler.getValue());
  }

  @Test
  public void testRemoveRelationship() {
    Identifier id = addTestRelationship();

    graph.relationships().remove(id, doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.relationships().fetch(id, relationshipHandler);
    Assert.assertNull(relationshipHandler.getValue());
  }

  @Test
  public void testClear() {
    Identifier nodeId = addTestNode();
    Identifier relationshipId = addTestRelationship();

    graph.clear(doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.nodes().fetch(nodeId, nodeHandler);
    Assert.assertNull(nodeHandler.getValue());

    graph.relationships().fetch(relationshipId, relationshipHandler);
    Assert.assertNull(relationshipHandler.getValue());
  }

  private Identifier addTestNode() {
    graph.nodes().create(testNode(), idHandler);
    return idHandler.getValue();
  }

  private Identifier addTestRelationship() {
    Identifier fromId = addTestNode();
    Identifier toId = addTestNode();
    graph.relationships().create(fromId, toId, "connected", testRelationship(), idHandler);
    return idHandler.getValue();
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
    result.put("content", "test node");
    return result;
  }

  private Map<String, Object> updatedTestNode() {
    Map<String, Object> result = new HashMap<>();
    result.put("content", "updated test node");
    return result;
  }

  private Map<String, Object> testRelationship() {
    Map<String, Object> result = new HashMap<>();
    result.put("content", "test relationship");
    return result;
  }

  private Map<String, Object> updatedTestRelationship() {
    Map<String, Object> result = new HashMap<>();
    result.put("content", "updated test relationship");
    return result;
  }

}
