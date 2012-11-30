package me.phifty.graph.test.neo4j;

import me.phifty.graph.Graph;
import me.phifty.graph.neo4j.Neo4jGraph;
import me.phifty.graph.test.FakeHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Neo4jRelationshipsTest {

  private FakeHandler<Object> idHandler = new FakeHandler<>();
  private FakeHandler<Boolean> doneHandler = new FakeHandler<>();
  private FakeHandler<Map<String, Object>> nodeHandler = new FakeHandler<>();
  private FakeHandler<Map<String, Object>> relationshipHandler = new FakeHandler<>();
  private Map<String, Object> properties;
  private Graph graph;

  @Before
  public void setUp() throws Exception {
    graph = new Neo4jGraph("/tmp/test",
      Fixtures.NODE_ID_FIELD,
      Fixtures.RELATIONSHIP_ID_FIELD,
      new FakeGraphDatabaseServiceFactory());
  }

  @After
  public void tearDown() {
    idHandler.reset();
    doneHandler.reset();
    nodeHandler.reset();
    graph.shutdown();
  }

  @Test
  public void testCreateRelationship() {
    Object fromId = addTestNode();
    Object toId = addTestNode();
    properties = Fixtures.testRelationship();

    graph.relationships().create(fromId, toId, "connected", properties, idHandler);
    Assert.assertNotNull(idHandler.getValue());

    graph.relationships().fetch(currentRelationshipId(), relationshipHandler);
    assertTestRelationship(relationshipHandler.getValue());
  }

  @Test
  public void testUpdateRelationship() {
    Object id = addTestRelationship();

    properties = Fixtures.updatedTestRelationship();
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

  private Object addTestNode() {
    properties = Fixtures.testNode();
    graph.nodes().create(properties, idHandler);
    return currentNodeId();
  }

  private Object addTestRelationship() {
    Object fromId = addTestNode();
    Object toId = addTestNode();
    properties = Fixtures.testRelationship();
    graph.relationships().create(fromId, toId, "connected", properties, idHandler);
    return currentRelationshipId();
  }

  private Object currentNodeId() {
    return Fixtures.NODE_ID_FIELD == null ? idHandler.getValue() : properties.get(Fixtures.NODE_ID_FIELD);
  }

  private Object currentRelationshipId() {
    return Fixtures.RELATIONSHIP_ID_FIELD == null ? idHandler.getValue() : properties.get(Fixtures.RELATIONSHIP_ID_FIELD);
  }

  private void assertTestRelationship(Map<String, Object> properties) {
    Assert.assertEquals("test relationship", properties.get("content"));
  }

  private void assertUpdatedTestRelationship(Map<String, Object> properties) {
    Assert.assertEquals("updated test relationship", properties.get("content"));
  }

}
