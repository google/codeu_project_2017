import React from 'react';
import $ from 'jquery';
import ConversationList from './ConversationList.jsx'

class App extends React.Component {
   constructor(props) {
      super(props);

      this.state = {
        url: "http://localhost",
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
