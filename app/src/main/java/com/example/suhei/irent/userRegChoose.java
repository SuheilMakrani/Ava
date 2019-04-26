package com.example.suhei.irent;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class userRegChoose extends AppCompatActivity {

	private String uID;
	private FirebaseAuth mAuth;
	private FirebaseFirestore fStore;
	private Button Tenant, HomeOwner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg_choose_user);

		mAuth = FirebaseAuth.getInstance();
		fStore = FirebaseFirestore.getInstance();
		FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
				.setPersistenceEnabled(true)
				.build();
		fStore.setFirestoreSettings(settings);

		findViewById(R.id.choose_tenant).setOnClickListener(buttonClickListener);
		findViewById(R.id.choose_owner).setOnClickListener(buttonClickListener);

	}
	private View.OnClickListener buttonClickListener= new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			uID = mAuth.getCurrentUser().getUid();

			Map< String, Object > userMap = new HashMap< >();
			switch (view.getId()){
				case R.id.choose_owner:
					userMap.put("user_type", "HouseOwner");

					fStore.collection("Users").document(uID).set(userMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if (task.isSuccessful()) {
								Intent setupIntent = new Intent(userRegChoose.this, homeOwner.class);
								startActivity(setupIntent);
								finish();
							} else {
								String errorMessage = task.getException().getMessage();
								Toast.makeText(userRegChoose.this, "(Firestore Error) : " + errorMessage, Toast.LENGTH_LONG).show();
							}
						}
					});
					break;

				case R.id.choose_tenant:
					userMap.put("user_type", "Tenant");
					userMap.put("user_id", uID);

					fStore.collection("Users").document(uID).set(userMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if (task.isSuccessful()) {
								Intent setupIntent = new Intent(userRegChoose.this, homeTenant.class);
								startActivity(setupIntent);
								finish();
							} else {
								String errorMessage = task.getException().getMessage();
								Toast.makeText(userRegChoose.this, "(Firestore Error) : " + errorMessage, Toast.LENGTH_LONG).show();
							}
						}
					});
					break;
			}
		}
	};
}
