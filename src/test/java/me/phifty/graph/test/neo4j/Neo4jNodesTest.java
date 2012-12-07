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
public class Neo4jNodesTest {

  private FakeHandler<Object> idHandler = new FakeHandler<>();
  private FakeHandler<Boolean> doneHandler = new FakeHandler<>();
  private FakeHandler<Map<String, Object>> nodeHandler = new FakeHandler<>();
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
  public void testCreateNode() throws Exception {
    properties = Fixtures.testNode();

    graph.nodes().create(properties, idHandler);
    Assert.assertNotNull(idHandler.getValue());

    graph.nodes().fetch(currentNodeId(), nodeHandler);
    assertTestNode(nodeHandler.getValue());
  }

  @Test
  public void testUpdateNode() throws Exception {
    Object id = addTestNode();

    properties = Fixtures.updatedTestNode();
    graph.nodes().update(id, properties, doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.nodes().fetch(currentNodeId(), nodeHandler);
    assertUpdatedTestNode(nodeHandler.getValue());
  }

  @Test
  public void testFetchNode() throws Exception {
    Object id = addTestNode();

    graph.nodes().fetch(id, nodeHandler);
    assertTestNode(nodeHandler.getValue());
  }

  @Test
  public void testRemoveNode() throws Exception {
    Object id = addTestNode();

    graph.nodes().remove(id, doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.nodes().fetch(id, nodeHandler);
    Assert.assertNull(nodeHandler.getValue());
  }

  private Object addTestNode() throws Exception {
    properties = Fixtures.testNode();
    graph.nodes().create(properties, idHandler);
    return currentNodeId();
  }

  private Object currentNodeId() {
    return Fixtures.NODE_ID_FIELD == null ? idHandler.getValue() : properties.get(Fixtures.NODE_ID_FIELD);
  }

  private void assertTestNode(Map<String, Object> properties) {
    Assert.assertEquals("test node", properties.get("content"));
  }

  private void assertUpdatedTestNode(Map<String, Object> properties) {
    Assert.assertEquals("updated test node", properties.get("content"));
  }

}
