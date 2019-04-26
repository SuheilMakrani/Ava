package com.example.suhei.irent;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.FirebaseApp;
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

import org.w3c.dom.Document;

import javax.annotation.Nullable;

public class Av1 extends Application {

    private DatabaseReference databaseReference;
    private FirebaseDatabase fData;
    private FirebaseAuth mAuth;
    private DocumentReference documentReference;
    private FirebaseFirestore fStore;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fData = FirebaseDatabase.getInstance();
        databaseReference = fData.getReference();

        if(mAuth.getCurrentUser() != null){
            final String userID = mAuth.getCurrentUser().getUid();
            documentReference = fStore.collection("Users").document(mAuth.getCurrentUser().getUid());

            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot!=null){
                        databaseReference.child("Users").child(userID).child("Online").setValue(ServerValue.TIMESTAMP);
                    }
                }
            });
        }
    }
}
