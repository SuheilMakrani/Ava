package com.example.suhei.irent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class userTenantDetails extends AppCompatActivity {

    private String pID, uID;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView firstName, lastName, gender, dob, contact, email, userInfo;
    private CircleImageView image;
    private Button accept, decline, sendmsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tenant_details);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        fStore.setFirestoreSettings(settings);

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        userInfo = findViewById(R.id.userInfo);
        gender = findViewById(R.id.gender);
        dob = findViewById(R.id.dob);
        contact = findViewById(R.id.reg_contact);
        email= findViewById(R.id.email);
        image = findViewById(R.id.setup_image);
        accept = findViewById(R.id.accept);
        decline = findViewById(R.id.decline);
        sendmsg = findViewById(R.id.button);

        uID = getIntent().getStringExtra("from_user_id");

        final String sendeeID = mAuth.getCurrentUser().getUid();

        pID = getIntent().getStringExtra("property_id");

        fStore.collection("Users").document(uID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    String firstname = task.getResult().getString("fname");
                    String lastname = task.getResult().getString("lname");
                    String userImage = task.getResult().getString("image_url");
                    String thumb = task.getResult().getString("image_thumb");
                    String gender1 = task.getResult().getString("gender");
                    String dob1 = task.getResult().getString("dob");
                    String contacts = task.getResult().getString("contact");
                    String mail = task.getResult().getString("email");
                    String info = task.getResult().getString("user_info");

                    firstName.setText(firstname);
                    lastName.setText(lastname);
                    gender.setText(gender1);
                    dob.setText(dob1);
                    contact.setText(contacts);
                    email.setText(mail);
                    userInfo.setText("' "+info+" '");

                    RequestOptions placeholderOption = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    placeholderOption.placeholder(R.drawable.hdpi);
                    Glide.with(userTenantDetails.this).applyDefaultRequestOptions(placeholderOption).load(userImage)
                            .thumbnail(Glide.with(userTenantDetails.this).applyDefaultRequestOptions(placeholderOption).load(thumb))
                            .into(image);
                }
            }
        });

        sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chat = new Intent(userTenantDetails.this, ChatActivity.class);
                chat.putExtra("user_id", uID);
                startActivity(chat);
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accept.setEnabled(isInternetOn());
                if(accept.isEnabled()) {
                    final String message = getString(R.string.notification2);

                    if (!TextUtils.isEmpty(message)) {

                        Map<String, Object> notificationMessage = new HashMap<>();
                        notificationMessage.put("message", message);
                        notificationMessage.put("sendeeID", sendeeID);
                        notificationMessage.put("property_id", pID);

                        fStore.collection("Users/" + uID + "/Notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(userTenantDetails.this, "Request Sent.", Toast.LENGTH_LONG).show();
                                Intent send = new Intent(userTenantDetails.this, homeOwner.class);
                                startActivity(send);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(userTenantDetails.this, "Error: " + e, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }else{
                    android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(userTenantDetails.this);
                    builder1.setTitle("No internet Connection");
                    builder1.setMessage("Oh no, looks like you do not have internet connection. Please try again later.");
                    builder1.setCancelable(true);
                    builder1.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    android.app.AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                decline.setEnabled(isInternetOn());
                if(decline.isEnabled()) {

                    final String message = getString(R.string.notification3);

                    if (!TextUtils.isEmpty(message)) {

                        Map<String, Object> notificationMessage = new HashMap<>();
                        notificationMessage.put("message", message);
                        notificationMessage.put("sendeeID", sendeeID);
                        notificationMessage.put("property_id", pID);

                        fStore.collection("Users/" + uID + "/Notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(userTenantDetails.this, "Request Sent.", Toast.LENGTH_LONG).show();
                                Intent send = new Intent(userTenantDetails.this, homeOwner.class);
                                startActivity(send);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(userTenantDetails.this, "Error: " + e, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }else{
                    android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(userTenantDetails.this);
                    builder1.setTitle("No internet Connection");
                    builder1.setMessage("Oh no, looks like you do not have internet connection. Please try again later.");
                    builder1.setCancelable(true);
                    builder1.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    android.app.AlertDialog alert11 = builder1.create();
                    alert11.show();
                }

            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Online").setValue(true);
    }
    @Override
    public void onPause () {
        super.onPause();
        if(mAuth.getCurrentUser() !=null){
            databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Online").setValue(ServerValue.TIMESTAMP);
        }
    }
    public final boolean isInternetOn()
    {
        ConnectivityManager connec = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // ARE WE CONNECTED TO THE NET
        if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED )
        {
            // MESSAGE TO SCREEN FOR TESTING (IF REQ)
            //Toast.makeText(this, connectionType + ” connected”, Toast.LENGTH_SHORT).show();
            return true;
        }
        else if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
                ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED  )
        {
            return false;
        }
        return false;
    }
}


