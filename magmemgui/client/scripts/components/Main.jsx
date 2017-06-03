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
import Image from 'react-bootstrap/lib/Image';
import Label from 'react-bootstrap/lib/Label';

class Main extends React.Component {


  constructor(props) {
     super(props);

     // State declaration
     this.state = {
       "addingConversation": false,
       "selection": "",
       "icon": ""
     }

     // Again, bind this function so it can set state.
     this.toggleVisibility = this.toggleVisibility.bind(this);
     this.threadSelect = this.threadSelect.bind(this);
  }

  componentDidMount() {
    this.setState({"icon":this.randomIntFromInterval(24, 52)});
  }

  /* Flips the state for addingConversation state. This propagates to
  AddConversation, either enabling or disabling the textbox.*/
  toggleVisibility() {
     this.setState({ "addingConversation": !this.state.addingConversation })
  }

  threadSelect(uuid) {
    this.setState({ "selection": uuid, "addingConversation": false });
  }

  randomIntFromInterval(min, max) {
    return Math.floor(Math.random()*(max-min+1)+min);
  }


  render() {

    console.log(this.state.imageUrl);

    /* The following structs are all for styling.*/
    var headerStyle = {
      "fontFamily": "Space Mono",
      "color": "white",
      "fontWeight": "bold",
      "paddingBottom": ".5%"
    }

    var divStyleStatic = {
      "height": "71vh",
      "overflowY": "hidden",
      "backgroundColor": "white",
      "borderRadius": "25px"
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

    var smallStyle = {
      "fontFamily": "Space Mono",
      "float": "left",
      "paddingLeft": "5%",
      "fontSize": "12pt"
    }

    var nameStyle = {
      "fontFamily": "Space Mono",
      "float": "left",
      "paddingLeft": "5%",
      "marginTop": "-1px"
    }

    var activeField =
    <span>
    <Col xs={8}>
      <MessagePanel url={this.props.url} port={this.props.port} selection={this.state.selection} user={this.props.user}/>
    <Row>
        <MessageBox url={this.props.url} port={this.props.port} selection={this.state.selection} user={this.props.user}/>
    </Row>
  </Col>
  </span>

    return (
      <div>
         <h1 style={headerStyle} id="myHeader">Magenta Messenger</h1>
         <Col xs={4} xsOffset={this.state.selection == "" ? 4 : 0}>
         <div style={divStyleStatic}>
            <ListGroupItem style={pinkHeader}>
               <Row>
                  <h2>
                     <div style={fontStyle}>Threads</div>
                     <div style={buttonStyle}>
                        <Button onClick={this.toggleVisibility}>
                        {!this.state.addingConversation ? "Start a new thread" : "Cancel"}
                        </Button>
                     </div>
                  </h2>
               </Row>
               <AddConversation url={this.props.url} port={this.props.port} toggle={this.toggleVisibility} show={this.state.addingConversation} user={this.props.user}/>
            </ListGroupItem>
            <div style={divStyle}>
               <ConversationList url={this.props.url} port={this.props.port} threadSelect={this.threadSelect}/>
            </div>
         </div>

       <Row>
         <Panel style={{"marginTop":"40px","borderRadius": "25px", "marginLeft": "11px", "width": "96.2%", "height": "13vh"}}>
           <Col xs={4}>
           <Image style={{"maxWidth": "65%", "maxHeight": "65%"}} src={this.props.imageUrl || "http://bugs.bluej.org/secure/useravatar?size=xsmall&avatarId=103" + this.state.icon}  circle />
           </Col>
           <Col xs={8}>
             <Row><h3 style={smallStyle}>Logged in as</h3></Row>
             <Row><h2 style={nameStyle}>{this.props.user.name + " "}<Label>{this.props.user.id.uuid}</Label></h2></Row>
           </Col>
         </Panel>
       </Row>
     </Col>


         {this.state.selection == "" ? "" : activeField}
      </div>
    );
  }


}


export default Main;
