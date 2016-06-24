var eventBus = new vertx.EventBus(window.location.protocol + '//' +
                            window.location.hostname + ':' +
                            window.location.port + '/eventbus');

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

function displayResult(result) { 
	
	// Display query result(s)
	console.log("displayResult() received the following result: ");
	console.log(result);
}

// Fire when connected to the eventbus
eventBus.onopen = function eventbusConnected() {

  // Our very simple query string
  var simpleQuerystring = "MATCH (n) RETURN n";

  // Notify user that eventbus is connected
  console.log("Eventbus connected");
  
  // Execute a Cypher query and use callbackFunction() to display the results
  doQuery(simpleQuerystring, displayResult);
}