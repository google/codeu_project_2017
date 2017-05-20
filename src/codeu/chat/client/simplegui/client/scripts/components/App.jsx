import React from 'react';
import $ from 'jquery';
import ConversationList from './ConversationList.jsx'

class App extends React.Component {
   constructor(props) {
      super(props);

      this.state = {
        url: "http://130.211.140.178",
        port: "10110"
      }

   }

   render() {
      return (
         <div>
            <h1>Magenta Messenger</h1>
            <ConversationList url={this.state.url} port={this.state.port}/>
         </div>
      );
   }
}


export default App;
