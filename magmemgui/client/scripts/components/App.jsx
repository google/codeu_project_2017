import React from 'react';
import $ from 'jquery';

class App extends React.Component {
   constructor(props) {
      super(props);

      this.state = {
        data: "temp"
      }

      this.setStateHandler = this.setStateHandler.bind(this);

   }

   request() {
    var settings = {
      "async": true,
      "crossDomain": false,
      "url": "http://localhost:8000/",
      "method": "GET",
      "contentType": "json",
      "headers": {
        "type": "ALL_USERS",
      }
    }

    $.ajax(settings).done(function (response) {
      console.log(response);
    });
   }

   setStateHandler() {
      this.setState({ data: "nvm"});
   };

   render() {
      return (
         <div>
            <button onClick = {this.request}>SET STATE</button>
            <h3> {this.state.data} </h3>
         </div>
      );
   }
}


export default App;
