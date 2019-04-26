package com.example.suhei.irent;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class addPropertyImages extends AppCompatActivity {

	private Toolbar addProp2;
	private ImageView propImage;
	private Uri propUri = null, download, thumb;
	private Button next1;
	private String pId, house,bedroom, bathroom, occupant, pname, oID, air, bed
			, study, closet, electric, Wifi, washing, dry, gas, freeze, microwave, filter;
	private FirebaseFirestore fStore;
	private StorageReference storageReference;
	private Bitmap compressedImageFile;
	private ProgressDialog progress;
	private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
	private FirebaseAuth mAuth = FirebaseAuth.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_property_images);


		storageReference = FirebaseStorage.getInstance().getReference();
		fStore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        fStore.setFirestoreSettings(settings);

		addProp2 = findViewById(R.id.addProp2);
		addProp2.setTitleTextColor(getResources().getColor(R.color.inputText));
		setSupportActionBar(addProp2);

		getSupportActionBar().setTitle("Show us how your property looks like");

		propImage = findViewById(R.id.p1);
		next1 = findViewById(R.id.next1);
		progress = new ProgressDialog(this);

		if(getIntent().hasExtra("pId")){
			pId = getIntent().getStringExtra("pId");
            oID = getIntent().getStringExtra("owner_id");
            house = getIntent().getStringExtra("house_type");
            bedroom = getIntent().getStringExtra("no_of_bedrooms");
            bathroom = getIntent().getStringExtra("no_of_bathrooms");
            occupant = getIntent().getStringExtra("occupants");
            pname = getIntent().getStringExtra("property_name");
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

		propImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				propImage.setEnabled(isInternetOn());
				if(propImage.isEnabled()) {
					uploadPic();
				}else{
					AlertDialog.Builder builder1 = new AlertDialog.Builder(addPropertyImages.this);
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

		next1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				next1.setEnabled(isInternetOn());
				if(next1.isEnabled()){
					if (propUri != null) {
						progress.setTitle("Uploading Image");
						progress.setMessage("Please wait while we upload your property image.");
						progress.setCanceledOnTouchOutside(false);
						progress.setCancelable(false);
						progress.show();
						// PHOTO UPLOAD
						final StorageReference ref = storageReference.child("PropertyImages/"+pId+".jpg");
						final StorageReference thumbfilepath = storageReference.child("PropertyImages").child("thumbs").child(pId+".jpg");
						UploadTask filepath = ref.putFile(propUri);

						filepath.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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

									File newThumbFile = new File(propUri.getPath());
									try {

										compressedImageFile = new Compressor(addPropertyImages.this)
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

											String downloadThumb = task.getResult().toString();

											Intent intent = new Intent(addPropertyImages.this, AddProperty.class);
											intent.putExtra("state1", "success1");
											intent.putExtra("pId", pId);
											intent.putExtra("owner_id", oID);
											intent.putExtra("property_name", pname);
											intent.putExtra("house_type", house);
											intent.putExtra("no_of_bedrooms", bedroom);
											intent.putExtra("no_of_bathrooms", bathroom);
											intent.putExtra("occupants", occupant);
											intent.putExtra("image_url", downloadUri);
											intent.putExtra("image_thumb", downloadThumb);
											intent.putExtra("ac", air);
											intent.putExtra("beds", bed);
											intent.putExtra("table", study);
											intent.putExtra("wardrobe", closet);
											intent.putExtra("electricity", electric);
											intent.putExtra("wifi", Wifi);
											intent.putExtra("washing", washing);
											intent.putExtra("dryer", dry);
											intent.putExtra("stove", gas);
											intent.putExtra("fridge", freeze);
											intent.putExtra("oven", microwave);
											intent.putExtra("water", filter);

											progress.dismiss();
											Toast.makeText(addPropertyImages.this, "Property Updated", Toast.LENGTH_LONG).show();
											startActivity(intent);
											finish();
										}
									});
								}
							}
						});
					}else {
						Toast.makeText(addPropertyImages.this, "Enter All fields", Toast.LENGTH_LONG).show();
					}
				}else{
					AlertDialog.Builder builder1 = new AlertDialog.Builder(addPropertyImages.this);
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
	private void uploadPic() {
		//If the user is running marshmellow version or below then the permissions are required
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

			if (ContextCompat.checkSelfPermission(addPropertyImages.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

				Toast.makeText(addPropertyImages.this, "Permission Denied", Toast.LENGTH_LONG).show();
				//app requests permission straight from the phone instead of fixing from settings
				ActivityCompat.requestPermissions(addPropertyImages.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

			} else {
				//allow user to select image and crop it out.
				ImagePick();
			}
		} else {
			//For permission below marshmellow
			ImagePick();
		}
	}

	private void ImagePick() {
		CropImage.activity()
				.setGuidelines(CropImageView.Guidelines.ON)
				.setAspectRatio(1, 1)
				.start(addPropertyImages.this);
	}
	//Get crop result

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			CropImage.ActivityResult result = CropImage.getActivityResult(data);
			if (resultCode == RESULT_OK) {
				propUri = result.getUri();
				propImage.setImageURI(propUri);
			} else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
				Exception e = result.getError();
			}
		}
	}
	@Override
	protected void onStart() {
		super.onStart();
		databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Online").setValue(true);
	}
	@Override
	protected void onStop() {
		super.onStop();
		databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Online").setValue(false);
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