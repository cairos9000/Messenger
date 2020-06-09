package com.example.ibkschat;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MyChat {
    int id;
    String uri;
    String name;
    OkHttpClient client;
    WebSocket ws;
    MyWebSocketListener wsl;
    Handler handler;
    List<MyMessage> messages = new ArrayList<>();
    String user;

    class MyMessage {
        int id;
        String author;
        String content;
        String timestamp;

        public MyMessage(MyMessage msg) {
            id = msg.id;
            author = msg.author;
            content = msg.content;
            timestamp = msg.timestamp;
        }

        public boolean isBelongsToCurrentUser() {
            return author.equals(user);
        }
    }

    public static final class MessageToSend {
        @SerializedName("from")
        String from;
        String message;
        int chatId;
        String command = "new_message";

        public MessageToSend(String msg, int id) {
            chatId = id;
            message = msg;
            from = Globals.getInstance().user;
        }

    }

    public MyChat(String address, int _id, String chatName, String user) {
        this.user = user;
        Log.d("qqq1", user);
        id = _id;
        uri = "ws://" + address + "/ws/chat/" + id + "/";
        name = chatName;
        client = new OkHttpClient();
        Request request = new Request.Builder().url(uri).build();
        wsl = new MyWebSocketListener();
        ws = client.newWebSocket(request, wsl);
        fetchMessages();
    }

    public void fetchMessages() {
        ws.send(String.format("{\"chatId\": %d,\"command\": \"fetch_messages\" }", id));
    }

    public void setHandler(Handler h) {
        handler = h;
    }

    public final class MyWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        public class MsgComp implements Comparator<MyMessage> {
            @Override
            public int compare(MyMessage o1, MyMessage o2) {
                return o1.id - o2.id;
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d("qqq", "Receiving : " + text);
            JsonObject jobj = new Gson().fromJson(text, JsonObject.class);
            if (!jobj.has("command")) {
                return;
            } else if (jobj.get("command").toString().equals("\"messages\"")) {
                JsonArray msgs = jobj.getAsJsonArray("messages");
                Message mmsg;
                for (JsonElement jo : msgs) {
                    MyMessage msg = MyChat.this.new MyMessage(new Gson().fromJson(jo, MyMessage.class));
                    messages.add(msg);
                }
                Collections.sort(messages,new MsgComp());
                if (handler != null) {
                    handler.sendEmptyMessage(0);
                }
            } else if (jobj.get("command").toString().equals("\"new_message\"")) {
                MyMessage msg = MyChat.this.new MyMessage(new Gson().fromJson(jobj.get("message"), MyMessage.class));
                messages.add(msg);
                if (handler != null) {
                    handler.sendEmptyMessage(0);
                }
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            Log.d("qqq", "Receiving bytes : " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            Log.d("qqq", "Closing : " + code + " / " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Log.d("qqq", "Error : " + t.getMessage());
        }

    }
}
