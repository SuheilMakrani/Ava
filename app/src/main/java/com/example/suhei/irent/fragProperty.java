package com.example.suhei.irent;

import android.content.Context;
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

public class fragProperty extends Fragment {

	private List<postProperty> postPropertyList;
	private adapterProperty adapterProperty;
	public Context context;
	private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
	private FirebaseAuth mAuth = FirebaseAuth.getInstance();

	public fragProperty() {
		// Required empty public constructor
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_property_fragment, container, false);

		postPropertyList = new ArrayList<>();

		RecyclerView proplist = view.findViewById(R.id.propertyList);

		adapterProperty = new adapterProperty(postPropertyList, this.context);
		proplist.setLayoutManager(new LinearLayoutManager(getActivity()));
		proplist.setAdapter(adapterProperty);


		//Retrieve data
		FirebaseFirestore fStore = FirebaseFirestore.getInstance();
		FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
				.setPersistenceEnabled(true)
				.build();
		fStore.setFirestoreSettings(settings);
		FirebaseAuth fAuth = FirebaseAuth.getInstance();
		String uId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
		Query first = fStore.collection("Properties").whereEqualTo("owner_id", uId);

		first.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
			@Override
			public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
				//Get data in Real Time and check for any changes in the document.
				if (documentSnapshots != null) {
					for (DocumentChange docChange : documentSnapshots.getDocumentChanges()) {
						//Check if the items are added
						if (docChange.getType() == DocumentChange.Type.ADDED) {

							postProperty propertypost = docChange.getDocument().toObject(postProperty.class);
							postPropertyList.add(propertypost);
							adapterProperty.notifyDataSetChanged();
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
