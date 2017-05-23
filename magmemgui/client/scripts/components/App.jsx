// The root component of the entire app.

import React from 'react';
import $ from 'jquery';

import Login from './Login.jsx'

class App extends React.Component {
   constructor(props) {
      super(props);

      /* Switch between these two state declarations depending on whether
      you are running a local test or a release. Meaning you must literally
      uncomment one and comment the other. */

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
     /* The first component that will always follow the App component is the
     login component. We pass the declared URl and PORT as props to all
     descendents. */
      return (
          <Login url={this.state.url} port={this.state.port}/>
      );
   }
}


export default App;
