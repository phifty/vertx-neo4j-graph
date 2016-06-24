package org.openpcf.neo4vertx.neo4j;

import io.vertx.ext.graph.neo4j.Neo4VertxConfiguration;

/**
 * Stub for the test configuration.
 *
 * @author https://github.com/rubin55[Rubin Simons]
 * @author https://github.com/Jotschi[Johannes Schüth]
 */
public class TestConfiguration extends Neo4VertxConfiguration {

    @Override
    public String getMode() {
        return Fixtures.getConfig().getMode();
    }

    @Override
    public String getPath() {
        return Fixtures.getConfig().getPath();
    }

    @Override
    public String getRestUrl() {
        return Fixtures.getConfig().getRestUrl();
    }

    @Override
    public String getBaseAddress() {
        return null;
    }

    @Override
    public String getHAInitialHosts() {
        return null;
    }

    @Override
    public String getHAServerID() {
        return null;
    }

    @Override
    public boolean getHASlaveOnly() {
        return false;
    }

    @Override
    public String getHAServer() {
        return null;
    }

    @Override
    public String getHAClusterServer() {
        return null;
    }

}
