package com.example.suhei.irent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class addPropAgreement extends AppCompatActivity {

	private Spinner tenure, notif;
	private EditText addRules, secDeposit, advRental, keyDeposit, rental;
	private String pId, house,bedroom, bathroom, occupant, pname, oID, imageUrl, imageThumb,air, bed
			, study, closet, electric, Wifi, washing, dry, gas, freeze, microwave, filter;
	private FirebaseFirestore fStore;
	private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
	private FirebaseAuth mAuth = FirebaseAuth.getInstance();

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agreement);

		fStore = FirebaseFirestore.getInstance();
		FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
				.setPersistenceEnabled(true)
				.build();
		fStore.setFirestoreSettings(settings);

		Toolbar addProp3 = findViewById(R.id.addProp3);
		addProp3.setTitleTextColor(getResources().getColor(R.color.inputText));
		setSupportActionBar(addProp3);
		Objects.requireNonNull(getSupportActionBar()).setTitle("Almost Done");

		if(getIntent().hasExtra("image_url")){
			pId = getIntent().getStringExtra("pId");
			oID = getIntent().getStringExtra("owner_id");
			house = getIntent().getStringExtra("house_type");
			bedroom = getIntent().getStringExtra("no_of_bedrooms");
			bathroom = getIntent().getStringExtra("no_of_bathrooms");
			occupant = getIntent().getStringExtra("occupants");
			pname = getIntent().getStringExtra("property_name");
			imageUrl = getIntent().getStringExtra("image_url");
			imageThumb = getIntent().getStringExtra("image_thumb");
			air = getIntent().getStringExtra("ac");
			bed = getIntent().getStringExtra("beds");
			study = getIntent().getStringExtra("table");
			closet = getIntent().getStringExtra("wardrobe");
			electric = getIntent().getStringExtra("electricity");
			Wifi = getIntent().getStringExtra("wifi");
			washing = getIntent().getStringExtra("washing");
			dry = getIntent().getStringExtra("dryer");
			gas = getIntent().getStringExtra("stove");
			freeze = getIntent().getStringExtra("fridge");
			microwave = getIntent().getStringExtra("oven");
			filter = getIntent().getStringExtra("water");
		}

		addRules = findViewById(R.id.addRules);
		secDeposit = findViewById(R.id.secDeposit);
		advRental = findViewById(R.id.advRental);
		keyDeposit = findViewById(R.id.keyDeposit);
		rental = findViewById(R.id.rental);
		tenure = findViewById(R.id.tenure);
		notif = findViewById(R.id.notification);
		spinner();

		final Button finish = findViewById(R.id.finish);

		finish.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				finish.setEnabled(isInternetOn());
				if(finish.isEnabled()){
					final String Tenure = tenure.getSelectedItem().toString();
					final String Notif = notif.getSelectedItem().toString();
					final String AddRules = addRules.getText().toString();
					final String security = secDeposit.getText().toString();
					final String advance = advRental.getText().toString();
					final String key = keyDeposit.getText().toString();
					final String rent = rental.getText().toString();

					if (!TextUtils.isEmpty(Tenure) && !TextUtils.isEmpty(Notif) & !TextUtils.isEmpty(security) && !TextUtils.isEmpty(advance)&& !TextUtils.isEmpty(key)&& !TextUtils.isEmpty(rent)) {
						//Create Map
						Map<String, Object> propertyMap = new HashMap<>();
						propertyMap.put("owner_id", oID);
						propertyMap.put("property_name", pname);
						propertyMap.put("property_id", pId);
						propertyMap.put("house_type", house);
						propertyMap.put("no_of_bedrooms", bedroom);
						propertyMap.put("no_of_bathrooms", bathroom);
						propertyMap.put("occupants", occupant);
						propertyMap.put("date_posted", FieldValue.serverTimestamp());//See when it was posted
						propertyMap.put("image_url", imageUrl);
						propertyMap.put("image_thumb", imageThumb);
						propertyMap.put("tenure_period", Tenure);
						propertyMap.put("prior_notification", Notif);
						propertyMap.put("additional_rules", AddRules);
						propertyMap.put("security_deposit", security);
						propertyMap.put("advance_rental", advance);
						propertyMap.put("key_deposit", key);
						propertyMap.put("monthly_rental", rent);
						//Start storing data
						fStore.collection("Properties").document(pId).set(propertyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
							@Override
							public void onComplete(@NonNull Task<Void> task) {
								if (task.isSuccessful()) {
									Map<String, Object> propertyMap1 = new HashMap<>();
									propertyMap1.put("ac", air);
									propertyMap1.put("beds", bed);
									propertyMap1.put("table", study);
									propertyMap1.put("wardrobe", closet);
									propertyMap1.put("electricity", electric);
									propertyMap1.put("wifi", Wifi);
									propertyMap1.put("washing", washing);
									propertyMap1.put("dryer", dry);
									propertyMap1.put("stove", gas);
									propertyMap1.put("fridge", freeze);
									propertyMap1.put("oven", microwave);
									propertyMap1.put("water", filter);

									fStore.collection("Properties").document(pId).collection("Amenities").document(pId).set(propertyMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
										@Override
										public void onComplete(@NonNull Task<Void> task) {
											Intent intent = new Intent(addPropAgreement.this, homeOwner.class);
											Toast.makeText(addPropAgreement.this, "Property Updated ", Toast.LENGTH_LONG).show();
											startActivity(intent);
											finish();
										}
									});
								} else {
									String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
									Toast.makeText(addPropAgreement.this, "(Firestore Error) : " + errorMessage, Toast.LENGTH_LONG).show();
								}
							}
						});
					}else {
						Toast.makeText(addPropAgreement.this, "Enter All fields", Toast.LENGTH_LONG).show();
					}
				}else{
					AlertDialog.Builder builder1 = new AlertDialog.Builder(addPropAgreement.this);
					builder1.setTitle("No internet Connection");
					builder1.setMessage("Oh no, looks like you do not have internet connection. Please try again later.");
					builder1.setCancelable(true);
					builder1.setNeutralButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});

					AlertDialog alert11 = builder1.create();
					alert11.show();
				}
			}
		});
	}
	private void spinner() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.tenurePeriod, R.layout.spinner);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tenure.setAdapter(adapter);

		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
				R.array.noti, R.layout.spinner);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		notif.setAdapter(adapter1);
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
