import React from 'react';
import $ from 'jquery';

import Login from './Login.jsx'


class App extends React.Component {
   constructor(props) {
      super(props);

      /*this.state = {
        url: "http://130.211.140.178",
        port: "10110"
      }*/

      this.state = {
        url: "http://localhost",
        port: "8000",
      }
   }

   render() {
      return (
          <Login url={this.state.url} port={this.state.port}/>
      );
   }
}


export default App;
