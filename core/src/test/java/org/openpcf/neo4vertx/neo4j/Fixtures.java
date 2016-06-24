package org.openpcf.neo4vertx.neo4j;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.graph.neo4j.Neo4VertxConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * The Fixtures object.
 *
 * @author https://github.com/phifty[Philipp Brüll]
 * @author https://github.com/rubin55[Rubin Simons]
 * @author https://github.com/fraik[Freek Alleman]
 */
public class Fixtures {

    public static final String NEO4VERTX_TEST_ENTITY_UUID = "3f2e3e50-1efa-11e4-8c21-0800200c9a66";
    public static final String NEO4VERTX_TEST_ENTITY_DESC = "Neo4vertx Test Entity. Safe to remove.";
    public static final String NEO4VERTX_TEST_ENTITY_INITIAL_VALUE = "Initial value";
    public static final String NEO4VERTX_TEST_ENTITY_UPDATED_VALUE = "Updated value";

    public static final String CREATE_TEST_ENTITY_QUERY = String.format("MERGE (n {uuid: '%s', desc:'%s', value:'%s'}) RETURN n.uuid, n.desc, n.value", NEO4VERTX_TEST_ENTITY_UUID, NEO4VERTX_TEST_ENTITY_DESC, NEO4VERTX_TEST_ENTITY_INITIAL_VALUE);
    public static final String READ_TEST_ENTITY_QUERY = String.format("MATCH (n {uuid: '%s'}) RETURN n.uuid, n.desc, n.value", NEO4VERTX_TEST_ENTITY_UUID);
    public static final String UPDATE_TEST_ENTITY_QUERY = String.format("MATCH (n {uuid: '%s'}) SET n.value = '%s' RETURN n.uuid, n.desc, n.value", NEO4VERTX_TEST_ENTITY_UUID, NEO4VERTX_TEST_ENTITY_UPDATED_VALUE);
    public static final String DELETE_TEST_ENTITY_QUERY = String.format("MATCH (n {uuid: '%s'}) DELETE n", NEO4VERTX_TEST_ENTITY_UUID);

    public static Neo4VertxConfiguration getConfig() {

        InputStream inputStream = Neo4jGraphTest.class.getResourceAsStream("/neo4vertx.json");

        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();

            String inputString;
            while ((inputString = streamReader.readLine()) != null)
                stringBuilder.append(inputString);

            JsonObject jsonObject = new JsonObject(stringBuilder.toString());
            return new Neo4VertxConfiguration(jsonObject);

        } catch (IOException e) {
            e.printStackTrace();
            return new Neo4VertxConfiguration(new JsonObject(""));
        }
    }

    public static JsonObject asJsonCypherQuery(String query) {
        return new JsonObject(String.format("{ \"query\":\"%s\", \"params\": {} }", query));
    }
}
