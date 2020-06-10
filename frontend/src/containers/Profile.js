import React from "react";
import { connect } from "react-redux";
import { Redirect } from "react-router-dom";
import Hoc from "../hoc/hoc";

class Profile extends React.Component {
  returnUser(){
    return(this.props.username);
  };    
  render() {
    if (this.props.token === null) {
      return <Redirect to="/" />;
    }
    return (
      <div className="contact-profile">
        {this.returnUser() !== null ? (
          <Hoc>
            <img src="https://windowstips.ru/wp-content/uploads/2016/06/Icon-User.png" alt="" />
            <p>{this.returnUser()}</p>
          </Hoc>
        ) : null}
      </div>
    );
  }
}

const mapStateToProps = state => {
  return {
    username: state.auth.username,
    token: state.auth.token
  };
};

export default connect(mapStateToProps)(Profile);