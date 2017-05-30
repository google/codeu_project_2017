/* Manages user registration and login.*/

import React from 'react';
import $ from 'jquery';

import Main from './Main.jsx'

import Jumbotron from 'react-bootstrap/lib/Jumbotron';
import Button from 'react-bootstrap/lib/Button';
import Form from 'react-bootstrap/lib/Form';
import Col from 'react-bootstrap/lib/Col';
import Row from 'react-bootstrap/lib/Row';
import Panel from 'react-bootstrap/lib/Panel';
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import FormControl from 'react-bootstrap/lib/FormControl';

class Login extends React.Component {


  constructor(props) {
     super(props);

     // Our initial states
     this.state = {
       "register": "",
       "username": "",
       "password": "",
       "loggedIn": false,
       "user:": "",
       "submitting": false
     }

     /* We bind these functions to this component's mounting so that they are
     allowed to setState. Quite simply, if a function must change the component
     state, we must bind it first here in the constructor. */
     this.onRegister = this.onRegister.bind(this);
     this.onLogin = this.onLogin.bind(this);
     this.handleRegisterChange = this.handleRegisterChange.bind(this);
     this.handleUsernameChange = this.handleUsernameChange.bind(this);
     this.handlePasswordChange = this.handlePasswordChange.bind(this);
  }

  /* Captures the submission of a new user registration and triggers a
  new user request.*/
  onRegister(e) {
    this.setState( {"submitting": true} );
    e.preventDefault();
    this.newUserRequest();
  }

  /* Captures the submission of a user login and triggers a login request. */
  onLogin(e) {
    alert(this.state.username);
    alert(this.state.password);
    e.preventDefault();
  }

  /* Called each time a character changes in the registration text box.
  This even handler keeps our inner state value updated. */
  handleRegisterChange(e) {
    this.setState( {"register": e.target.value} );
  }

  /* See above */
  handleUsernameChange(e) {
    this.setState( {"username": e.target.value} );
  }

  /* See above */
  handlePasswordChange(e) {
    this.setState( {"password": e.target.value} );
  }

  /* Request function. Triggers a single ajax post request for making a new
  user. Called when user clicks on the Register button. On success, the new
  user is created serverside and the client is logged in to the main page. */
  newUserRequest() {
    var settings = {
      "async": true,
      "crossDomain": true,
      "url": this.props.url + ":" + this.props.port,
      "method": "POST",
      "contentType": "json",
      "headers": {
        "type": "NEW_USER",
      },
      "data": this.state.register,
      "success": function(response) {
        var struct = JSON.parse(response);
        this.setState({ "loggedIn": true, "user": struct, "submitting": false});
      }.bind(this),
      "error": function(xhr, status, err) {
        this.setState({"submitting": false});
      }.bind(this)
    }

    $.ajax(settings).done();
  }

  render() {

    /* Following structs are just style declarations for the html.*/
    var headerStyle = {
      "fontFamily": "Space Mono",
      "color": "white",
      "fontWeight": "bold"
    }

    var jumboStyle = {
      "backgroundColor": "magenta"
    }

    var loginStyle= {
      "borderColor": "white",
      "color": "white",
      "backgroundColor": "magenta"
    }

    var loginPage =
      <div style={headerStyle}>
         <Jumbotron style = {jumboStyle}>
            <h1>Magenta Messenger</h1>
            <p>Bold and brash</p>
            <p><Button href="https://github.com/petosa/codeu_project_2017" target="_blank">Learn more</Button></p>
         </Jumbotron>
         <Col md={4} mdOffset={4}>
         <Panel header="First Time" style={loginStyle}>
            <Form horizontal onSubmit={this.onRegister}>
               <Row>
                  <FormGroup controlId="formHorizontalUser">
                     <Col componentClass={ControlLabel} mdOffset={1} md={2}>
                     Register
                     </Col>
                     <Col mdOffset={2} md={7}>
                     <FormControl style={loginStyle} value={this.state.register} onChange={this.handleRegisterChange} type="search" />
                     </Col>
                  </FormGroup>
               </Row>
               <FormGroup>
                  <Col md={12}>
                  <Button type="submit" disabled={this.state.submitting}>
                  Sign up
                  </Button>
                  </Col>
               </FormGroup>
            </Form>
         </Panel>
        {/* <Panel header="Returning User" style={loginStyle}>
            <Form horizontal onSubmit={this.onLogin}>
               <Row>
                  <FormGroup controlId="formHorizontalUser">
                     <Col componentClass={ControlLabel} mdOffset={1} md={2}>
                     Username
                     </Col>
                     <Col mdOffset={2} md={7}>
                     <FormControl style={loginStyle} value={this.state.username} onChange={this.handleUsernameChange} type="search" />
                     </Col>
                  </FormGroup>
               </Row>
               <Row>
                  <FormGroup controlId="formHorizontalPassword">
                     <Col componentClass={ControlLabel} md={2} smOffset={1}>
                     Password
                     </Col>
                     <Col mdOffset={2} md={7}>
                     <FormControl style={loginStyle} value={this.state.password} onChange={this.handlePasswordChange} type="password" />
                     </Col>
                  </FormGroup>
               </Row>
               <FormGroup>
                  <Col md={12}>
                  <Button type="submit" disabled={this.state.submitting}>
                  Log in
                  </Button>
                  </Col>
               </FormGroup>
            </Form>
         </Panel> */}
         </Col>
      </div>

    var mainPage = <Main url={this.props.url} port={this.props.port} user={this.state.user}/>

    /* Depending on login state, either display the main page or the current
    login page.*/
    return (
      <span>
        {this.state.loggedIn ? mainPage : loginPage}
      </span>
    );
  }


}


export default Login;
