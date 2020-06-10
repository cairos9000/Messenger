package com.example.ibkschat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;

import okhttp3.WebSocket;


public class MessageListActivity extends AppCompatActivity {
    MessageListAdapter mMessageAdapter;
    ListView mMessageView;
    WebSocket ws;
    Handler mHandler;
    MyChat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("qqq", "onCreate call!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        chat = RoomListAdapter.chats.get(this.getIntent().getIntExtra("chat_id", 0));
        mMessageAdapter = new MessageListAdapter(this, chat.messages);
        mMessageView = findViewById(R.id.messages_view);
        mMessageView.setAdapter(mMessageAdapter);
        mHandler = new IncomingHandler(this);
        chat.setHandler(mHandler);
        ws = chat.ws;
        mMessageAdapter.notifyDataSetChanged();
        mMessageView.setSelection(mMessageView.getCount() - 1);
    }

    class IncomingHandler extends Handler {
        private final WeakReference<MessageListActivity> act;

        IncomingHandler(MessageListActivity activity) {
            act = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            mMessageAdapter.notifyDataSetChanged();
            mMessageView.setSelection(mMessageView.getCount() - 1);
        }
    }

    public void SendMessage(final View view) {
        EditText msg_line = findViewById(R.id.edittext_chatbox);
        MyChat.MessageToSend msg2 = new MyChat.MessageToSend(msg_line.getText().toString(),chat.id);
        ws.send(new Gson().toJson(msg2));
    }
}
