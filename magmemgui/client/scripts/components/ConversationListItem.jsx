import React from 'react';


class ConversationListItem extends React.Component {
   render() {
      return (
         <div>
            <h3>{this.props.data.title}</h3>
            <h5>Thread started {this.props.data.creation.date}</h5>
         </div>
      );
   }
}


export default ConversationListItem;
