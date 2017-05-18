var express = require('express');
var request = require('request');
var bodyParser = require('body-parser');

var app = express();

app.use( bodyParser.json() );
app.use(express.static(__dirname + '/../client'));


var port = 443;

app.listen(port, function() {
  console.log(`listening on port ${port}`);
});
