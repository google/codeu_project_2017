import React from 'react';
import FormControl from 'react-bootstrap/lib/FormControl';
import FormGroup from 'react-bootstrap/lib/FormGroup';
import InputGroup from 'react-bootstrap/lib/InputGroup';
import Form from 'react-bootstrap/lib/FormControl';
import Button from 'react-bootstrap/lib/Button';

class AddConversation extends React.Component {


  constructor(props) {
     super(props);

     this.state = {
       submittable: false,
       innerShow: false
     }

     this.handleChange = this.handleChange.bind(this);
     this.clicked = this.clicked.bind(this);

  }


  handleChange(e) {
    this.setState({ "submittable": e.target.value.length > 1 });
  }

  clicked() {
    this.setState({ "innerShow": false, "submittable": false });
  }

  componentWillReceiveProps(nextProps) {
    if (this.state.innerShow != nextProps.show) {
      this.setState({ "innerShow": nextProps.show, "submittable": false });
    } else {
      this.setState({ "innerShow": !nextProps.show, "submittable": false });
    }
  }


  // ADD REQUEST


  render() {

     var myStyle = {
       "fontFamily": "Space Mono",
       "paddingTop": "2%"
     }

      return (
        <div style={myStyle}>
         {this.state.innerShow ?
              <FormGroup>
                 <InputGroup>
                   <FormControl type="text" value={this.state.value} placeholder="Name your thread." onChange={this.handleChange}/>
                   <InputGroup.Button>
                     <Button disabled={!this.state.submittable} onClick={this.clicked}>Submit</Button>
                   </InputGroup.Button>
                 </InputGroup>
               </FormGroup>
            : ""}
         </div>
      );
   }
}


export default AddConversation;
