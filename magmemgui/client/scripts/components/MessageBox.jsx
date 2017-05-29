import React from 'react';
import $ from 'jquery';

import Button from 'react-bootstrap/lib/Button';
import Col from 'react-bootstrap/lib/Col';
import Row from 'react-bootstrap/lib/Row';
import Panel from 'react-bootstrap/lib/Panel';
import {Form, FormGroup, FormControl} from 'react-bootstrap';



class MessageBox extends React.Component {

	constructor(props) {
		super(props)

		this.state = {
			"value": "",
			"disabled": false
		}

		this.submitAction = this.submitAction.bind(this);
		this.handleChange = this.handleChange.bind(this);
	}

	submitAction(e) {
		if (this.state.value != "") {
			this.request();
	  }
		e.preventDefault();
	}

	handleChange(e) {
		if (e.target.value[e.target.value.length - 1] == "\n" && e.key != "Shift") {
			this.submitAction(e);
		} else {
			this.setState({ "value": e.target.value });
	  }
	}

	request() {
		this.setState( {"disabled": true} );
    var settings = {
      "async": true,
      "crossDomain": true,
      "url": this.props.url + ":" + this.props.port,
      "method": "POST",
      "contentType": "json",
      "headers": {
        "type": "NEW_MESSAGE",
        "author": this.props.user.id.uuid,
        "conversation": this.props.selection
      },
      "data": this.state.value,
      "error": function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    }

    $.ajax(settings).done(function (response) {
      this.setState({ "value": "", "disabled": false });
    }.bind(this));
  }

	render() {

  	var buttonStyle = {
      "fontFamily": "Space Mono",
      "float": "right",
      "paddingRight": "2%",
			"marginTop": "-60px",
    }

		var msgBoxStyle = {
      "height": "13vh",
			"fontSize": "18pt",
			"resize": "none"
    }

		var formStyle = {
      "marginRight": "-15px",
      "paddingRight": "-15px",
			"fontFamily": "Space Mono",
			"width": "99.5%",
			"marginLeft": "13px"
    }


	return (
		<div>
			<Form style={formStyle} onSubmit={this.submitAction}>
		    <FormGroup>
		    	<FormControl componentClass="textarea" value={this.state.value} onChange={this.handleChange} style={msgBoxStyle} placeholder="Type a message here" />
		    </FormGroup>
         <div style={buttonStyle}>
                <Button bsStyle="info" disabled={this.state.disabled} type="submit">
                Send
                </Button>
         </div>
			 </Form>
         </div>

		)
	}

}

export default MessageBox;
