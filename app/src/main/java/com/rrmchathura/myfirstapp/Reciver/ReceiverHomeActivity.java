package com.rrmchathura.myfirstapp.Reciver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rrmchathura.myfirstapp.DonationActivity;
import com.rrmchathura.myfirstapp.LoginActivity;
import com.rrmchathura.myfirstapp.Notification.FcmNotificationsSender;
import com.rrmchathura.myfirstapp.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ReceiverHomeActivity extends AppCompatActivity {

    EditText title, message, token;
    Button alldeviceid, btnaddress;
    FirebaseAuth firebaseAuth;
    SharedPreferences user_session;
    ImageView locationimg;
    TextView friendsId;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_home);

        FirebaseMessaging.getInstance().subscribeToTopic("all");
        firebaseAuth = FirebaseAuth.getInstance();

        title = findViewById(R.id.titileId);
        message = findViewById(R.id.messageId);
      //  token = findViewById(R.id.tokenId);
        alldeviceid = findViewById(R.id.alldeviceId);
       // btnaddress = findViewById(R.id.btnaddress);
        locationimg = findViewById(R.id.locationimg);
      //  friendsId = findViewById(R.id.friendsId);

        ///Initialize fusedLocationProviderClient

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check permission

                if (ActivityCompat.checkSelfPermission(ReceiverHomeActivity.this
                        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    //when permission granted
                    getLocation();

                } else {
                    //when permission denied
                    ActivityCompat.requestPermissions(ReceiverHomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }

        });

//        friendsId.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                token.setVisibility(View.VISIBLE);
//
//            }
//        });

//        btnaddress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //check permission
//
//                if (ActivityCompat.checkSelfPermission(ReceiverHomeActivity.this
//                        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//                    //when permission granted
//                    getLocation();
//
//                } else {
//                    //when permission denied
//                    ActivityCompat.requestPermissions(ReceiverHomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
//                }
//            }
//        });


        alldeviceid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!title.getText().toString().isEmpty() && !message.getText().toString().isEmpty()) {

                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/all", title.getText().toString(),
                            message.getText().toString(), getApplicationContext(), ReceiverHomeActivity.this);
                    notificationsSender.SendNotifications();


                } else {
                    Toast.makeText(getApplicationContext(), "Write Your Address", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getLocation() {

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Location> task) {
                //Intialize Location

                Location location = task.getResult();
                if (location != null) {
                    try {
                        //Intialize geoCoder
                        Geocoder geocoder = new Geocoder(ReceiverHomeActivity.this
                                , Locale.getDefault());

                        //Initialize address list
                        List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        //set address in messageId
                        message.setText(address.get(0).getAddressLine(0));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }


//    public void Logout(View view) {
//        firebaseAuth.signOut();
//
//        user_session = this.getSharedPreferences("user_details", MODE_PRIVATE);
//
//
//        //sessions clear when user log out
//        SharedPreferences.Editor editor = user_session.edit();
//        editor.clear();
//        editor.putBoolean("isLoggedIn",false);
//        editor.putString("cu_type", "");
//        editor.commit();
//
//        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }

    public void logout(View view) {
        firebaseAuth.signOut();

        user_session = this.getSharedPreferences("user_details", MODE_PRIVATE);


        //sessions clear when user log out
        SharedPreferences.Editor editor = user_session.edit();
        editor.clear();
        editor.putBoolean("isLoggedIn",false);
        editor.putString("cu_type", "");
        editor.commit();

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}