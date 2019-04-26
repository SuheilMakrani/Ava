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
import android.support.v7.widget.AppCompatCheckBox;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
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

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class editProperty extends AppCompatActivity {

    private String pID, downloadURI, air, bed, study, closet, electric, Wifi, washing
            , dry, gas, freeze, microwave, filter;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView secDep, keyDep, adv, rent ,rules, changeimage, changeRules, changePay;
    private ImageView image;
    private Button save;
    private Uri mainImageURI, download;
    private Bitmap compressedImageBitmap;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private DocumentReference contact;
    private ProgressDialog progress;
    private AppCompatCheckBox ac, beds,table, wardrobe, electricity, wifi, wmachini, dryer, stove, fridge, oven, water;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_property);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        fStore.setFirestoreSettings(settings);

        secDep = findViewById(R.id.Sec_dep);
        image = findViewById(R.id.p1);
        keyDep = findViewById(R.id.key_dep);
        adv = findViewById(R.id.adv_rent);
        rent = findViewById(R.id.month_rent);
        rules = findViewById(R.id.rules);
        changeimage = findViewById(R.id.editImage);
        changeRules = findViewById(R.id.editRules);
        changePay = findViewById(R.id.editPay);
        save = findViewById(R.id.save);

        ac = findViewById(R.id.aircond);
        beds = findViewById(R.id.beds);
        table = findViewById(R.id.table);
        wardrobe = findViewById(R.id.wardrobe);
        electricity = findViewById(R.id.electricity);
        wifi = findViewById(R.id.wifi);
        wmachini = findViewById(R.id.washing);
        dryer = findViewById(R.id.dryer);
        stove = findViewById(R.id.stove);
        fridge = findViewById(R.id.fridge);
        oven = findViewById(R.id.microwave);
        water = findViewById(R.id.water);


        progress = new ProgressDialog(this);

        pID = getIntent().getStringExtra("pid");

        contact = fStore.collection("Properties").document(pID);

       contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                String imageURL = task.getResult().getString("image_url");
                String propertyname = task.getResult().getString("property_name");
                String house = task.getResult().getString("house_type");
                String tenure = task.getResult().getString("tenure_period");
                String prior = task.getResult().getString("prior_notification");
                String rules1 = task.getResult().getString("additional_rules");
                String bedrooms = task.getResult().getString("no_of_bedrooms");
                String bathrooms = task.getResult().getString("no_of_bathrooms");
                String secDeposit = task.getResult().getString("security_deposit");
                String keyDeposit = task.getResult().getString("key_deposit");
                String advanceRental = task.getResult().getString("advance_rental");
                String rental = task.getResult().getString("monthly_rental");
                String thumb = task.getResult().getString("image_thumb");

                Glide.with(editProperty.this)
                        .load(imageURL)
                        .thumbnail(Glide.with(editProperty.this).load(thumb))
                        .into(image);

                TextView name = findViewById(R.id.propertynametxt);
                name.setText(propertyname);

                TextView housetype = findViewById(R.id.house);
                housetype.setText(house);

                TextView tenurep = findViewById(R.id.tenure);
                tenurep.setText("Tenure Period: "+tenure);

                TextView prion = findViewById(R.id.notif);
                prion.setText("Notify "+prior+" in advance before moving out");

                rules.setText(rules1);

                TextView bed = findViewById(R.id.Bedroom);
                bed.setText(bedrooms+" Bedrooms");

                TextView bath = findViewById(R.id.bathroom);
                bath.setText(bathrooms+" Bathrooms");
                secDep.setText("Security Deposit: RM "+secDeposit);

                keyDep.setText("Key Deposit: RM "+keyDeposit);

                adv.setText("Advance Rental: RM "+advanceRental);

                rent.setText("Monthly Rental: RM "+rental);

            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(editProperty.this);

        final EditText txtUrl = new EditText(editProperty.this);

        changeRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeRules.setEnabled(isInternetOn());
                if(changeRules.isEnabled()){
                    txtUrl.setHint("Enter your new Rules");
                    txtUrl.setPadding(20, 50, 20, 20);

                    builder.setTitle("Status").setView(txtUrl).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            final String url = txtUrl.getText().toString();
                            progress.setTitle("Changing Rules");
                            progress.setMessage("Please wait while Update your Rules.");
                            progress.setCanceledOnTouchOutside(false);
                            progress.setCancelable(false);
                            progress.show();
                            contact.update("additional_rules", url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) { rules.setText(url);
                                    progress.dismiss();
                                    Toast.makeText(editProperty.this, "Updated Successfully",
                                        Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            }
                    }).show();
                }else{
                    android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(editProperty.this);
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
        changePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePay.setEnabled(isInternetOn());
                if(changePay.isEnabled()){
                    AlertDialog.Builder alert = new AlertDialog.Builder(editProperty.this);
                    alert.setTitle("Payment Details");

                    LinearLayout layout = new LinearLayout(editProperty.this);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText Wifi_textbox = new EditText(editProperty.this);
                    Wifi_textbox.setHint("Security Deposit");
                    Wifi_textbox.setPadding(20, 50, 20, 20);
                    Wifi_textbox.setInputType(InputType.TYPE_CLASS_NUMBER);
                    layout.addView(Wifi_textbox);

                    final EditText Mac_textbox = new EditText(editProperty.this);
                    Mac_textbox.setHint("Key Deposit");
                    Mac_textbox.setPadding(20, 50, 20, 20);
                    Mac_textbox.setInputType(InputType.TYPE_CLASS_NUMBER);
                    layout.addView(Mac_textbox);

                    final EditText Floorname = new EditText(editProperty.this);
                    Floorname.setHint("Advance Rental");
                    Floorname.setPadding(20, 50, 20, 20);
                    Floorname.setInputType(InputType.TYPE_CLASS_NUMBER);
                    layout.addView(Floorname);

                    final EditText WingName = new EditText(editProperty.this);
                    WingName.setHint("Monthly Rental");
                    WingName.setPadding(20, 50, 20, 20);
                    WingName.setInputType(InputType.TYPE_CLASS_NUMBER);
                    layout.addView(WingName);

                    alert.setView(layout);

                    alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            progress.setTitle("Changing Payment Details");
                            progress.setMessage("Please wait while Update your Payment Details.");
                            progress.setCanceledOnTouchOutside(false);
                            progress.setCancelable(false);
                            progress.show();

                            final String wname = Wifi_textbox.getText().toString();
                            final String mname = Mac_textbox.getText().toString();
                            final String fname = Floorname.getText().toString();
                            final String wgname = WingName.getText().toString();

                            Map<String, Object>updt = new HashMap<>();
                            updt.put("security_deposit", wname);
                            updt.put("key_deposit", mname);
                            updt.put("advance_rental", fname);
                            updt.put("monthly_rental", wgname);
                            contact.update(updt).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    secDep.setText("Security Deposit: RM "+wname);
                                    keyDep.setText("Key Deposit: RM "+mname);
                                    adv.setText("Advance Rental: RM "+fname);
                                    rent.setText("Monthly Rental: RM "+wgname);

                                    progress.dismiss();
                                }
                            });
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    alert.show();
                }else{
                    android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(editProperty.this);
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
        changeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeimage.setEnabled(isInternetOn());
                if (changeimage.isEnabled()) {
                    CropImage.activity(mainImageURI)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(editProperty.this);
                }else{
                    android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(editProperty.this);
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

        fStore.collection("Properties").document(pID).collection("Amenities").document(pID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                air = task.getResult().getString("ac");
                if(air.equals("true")){
                    ac.setChecked(true);
                }else{ac.setChecked(false);}
                bed = task.getResult().getString("beds");
                if(bed.equals("true")){
                    beds.setChecked(true);
                }else{beds.setChecked(false);}
                dry = task.getResult().getString("dryer");
                if(dry.equals("true")){
                    dryer.setChecked(true);
                }else{dryer.setChecked(false);}
                electric = task.getResult().getString("electricity");
                if(electric.equals("true")){
                    electricity.setChecked(true);
                }else{electricity.setChecked(false);}
                freeze = task.getResult().getString("fridge");
                if(freeze.equals("true")){
                    fridge.setChecked(true);
                }else{fridge.setChecked(false);}
                microwave = task.getResult().getString("oven");
                if(microwave.equals("true")){
                    oven.setChecked(true);
                }else{oven.setChecked(false);}
                gas = task.getResult().getString("stove");
                if(gas.equals("true")){
                    stove.setChecked(true);
                }else{stove.setChecked(false);}
                study = task.getResult().getString("table");
                if(study.equals("true")){
                    table.setChecked(true);
                }else{table.setChecked(false);}
                closet = task.getResult().getString("wardrobe");
                if(closet.equals("true")){
                    wardrobe.setChecked(true);
                }else{wardrobe.setChecked(false);}
                washing = task.getResult().getString("washing");
                if(washing.equals("true")){
                    wmachini.setChecked(true);
                }else{wmachini.setChecked(false);}
                filter = task.getResult().getString("water");
                if(filter.equals("true")){
                    water.setChecked(true);
                }else{water.setChecked(false);}
                Wifi = task.getResult().getString("wifi");
                if(Wifi.equals("true")){
                    wifi.setChecked(true);
                }else{wifi.setChecked(false);}
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                save.setEnabled(isInternetOn());
                if (save.isEnabled()) {

                    if (ac.isChecked()) {
                        air = "true";
                    } else {
                        air = "false";
                    }
                    if (beds.isChecked()) {
                        bed = "true";
                    } else {
                        bed = "false";
                    }
                    if (table.isChecked()) {
                        study = "true";
                    } else {
                        study = "false";
                    }
                    if (wardrobe.isChecked()) {
                        closet = "true";
                    } else {
                        closet = "false";
                    }
                    if (electricity.isChecked()) {
                        electric = "true";
                    } else {
                        electric = "false";
                    }
                    if (wifi.isChecked()) {
                        Wifi = "true";
                    } else {
                        Wifi = "false";
                    }
                    if (wmachini.isChecked()) {
                        washing = "true";
                    } else {
                        washing = "false";
                    }
                    if (dryer.isChecked()) {
                        dry = "true";
                    } else {
                        dry = "false";
                    }
                    if (stove.isChecked()) {
                        gas = "true";
                    } else {
                        gas = "false";
                    }
                    if (fridge.isChecked()) {
                        freeze = "true";
                    } else {
                        freeze = "false";
                    }
                    if (oven.isChecked()) {
                        microwave = "true";
                    } else {
                        microwave = "false";
                    }
                    if (water.isChecked()) {
                        filter = "true";
                    } else {
                        filter = "false";
                    }

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

                    fStore.collection("Properties").document(pID).collection("Amenities").document(pID)
                            .update(propertyMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(editProperty.this, homeOwner.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }else{
                    android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(editProperty.this);
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
                progress.setMessage("Please wait while Update your Property Image.");
                progress.setCanceledOnTouchOutside(false);
                progress.setCancelable(false);
                progress.show();

                final StorageReference filepath = storageReference.child("ProfileImages/"+pID+".jpg");
                final StorageReference thumbfilepath = storageReference.child("ProfileImages").child("thumbs").child(pID+".jpg");

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
                            Toast.makeText(editProperty.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

