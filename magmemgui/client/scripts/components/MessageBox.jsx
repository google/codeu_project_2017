import React from 'react';

import Button from 'react-bootstrap/lib/Button';
import Col from 'react-bootstrap/lib/Col';
import Row from 'react-bootstrap/lib/Row';
import Panel from 'react-bootstrap/lib/Panel';
import {Form, FormGroup, FormControl} from 'react-bootstrap';



class MessageBox extends React.Component {

	constructor(props) {
		super(props)
	}

	render() {

  	var buttonStyle = {
      "fontFamily": "Space Mono",
      "float": "right",
      "paddingRight": "2%",
			"marginTop": "-60px"
    }

		var msgBoxStyle = {
      "height": "15vh",
			"fontSize": "18pt",
			"resize": "none"
    }

		var formStyle = {
      "marginRight": "-15px",
      "paddingRight": "-15px",
			"fontFamily": "Space Mono"
    }


	return (
		<div>
			<Form style={formStyle}>
		    <FormGroup>
		    	<FormControl componentClass="textarea" style={msgBoxStyle} placeholder="Type a message here" />
		    </FormGroup>
         <div style={buttonStyle}>
                <Button bsStyle="info" onClick={this.toggleVisibility}>
                Send
                </Button>
         </div>
			 </Form>
         </div>

		)
	}

}

export default MessageBox;
