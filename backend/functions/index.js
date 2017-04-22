const functions = require('firebase-functions');
const admin = require('firebase-admin');

const async = require('async');

admin.initializeApp(functions.config().firebase);

/**
 * @POST
 * Generates a map from user ids to their details such as full name and profile pic
 * urls. Accepts a list of user ids in the post request body
 */
exports.getUserDetails = functions.https.onRequest((request, response) => {
	var ref = admin.database().ref("users");
	var result = {};

	if (typeof request.body.ids === 'string') {
		ids = [ request.body.ids ]
	} else {
		ids = request.body.ids
	}

	async.map(ids, function(id, callback) {
		ref.orderByKey().equalTo(id).on("child_added", function(snapshot) {
			var data = snapshot.val();
			result[id] = {
				"fullName": data.fullName,
				"photoUrl": data.photoUrl
			}

			return callback(null, result[id]);
		});

	}, function(err, contents) {
		if (err) console.error(err);
		console.log(result);
		response.send(result);
	});
});

