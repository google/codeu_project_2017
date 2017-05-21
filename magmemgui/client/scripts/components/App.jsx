import React from 'react';
import $ from 'jquery';
import ConversationList from './ConversationList.jsx'
import AddConversation from './AddConversation.jsx'
import Col from 'react-bootstrap/lib/Col';
import Row from 'react-bootstrap/lib/Row';
import ListGroupItem from 'react-bootstrap/lib/ListGroupItem';
import Button from 'react-bootstrap/lib/Button';
import ButtonGroup from 'react-bootstrap/lib/ButtonGroup';

class App extends React.Component {
   constructor(props) {
      super(props);

      /*this.state = {
        url: "http://130.211.140.178",
        port: "10110"
      }*/

      this.state = {
        url: "http://localhost",
        port: "8000",
        addingConversation: false,
        user: ""
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
              <h2><div style={fontStyle}>Threads</div>
              <div style={buttonStyle}>
                    <Button bsStyle="info" onClick={this.toggleVisibility}>
                    Start a new thread
                    </Button>
                    </div>
              </h2>
              </Row>
              <AddConversation show={this.state.addingConversation}/>
              </ListGroupItem>
              <div style={divStyle}>
                 <ConversationList url={this.state.url} port={this.state.port}/>
              </div>
           </div>
           </Col>
        </div>
      );
   }
}


export default App;
