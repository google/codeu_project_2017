const functions = require('firebase-functions');
const admin = require('firebase-admin');

const async = require('async');

admin.initializeApp(functions.config().firebase);

exports.helloWorld = functions.https.onRequest((request, response) => {
 response.send("Hello from Firebase!");
});

exports.getUserNames = functions.https.onRequest((request, response) => {
	var ref = admin.database().ref("users");
	var result = {};

	async.map(request.body.ids, function(id, callback) {
		ref.orderByKey().equalTo(id).on("child_added", function(snapshot) {
			result[id] = snapshot.val().name;
			return callback(null, snapshot.val().name);
		});

	}, function(err, contents) {
		if (err) console.error(err);
		console.log(result);
		response.send(result);
	});
});