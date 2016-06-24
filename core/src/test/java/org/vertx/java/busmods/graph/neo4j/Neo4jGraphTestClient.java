package org.vertx.java.busmods.graph.neo4j;

import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.VertxTestBase;

import org.junit.Test;
import org.openpcf.neo4vertx.neo4j.Fixtures;

/**
 * The Neo4jGraphTestClient object.
 *
 * @author https://github.com/phifty[Philipp Brüll]
 * @author https://github.com/rubin55[Rubin Simons]
 * @author https://github.com/fraik[Freek Alleman]
 */
public class Neo4jGraphTestClient extends VertxTestBase {

    /**
     * TODO build own test verticle?
    @Override
    public void start() {

        vertx.deployVerticle(Neo4jGraphModule.class.getName(), null, 1, ar -> {
            appReady();
            vertx.eventBus().send("test.neo4j-graph.cypher.query", Fixtures.asJsonCypherQuery(Fixtures.DELETE_TEST_ENTITY_QUERY), Future.future());
        });
    }
     **/

    @Test
    public void testCreateEntity() {
        vertx.eventBus().send("test.neo4j-graph.cypher.query", Fixtures.asJsonCypherQuery(Fixtures.CREATE_TEST_ENTITY_QUERY), ar -> {
                //TODO fix generics
                JsonArray jsonArray = ((JsonObject)(ar.result().body())).getJsonArray("data").getJsonArray(0);
                assert(Fixtures.NEO4VERTX_TEST_ENTITY_UUID.equals(jsonArray.getJsonObject(0)));
                testComplete();
            }
        );
        await();
    }

    @Test
    public void testReadEntity() {
        vertx.eventBus().send("test.neo4j-graph.cypher.query", Fixtures.asJsonCypherQuery(Fixtures.CREATE_TEST_ENTITY_QUERY), message -> {
                vertx.eventBus().send("test.neo4j-graph.cypher.query", Fixtures.asJsonCypherQuery(Fixtures.READ_TEST_ENTITY_QUERY), ar -> {
                    //TODO use generics here
                    JsonArray jsonArray = ((JsonObject)message.result().body()).getJsonArray("data").getJsonArray(0);
                    assert(Fixtures.NEO4VERTX_TEST_ENTITY_UUID.equals(jsonArray.getJsonObject(0)));
                    testComplete();
                });
            }
        );
        await();
    }

    @Test
    public void testUpdateEntity() {
        vertx.eventBus().send("test.neo4j-graph.cypher.query",
            Fixtures.asJsonCypherQuery(Fixtures.CREATE_TEST_ENTITY_QUERY), message -> {
                vertx.eventBus().send("test.neo4j-graph.cypher.query", Fixtures.asJsonCypherQuery(Fixtures.UPDATE_TEST_ENTITY_QUERY), (AsyncResult<Message<JsonObject>> ar) -> {
                    JsonArray jsonArray = ar.result().body().getJsonArray("data").getJsonArray(0);
                    assert(Fixtures.NEO4VERTX_TEST_ENTITY_UPDATED_VALUE.equals(jsonArray.getJsonObject(2)));
                    testComplete();
                });
            }
        );
        await();
    }

    @Test
    public void testDeleteEntity() {
        vertx.eventBus().send("test.neo4j-graph.cypher.query", Fixtures.asJsonCypherQuery(Fixtures.CREATE_TEST_ENTITY_QUERY), message -> {
            vertx.eventBus().send("test.neo4j-graph.cypher.query", Fixtures.asJsonCypherQuery(Fixtures.DELETE_TEST_ENTITY_QUERY), ar -> {
                vertx.eventBus().send("test.neo4j-graph.cypher.query", Fixtures.asJsonCypherQuery(Fixtures.READ_TEST_ENTITY_QUERY), (AsyncResult<Message<JsonObject>> ar2) -> {
                    JsonArray jsonArray = ar2.result().body().getJsonArray("data");
                    assert (0 == jsonArray.size());
                    testComplete();
                });
            });
        });
        await();
    }
}
