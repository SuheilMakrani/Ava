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

import javax.annotation.Nullable;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class fragTenant extends Fragment {

	private List<postTenant> postTenantList;
	private adapterTenant adapterTenant;
	private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
	private FirebaseAuth mAuth = FirebaseAuth.getInstance();
	private String OwnerID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
	public Context context;
	private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

	public fragTenant() {
		// Required empty public constructor
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.activity_tenant_fragment, container, false);

		postTenantList = new ArrayList<>();
		RecyclerView recyclerView = view.findViewById(R.id.tenantList);
		adapterTenant = new adapterTenant(this.context, postTenantList);

		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setAdapter(adapterTenant);
		Query first = fStore.collection("Occupants").whereEqualTo("owner_id",OwnerID );

		first.addSnapshotListener(Objects.requireNonNull(getActivity()), new EventListener<QuerySnapshot>() {
			@Override
			public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {

                            postTenant postTenant = documentChange.getDocument().toObject(postTenant.class);
                            postTenantList.add(postTenant);
                            adapterTenant.notifyDataSetChanged();
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
