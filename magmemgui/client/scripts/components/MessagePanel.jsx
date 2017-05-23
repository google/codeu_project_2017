// Container for messages

import React from 'react';

import Message from './Message.jsx'

import Panel from 'react-bootstrap/lib/Panel';
import Col from 'react-bootstrap/lib/Col';

class MessagePanel extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      data: [
        {
          "mine":true,
          "message":"A message from me"
        },
        {
          "mine":false,
          "message":"A message from a stranger"
        },
        {
          "mine":false,
          "message":"A message from a stranger"
        },
        {
          "mine":true,
          "message":"A message from a stranger"
        },
        {
          "mine":true,
          "message":"A message from a stranger"
        },
        {
          "mine":true,
          "message":"A message from a stranger"
        },
        {
          "mine":false,
          "message":"A message from a stranger"
        },
        {
          "mine":false,
          "message":"A message from a stranger"
        },
        {
          "mine":false,
          "message":"A message from a stranger"
        },
        {
          "mine":false,
          "message":"A message from a stranger"
        },
        {
          "mine":false,
          "message":"A message from a stranger"
        },
        {
          "mine":false,
          "message":"A message from a stranger"
        },
        {
          "mine":false,
          "message":"A message from a stranger"
        }
      ]
    }

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
        <div style={fieldStyle}>
        {this.state.data.map((msg, i) => <Message key={i} mine={msg.mine} message={msg.message} />)}
      </div>
        </Panel>
      );
   }
}


export default MessagePanel;
