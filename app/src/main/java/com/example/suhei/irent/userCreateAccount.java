package com.example.suhei.irent;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class userCreateAccount extends AppCompatActivity {

	//Declare the circle view
	private CircleImageView setupImage;
	//Declare the Spinners
	private Spinner gender;
	private Uri mainImageURI, download;
	private EditText firstName, lastName, contactNo, userinfo;
	private String uID;
	private Bitmap compressedImageFile;
	private TextView dob;
	private DatePickerDialog.OnDateSetListener mDateListener;

	private StorageReference storageReference;
	private FirebaseAuth mAuth;
	private FirebaseFirestore fStore;
	private DatabaseReference databaseReference;
	private boolean change = false;

	private EditText regEmail;
	private EditText regPassword;
	private EditText confirmPassword;
	private Button create;
	private ProgressDialog progress;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);

		//Firebase functions
		mAuth = FirebaseAuth.getInstance();
		storageReference = FirebaseStorage.getInstance().getReference();
		fStore = FirebaseFirestore.getInstance();
		final FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
				.setPersistenceEnabled(true)
				.build();
		fStore.setFirestoreSettings(settings);

		//Initialise the data fields
		firstName = findViewById(R.id.first_name);
		lastName = findViewById(R.id.last_name);
		contactNo = findViewById(R.id.reg_contact);
		dob = findViewById(R.id.date_pick);
		setupImage = findViewById(R.id.setup_image);
		userinfo = findViewById(R.id.userInfo);

		Date();

		spinner();
		regEmail = findViewById(R.id.email);
		regPassword = findViewById(R.id.reg_pass);
		confirmPassword = findViewById(R.id.reg_confirm_pass);
		create = findViewById(R.id.reg_btn);
		progress = new ProgressDialog(this);

		setupImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setupImage.setEnabled(isInternetOn());
				if(setupImage.isEnabled()){
					//If the user is running marshmellow version or below then the permissions are required
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

						if (ContextCompat.checkSelfPermission(userCreateAccount.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

							Toast.makeText(userCreateAccount.this, "Permission Denied", Toast.LENGTH_LONG).show();
							//app requests permission straight from the phone instead of fixing from settings
							ActivityCompat.requestPermissions(userCreateAccount.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

						} else {
							//allow user to select image and crop it out.
							ImagePick();
						}
					} else {
						//For permission below marshmellow
						ImagePick();
					}
				}else{
					android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(userCreateAccount.this);
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

		create.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				create.setEnabled(isInternetOn());
				if(create.isEnabled()) {

					progress.setTitle("Create Account");
					progress.setMessage("Please wait while we create your account.");
					progress.setCanceledOnTouchOutside(false);
					progress.setCancelable(false);
					progress.show();

					final String email = regEmail.getText().toString();
					String pass = regPassword.getText().toString();
					String confirm_pass = confirmPassword.getText().toString();
					final String FirstName = firstName.getText().toString();
					final String LastName = lastName.getText().toString();
					final String ContactNo = contactNo.getText().toString();
					final String Gender = gender.getSelectedItem().toString();
					final String DOB = dob.getText().toString();
					final String UserInfo = userinfo.getText().toString();

					if (mainImageURI != null && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) & !TextUtils.isEmpty(confirm_pass) && !TextUtils.isEmpty(FirstName) && !TextUtils.isEmpty(LastName) &&
							!TextUtils.isEmpty(ContactNo) && !TextUtils.isEmpty(Gender) && !TextUtils.isEmpty(DOB) && !TextUtils.isEmpty(UserInfo)) {

						if (pass.equals(confirm_pass)) {

							mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

								@Override
								public void onComplete(@NonNull Task<AuthResult> task) {

									if (task.isSuccessful()) {
										final String randomName = UUID.randomUUID().toString();

										uID = mAuth.getCurrentUser().getUid();
										//Create a folder to store the profile Images and and store image as name of user ID.
										final StorageReference ref = storageReference.child("ProfileImages/" + uID + ".jpg");
										final StorageReference thumbfilepath = storageReference.child("ProfileImages").child("thumbs").child(uID + ".jpg");
										UploadTask filepath = ref.putFile(mainImageURI);

										Task<Uri> urlTask = filepath.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
											@Override
											public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
												if (!task.isSuccessful()) {
													throw task.getException();
												}
												return ref.getDownloadUrl();
											}
										}).addOnCompleteListener(new OnCompleteListener<Uri>() {
											@Override
											public void onComplete(@NonNull Task<Uri> task) {
												if (task.isSuccessful()) {
													download = task.getResult();

													final String downloadUri = download.toString();

													File newThumbFile = new File(mainImageURI.getPath());
													try {

														compressedImageFile = new Compressor(userCreateAccount.this)
																.setMaxHeight(100)
																.setMaxWidth(100)
																.setQuality(1)
																.compressToBitmap(newThumbFile);

													} catch (IOException e) {
														e.printStackTrace();
													}

													ByteArrayOutputStream baos = new ByteArrayOutputStream();
													compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
													byte[] thumbData = baos.toByteArray();


													thumbfilepath.putBytes(thumbData).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
														@Override
														public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
															if (!task.isSuccessful()) {
																throw task.getException();
															}
															return thumbfilepath.getDownloadUrl();
														}
													}).addOnCompleteListener(new OnCompleteListener<Uri>() {
														@Override
														public void onComplete(@NonNull Task<Uri> task) {
															if (task.isSuccessful()) {

																String downloadThumb = task.getResult().toString();
																String token_id = FirebaseInstanceId.getInstance().getToken();
																Map<String, Object> usermap = new HashMap<>();
																usermap.put("image_url", downloadUri);
																usermap.put("image_thumb", downloadThumb);
																usermap.put("fname", FirstName);
																usermap.put("lname", LastName);
																usermap.put("contact", ContactNo);
																usermap.put("dob", DOB);
																usermap.put("gender", Gender);
																usermap.put("email", email);
																usermap.put("user_info", UserInfo);
																usermap.put("token_id", token_id);

																fStore.collection("Users").document(uID).set(usermap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
																	@Override
																	public void onComplete(@NonNull Task<Void> task) {
																		if (task.isSuccessful()) {
																			progress.dismiss();
																			Intent intent = new Intent(userCreateAccount.this, userRegChoose.class);
																			startActivity(intent);
																			finish();
																		} else {
																			progress.dismiss();
																			String errorMessage = task.getException().getMessage();
																			Toast.makeText(userCreateAccount.this, "(Firestore Error) : " + errorMessage, Toast.LENGTH_LONG).show();
																		}
																	}
																});
															}
														}
													});
												} else {
													progress.dismiss();
													String errorMessage = task.getException().getMessage();
													Toast.makeText(userCreateAccount.this, "(Image Error) : " + errorMessage, Toast.LENGTH_LONG).show();
												}
											}
										});
									} else {
										progress.dismiss();
										String errorMessage = task.getException().getMessage();
										Toast.makeText(userCreateAccount.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
									}
								}
							});

						} else {
							progress.dismiss();
							Toast.makeText(userCreateAccount.this, "Confirm Password and Password Field doesn't match.", Toast.LENGTH_LONG).show();
						}
					} else {
						progress.dismiss();
						Toast.makeText(userCreateAccount.this, "Enter All fields", Toast.LENGTH_LONG).show();
					}
				}else{
					android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(userCreateAccount.this);
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
	protected void onStart() {
		super.onStart();

		FirebaseUser currentUser = mAuth.getCurrentUser();
		if(currentUser != null){
			sendToMain();
		}
	}
	private void sendToMain() {
		//if statement later
		Intent mainIntent = new Intent(userCreateAccount.this, homeOwner.class);
		startActivity(mainIntent);
		finish();
	}
	private void Date() {
		dob.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Calendar cal = Calendar.getInstance();
				int year = cal.get(Calendar.YEAR);
				int month = cal.get(Calendar.MONTH);
				int date = cal.get(Calendar.DAY_OF_MONTH);

				DatePickerDialog dialog = new DatePickerDialog(userCreateAccount.this,
						android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateListener,year,month,date);
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				dialog.show();
			}
		});

		mDateListener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int year, int month, int date) {
				month = month+1;
				String day = month + "/" +date+"/"+year;
				dob.setText(day);
			}
		};
	}

	private void ImagePick() {
		CropImage.activity()
				.setGuidelines(CropImageView.Guidelines.ON)
				.setAspectRatio(1, 1)
				.start(userCreateAccount.this);
	}
	//Get crop result
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			CropImage.ActivityResult result = CropImage.getActivityResult(data);
			if (resultCode == RESULT_OK) {
				mainImageURI = result.getUri();
				setupImage.setImageURI(mainImageURI);
				change = true;
			} else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
				Exception e = result.getError();
			}
		}
	}
	private void spinner() {
		gender = findViewById(R.id.genderChoice);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.gender, R.layout.spinner);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		gender.setAdapter(adapter);

	}
}
