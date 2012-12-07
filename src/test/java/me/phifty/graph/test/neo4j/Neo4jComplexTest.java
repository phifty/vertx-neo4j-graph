package me.phifty.graph.test.neo4j;

import me.phifty.graph.ComplexResetNodeRelationshipsResult;
import me.phifty.graph.Graph;
import me.phifty.graph.neo4j.DynamicRelationshipType;
import me.phifty.graph.neo4j.Neo4jGraph;
import me.phifty.graph.test.FakeHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * @author phifty <b.phifty@gmail.com>
 */
public class Neo4jComplexTest {

  private FakeHandler<Object> idHandler = new FakeHandler<>();
  private FakeHandler<Iterable<Object>> idsHandler = new FakeHandler<>();
  private FakeHandler<Iterable<Map<String, Object>>> nodesHandler = new FakeHandler<>();
  private FakeHandler<Iterable<Map<String, Object>>> relationshipsHandler = new FakeHandler<>();
  private FakeHandler<ComplexResetNodeRelationshipsResult> resetNodeRelationshipsHandler = new FakeHandler<>();
  private Map<String, Object> properties;
  private Graph graph;
  private Object fromNodeId;
  private Object toNodeId;

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
    idsHandler.reset();
    nodesHandler.reset();
    relationshipsHandler.reset();
    resetNodeRelationshipsHandler.reset();
    graph.shutdown();
  }

  @Test
  public void testFetchAllRelatedNodes() throws Exception {
    addTestRelationship();

    graph.complex().fetchAllRelatedNodes(fromNodeId, "connected", "outgoing", nodesHandler);
    assertTestNode(nodesHandler.getValue().iterator().next());

    DynamicRelationshipType.clearCache();

    graph.complex().fetchAllRelatedNodes(fromNodeId, "connected", "outgoing", nodesHandler);
    assertTestNode(nodesHandler.getValue().iterator().next());
  }

  @Test
  public void testResetNodeRelationships() throws Exception {
    Object id = addTestNode();
    Object targetIdOne = addTestNode();
    Object targetIdTwo = addTestNode();

    Set<Object> targetIds = new HashSet<>();
    targetIds.add(targetIdOne);
    targetIds.add(targetIdTwo);

    graph.complex().resetNodeRelationships(id, "connected", targetIds, resetNodeRelationshipsHandler);

    Set<Object> addedNodeIds = resetNodeRelationshipsHandler.getValue().addedNodeIds;
    Assert.assertEquals(2, addedNodeIds.size());
    Assert.assertTrue(addedNodeIds.contains(targetIdOne));
    Assert.assertTrue(addedNodeIds.contains(targetIdTwo));

    Set<Object> removedNodeIds = resetNodeRelationshipsHandler.getValue().removedNodeIds;
    Assert.assertEquals(0, removedNodeIds.size());

    targetIds.remove(targetIdTwo);
    graph.complex().resetNodeRelationships(id, "connected", targetIds, resetNodeRelationshipsHandler);

    addedNodeIds = resetNodeRelationshipsHandler.getValue().addedNodeIds;
    Assert.assertEquals(0, addedNodeIds.size());

    removedNodeIds = resetNodeRelationshipsHandler.getValue().removedNodeIds;
    Assert.assertEquals(1, removedNodeIds.size());
    Assert.assertTrue(removedNodeIds.contains(targetIdTwo));

    graph.relationships().fetchAllOfNode(id, relationshipsHandler);
    Assert.assertTrue(relationshipsHandler.getValue().iterator().hasNext());
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

  private void assertTestNode(Map<String, Object> properties) {
    Assert.assertEquals("test node", properties.get("content"));
  }

}
