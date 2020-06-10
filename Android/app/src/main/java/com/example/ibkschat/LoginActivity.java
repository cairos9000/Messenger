package com.example.ibkschat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    Button SignIn;
    Button SignUp;
    ProgressBar progressBar;
    AppCompatActivity activity = this;
    OkHttpClient client;
    String address;
    String token;
    String user;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    class ChatData {
        int id;
        String chatName;
        List<Integer> messages;
        List<String> participants;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        client = Globals.getInstance().client;
        address = Globals.getInstance().address;
        SignIn = findViewById(R.id.SignIn);
        SignUp = findViewById(R.id.SignUp);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);

    }

    public void OnSignUp(View v) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    public void OnSignIn(View view) {
        SignIn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE); // To show the ProgressBar

        final String Login = ((EditText) findViewById(R.id.Login)).getText().toString();
        final String Password = ((EditText) findViewById(R.id.Password)).getText().toString();

        new Thread(new Runnable() {
            public void run() {
                final boolean success;
                boolean success1 = false;
                RequestBody formBody = new FormBody.Builder()
                        .add("username", Login)
                        .add("password", Password)
                        .build();
                Request request = new Request.Builder()
                        .url("http://" + address + "/rest-auth/login/")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    user = Login;
                    token = new Gson().fromJson(response.body().string(), JsonObject.class).get("key").getAsString();
                    success1 = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Globals g = Globals.getInstance();
                g.user = user;
                g.token = token;
                request = new Request.Builder()
                        .url("http://" + address + "/chat/?format=json&username=" + Login)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Type listType = new TypeToken<List<ChatData>>() {
                    }.getType();
                    List<ChatData> chats = new Gson().fromJson(response.body().string(), listType);
                    for (ChatData c : chats) {
                        RoomListAdapter.chats.add(new MyChat(address, c.id, c.chatName, user));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                success = success1;
                activity.runOnUiThread(
                        new Runnable() {
                            public void run() {
                                if (success) {
                                    onLoginSuccess();
                                } else {
                                    onLoginFailed();
                                }
                            }
                        });
            }
        }).start();


    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        SignIn.setEnabled(true);
        Intent intent = new Intent(activity, RoomListActivity.class);
        progressBar.setVisibility(View.INVISIBLE);
        startActivity(intent);
    }

    public void onLoginFailed() {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        SignIn.setEnabled(true);
    }

}

