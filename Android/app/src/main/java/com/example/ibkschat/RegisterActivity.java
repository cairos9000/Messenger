package com.example.ibkschat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        client = Globals.getInstance().client;
        address = Globals.getInstance().address;
        SignIn = findViewById(R.id.SignIn);
        SignUp = findViewById(R.id.SignUp);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void OnSignIn(View v) {
        finish();
    }

    public void OnSignUp(View view) {
        SignUp.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        final String Login = ((EditText) findViewById(R.id.Login)).getText().toString();
        final String Email = ((EditText) findViewById(R.id.Email)).getText().toString();
        final String Password1 = ((EditText) findViewById(R.id.Password1)).getText().toString();
        final String Password2 = ((EditText) findViewById(R.id.Password2)).getText().toString();

        new Thread(new Runnable() {
            public void run() {
                final boolean success;
                boolean success1 = false;
                RequestBody formBody = new FormBody.Builder()
                        .add("username", Login)
                        .add("email", Email)
                        .add("password1", Password1)
                        .add("password2", Password2)
                        .build();
                Request request = new Request.Builder()
                        .url("http://" + address + "/rest-auth/registration/")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    success1 = true;
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

    public void onLoginSuccess() {
        progressBar.setVisibility(View.INVISIBLE);
        SignIn.setEnabled(true);
        Intent intent = new Intent(activity, RoomListActivity.class);
        Globals g = Globals.getInstance();
        g.user = user;
        g.token = token;
        startActivity(intent);
    }

    public void onLoginFailed() {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        SignIn.setEnabled(true);
    }


}
