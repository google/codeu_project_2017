// View for a ListGroupItem, literally a conversation name.

import React from 'react';
import ListGroupItem from 'react-bootstrap/lib/ListGroupItem';

class ConversationListItem extends React.Component {
   render() {

     // Styling struct
     var myStyle = {
       "fontFamily": "Space Mono",
       "backgroundColor": "magenta"
     }

      // Use props in our display.
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
