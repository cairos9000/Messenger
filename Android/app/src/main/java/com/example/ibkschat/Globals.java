package com.example.ibkschat;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Globals {
    private static Globals instance;
    String user;
    String token = null;
    String address = "35.228.234.14:8000";
    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new AuthInterceptor())
            .build();


    private Globals() {
    }

    final class AuthInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request originalRequest = chain.request();
            if (originalRequest.body() == null || token == null) {
                return chain.proceed(originalRequest);
            }

            Request authRequest = originalRequest.newBuilder()
                    .header("Authorization", "Token " + token)
                    .build();
            return chain.proceed(authRequest);
        }
    }

    public static synchronized Globals getInstance() {
        if (instance == null) {
            instance = new Globals();
        }
        return instance;
    }
}