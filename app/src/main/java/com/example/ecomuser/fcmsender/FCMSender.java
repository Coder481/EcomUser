package com.example.ecomuser.fcmsender;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FCMSender {

    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send"
            , KEY_STRING = "key=AAAAUTGZlKY:APA91bHGLB3e344eJm8ATrDUcskAn9YnjPJDelG5OKmBgGKFdSW53zMzZwTaPg_77Zzy_cS0UdiO8J9bpkdQpBdiaX9mC6TjZSRFSKxKUotpmI5YG8k1J1Zq4K-hWoAHQyn3b21X3PDV";

    OkHttpClient client = new OkHttpClient();
    public void send(String message, Callback callback){
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(FCM_URL)
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization",KEY_STRING)
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

}
