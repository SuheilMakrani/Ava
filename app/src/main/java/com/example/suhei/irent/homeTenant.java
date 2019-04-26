package com.example.suhei.irent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class homeTenant extends AppCompatActivity {
	private Toolbar mainToolbar;
	private FirebaseAuth mAuth;
	private FirebaseFirestore fStore;
	private String userID, pid;
	private fragTenProperty fragTenProperty;
	private fragMessages fragTenMessages;
	private fragTenNotification fragTenNotification;
	private fragTenAllproperty fragTenAllproperty;
	private fragTenLikes fragTenLikes;
	private BottomNavigationView Nav;
	private ProgressDialog progress;
	private DatabaseReference databaseReference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tenant_home);

		mAuth = FirebaseAuth.getInstance();
		fStore = FirebaseFirestore.getInstance();
		userID = mAuth.getCurrentUser().getUid();
		databaseReference = FirebaseDatabase.getInstance().getReference();
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
			fragTenProperty = new fragTenProperty();
			fragTenNotification = new fragTenNotification();
			fragTenMessages = new fragMessages();
			fragTenAllproperty = new fragTenAllproperty();
			fragTenLikes = new fragTenLikes();
			//Setting default frame layout
			fragmentChange(fragTenAllproperty);

			Nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

				@Override
				public boolean onNavigationItemSelected(@NonNull MenuItem item) {

					switch (item.getItemId()) {
						case R.id.tenpropertynav:
							fragmentChange(fragTenProperty);
							return true;
						case R.id.tennotification:
							fragmentChange(fragTenNotification);
							return true;
						case R.id.tenmessages:
							fragmentChange(fragTenMessages);
							return true;
						case R.id.allproperty:
							fragmentChange(fragTenAllproperty);
							return true;
						case R.id.tenlikes:
							fragmentChange(fragTenLikes);
							return true;
						default:
							fragmentChange(fragTenAllproperty);
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
					android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(homeTenant.this);
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
				Intent account = new Intent(homeTenant.this, userAccountSettings.class);
				startActivity(account);
				break;

			case R.id.susan:
				Intent sus = new Intent(homeTenant.this, Ava.class);
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
		Intent login = new Intent(homeTenant.this, userLogin.class);
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
	private void fragmentChange (Fragment fragment){
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.framelayout1, fragment);
		fragmentTransaction.commit();
	}
}

