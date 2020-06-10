import React from "react";
import { Spin, Icon } from "antd";
import { connect } from "react-redux";
import * as actions from "../store/actions/auth";
import * as navActions from "../store/actions/nav";
import * as messageActions from "../store/actions/message";
import Contact from "../components/Contact";
import * as pro from "../containers/Profile";

const antIcon = <Icon type="loading" style={{ fontSize: 24 }} spin />;

class Sidepanel extends React.Component {
  state = {
    loginForm: true
  };

  renderProfile(){ 
    let name = this.props.username;
    console.log(name);
    if(name === null &&
        name == undefined)
    {
        return("Chat Application");
    }
    else
    {
        return(name);
    }
  };

  renderActiveChats()
  {
    let auth = this.props.isAuthenticated;
    console.log(this.props.isAuthenticated);
    if(auth === true)
    {
        let activeChats = this.props.chats.map(c => {
            
            console.log("=====================");
            console.log(c.chatName);
            console.log(this.props.username);
            console.log(c.participants);
            

            return (
              <Contact
                key={c.id}
                name={c.chatName}
                picURL="data:image/png;base64,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"
                status="online"
                chatURL={`/${c.id}`}
              />
            );
          });
        return(activeChats);
    }
    else
    {
        return(null);
    }
  };

  waitForAuthDetails() {
    const component = this;
    setTimeout(function() {
      if (
        component.props.token !== null &&
        component.props.token !== undefined
      ) {
        component.props.getUserChats(
          component.props.username,
          component.props.token
        );
        return;
      } else {
        console.log("waiting for authentication details...");
        component.waitForAuthDetails();
      }
    }, 100);
  }

  componentDidMount() {
    this.waitForAuthDetails();
  }

  openAddChatPopup() {
    this.props.addChat();
  }

  changeForm = () => {
    this.setState({ loginForm: !this.state.loginForm });
  };

  authenticate = e => {
    e.preventDefault();
    if (this.state.loginForm) {
      this.props.login(e.target.username.value, e.target.password.value);
    } else {
      this.props.signup(
        e.target.username.value,
        e.target.email.value,
        e.target.password.value,
        e.target.password2.value
      );
    }
  };

  
  render() {

    return (
      <div id="sidepanel">
        <div id="profile">
          <div className="wrap">
            <img
              id="profile-img"
              src="data:image/jpeg;base64,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"
              className="online"
              alt=""
            />
            <p>{this.renderProfile()}</p>
            <i
              className="fa fa-chevron-down expand-button"
              aria-hidden="true"
            />
            <div id="status-options">
              <ul>
                <li id="status-online" className="active">
                  <span className="status-circle" /> <p>Online</p>
                </li>
                <li id="status-away">
                  <span className="status-circle" /> <p>Away</p>
                </li>
                <li id="status-busy">
                  <span className="status-circle" /> <p>Busy</p>
                </li>
                <li id="status-offline">
                  <span className="status-circle" /> <p>Offline</p>
                </li>
              </ul>
            </div>
            <div id="expanded">
              {this.props.loading ? (
                <Spin indicator={antIcon} />
              ) : this.props.isAuthenticated ? (
                <button onClick={() => {this.props.logout();this.render()}} className="authBtn">
                  <span>Logout</span>
                </button>
              ) : (
                <div>
                  <form method="POST" onSubmit={this.authenticate}>
                    {this.state.loginForm ? (
                      <div>
                        <input
                          name="username"
                          type="text"
                          placeholder="username"
                        />
                        <input
                          name="password"
                          type="password"
                          placeholder="password"
                        />
                      </div>
                    ) : (
                      <div>
                        <input
                          name="username"
                          type="text"
                          placeholder="username"
                        />
                        <input name="email" type="email" placeholder="email" />
                        <input
                          name="password"
                          type="password"
                          placeholder="password"
                        />
                        <input
                          name="password2"
                          type="password"
                          placeholder="password confirm"
                        />
                      </div>
                    )}

                    <button type="submit">Authenticate</button>
                  </form>

                  <button onClick={this.changeForm}>Switch</button>
                </div>
              )}
            </div>
          </div>
        </div>
        <div id="contacts">
          <ul>{this.renderActiveChats()}</ul>
        </div>
        <div id="bottom-bar">
          <button id="addChat" onClick={() => this.openAddChatPopup()}>
            <i className="fa fa-user-plus fa-fw" aria-hidden="true" />
            <span>Create chat</span>
          </button>
        </div>
      </div>
    );
  }

}

const mapStateToProps = state => {
  return {
    isAuthenticated: state.auth.token !== null,
    loading: state.auth.loading,
    token: state.auth.token,
    username: state.auth.username,
    chats: state.message.chats
  };
};

const mapDispatchToProps = dispatch => {
  return {
    login: (userName, password) =>
      dispatch(actions.authLogin(userName, password)),
    logout: () => dispatch(actions.logout()),
    signup: (username, email, password1, password2) =>
      dispatch(actions.authSignup(username, email, password1, password2)),
    addChat: () => dispatch(navActions.openAddChatPopup()),
    getUserChats: (username, token) =>
      dispatch(messageActions.getUserChats(username, token))
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Sidepanel);