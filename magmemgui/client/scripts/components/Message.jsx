// Container for messages

import React from 'react';

import Panel from 'react-bootstrap/lib/Panel';
import Row from 'react-bootstrap/lib/Row';

class Message extends React.Component {

   render() {

     // Styling struct
     var messageStyle = {
       "fontFamily": "Space Mono",
       "backgroundColor": "#DCDCDC",
       "fontSize": "13pt",
       "float": "left",
       "width": "70%",
       "marginLeft": "2%",
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

     var strangerSpike = {
       "float": "left",
       "marginLeft": "3%"
     }
     var mySpike = {
       "float": "right",
       "marginRight": "5%"
     }

      return (
        <span>
        <Row>
        <Panel style={this.props.mine ? myStyle : messageStyle}>
        {this.props.message}
        </Panel>
      </Row>
      <Row>
      <div style={this.props.mine ? mySpike : strangerSpike} className={this.props.mine ? "magentaspike" : "grayspike"}></div>
    </Row>
    </span>
      );
   }
}


export default Message;
