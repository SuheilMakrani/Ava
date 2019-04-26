package com.example.suhei.irent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class userSort extends AppCompatActivity {

	private FirebaseAuth mAuth;
	private FirebaseFirestore fStore;
	private FirebaseDatabase fData;
	private DatabaseReference databaseReference;
	private String userID;
	private ProgressDialog progress;

	@Override
	protected void onStart() {
		super.onStart();

		fData = FirebaseDatabase.getInstance();
		databaseReference = fData.getReference();

		progress = new ProgressDialog(this);
		progress.setTitle("Starting Ava");
		progress.setMessage("Thank you for your patience");
		progress.setCanceledOnTouchOutside(false);
		progress.setCancelable(false);
		progress.show();

		mAuth = FirebaseAuth.getInstance();

		fStore = FirebaseFirestore.getInstance();
		FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
				.setPersistenceEnabled(true)
				.build();
		fStore.setFirestoreSettings(settings);

		//Get the currently Signed in User
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if (user == null){
			progress.dismiss();
			sendToLogin();
		} else {
			userID = mAuth.getCurrentUser().getUid();
			//Sends to setup activity if the data doesnt exist
			fStore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<DocumentSnapshot> task) {
					if (task.isSuccessful()) {
						if (!task.getResult().exists()) {
							progress.dismiss();
							Intent setupIntent = new Intent(userSort.this, userCreateAccount.class);
							startActivity(setupIntent);
							finish();
						}else{
							fStore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
								@Override
								public void onComplete(@NonNull Task<DocumentSnapshot> task) {
									String username = task.getResult().getString("user_type");
									assert username != null;
									if(username.equals("Tenant")) {
										progress.dismiss();
										Intent intent = new Intent(userSort.this, homeTenant.class);
										startActivity(intent);
										finish();
									}else{
										progress.dismiss();
										Intent intent = new Intent(userSort.this, homeOwner.class);
										startActivity(intent);
										finish();
									}
								}
							}).addOnFailureListener(new OnFailureListener() {
								@Override
								public void onFailure(@NonNull Exception e) {
									progress.dismiss();
									Toast.makeText(userSort.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
									Intent fail = new Intent(userSort.this, userLogin.class);
									startActivity(fail);
									finish();
								}
							});
						}
					} else {
						//Error Handling
						String Error = task.getException().getMessage();
						Toast.makeText(userSort.this, "Error: " + Error, Toast.LENGTH_LONG).show();
					}
				}
			});
		}
	}

	//userLogin to the app
	private void sendToLogin() {
		Intent login = new Intent(userSort.this, userLogin.class);
		startActivity(login);
		finish();
	}
}

