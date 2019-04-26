package com.example.suhei.irent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class homeOwner extends AppCompatActivity {

	private Toolbar mainToolbar;
	private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
	private FirebaseAuth mAuth = FirebaseAuth.getInstance();
	private FirebaseFirestore fStore;
	private String userID;
	private BottomNavigationView Nav;
	private fragProperty fragProperty;
	private fragTenant fragTenant;
	private fragMessages fragMessages;
	private fragNotification fragNotification;
	private ProgressDialog progress;

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_owner_home);

		userID = mAuth.getCurrentUser().getUid();
		fStore = FirebaseFirestore.getInstance();
		FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
				.setPersistenceEnabled(true)
				.build();
		fStore.setFirestoreSettings(settings);

		mainToolbar = findViewById(R.id.main_toolbar1);
		setSupportActionBar(mainToolbar);

		progress = new ProgressDialog(this);

		if(mAuth.getCurrentUser() != null) {
			//Navigation bar
			Nav = findViewById(R.id.nav);
			//Fragments
			fragProperty = new fragProperty();
			fragTenant = new fragTenant();
			fragMessages = new fragMessages();
			fragNotification = new fragNotification();
			//Setting default frame layout
			fragmentChange(fragProperty);


			Nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

				//Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.framelayout);

				@Override
				public boolean onNavigationItemSelected(@NonNull MenuItem item) {
					switch (item.getItemId()) {
						case R.id.propertynav:
							fragmentChange(fragProperty);
							return true;
						case R.id.tenantNav:
							fragmentChange(fragTenant);
							return true;
						case R.id.addProperty1:
							Intent intent = new Intent(homeOwner.this, AddProperty.class);
							startActivity(intent);
							return true;
						case R.id.messages:
							fragmentChange(fragMessages);
							return true;
						case R.id.notification:
							fragmentChange(fragNotification);
							return true;
						default:
							fragmentChange(fragProperty);
							return true;
					}
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Pass in the menu file
		getMenuInflater().inflate(R.menu.main_menu, menu);

		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.logout_action_button:
				item.setEnabled(isInternetOn());
				if(item.isEnabled()) {
					logout();
				}else{
					android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(homeOwner.this);
					builder1.setTitle("No internet Connection");
					builder1.setMessage("Oh no, looks like you do not have internet connection. Please try again later.");
					builder1.setCancelable(true);
					builder1.setNeutralButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});

					android.app.AlertDialog alert11 = builder1.create();
					alert11.show();
				}
				break;

			case R.id.account_setting_button:
				Intent account = new Intent(homeOwner.this, userAccountSettings.class);
				startActivity(account);
				break;

			case R.id.susan:
				Intent sus = new Intent(homeOwner.this, Ava.class);
				startActivity(sus);
				break;
		}
		return false;
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
	private void fragmentChange (Fragment fragment){
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.framelayout, fragment);
		fragmentTransaction.commit();
	}
	@Override
	protected void onResume() {
		super.onResume();
		databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Online").setValue(true);
	}
	@Override
	protected void onPause () {
		super.onPause();
		if(mAuth.getCurrentUser() == null){
			sendToLogin();
		}else{
			databaseReference.child("Users").child(userID).child("Online").setValue(ServerValue.TIMESTAMP);
		}
	}
	//userLogin to the app
	private void sendToLogin() {
		Intent login = new Intent(homeOwner.this, userLogin.class);
		startActivity(login);
		finish();
	}
	//Logout of the APP
	private void logout() {
		databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Online").setValue(ServerValue.TIMESTAMP);
		Map<String, Object> tokenMapRemove = new HashMap<>();
		tokenMapRemove.put("token_id","");
		fStore.collection("Users").document(userID).update(tokenMapRemove);
		mAuth.signOut();
		progress.dismiss();
		sendToLogin();
	}
}

