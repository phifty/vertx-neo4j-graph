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
public class Neo4jGraphTest {

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
    relationshipHandler.reset();
    graph.shutdown();
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

}
