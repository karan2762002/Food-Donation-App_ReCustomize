package com.rrmchathura.myfirstapp.Reciver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rrmchathura.myfirstapp.LoginActivity;
import com.rrmchathura.myfirstapp.R;
import com.rrmchathura.myfirstapp.RegisterActivity;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText username,email,phone,password;
    Button mregisterBtn;
    ProgressBar progressBar;
    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        mregisterBtn = findViewById(R.id.registerBtn);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        mregisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });

    }

    private void ValidateData() {

        String txtname = username.getText().toString();
        String txtemail =email.getText().toString();
        String txtphone = phone.getText().toString();
        String txtpassword = password.getText().toString();

        if (TextUtils.isEmpty(txtname)){
            username.setError("UserName Required");
            return;

        }if (TextUtils.isEmpty(txtemail)){
            email.setError("Email Required");
            return;

        }if (TextUtils.isEmpty(txtphone)){
            phone.setError("Phone is Empty");
            return;

        }if (TextUtils.isEmpty(txtpassword)){
            password.setError("Password is Empty");
            return;
        }
        if (txtpassword.length() < 6){
            password.setError("Password  Must be >= 6 Characters");
        }
        progressBar.setVisibility(View.VISIBLE);

        fAuth.createUserWithEmailAndPassword(txtemail,txtpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    ////send verification link onece user Registerd

                    FirebaseUser fuser = fAuth.getCurrentUser();
                    fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(),"Verification Email Has been Sent",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           // Log.d(TAG,"onFailure Email sent " + e.getMessage());
                        }
                    });


                    Toast.makeText(getApplicationContext(),"Receiver is Created",Toast.LENGTH_SHORT).show();
                    userID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.
                            collection("users").document(userID);
                    Map<String,Object> user = new HashMap<>();
                    user.put("fName",txtname);
                    user.put("email",txtemail);
                    user.put("phone",txtphone);
                    user.put("password",txtpassword);
                    user.put("isReceiver","1");
                    documentReference.set(user).
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                   // Log.d(TAG,"onSuccess: user Profile is created for "+ userID);
                                }

                            });
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                }else {
                    Toast.makeText(getApplicationContext(),
                            "Error !" + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                }

            }
        });
    }
}