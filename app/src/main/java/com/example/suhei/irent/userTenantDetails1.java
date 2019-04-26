package com.example.suhei.irent;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class userTenantDetails1 extends AppCompatActivity {

    private String oID, pID, uID;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView lastName, gender, dob, contact, email, reside, rent, userinfo;
    private CircleImageView image;
    private Button sendmsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tenant_details1);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        fStore.setFirestoreSettings(settings);

        lastName = findViewById(R.id.last_name);
        gender = findViewById(R.id.gender);
        dob = findViewById(R.id.dob);
        contact = findViewById(R.id.reg_contact);
        email= findViewById(R.id.email);
        image = findViewById(R.id.setup_image);
        reside= findViewById(R.id.add);
        rent = findViewById(R.id.status);
        sendmsg = findViewById(R.id.button);
        userinfo = findViewById(R.id.userInfo2);

        uID = getIntent().getStringExtra("from_user_id");
        pID = getIntent().getStringExtra("property_id");

        final DocumentReference docRef = fStore.collection("Occupants").document(uID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    String status = snapshot.getString("status");
                    oID = snapshot.getString("owner_id");

                    if(status.equals("paid")){

                        String ontime=getApplicationContext().getResources().getString(R.string.ontime);
                        rent.setTextColor(getResources().getColor(R.color.ontime));
                        rent.setText(ontime);

                    } else if(status.equals("overdue")){
                        String overdue=getApplicationContext().getResources().getString(R.string.overdue);
                        rent.setTextColor(getResources().getColor(R.color.overdue));
                        rent.setText(overdue);
                    }
                }
            }
        });
        //------------------------------Set property Name----------------------------//
        fStore.collection("Properties").document(pID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String propname = task.getResult().getString("property_name");
                reside.setText(propname);
            }
        });
        //------------------------------Set Tenant Details----------------------------//
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

                    lastName.setText(firstname+" "+lastname);
                    gender.setText(gender1);
                    dob.setText(dob1);
                    contact.setText(contacts);
                    email.setText(mail);
                    userinfo.setText("' "+info+" '");

                    RequestOptions placeholderOption = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    placeholderOption.placeholder(R.drawable.hdpi);
                    Glide.with(userTenantDetails1.this).applyDefaultRequestOptions(placeholderOption).load(userImage)
                            .thumbnail(Glide.with(userTenantDetails1.this).applyDefaultRequestOptions(placeholderOption).load(thumb))
                            .into(image);
                }
            }
        });
        sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chat = new Intent(userTenantDetails1.this, ChatActivity.class);
                chat.putExtra("user_id", uID);
                startActivity(chat);
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
}
