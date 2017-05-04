import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';

class App extends React.Component {
  constructor(props) {
    super(props);

 }
 render() {
    return(
    <h1>Hello World!</h1>
    )
 }
}

ReactDOM.render(<App />, document.getElementById('app'));

