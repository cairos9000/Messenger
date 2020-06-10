package com.example.ibkschat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class RoomListActivity extends AppCompatActivity {
    RoomListActivity act = this;
    ListView mRoomList;
    RoomListAdapter mRoomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        mRoomAdapter = new RoomListAdapter(this);
        mRoomList = findViewById(R.id.rooms_view);
        mRoomList.setAdapter(mRoomAdapter);
        mRoomList.setOnItemClickListener(mOnItemClickListener);
        mRoomAdapter.notifyDataSetChanged();
        EditText SearchBox = findViewById(R.id.find_chat_box);
        SearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRoomAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //mHandler = new MessageListActivity.IncomingHandler(this);
//        Globals.getInstance().chat.setHandler(mHandler);
    }

    public static class UpdateChats extends AsyncTask<RoomListAdapter, Integer, Integer> {
        List<MyChat> tmp_chats = new ArrayList<>();
        RoomListAdapter mRoomListAdapter;

        @Override
        protected Integer doInBackground(RoomListAdapter... parameter) {
            mRoomListAdapter = parameter[0];
            Request request = new Request.Builder()
                    .url("http://" + Globals.getInstance().address + "/chat/?format=json&username=" + Globals.getInstance().user)
                    .build();

            try (Response response = Globals.getInstance().client.newCall(request).execute()) {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                Type listType = new TypeToken<List<LoginActivity.ChatData>>() {
                }.getType();
                List<LoginActivity.ChatData> chats = new Gson().fromJson(response.body().string(), listType);
                for (LoginActivity.ChatData c : chats) {
                    MyChat cc = new MyChat(Globals.getInstance().address, c.id, c.chatName, Globals.getInstance().user);
                    tmp_chats.add(cc);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            RoomListAdapter.chats.clear();
            RoomListAdapter.chats.addAll(tmp_chats);
            mRoomListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("qqq", "Herer");
        mRoomAdapter.notifyDataSetChanged();
        new UpdateChats().execute(mRoomAdapter);
    }

    public AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(act, MessageListActivity.class);
            intent.putExtra("chat_id", position);
            startActivity(intent);
        }
    };

    public void createChat(View view) {
        CreateChatDialog dlg = new CreateChatDialog();
        dlg.mRoomListAdapter = mRoomAdapter;
        dlg.show(getFragmentManager(), "");
    }

}
