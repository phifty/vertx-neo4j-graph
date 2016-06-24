package org.openpcf.neo4vertx.neo4j.service;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.graph.neo4j.Neo4VertxConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.Bootstrapper;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.ServerConfigurator;
import org.neo4j.server.rest.repr.BadInputException;
import org.neo4j.server.rest.repr.CypherResultRepresentation;
import org.neo4j.server.rest.repr.OutputFormat;
import org.neo4j.server.rest.repr.formats.JsonFormat;
import org.openpcf.neo4vertx.neo4j.Neo4jGraphDatabaseServiceFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

public class GuiGraphService extends EmbeddedGraphService {

    public GuiGraphService(Neo4VertxConfiguration configuration) throws Exception {
        super(configuration);
    }

    private Bootstrapper bootStrapper;

    @Override
    public void initialize(Neo4VertxConfiguration configuration) throws Exception {
        graphDatabaseService = new Neo4jGraphDatabaseServiceFactory().create(configuration);
        ServerConfigurator webConfig = new ServerConfigurator((GraphDatabaseAPI) graphDatabaseService);
        webConfig.configuration().setProperty(Configurator.WEBSERVER_ADDRESS_PROPERTY_KEY, configuration.getWebServerBindAddress());
        bootStrapper = new WrappingNeoServerBootstrapper((GraphDatabaseAPI) graphDatabaseService, webConfig);
        bootStrapper.start();
    }

    private String transformExecutionResult(ExecutionResult result) throws JsonProcessingException, BadInputException, URISyntaxException {
        CypherResultRepresentation repr = new CypherResultRepresentation(result, false, false);
        OutputFormat format = new OutputFormat(new JsonFormat(), new URI("http://localhost:8000"), null);
        return format.assemble(repr);
    }

    /**
     * Query to the embedded neo4j database.
     * 
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public JsonObject query(JsonObject request) throws Exception {
        ExecutionEngine engine = new ExecutionEngine(graphDatabaseService);
        ExecutionResult result;
        try (Transaction tx = graphDatabaseService.beginTx()) {
            result = engine.execute(request.getString("query"));
            String json = transformExecutionResult(result);
            JsonObject response = new JsonObject(json);
            // sanitizeJson(response);

            tx.success();
            return response;
        } catch (Exception e) {
            throw new Exception("Error while evaluating query: {" + request.getString("query") + "}", e);
        }
    }

    /**
     * Removes various bogus attributes which are not useful when executing queries in internal mode.
     * 
     * @param response
     */
    private void sanitizeJson(JsonObject response) {
        List<String> keysToBeRemoved = new ArrayList<String>();
        for (String key : response.getJsonArray("data").getJsonArray(0).getJsonObject(0).fieldNames()) {
            if ("data".equalsIgnoreCase(key)) {
                continue;
            } else {
                keysToBeRemoved.add(key);
            }
        }
        for (String key : keysToBeRemoved) {
            response.getJsonArray("data").getJsonArray(0).getJsonObject(0).remove(key);
        }

    }

    @Override
    public void shutdown() {
        if (bootStrapper != null) {
            bootStrapper.stop();
        }

        if (graphDatabaseService != null) {
            graphDatabaseService.shutdown();
        }
    }

}
