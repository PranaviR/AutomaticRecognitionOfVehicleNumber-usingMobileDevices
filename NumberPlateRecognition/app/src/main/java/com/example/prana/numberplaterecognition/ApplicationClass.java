package com.example.prana.numberplaterecognition;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ApplicationClass extends AsyncTask<String,String,String> {

    @Override
    protected String doInBackground(String... strings) {
        String token = strings[0];
        String Amount = strings[1];
        Log.d("token","firebase");
        NotificationData notificationData = new NotificationData();
        NotificationRequestModel notificationRequestModel = new NotificationRequestModel();
        notificationData.setDetail("This is a firebase push notification from Fuel station app ");
        notificationData.setTitle("Firebase Push Notification");

        notificationData.setMupi("upi://pay?pa=9160609999@upi&pn=RAMBHAKTA%20PRANAVI&tid=422d97c1-f0fc-4bea-b24a-511ffa85e86f&am="+Amount+"&tn=Test%transaction");

        notificationRequestModel.setData(notificationData);
        notificationRequestModel.setTo(token);


        Gson gson = new Gson();
        Type type = new TypeToken<NotificationRequestModel>() {
        }.getType();

        String json = gson.toJson(notificationRequestModel, type);

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .header("Authorization", "key=" + "AAAA9bn8dHM:APA91bF1pKgRBL_8OrA7fik98Vqjy8lKWcX7x0Hy_xZKcyB1zzrUAYw_JYYfDSJy0FSF41Va91frkXbVIAFs8mBgRdAU9y_npdDNpFKR2yx3p3u02lj3wGehofIH0-GIGq5wQlgKfvIhz2bhzzodQfIFodK5AZ8saA")
                .url("http://fcm.googleapis.com/fcm/send")
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();

            // Do something with the response.
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
