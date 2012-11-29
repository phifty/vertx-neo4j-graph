package org.vertx.java.busmods.test;

import org.vertx.java.framework.TestBase;

public class Neo4jGraphModuleTest extends TestBase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    startApp(Neo4jGraphTestClient.class.getName());
  }

  public void testCreateNode() {
    startTest(getMethodName());
  }

  public void testUpdateNode() {
    startTest(getMethodName());
  }

  public void testFetchNode() {
    startTest(getMethodName());
  }

  public void testRemoveNode() {
    startTest(getMethodName());
  }

  public void testCreateRelationship() {
    startTest(getMethodName());
  }

  public void testUpdateRelationship() {
    startTest(getMethodName());
  }

  public void testFetchRelationship() {
    startTest(getMethodName());
  }

  public void testRemoveRelationship() {
    startTest(getMethodName());
  }

  public void testClear() {
    startTest(getMethodName());
  }

}
