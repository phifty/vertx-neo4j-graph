neo4vertx JavaScript Example
===========================

This example is outdated and must be updated once vert.x 3.x has decent support 
for JavaScript.

----


This example shows the basic usage of neo4vertx. This example is only meant
to show conceptually how neo4vertx works. We send a Cypher query directly 
from the client.js, utilizing the Vert.x eventbus Javascript bridge. In real
life you should probably not do this, as exposing the Cypher REST api in this
way will effectively allow anyone with access to your site to fire ANY kind
of Cypher REST query at your Neo4j instance.

Ideally, one would create backend verticles that handle all sorts of abstract
things, utilizing neo4vertx; the client.js would then talk to these backend
verticles instead!

Follow the steps below to run the example.

### Start the application ###

> vertx run app.js

If this is the first time you run the example, you'll see vertx downloading 
and deploying 2 modules (mod-web-server and ofcourse neo4vertx). Now that 
the modules are deployed, let's run the example:


### Run the example

Open [http://localhost:8080/](http://localhost:8080/) in a webbrowser to run 
the example (you can see the output of client.js in the webconsole). It 
should look something like this:

```
"Eventbus connected"
"displayResult() received the following result: "
Object { columns: Array[1], data: Array[0] }
```


### Open the neo4j browser
Open [http://localhost:7474/](http://localhost:7474/) to see the neo4j browser.



----------


# Some more details
Now that we've succesfully run the example, let's take a closer look at the 
code. The application basically consists of 3 files:

 - app.js (server-side code)
 - web/client.js (client-side code)
 - web/index.html (the delivery method for the client-side code)

Let's take a look at the server-side code!


## Server-side code (app.js)

### Requires

First off, we include container and console.

```
var container = require("vertx/container"),
    console = require("vertx/console");
```

### Configure the modules

Then we'll define the configuration for the two modules which we'll deploy; 
_mod-web-server_ and _neo4vertx_. The configuration parameters all pretty 
much speak for themselves, for complete information see 
[mod-web-server ](https://github.com/vert-x/mod-web-server#configuration) and 
[neo4vertx](https://github.com/raaftech/neo4vertx#configuration) configuration 
documentation.

```
// webserver configuration
var webServerConfig = {
    port: 8080,
    host: "0.0.0.0",
    bridge: true,
    inbound_permitted: [
        { address_re: 'neo4j-graph.*' }
    ]
};
// neo4vertx configuration
var neo4jConfig = {
    mode: "default",
    path: "/tmp/db",
    baseAddress: "neo4j-graph"
};
```

### Deploy the modules
On the server-side we deploy a webserver-module and the neo4vertx module:

```
// deploy mod-web-server
container.deployModule("io.vertx~mod-web-server~2.0.0-final", webServerConfig, 1, deploymentCompleteHandler);

// deploy neo4vertx
container.deployModule("org.openpcf~neo4vertx~1.3.2", neo4jConfig, 1, deploymentCompleteHandler);
```


### Deployment results
Each deployment calls _deploymentCompleteHandler(error, deploymentID)_ when 
it's done deploying this notifies the user of the deploymentID when the 
deployment succeeded or the error if something went wrong.

```
// display deployment status
function deploymentCompleteHandler(error, deploymentID){
    if (error) {
        console.log("Something went wrong with deployment: ");
        console.log(error)
    } else {
        console.log("Deployment Completed succesfully, deployment ID: " + deploymentID);
    }
}
```


## Client-side code (client.js)


### Connect to the EventBus

On the client-side we create a new EventBus:

```
var eventBus = new vertx.EventBus(window.location.protocol + '//' +
                            window.location.hostname + ':' +
                            window.location.port + '/eventbus');
```

### Query function

We define a function _doQuery(querystring, callback)_ which sends a querystring 
on the eventbus to be executed and calls the _callback_ with the results.

```
// Execute a Cypher query and call the callback with the results
function doQuery(querystring, callback) {

    // Json object containing our very simple querystring
    var queryJsonObject = { "query": querystring };

    // Put the query message on the eventbus
    eventBus.send(
        'neo4j-graph.cypher.query',
        queryJsonObject,
        callback
    );
}
```

### Callback function

The callback _displayResults(results)_ notifies the user on the console 
what the results were.

```
function displayResult(result) { 
    
    // Display query result(s)
    console.log("displayResult() received the following result: ");
    console.log(result);
}
```

### Execute a query

As soon as we're connected to the eventbus (_eventBus.onopen_), we notify the 
user of this through the console, execute a Cypher query (using _doQuery_ defined earlier) and then display the results using the passed callback (_displayResults_).

```
// Fire when connected to the eventbus
eventBus.onopen = function eventbusConnected() {

  // Our very simple query string
  var simpleQuerystring = "MATCH (n) RETURN n";

  // Notify user that eventbus is connected
  console.log("Eventbus connected");
  
  // Execute a Cypher query and use callbackFunction() to display the results
  doQuery(simpleQuerystring, displayResult);
}
```
