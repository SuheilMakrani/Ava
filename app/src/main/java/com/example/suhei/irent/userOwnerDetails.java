package com.example.suhei.irent;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class userOwnerDetails extends AppCompatActivity {

    private String oID;
    private FirebaseFirestore fStore;
    private TextView lastName, gender, dob, contact, email, userInfo;
    private CircleImageView image;
    private Button sendmsg;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_owner_details);

        lastName = findViewById(R.id.last_name);
        gender = findViewById(R.id.gender);
        dob = findViewById(R.id.dob);
        contact = findViewById(R.id.reg_contact);
        email= findViewById(R.id.email);
        image = findViewById(R.id.setup_image);
        userInfo = findViewById(R.id.userInfo3);
        sendmsg = findViewById(R.id.messagebtn);


        if(getIntent().hasExtra("from_user_id")){
            oID = getIntent().getStringExtra("from_user_id");
        }else{
            oID = getIntent().getStringExtra("oID");
        }

        fStore = FirebaseFirestore.getInstance();

        fStore.collection("Users").document(oID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                    userInfo.setText("' "+info+" '");

                    RequestOptions placeholderOption = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    placeholderOption.placeholder(R.drawable.hdpi);
                    Glide.with(userOwnerDetails.this).applyDefaultRequestOptions(placeholderOption).load(userImage)
                            .thumbnail(Glide.with(userOwnerDetails.this).applyDefaultRequestOptions(placeholderOption).load(thumb))
                            .into(image);
                }
            }
        });
        sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chat = new Intent(userOwnerDetails.this, ChatActivity.class);
                chat.putExtra("user_id", oID);
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

