/* The popdown dialog that shows when a user selects 'Create a new thread.'
Makes an ajax POST request to create a new conversation server-side.*/

import React from 'react';
import $ from 'jquery';

import Form from 'react-bootstrap/lib/FormControl';
import Button from 'react-bootstrap/lib/Button';
import Col from 'react-bootstrap/lib/Col';
import Row from 'react-bootstrap/lib/Row';
import Panel from 'react-bootstrap/lib/Panel';
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import FormControl from 'react-bootstrap/lib/FormControl';
import InputGroup from 'react-bootstrap/lib/InputGroup';

class AddConversation extends React.Component {


  constructor(props) {
     super(props);

     // State declaration. Tracks textbox value and current visibility of dialog
     this.state = {
       "value": "",
       "show": false
     }

     // Bindings so we can setState
     this.handleChange = this.handleChange.bind(this);
     this.onAdd = this.onAdd.bind(this);
  }

  /* Called every time a new character is entered in the textbox. */
  handleChange(e) {
    this.setState({ "value": e.target.value });
  }

  /* Called when user clicks the create button. Hides the dialog and requests.*/
  onAdd(e) {
    e.preventDefault();
    this.request();
  }

  /* The post request that creates a new conversation. It also clears the
  existing fields. */
  request() {
    var settings = {
      "async": true,
      "crossDomain": true,
      "url": this.props.url + ":" + this.props.port,
      "method": "POST",
      "contentType": "json",
      "headers": {
        "type": "NEW_CONVERSATION",
        "owner": this.props.user.id.uuid,
      },
      "data": this.state.value,
      "error": function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    }

    $.ajax(settings).done(function (response) {
      this.setState({ "value": "" });
      this.props.toggle();
    }.bind(this));
  }

  render() {

     /* These are just styling structs. */
     var loginStyle = {
       "fontFamily": "Space Mono",
     }

     var formStyle = {
       "marginBottom": "0%",
       "paddingBottom": "0%"
     }

     var addConvo =
     <Row>
    <Col xs={12} style={loginStyle}>
     <form style={formStyle} onSubmit={this.onAdd}>
       <FormGroup>
         <InputGroup >
           <FormControl type="text" value={this.state.value} onChange={this.handleChange} />
           <InputGroup.Button>
             <Button type="submit" disabled={this.state.value.length < 3}>
             Create
           </Button>
           </InputGroup.Button>
         </InputGroup>
       </FormGroup>
      </form>
    </Col>
  </Row>

      return (
        /* Visibility of dialog is determined by inner state. */
         <span>{(this.props.show) ? addConvo : ""}</span>
      );
   }
}


export default AddConversation;
