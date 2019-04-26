package com.example.suhei.irent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StringLoader;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.primitives.Bytes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class userAccountSettings extends AppCompatActivity {

    private String uID, downloadURI;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView name, userInfo, changestatus, changeimage, changeEmail, changePhone, phone, email;
    private CircleImageView image;
    private Button save;
    private Uri mainImageURI, download;
    private Bitmap compressedImageBitmap;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private DocumentReference contact;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_settings);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        fStore.setFirestoreSettings(settings);

        name = findViewById(R.id.last_name2);
        image = findViewById(R.id.setup_image2);
        userInfo = findViewById(R.id.userInfo4);
        phone = findViewById(R.id.reg_contact2);
        email = findViewById(R.id.email2);
        changestatus = findViewById(R.id.changeStatus);
        changeimage = findViewById(R.id.changeImage);
        changeEmail = findViewById(R.id.changeEmail);
        changePhone = findViewById(R.id.changeNumber);
        save = findViewById(R.id.save);

        progress = new ProgressDialog(this);
        uID = mAuth.getCurrentUser().getUid();
        contact = fStore.collection("Users").document(uID);
        contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String fName = task.getResult().getString("fname");
                String lName = task.getResult().getString("lname");
                String Image = task.getResult().getString("image_url");
                String Thumb = task.getResult().getString("image_thumb");
                String info = task.getResult().getString("user_info");
                String mail = task.getResult().getString("email");
                String contact = task.getResult().getString("contact");

                name.setText(fName + " " + lName);
                userInfo.setText("' " + info + " '");
                phone.setText(contact);
                email.setText(mail);

                RequestOptions placeholderOption = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL);

                placeholderOption.placeholder(R.drawable.ic_hdpi160b);
                Glide.with(getApplicationContext())
                        .applyDefaultRequestOptions(placeholderOption)
                        .load(Image)
                        .thumbnail(Glide.with(userAccountSettings.this).load(Thumb))
                        .into(image);
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(userAccountSettings.this);
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(userAccountSettings.this);
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(userAccountSettings.this);

        final EditText txtUrl = new EditText(userAccountSettings.this);
        final EditText txtUrl1 = new EditText(userAccountSettings.this);
        final EditText txtUrl2 = new EditText(userAccountSettings.this);

        changestatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changestatus.setEnabled(isInternetOn());
                if(changestatus.isEnabled()) {

                    // Set the default text to a link of the Queen
                    txtUrl.setHint("Enter your new status");
                    txtUrl.setPadding(20, 50, 20, 20);

                    builder.setTitle("Status")
                            .setView(txtUrl)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    final String url = txtUrl.getText().toString();
                                    progress.setTitle("Changing Status");
                                    progress.setMessage("Please wait while Update your status.");
                                    progress.setCanceledOnTouchOutside(false);
                                    progress.setCancelable(false);
                                    progress.show();
                                    contact.update("user_info", url)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    userInfo.setText("' " + url + " '");
                                                    progress.dismiss();
                                                    Toast.makeText(userAccountSettings.this, "Updated Successfully",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                            .show();
                }else{
                    android.app.AlertDialog.Builder builder3 = new android.app.AlertDialog.Builder(userAccountSettings.this);
                    builder3.setTitle("No internet Connection");
                    builder3.setMessage("Oh no, looks like you do not have internet connection. Please try again later.");
                    builder3.setCancelable(true);
                    builder3.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    android.app.AlertDialog alert11 = builder3.create();
                    alert11.show();
                }
            }
        });
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changestatus.setEnabled(isInternetOn());
                if(changestatus.isEnabled()) {

                    txtUrl1.setHint("Enter your new Email");
                    txtUrl1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    txtUrl1.setPadding(20, 50, 20, 20);

                    builder1.setTitle("Email")
                            .setView(txtUrl1)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    final String url1 = txtUrl1.getText().toString();
                                    progress.setTitle("Changing Email");
                                    progress.setMessage("Please wait while Update your Email.");
                                    progress.setCanceledOnTouchOutside(false);
                                    progress.setCancelable(false);
                                    progress.show();

                                    contact.update("email", url1)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    email.setText(url1);
                                                    progress.dismiss();
                                                    Toast.makeText(userAccountSettings.this, "Updated Successfully",
                                                            Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                            .show();
                }else{
                    android.app.AlertDialog.Builder builder3 = new android.app.AlertDialog.Builder(userAccountSettings.this);
                    builder3.setTitle("No internet Connection");
                    builder3.setMessage("Oh no, looks like you do not have internet connection. Please try again later.");
                    builder3.setCancelable(true);
                    builder3.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    android.app.AlertDialog alert11 = builder3.create();
                    alert11.show();
                }
            }
        });
        changePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changePhone.setEnabled(isInternetOn());
                if(changePhone.isEnabled()) {
                    txtUrl2.setHint("Enter your new Phone number");
                    txtUrl2.setInputType(InputType.TYPE_CLASS_PHONE);
                    txtUrl2.setPadding(20, 50, 20, 20);

                    builder2.setTitle("Phone Number")
                            .setView(txtUrl2)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    final String url2 = txtUrl2.getText().toString();
                                    progress.setTitle("Changing Phone Number");
                                    progress.setMessage("Please wait while Update your Phone number.");
                                    progress.setCanceledOnTouchOutside(false);
                                    progress.setCancelable(false);
                                    progress.show();
                                    contact.update("contact", url2)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    phone.setText(url2);
                                                    progress.dismiss();
                                                    Toast.makeText(userAccountSettings.this, "Updated Successfully",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                            .show();
                }else{
                    android.app.AlertDialog.Builder builder3 = new android.app.AlertDialog.Builder(userAccountSettings.this);
                    builder3.setTitle("No internet Connection");
                    builder3.setMessage("Oh no, looks like you do not have internet connection. Please try again later.");
                    builder3.setCancelable(true);
                    builder3.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    android.app.AlertDialog alert11 = builder3.create();
                    alert11.show();
                }
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                image.setEnabled(isInternetOn());
                if (image.isEnabled()) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(userAccountSettings.this);
                }else{
                    android.app.AlertDialog.Builder builder3 = new android.app.AlertDialog.Builder(userAccountSettings.this);
                    builder3.setTitle("No internet Connection");
                    builder3.setMessage("Oh no, looks like you do not have internet connection. Please try again later.");
                    builder3.setCancelable(true);
                    builder3.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    android.app.AlertDialog alert11 = builder3.create();
                    alert11.show();
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String User = task.getResult().getString("user_type");

                        if(User.equals("Tenant")){
                            Intent intent = new Intent(userAccountSettings.this, homeTenant.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent = new Intent(userAccountSettings.this, homeOwner.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });


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
    //Get crop result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                File thumbFile = new File(mainImageURI.getPath());

                try {
                    compressedImageBitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumbFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumbData = baos.toByteArray();

                progress.setTitle("Changing Image");
                progress.setMessage("Please wait while Update your Profile Image.");
                progress.setCanceledOnTouchOutside(false);
                progress.setCancelable(false);
                progress.show();

                final StorageReference filepath = storageReference.child("ProfileImages/"+uID+".jpg");
                final StorageReference thumbfilepath = storageReference.child("ProfileImages").child("thumbs").child(uID+".jpg");

                filepath.putFile(mainImageURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            Uri downloadUri = task.getResult();
                            downloadURI = downloadUri.toString();

                            /*final UploadTask uploadTask = thumbfilepath;*//*.putBytes(thumbData)*/;
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

                                        final String downloadThumb = task.getResult().toString();

                                        contact.update("image_thumb", downloadThumb);
                                        contact.update("image_url", downloadURI);

                                        progress.dismiss();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(userAccountSettings.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                image.setImageURI(mainImageURI);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception e = result.getError();
            }
        }
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

