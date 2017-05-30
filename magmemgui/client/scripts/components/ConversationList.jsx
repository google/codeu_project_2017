/* This is the view for the available conversations. It is called by the
Main component and makes ajax calls every second to see if there are any new
conversations. */

import React from 'react';
import $ from 'jquery';

import ConversationListItem from "./ConversationListItem.jsx"
import ListGroup from 'react-bootstrap/lib/ListGroup';


class ConversationList extends React.Component {

  constructor(props) {
    super(props);

    /* State declaration. data will contain our conversation items, and
    lastClick keeps track of the last time we requested new data. This is done
    so that we can only request to update with conversations created between
    the last call and the current time. This cuts down on memory usage and
    search time. */
    this.state = {
      data: [],
      lastClick: 1,
      selection: ""
    }

    // Again, bind a setState function.
    this.request = this.request.bind(this);
    this.setSelection = this.setSelection.bind(this);
  }

  /* This is called when the parent initializes this child.
  An update request is made. */
  componentWillMount() {
    this.request();
  }

  /* If the mounting was a success, then call an update every second. */
  componentDidMount() {
    window.setInterval(function () {
      this.request();
    }.bind(this), 1000);
  }

  /* The update function. Updates our data state with new conversations. */
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

    this.setState({"lastClick": clickTime});

    $.ajax(settings).done(function (response) {
      this.setState({ "data": JSON.parse(response).reverse().concat(this.state.data) });
    }.bind(this));

  }

  setSelection(uuid) {
    this.setState({ "selection": uuid });
    this.props.threadSelect(uuid);
  }

  /* This syntactic sugar is essentially a "foreach". Draw a
  ConversationListItem for each item in data. */
  render() {
    return (
      <ListGroup>
        {this.state.data.map((convo, i) => <ConversationListItem selection={this.state.selection} setSelection={this.setSelection} uuid={convo.id.uuid} key = {i} data = {convo} />)}
      </ListGroup>
    );
  }

}


export default ConversationList;
