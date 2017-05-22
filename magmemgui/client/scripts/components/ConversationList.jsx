import React from 'react';
import ConversationListItem from "./ConversationListItem.jsx"
import $ from 'jquery';
import ListGroup from 'react-bootstrap/lib/ListGroup';


class ConversationList extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      data: [],
      lastClick: 1
    }

    this.request = this.request.bind(this);
  }

  componentWillMount() {
    this.request();
  }

  componentDidMount() {
    window.setInterval(function () {
      this.request();
    }.bind(this), 1000);
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
        "type": "TIMED_CONVERSATIONS",
        "to": clickTime,
        "from": this.state.lastClick
      },
      "error": function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    }

    $.ajax(settings).done(function (response) {
      this.setState({ "data": JSON.parse(response).reverse().concat(this.state.data), "lastClick": clickTime });
    }.bind(this));

  }

  render() {
    return (
      <ListGroup>
        {this.state.data.map((convo, i) => <ConversationListItem key = {i} data = {convo} />)}
      </ListGroup>
    );
  }

}


export default ConversationList;
