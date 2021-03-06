package com.example.suhei.irent;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;


public class fragTenProperty extends Fragment {

	private RecyclerView allprop;
	private List<postProperty> postPropertyList1;
	private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
	private FirebaseAuth fAuth = FirebaseAuth.getInstance();
	private adapterProperty1 adapterProperty1;
	public Context context1;
	private String pID, userID;
	private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
	private FirebaseAuth mAuth = FirebaseAuth.getInstance();

	public fragTenProperty() {
		// Required empty public constructor
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		userID = fAuth.getCurrentUser().getUid();
		FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
				.setPersistenceEnabled(true)
				.build();
		fStore.setFirestoreSettings(settings);
		fStore.collection("Occupants").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				pID = task.getResult().getString("property_id");
			}
		});
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_ten_property, container, false);
		postPropertyList1 = new ArrayList<>();
		allprop = view.findViewById(R.id.tenpropertylist);

		adapterProperty1 = new adapterProperty1(postPropertyList1, this.context1);
		allprop.setLayoutManager(new LinearLayoutManager(getActivity()));
		allprop.setAdapter(adapterProperty1);

		fStore = FirebaseFirestore.getInstance();
		Query first = fStore.collection("Properties").whereEqualTo("property_id", pID);

		first.addSnapshotListener(Objects.requireNonNull(getActivity()), new EventListener<QuerySnapshot>() {
			@Override
			public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {

                            postProperty propertypost = documentChange.getDocument().toObject(postProperty.class);
                            postPropertyList1.add(propertypost);
                            adapterProperty1.notifyDataSetChanged();
                        }
                    }
                }
            }
		});
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

