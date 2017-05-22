import React from 'react';

import AddConversation from './AddConversation.jsx'
import ConversationList from './ConversationList.jsx'

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

     this.state = {
       addingConversation: false,
     }

     this.toggleVisibility = this.toggleVisibility.bind(this);
  }


  toggleVisibility() {
     this.setState({ "addingConversation": !this.state.addingConversation })
  }


  render() {

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
      "paddingLeft": "2%"
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
                        <Button bsStyle="info" onClick={this.toggleVisibility}>
                        Start a new thread
                        </Button>
                     </div>
                  </h2>
               </Row>
               <AddConversation url={this.props.url} port={this.props.port} show={this.state.addingConversation} user={this.props.user} username={this.props.username} uuid={this.props.uuid}/>
            </ListGroupItem>
            <div style={divStyle}>
               <ConversationList url={this.props.url} port={this.props.port}/>
            </div>
         </div>
         </Col>
      </div>
    );
  }


}


export default Main;
