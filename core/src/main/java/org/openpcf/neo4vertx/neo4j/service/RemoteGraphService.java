package org.openpcf.neo4vertx.neo4j.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.graph.neo4j.Neo4VertxConfiguration;

import org.neo4j.graphdb.GraphDatabaseService;

public class RemoteGraphService extends AbstractGraphService {

    public RemoteGraphService(Neo4VertxConfiguration configuration) throws Exception {
        super(configuration);
    }

    private String restUrl;

    @Override
    public GraphDatabaseService getGraphDatabaseService() {
        return null;
    }

    @Override
    public JsonObject query(JsonObject request) throws Exception {
        if (restUrl == null) {
            throw new IllegalArgumentException("Can't connect to rest service since the resturl has not been configured or service was shutdown");
        }
        URL obj = new URL(restUrl);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Mozilla/1.0");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");

        connection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(request.toString());
        wr.flush();
        wr.close();

        int responseCode = connection.getResponseCode();

        // TODO fix logging
        // System.out.println("\nSending 'POST' request to URL : " + restUrl);
        // System.out.println("Post parameters : " + request.toString());
        // System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return new JsonObject(response.toString());
    }

    @Override
    public void shutdown() {
        restUrl = null;
    }

    @Override
    public void initialize(Neo4VertxConfiguration configuration) throws Exception {
        this.restUrl = configuration.getRestUrl();
    }

}
