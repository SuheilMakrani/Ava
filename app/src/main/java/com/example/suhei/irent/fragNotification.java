package com.example.suhei.irent;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class fragNotification extends Fragment {

    private List<postNotifications> postNotificationsList;
    private adapterNotification adapterNotification;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private LinearLayoutManager linearLayout;

    public fragNotification() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_notification_fragment, container, false);

        postNotificationsList = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.notificationList);
        adapterNotification = new adapterNotification(getContext(), postNotificationsList);
        linearLayout = new LinearLayoutManager(getActivity());
        linearLayout.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayout);
        recyclerView.setAdapter(adapterNotification);

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        fStore.setFirestoreSettings(settings);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        Query first = fStore.collection("Users").document(user_id).collection("Notifications");

        first.addSnapshotListener(Objects.requireNonNull(getActivity()), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                //Get data in Real Time and check for any changes in the document.
                if (documentSnapshots != null) {
                    for (DocumentChange docChange : documentSnapshots.getDocumentChanges()) {
                        //Check if the items are added
                        if (docChange.getType() == DocumentChange.Type.ADDED) {

                            postNotifications postNotifications = docChange.getDocument().toObject(postNotifications.class);
                            postNotificationsList.add(postNotifications);
                            adapterNotification.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        //Inflate the layout for the fragment
        return view;
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