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

    var settings = {
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

    $.ajax(settings).done(function (response) {
      this.setState({ "data": this.state.data.concat(JSON.parse(response)), "lastClick": clickTime });
      if (JSON.parse(response).length != 0) {
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
       "paddingRight": "-2%"
     }

      return (
        <Panel style={panelStyle}>
        <div style={fieldStyle} ref="box">
        {this.state.data.map((msg, i) => <Message key={i} mine={this.props.user.id.uuid == msg.author.uuid} message={msg.content} />)}
        <div ref="messagesEnd"/>
        </div>
        </Panel>
      );
   }
}


export default MessagePanel;
