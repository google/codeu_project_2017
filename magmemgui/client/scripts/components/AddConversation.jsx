import React from 'react';
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

     this.state = {
       "value": "",
       "show": false
     }

     this.handleChange = this.handleChange.bind(this);
     this.onAdd = this.onAdd.bind(this);
  }

  componentWillReceiveProps(nextProps) {
    this.setState( {"show": !this.state.show} );
  }

  handleChange(e) {
    this.setState({ "value": e.target.value });
  }

  onAdd(e) {
    this.setState( {"show": false} );
    alert(this.props.username);
    e.preventDefault();
    this.request();
  }

  render() {

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
         <span>{(this.state.show) ? addConvo : ""}</span>
      );
   }
}


export default AddConversation;
