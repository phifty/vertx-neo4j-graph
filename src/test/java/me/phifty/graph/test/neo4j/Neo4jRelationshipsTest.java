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
  private FakeHandler<Iterable<Map<String, Object>>> relationshipsHandler = new FakeHandler<>();
  private Map<String, Object> properties;
  private Object fromNodeId;
  private Object toNodeId;
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
    relationshipHandler.reset();
    relationshipsHandler.reset();
    graph.shutdown();
  }

  @Test
  public void testCreateRelationship() throws Exception {
    Object fromId = addTestNode();
    Object toId = addTestNode();
    properties = Fixtures.testRelationship();

    graph.relationships().create(fromId, toId, "connected", properties, idHandler);
    Assert.assertNotNull(idHandler.getValue());

    graph.relationships().fetch(currentRelationshipId(), relationshipHandler);
    assertTestRelationship(relationshipHandler.getValue());
  }

  @Test
  public void testUpdateRelationship() throws Exception {
    Object id = addTestRelationship();

    properties = Fixtures.updatedTestRelationship();
    graph.relationships().update(id, properties, doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.relationships().fetch(currentRelationshipId(), relationshipHandler);
    assertUpdatedTestRelationship(relationshipHandler.getValue());
  }

  @Test
  public void testFetchRelationship() throws Exception {
    Object id = addTestRelationship();

    graph.relationships().fetch(id, relationshipHandler);
    assertTestRelationship(relationshipHandler.getValue());
  }

  @Test
  public void testFetchAllRelationshipsOfNode() throws Exception {
    addTestRelationship();

    graph.relationships().fetchAllOfNode(fromNodeId, relationshipsHandler);
    assertTestRelationship(relationshipsHandler.getValue().iterator().next());
  }

  @Test
  public void testRemoveRelationship() throws Exception {
    Object id = addTestRelationship();

    graph.relationships().remove(id, doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.relationships().fetch(id, relationshipHandler);
    Assert.assertNull(relationshipHandler.getValue());
  }

  private Object addTestNode() throws Exception {
    properties = Fixtures.testNode();
    graph.nodes().create(properties, idHandler);
    return currentNodeId();
  }

  private Object addTestRelationship() throws Exception {
    fromNodeId = addTestNode();
    toNodeId = addTestNode();
    properties = Fixtures.testRelationship();
    graph.relationships().create(fromNodeId, toNodeId, "connected", properties, idHandler);
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
