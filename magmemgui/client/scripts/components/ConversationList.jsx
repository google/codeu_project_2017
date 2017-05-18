import React from 'react';
import ConversationListItem from "./ConversationListItem.jsx"
import $ from 'jquery';

class ConversationList extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      data: [],
      lastClick: 1
    }

    this.request = this.request.bind(this);
  }

  request() {

    var clickTime = (new Date()).getTime();

    var settings = {
      "async": true,
      "crossDomain": false,
      "url": this.props.url + ":" + this.props.port,
      "method": "GET",
      "contentType": "json",
      "headers": {
        "type": "TIMED_CONVERSATIONS",
        "to": clickTime,
        "from": this.state.lastClick
      }
    }

    $.ajax(settings).done(function (response) {
      this.setState({ "data": this.state.data.concat(JSON.parse(response)), "lastClick": clickTime });
    }.bind(this));
  }

  render() {
    return (
      <div>
      <button onClick = {this.request}>SET STATE</button>
      <h1>
        {this.state.data.map((convo, i) => <ConversationListItem key = {i} data = {convo} />)}
      </h1>
      </div>
    );
  }

}


export default ConversationList;
