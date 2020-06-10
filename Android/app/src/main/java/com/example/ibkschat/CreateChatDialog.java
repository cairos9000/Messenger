package com.example.ibkschat;


import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateChatDialog extends DialogFragment {
    View v;
    List<String> users = new ArrayList<>();
    String chatName;
    ArrayAdapter<String> adapter;
    OkHttpClient client = Globals.getInstance().client;
    String address = Globals.getInstance().address;
    ProgressBar progressBar;
    RoomListAdapter mRoomListAdapter;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");


    class CreateChatRequest {
        List<Integer> messages;
        List<String> participants;
        String chatName;

        public CreateChatRequest(List<Integer> _messages, List<String> _participants, String _chatName) {
            messages = _messages;
            participants = _participants;
            chatName = _chatName;
        }
    }

    private class CreateChatTask extends AsyncTask<String, Integer, Boolean> {
        protected Boolean doInBackground(String... not_used) {
            Boolean success = false;
            CreateChatRequest req = new CreateChatRequest(new ArrayList<Integer>(), users, chatName);
            String postBody = new Gson().toJson(req);
            Request request = new Request.Builder()
                    .url("http://" + address + "/chat/create/")
                    .post(RequestBody.create(JSON, postBody))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return success;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.INVISIBLE);
            v.findViewById(R.id.create_chat).setEnabled(true);
            if (!result)
                Toast.makeText(v.getContext(), "Create chat failed", Toast.LENGTH_LONG).show();
            else
                dismiss();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Create chat");
        v = inflater.inflate(R.layout.create_chat_dialog, null);
        users.add(Globals.getInstance().user);
        adapter = new ArrayAdapter<String>(v.getContext(),
                android.R.layout.simple_list_item_1,
                users);
        ListView lv = v.findViewById(R.id.users_list);
        lv.setAdapter(adapter);
        progressBar = v.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        v.findViewById(R.id.create_chat).setOnClickListener(new OnClickListener() {
            public void onClick(View
                                        view) {
                EditText chat_name_box = v.findViewById(R.id.chat_name_box);
                chatName = chat_name_box.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                v.findViewById(R.id.create_chat).setEnabled(false);
                new CreateChatTask().execute("");
            }
        });

        v.findViewById(R.id.add_chat_user).setOnClickListener(new OnClickListener() {
            public void onClick(View
                                        view) {
                EditText user_box = v.findViewById(R.id.chat_member_box);
                String user = user_box.getText().toString();
                users.add(user);
                ListView lv = v.findViewById(R.id.users_list);
                adapter.notifyDataSetChanged();
            }
        });

        return v;
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        new RoomListActivity.UpdateChats().execute(mRoomListAdapter);
    }


    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        new RoomListActivity.UpdateChats().execute(mRoomListAdapter);
    }

}