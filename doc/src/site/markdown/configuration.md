# Configuration

The configuration is stored in a JSON structure. You can pass this configuration
using the standard Vert.x parameter '-conf' when loading the extension or you can
put it in 'neo4vertx.json' in the Java resources directory.

## Settings

### mode

There are different modes that neo4vertx can run in: default, cluster, 
remote and gui mode. 

#### default

In default mode, neo4vertx starts an embedded Neo4j graph.

```
{
  "mode": "default",
  "path": "/var/graph",
  "baseAddress": "graph"
}
```

#### remote

In remote mode, no embedded Neo4j instance is started; neo4vertx simply uses 
the restUrl parameter to talk to a remote Neo4j instance.

```
{
  "mode": "remote",
  "restUrl": "http://localhost:7474/db/data/cypher",
  "baseAddress": "graph"
}
```

#### gui

The gui is similar to default mode but additionally a REST and web interface is startup.
The interface can be accessed on port 7474.

```
{
  "mode": "gui",
  "path": "/var/graph",
  "baseAddress": "graph"
}
```


#### cluster

In cluster mode, neo4vertx starts a HA cluster enabled embedded Neo4j instance, 
handy for sharing graph data with multiple Vert.x instances. 

```
{
    "mode": "cluster",
    "path": "/tmp/graph-master",
    "restUrl": "http://localhost:7474/db/data/cypher",
    "baseAddress": "graph",
    "ha.initial_hosts": "localhost:5001,localhost:5002",
    "ha.server_id": "1",
    "ha.server": "localhost:6001",
    "ha.slave_only": "false",
    "ha.cluster_server": "localhost:5001"
}
```

Please refer to the [neo4j HA documentation](http://docs.neo4j.org/chunked/stable/ha-setup-tutorial.html)
for more information about the various neo4j ha settings or checkout one of the cluster java examples.

### path

The path setting controls where Neo4j stores its data files when running in
default or cluster mode. When running in remote mode, this setting does not apply.
If for some reason the setting is omitted, it will default to:

    System.getProperty("user.dir") + File.separator + "db"

### restUrl

The restUrl setting is used by the query message functionality to decide what 
to talk to. The default (when unset) is "http://localhost:7474/db/data/cypher". 
In remote mode, this is the only setting that makes sense. When you use a 
separate, stand-alone neo4j instance on your local machine that listens on
the default localhost:7474, you might want to set remote mode (see previous).

### baseAddress

This setting controls the baseAddress of the event bus. It controls what name you use to talk to neo4vertx.

