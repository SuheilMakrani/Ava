package com.example.suhei.irent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class userLogin extends Activity {

	private EditText loginEmailText;
	private EditText loginPasswordText;
	private Button loginButton;
	private Button loginRegButton;
	private String uID, UserTypes;

	private FirebaseAuth mAuth = FirebaseAuth.getInstance();

	private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
	private ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
				.setPersistenceEnabled(true)
				.build();
		fStore.setFirestoreSettings(settings);

		loginEmailText = findViewById(R.id.login_email);
		loginPasswordText = findViewById(R.id.login_password);
		loginButton = findViewById(R.id.login_button);
		loginRegButton = findViewById(R.id.login_reg_button);
		progress = new ProgressDialog(this);

		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loginButton.setEnabled(isInternetOn());
				if(loginButton.isEnabled()) {

					progress.setTitle("Login");
					progress.setMessage("Welcome back.");
					progress.setCanceledOnTouchOutside(false);
					progress.setCancelable(false);
					progress.show();

					String loginEmail = loginEmailText.getText().toString();
					String loginPass = loginPasswordText.getText().toString();

					if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)) {

						mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
							@Override
							public void onComplete(@NonNull Task<AuthResult> task) {
								if (task.isSuccessful()) {

									String token_id = FirebaseInstanceId.getInstance().getToken();
									String current_id = mAuth.getCurrentUser().getUid();

									Map<String, Object> tokenMap = new HashMap<>();
									tokenMap.put("token_id", token_id);

									fStore.collection("Users").document(current_id).set(tokenMap, SetOptions.merge())
											.addOnSuccessListener(new OnSuccessListener<Void>() {
										@Override
										public void onSuccess(Void aVoid) {
											progress.dismiss();
											mainPage();
										}
									}).addOnFailureListener(new OnFailureListener() {
										@Override
										public void onFailure(@NonNull Exception e) {
											Toast.makeText(userLogin.this, "Tokenisation failed: " + e, Toast.LENGTH_LONG).show();
										}
									});
								} else {
									progress.dismiss();
									String err = task.getException().getMessage();
									Toast.makeText(userLogin.this, "Error:" + err, Toast.LENGTH_LONG).show();
								}
							}
						});
					}
				}else{
					android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(userLogin.this);
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
			}
		});
		loginRegButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(userLogin.this, userCreateAccount.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		FirebaseUser currentUser = mAuth.getCurrentUser();
		if (currentUser != null) {
			mainPage();
		}
	}
	private void mainPage() {
		Intent intent  = new Intent(userLogin.this, userSort.class);
		startActivity(intent);
		finish();
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
