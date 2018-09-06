package com.example.prana.numberplaterecognition;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.graphics.Bitmap;
import android.content.Intent;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import android.util.Base64;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.github.kittinunf.fuel.core.FuelError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;


public class MainActivity extends AppCompatActivity {

    public final static int MY_REQUEST_CODE = 1;
    String results = "";
    String token;
    String Amount;

    public void takePicture(View view) {
        Amount =((EditText) findViewById(R.id.Amount)).getText().toString();
       if(Amount.length()<1)
       {
           Toast.makeText(this, "Please enter the amount to proceed", Toast.LENGTH_SHORT).show();
           return;
       }
       else {
           Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
           intent.putExtra("android.intent.extra.quickCapture", true);
           startActivityForResult(intent, MY_REQUEST_CODE);
       }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if(requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK) {

            // Convert image data to bitmap
            Bitmap picture = (Bitmap)data.getExtras().get("data");
            ImageView car = (ImageView) findViewById(R.id.car);
            car.animate().alpha(0f).setDuration(1000);

            // Set the bitmap as the source of the ImageView
            ((ImageView)findViewById(R.id.previewImage))
                    .setImageBitmap(picture);
            ((TextView)findViewById(R.id.pictureText)).setText("This is the picture you clicked");
            ((TextView)findViewById(R.id.textView)).setText("Click again to retake picture");

            // More code goes here

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.JPEG, 90, byteStream);

            String base64Data = Base64.encodeToString(byteStream.toByteArray(),
                    Base64.URL_SAFE);

            String requestURL ="https://vision.googleapis.com/v1/images:annotate?key=" +getResources().getString(R.string.mykey);


            // Create an array containing
            // the LABEL_DETECTION feature
            JSONArray features = new JSONArray();
            JSONObject feature = new JSONObject();

             try{
                 feature.put("type", "TEXT_DETECTION");
             }
             catch (JSONException e) {
                 System.out.println("Error in feature.put");
             }
             features.put(feature);
             //Create an object containing the Base64-encoded image data
            JSONObject imageContent = new JSONObject();
            try{
                imageContent.put("content", base64Data);
            }
            catch (JSONException e) {
                System.out.println("Error in imageContent.put");
            }
            // Put the array and object into a single request
            // and then put the request into an array of requests
            JSONArray requests = new JSONArray();
            JSONObject request = new JSONObject();
            try{
                request.put("image", imageContent);
                request.put("features", features);
                requests.put(request);
            }
            catch (JSONException e) {
                System.out.println("Error in imageContent.put");
            }
            JSONObject postData = new JSONObject();
            try{
                postData.put("requests", requests);
            }
            catch (JSONException e) {
                System.out.println("Error in imageContent.put");
            }
            // Convert the JSON into a string
            String body = postData.toString();
            Toast.makeText(MainActivity.this, "Please wait!", Toast.LENGTH_SHORT).show();


            Fuel.post(requestURL).header(new Pair<String, Object>("content-length", body.length()), new Pair<String, Object>("content-type", "application/json"))
                    .body(body.getBytes())
                    .responseString(new Handler<String>() {

                        @Override
                        public void success(@NotNull Request request,
                                            @NotNull Response response,
                                            String data) {
                            JSONArray labels=new JSONArray();
                            try {
                                JSONObject D=new JSONObject(data);
                                labels = D.getJSONArray("responses")
                                        .getJSONObject(0)
                                        .getJSONArray("textAnnotations");
                            }
                            catch(JSONException e){
                                System.out.println("Error occurred-labels");
                            }
                           // Loop through the array and extract the
                            // description key for each item
                            for(int i=0;i<1;i++) {
                                try {
                                    results = results +
                                            labels.getJSONObject(i).getString("description") +
                                            "\n";
                                }
                                catch(JSONException e){
                                    System.out.println("Error occurred-results");
                                }
                            }
                        }
                        @Override
                        public void failure(@NotNull Request request,
                                            @NotNull Response response,
                                            @NotNull FuelError fuelError) {
                            Toast.makeText(MainActivity.this, fuelError.getMessage(), Toast.LENGTH_SHORT).show();;
                        }

                    });
            // Display the annotations inside the TextView
            ((TextView)findViewById(R.id.resultsText)).setText(results);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
            ref.child(results).child("token").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    token = (String) dataSnapshot.getValue();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            new ApplicationClass().execute(token, Amount);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
