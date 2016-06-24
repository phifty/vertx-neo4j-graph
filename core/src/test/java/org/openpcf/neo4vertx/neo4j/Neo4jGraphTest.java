package org.openpcf.neo4vertx.neo4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openpcf.neo4vertx.neo4j.service.EmbeddedGraphService;
import org.openpcf.neo4vertx.neo4j.service.GraphService;

/**
 * The Neo4jGraphTest object.
 *
 * @author https://github.com/phifty[Philipp Brüll]
 * @author https://github.com/rubin55[Rubin Simons]
 * @author https://github.com/fraik[Freek Alleman]
 */
public class Neo4jGraphTest {

    protected GraphService service;

    @Before
    public void setUp() throws Exception {
        service = new EmbeddedGraphService(new TestConfiguration());
        deleteTestEntity();
    }

    @After
    public void tearDown() throws Exception {
        deleteTestEntity();
        service.shutdown();
    }

    @Test
    @Ignore("Currently fetching json is not supported without using the gui extension")
    public void testCrud() throws Exception {
        String createResult = createTestEntity();
        assertEquals(Fixtures.NEO4VERTX_TEST_ENTITY_UUID, createResult);

        String readResult = readTestEntity();
        assertEquals(Fixtures.NEO4VERTX_TEST_ENTITY_UUID, readResult);

        String updateTestResult = updateTestEntity();
        assertEquals(Fixtures.NEO4VERTX_TEST_ENTITY_UPDATED_VALUE, updateTestResult);

        Boolean deleteTestResult = deleteTestEntity();
        assertTrue(deleteTestResult);
    }

    protected JsonObject executeQuery(String queryString) throws Exception {
        JsonObject queryJson = new JsonObject("{ \"query\":\"" + queryString + "\", \"params\":{} }");
        return service.query(queryJson);
    }

    protected String createTestEntity() throws Exception {
        JsonObject queryResult = executeQuery(Fixtures.CREATE_TEST_ENTITY_QUERY);
        JsonArray jsonArray = queryResult.getJsonArray("data").getJsonArray(0);
        return jsonArray.getString(0);
    }

    protected String readTestEntity() throws Exception {
        JsonObject queryResult = executeQuery(Fixtures.READ_TEST_ENTITY_QUERY);
        JsonArray jsonArray = queryResult.getJsonArray("data");
        if (jsonArray.size() == 0) {
            return "";
        } else {
            JsonArray embeddedJsonArray = jsonArray.getJsonArray(0);
            return embeddedJsonArray.getString(0);
        }
    }

    protected String updateTestEntity() throws Exception {
        JsonObject queryResult = executeQuery(Fixtures.UPDATE_TEST_ENTITY_QUERY);
        JsonArray jsonArray = queryResult.getJsonArray("data").getJsonArray(0);
        return jsonArray.getString(2);
    }

    protected Boolean deleteTestEntity() throws Exception {
        executeQuery(Fixtures.DELETE_TEST_ENTITY_QUERY);
        return (!Fixtures.NEO4VERTX_TEST_ENTITY_UUID.equals(readTestEntity()));
    }
}
