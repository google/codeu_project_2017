import React from 'react';
import ListGroupItem from 'react-bootstrap/lib/ListGroupItem';

class ConversationListItem extends React.Component {
   render() {

     var myStyle = {
       "fontFamily": "Space Mono",
       "backgroundColor": "magenta"
     }

      return (
        <span style={myStyle}>
         <ListGroupItem header={this.props.data.title} href="#">
            Thread started {this.props.data.creation.date}
         </ListGroupItem>
         </span>
      );
   }
}


export default ConversationListItem;
