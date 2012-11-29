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

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Neo4jGraphTest {

  private FakeHandler<Long> idHandler = new FakeHandler<>();
  private FakeHandler<Boolean> doneHandler = new FakeHandler<>();
  private FakeHandler<Map<String, Object>> nodeHandler = new FakeHandler<>();
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
    Assert.assertTrue(idHandler.getValue() > 0);

    graph.nodes().fetch(idHandler.getValue(), nodeHandler);
    assertTestNode(nodeHandler.getValue());
  }

  @Test
  public void testUpdateNode() {
    long id = addTestNode();

    graph.nodes().update(id, updatedTestNode(), doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.nodes().fetch(id, nodeHandler);
    assertUpdatedTestNode(nodeHandler.getValue());
  }

  @Test
  public void testFetchNode() {
    long id = addTestNode();

    graph.nodes().fetch(id, nodeHandler);
    assertTestNode(nodeHandler.getValue());
  }

  @Test
  public void testRemoveNode() {
    long id = addTestNode();

    graph.nodes().remove(id, doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.nodes().fetch(id, nodeHandler);
    Assert.assertNull(nodeHandler.getValue());
  }

  @Test
  public void testClear() {
    long id = addTestNode();

    graph.clear(doneHandler);
    Assert.assertTrue(doneHandler.getValue());

    graph.nodes().fetch(id, nodeHandler);
    Assert.assertNull(nodeHandler.getValue());
  }

  private long addTestNode() {
    graph.nodes().create(testNode(), idHandler);
    return idHandler.getValue();
  }

  private void assertTestNode(Map<String, Object> properties) {
    Assert.assertEquals("test node", properties.get("content"));
  }

  private void assertUpdatedTestNode(Map<String, Object> properties) {
    Assert.assertEquals("updated test node", properties.get("content"));
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

}
