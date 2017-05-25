/* Once making it past the login screen, the user can view the main page.
This is the container component for the rest of the app. */

import React from 'react';

import AddConversation from './AddConversation.jsx'
import ConversationList from './ConversationList.jsx'
import MessagePanel from './MessagePanel.jsx'
import MessageBox from './MessageBox.jsx'

import Jumbotron from 'react-bootstrap/lib/Jumbotron';
import ListGroupItem from 'react-bootstrap/lib/ListGroupItem';
import Button from 'react-bootstrap/lib/Button';
import Form from 'react-bootstrap/lib/Form';
import Col from 'react-bootstrap/lib/Col';
import Row from 'react-bootstrap/lib/Row';
import Panel from 'react-bootstrap/lib/Panel';

class Main extends React.Component {


  constructor(props) {
     super(props);

     // State declaration
     this.state = {
       "addingConversation": false,
       "selection": ""
     }

     // Again, bind this function so it can set state.
     this.toggleVisibility = this.toggleVisibility.bind(this);
     this.threadSelect = this.threadSelect.bind(this);
  }


  /* Flips the state for addingConversation state. This propagates to
  AddConversation, either enabling or disabling the textbox.*/
  toggleVisibility() {
     this.setState({ "addingConversation": !this.state.addingConversation })
  }

  threadSelect(uuid) {
    this.setState({ "selection": uuid });
  }


  render() {

    /* The following structs are all for styling.*/
    var headerStyle = {
      "fontFamily": "Space Mono",
      "color": "white",
      "fontWeight": "bold"
    }

    var divStyleStatic = {
      "height": "71vh",
      "overflowY": "hidden"
    }

    var divStyle = {
      "height": "65vh",
      "overflowY": "auto"
    }

    var pinkHeader = {
      "backgroundColor": "#ff66ff",
      "borderColor": "#ff66ff",
      "color": "white"
    }

    var buttonStyle = {
      "fontFamily": "Space Mono",
      "float": "right",
      "paddingRight": "2%",
    }

    var fontStyle = {
      "fontFamily": "Space Mono",
      "float": "left",
      "paddingLeft": "5%"
    }

    return (
      <div>
         <h1 style={headerStyle} id="myHeader">Magenta Messenger</h1>
         <Col xs={4}>
         <div style={divStyleStatic}>
            <ListGroupItem style={pinkHeader}>
               <Row>
                  <h2>
                     <div style={fontStyle}>Threads</div>
                     <div style={buttonStyle}>
                        <Button onClick={this.toggleVisibility}>
                        Start a new thread
                        </Button>
                     </div>
                  </h2>
               </Row>
               <AddConversation url={this.props.url} port={this.props.port} show={this.state.addingConversation} user={this.props.user}/>
            </ListGroupItem>
            <div style={divStyle}>
               <ConversationList url={this.props.url} port={this.props.port} threadSelect={this.threadSelect}/>
            </div>
         </div>
         </Col>
         <Col xs={8}>
           <MessagePanel url={this.props.url} port={this.props.port} selection={this.state.selection} user={this.props.user}/>
         </Col>
         <Row>
           <Col xs={8} xsOffset={4}>
             <MessageBox url={this.props.url} port={this.props.port} selection={this.state.selection} user={this.props.user}/>
           </Col>
         </Row>
      </div>
    );
  }


}


export default Main;
