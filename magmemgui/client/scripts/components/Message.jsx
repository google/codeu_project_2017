// Container for messages

import React from 'react';

import Panel from 'react-bootstrap/lib/Panel';
import Col from 'react-bootstrap/lib/Col';
import Row from 'react-bootstrap/lib/Row';
import Label from 'react-bootstrap/lib/Label';

class Message extends React.Component {


  constructor(props) {
     super(props);

     // Again, bind this function so it can set state.
  }

  getRGB(uuid) {
    try {
    var arr = uuid.split(".");
    var elem = (arr[arr.length-1]);
    var red = elem % 256;
    var green = Math.floor(elem/1000 % 256);
    var blue =  Math.floor(elem/1000000 % 256);
    return "rgb(" + red + ", " + green + ", " + blue + ")";
  } catch(e){
    return "rgb(0,0,0)";
  }
  }


   render() {

     // Styling struct
     var messageStyle = {
       "fontFamily": "Space Mono",
       "backgroundColor": "#DCDCDC",
       "fontSize": "13pt",
       "float": "left",
       "width": "70%",
       "marginBottom": "-1px",
       "marginTop": "1%",
       "border": 0
     }

     var myStyle = {
       "fontFamily": "Space Mono",
       "backgroundColor": "magenta",
       "color": "white",
       "float": "right",
       "fontSize": "13pt",
       "width": "70%",
       "marginRight": "4%",
       "marginTop": "1%",
       "marginBottom": "-3px"
     }


     var userStyle = {
       "marginLeft": this.props.mine ? "0%": "2%",
       "marginRight": this.props.mine ? "2%": "0%",
       "marginTop": "-3px",
       "float": this.props.mine ? "right ": "left",
       "backgroundColor": this.props.message.user!=undefined ? this.getRGB(this.props.message.user.id.uuid) : ""
     }

     var strangerSpike = {
       "float": "left",
       "marginLeft": "5%"
     }

     var mySpike = {
       "float": "right",
       "marginRight": "5%"
     }

      return (
        <span>
        <Row>
        <Panel style={this.props.mine ? myStyle : messageStyle}>
        {this.props.message.content}
        </Panel>
      </Row>
      <Row>
      <span style={this.props.mine ? mySpike : strangerSpike} className={this.props.mine ? "magentaspike" : "grayspike"}/>
    </Row>
    <Row>
    <span style={userStyle}>
      <Label>
      <span>{this.props.message.user!=undefined ? (this.props.message.user.name + " (" + this.props.message.user.id.uuid + ")") : "Loading..."}</span>
    </Label></span>
  </Row>
    </span>
      );
   }
}


export default Message;
