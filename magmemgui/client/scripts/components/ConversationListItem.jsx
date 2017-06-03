// View for a ListGroupItem, literally a conversation name.

import React from 'react';
import ListGroupItem from 'react-bootstrap/lib/ListGroupItem';

class ConversationListItem extends React.Component {


  constructor(props) {
     super(props);

     this.handleSelect = this.handleSelect.bind(this);
  }

   componentDidMount() {
     console.log(this.state)
   }

   handleSelect() {
     this.props.setSelection(this.props.uuid);
   }

   render() {

     // Styling struct
     var myStyle = {
       "fontFamily": "Space Mono",
       "backgroundColor": "magenta"
     }

     var selectStyle = {
       "color": "magenta",
       "backgroundColor": "#FFCCFF"
     }

     var noStyle = {
     }

      // Use props in our display.
      return (
        <span style={myStyle}>
         <ListGroupItem header={this.props.data.title} onClick={this.handleSelect} style={this.props.selection != this.props.uuid ? noStyle : selectStyle}>
            Thread started {this.props.data.creation.date}
         </ListGroupItem>
         </span>
      );
   }
}


export default ConversationListItem;
