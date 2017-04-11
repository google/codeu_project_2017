var app = {
  server: "http://parse.sfm8.hackreactor.com/chatterbox/classes/lobby",
  // convo: 'lobby',

	init: function() {
    app.clearMessages();
    app.fetch();

    // Fetch messages from the default server
    // Iterate through message objects
      // Render messages

	},

	send: function(message) {
		$.ajax({
		// URL to communicate with the parse API server.
		url: this.server,
		type: 'POST',
		data: JSON.stringify(message),
		contentType: 'application/json',
		success: function (data) {
		console.log('Message sent');
    app.init()
		},
		error: function (data) {
		// https://developer.mozilla.org/en-US/docs/Web/API/console.error
		console.error('chatterbox: Failed to send message', data);
		}
		});
	},

  fetch: function() {
    $.ajax({
    // url to communicate with the parse API server.
    url: this.server,
    type: 'GET',
    contentType: 'application/json',
    success: function (data) {
        data = data.results;
        for (var key in data) {
        app.renderMessage(data[key])
       }
    },
    error: function (data) {
    //https://developer.mozilla.org/en-US/docs/Web/API/console.error
    console.error('Failed to send message', data);
    }
    });
  },

  clearMessages: function() {
    $('#chats').empty();
  },

  renderMessage: function(message) {
    // Get the #chats element
    // Create a new element and prepend to #chats
    let $paragraph = ('<div class="chat"><div class="username">' + message.username + '</div> <div>' + message.text + '</div></div>');
    $('#chats').prepend($paragraph);


  },

  renderConvo: function(convo) {
    let $convo = ('<option value=' + convo + '>' + convo + '</option>');
    $('#convoSelect').append($convo);
    app.server = "http://parse.sfm8.hackreactor.com/chatterbox/classes/" + convo
    // create a new convo
  },

  handleUsernameClick: function(userNameText) {
    $( '.username:contains(' + userNameText + ')' ).addClass('friend')
    // $('.chat username[value=' + userNameText + ']').addClass('friend');
    console.log("CLICKED")
  },

  handleSubmit: function(username, text, convo) {
    var obj = {"username": username,
    "text": text,
    "convoname": convo
  }
    // Wrap text in a message
    // Send to the server
    this.send(obj)
    // Init
  }
}

$(document).ready(function() {

  // Click on a username to add a friend; friends should be bolded in messages
  $('#chats').on('click', '.username', function(event) {
    $test = $(event.currentTarget);
    var userNameText = ($test.text());
    app.handleUsernameClick(userNameText);
    // Create a new convo with renderconvo
    // Switch to renderconvo
    // Fetch to get messages from the convo
  });

  // Send messages to the server
  $('#send .submit').on('click', function(event) {
    event.preventDefault();
    //app.handleSubmit();
    var text = ($( "input:first" ).val());
    var username = window.location.search.split('=').slice(-1).toString()
    var currentConvo = $('#convoSelect option:selected').text()
    app.handleSubmit(username, text, currentConvo)
    // need to get the convo
  });
  $('#create-convo .submit').on('click', function(event) {
    event.preventDefault();
    var text = ($( "#create-convo input:first" ).val());
    var appendElement = '<option value=' + text + '>' + text + '</option> ';

    $('#convoSelect').append(appendElement);
    // app.handleSubmit(username, text, currentConvo)
    // need to get the convo
  });



  // Create a new convo when new convo dropdown is selected
    // Listen for when a user selects to make a new convo
    // Get name of convo from user
    // renderconvo, which adds to list of convos
    // Go to the convo (optional)

  // Switch convos and fetch messages for the convo
  $("#convoSelect").change(function() {
    // if create a new convo is selected
      // prompt user for convo name
      // add the new convo to the drop down



    var currentConvo = ($('#convoSelect option:selected').text())
    app.server = "http://parse.sfm8.hackreactor.com/chatterbox/classes/" + $('#convoSelect option:selected').text()
    app.init()

  })
    // Need to init on the convo
    // Set the convo somehow
});

app.init()

setInterval(function() {
  app.init()
}, 5000)
// send messages and fetch as soon as it's sent
// Add click handler on submit
  // Grab the text in the input box and set to message
  // send message and fetch from the current convo you're in

// fetch and render
