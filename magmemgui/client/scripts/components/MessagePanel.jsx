// Container for messages

import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';

import Message from './Message.jsx'

import Panel from 'react-bootstrap/lib/Panel';
import Col from 'react-bootstrap/lib/Col';

class MessagePanel extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      "data": [],
      "lastClick": 1,
      "cachedSelection": ""
    }

  }

  componentDidMount() {
    this.scrollToBottom();
    window.setInterval(function () {
      if (this.props.selection != "") {
        this.request();
      }
    }.bind(this), 300);
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.selection != this.state.cachedSelection) {
      this.setState( {"data": [], "cachedSelection": nextProps.selection, "lastClick": 1} );
    }
  }

  scrollToBottom() {
    ReactDOM.findDOMNode(this.refs.messagesEnd).scrollIntoView();
  }

  request() {

    var clickTime = (new Date()).getTime();

    // Settings for a first ajax call to TIMED_MESSAGES
    var settings1 = {
      "async": true,
      "crossDomain": true,
      "url": this.props.url + ":" + this.props.port,
      "method": "GET",
      "contentType": "json",
      "headers": {
        "type": "TIMED_MESSAGES",
        "conversation": this.props.selection,
        "to": clickTime,
        "from": this.state.lastClick
      },
      "error": function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    }

    this.setState({ "lastClick": clickTime });

    // Detect all uuids
    $.ajax(settings1).done(function (msgResponse) {
      var ids = []
      msgResponse = JSON.parse(msgResponse)
      msgResponse.forEach(function(i) {
        ids.push(i.author.uuid);
      })

      // Settings for a second ajax call for GET_USERS
      var settings2 = {
        "async": true,
        "crossDomain": true,
        "url": this.props.url + ":" + this.props.port,
        "method": "GET",
        "contentType": "json",
        "headers": {
          "type": "GET_USERS",
          "uuids": "[" + ids + "]",
        },
        "error": function(xhr, status, err) {
          console.error(this.props.url, status, err.toString());
        }.bind(this)
      }

      // Make the GET_USERS request.
      $.ajax(settings2).done(function (response) {
        var dict = {};
        // Create a dictionary based on results of UUID -> Name
        JSON.parse(response).forEach(function(d) {
          dict[d.id.uuid] = d;
        })
        // Update name in original message objects
        msgResponse.forEach(function(d) {
          d["user"] = dict[d.author.uuid]
        })
      });

      this.setState({ "data": this.state.data.concat(msgResponse) });
      if (msgResponse.length != 0) {
        this.scrollToBottom();
      }

    }.bind(this));

  }

   render() {

     // Styling struct
     var panelStyle = {
       "height": "71vh",
     }

     var fieldStyle = {
       "height": "71vh",
       "overflowY": "auto",
       "marginTop": "-16px",
       "overflowX": "hidden",
       "backgroundColor": "white"
     }

      return (
        <Panel style={panelStyle}>
        <Col xs={16}>
        <div style={fieldStyle} ref="box">
        {this.state.data.map((msg, i) => <Message key={i} mine={this.props.user.id.uuid == msg.author.uuid} message={msg} user={this.props.user} />)}
        <div ref="messagesEnd"/>
        </div>
      </Col>
    </Panel>
      );
   }
}


export default MessagePanel;
