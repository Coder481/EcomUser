package com.example.ecomuser.Services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ecomuser.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFCMService  extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "MyFCMService";
    private SharedPreferences mSharedPref;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String version = remoteMessage.getData().get("version");


        mSharedPref = getSharedPreferences("signInId",MODE_PRIVATE);
        String username =  mSharedPref.getString("username","");


        Map<String,String> dataMap = remoteMessage.getData();

        if (!(dataMap.get("for").equals(username)))
            return;

        createNotificationChannel();
        /*Log.e("fcmDemo","version- "+version);
        Log.e("fcmDemo","notiTitle- "+remoteMessage.getNotification().getTitle());
        Log.e("fcmDemo","notiBody- "+remoteMessage.getNotification().getBody());*/

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add_icon)
                .setContentTitle(dataMap.get("title"))
                .setContentText(dataMap.get("body"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(new Random().nextInt(5000)+5000,builder.build());
    }

    private void createNotificationChannel() {

        // Create the notification channel : but only with API 26+ because
        // the notification channel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviours after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
