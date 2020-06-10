import React from "react";
import { connect } from "react-redux";
import WebSocketInstance from "../websocket";
import Hoc from "../hoc/hoc";

class Chat extends React.Component {
  state = { message: "" };

  initialiseChat() {
    this.waitForSocketConnection(() => {

      WebSocketInstance.fetchMessages(
        this.props.username,
        this.props.match.params.chatID
      );
    });
    WebSocketInstance.connect(this.props.match.params.chatID);
  }

  constructor(props) {
    super(props);
    this.initialiseChat();
  }

  waitForSocketConnection(callback) {
    const component = this;
    setTimeout(function() {
      if (WebSocketInstance.state() === 1) {
        console.log("Connection is made");
        callback();
        return;
      } else {
        console.log("wait for connection...");
        component.waitForSocketConnection(callback);
      }
    }, 100);
  }

  messageChangeHandler = event => {
    this.setState({ message: event.target.value });
  };

  sendMessageHandler = e => {
    e.preventDefault();
    const messageObject = {
      from: this.props.username,
      content: this.state.message,
      chatId: this.props.match.params.chatID
    };
    WebSocketInstance.newChatMessage(messageObject);
    this.setState({ message: "" });
  };

  renderTimestamp = timestamp => {
    let prefix = ''; 
    const timeDiff = Math.round((new Date().getTime() - new Date(timestamp).getTime())/60000);
    const sec = new Date().getSeconds() - new Date(timestamp).getSeconds()
    if (timeDiff < 1) { 
        prefix = `${sec} секунд назад`;

    }
    else if(timeDiff == 1){
        prefix = `${timeDiff} минуту назад`;

    }
    else if(timeDiff > 1 && timeDiff < 5){
        prefix = `${timeDiff} минуты назад`;
    }
    else if (timeDiff < 60 && timeDiff >= 5) { 
        prefix = `${timeDiff} минут назад`;
    } else if (timeDiff < 5*60 && timeDiff > 60) { 
        prefix = `${Math.round(timeDiff/60)} часа назад`;
    }else if (timeDiff < 24*60 && timeDiff > 5*60) { 
        prefix = `${Math.round(timeDiff/60)} часов назад`;
    }
     else if (timeDiff < 31*24*60 && timeDiff > 24*60) { 
        prefix = `${Math.round(timeDiff/(60*24))} дней назад`;
    } else {
        prefix = `${new Date(timestamp)}`;
    }
    return prefix;
  };

  renderMessages = messages => {
    const currentUser = this.props.username;
    return messages.map((message, i, arr) => (
      <li
        key={message.id}
        style={{ marginBottom: arr.length - 1 === i ? "300px" : "15px" }}
        className={message.author === currentUser ? "sent" : "replies"}
      >
        <img src="https://windowstips.ru/wp-content/uploads/2016/06/Icon-User.png" />
        <p>
          {message.content}
          <br />
          <small>
              {message.author}
          </small>
          <br />
          <small>
              {this.renderTimestamp(message.timestamp)}
          </small>
        </p>
      </li>
    ));
  };

  scrollToBottom = () => {
    this.messagesEnd.scrollIntoView({ behavior: "smooth" });
  };

  componentDidMount() {
    this.scrollToBottom();
  }

  componentDidUpdate() {
    this.scrollToBottom();
  }

  componentWillReceiveProps(newProps) {
    if (this.props.match.params.chatID !== newProps.match.params.chatID) {
      WebSocketInstance.disconnect();
      this.waitForSocketConnection(() => {
        WebSocketInstance.fetchMessages(
          this.props.username,
          newProps.match.params.chatID
        );
      });
      WebSocketInstance.connect(newProps.match.params.chatID);
    }
  }

  render() {
    const messages = this.state.messages;
    return (
      <Hoc>
        <div className="messages">
          <ul id="chat-log">
            {this.props.messages && this.renderMessages(this.props.messages)}
            <div
              style={{ float: "left", clear: "both" }}
              ref={el => {
                this.messagesEnd = el;
              }}
            />
          </ul>
        </div>
        <div className="message-input">
          <form onSubmit={this.sendMessageHandler}>
            <div className="wrap">
              <input
                onChange={this.messageChangeHandler}
                value={this.state.message}
                required
                id="chat-message-input"
                type="text"
                placeholder="Write your message..."
              />
              <button id="chat-message-submit" className="submit">
                <i className="fa fa-paper-plane" aria-hidden="true" />
              </button>
            </div>
          </form>
        </div>
      </Hoc>
    );
  }
}

const mapStateToProps = state => {
  return {
    username: state.auth.username,
    messages: state.message.messages
  };
};

export default connect(mapStateToProps)(Chat);