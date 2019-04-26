package com.example.suhei.irent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class displayPropertyTen extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String pID, oID, message, sendeeID, air, bed, study, closet, electric, Wifi, washing
            , dry, gas, freeze, microwave, filter;
    private TextView FName;
    private TextView LName;
    private CircleImageView UserImage;
    private CardView cardView;
    private Button rent;
    private ImageButton like;
    private DocumentReference contact;
    private AppCompatCheckBox ac, beds,table, wardrobe, electricity, wifi, wmachini, dryer, stove, fridge, oven, water;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ten_property_display);

        fStore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        fStore.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();
        sendeeID = mAuth.getCurrentUser().getUid();

        pID = getIntent().getStringExtra("property_id");
        fStore.collection("Properties").document(pID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String imageURL = task.getResult().getString("image_url");
                String imageThumb = task.getResult().getString("image_thumb");
                String propertyname = task.getResult().getString("property_name");
                String house = task.getResult().getString("house_type");
                String tenure = task.getResult().getString("tenure_period");
                String prior = task.getResult().getString("prior_notification");
                String rules = task.getResult().getString("additional_rules");
                String bedrooms = task.getResult().getString("no_of_bedrooms");
                String bathrooms = task.getResult().getString("no_of_bathrooms");
                //String occupants = task.getResult().getString("occupants");
                String secDeposit = task.getResult().getString("security_deposit");
                String keyDeposit = task.getResult().getString("key_deposit");
                String advanceRental = task.getResult().getString("advance_rental");
                String rental = task.getResult().getString("monthly_rental");
                setdata(imageURL, imageThumb, propertyname, house, tenure, prior, rules, bedrooms, bathrooms/*, occupants*/, secDeposit, keyDeposit, advanceRental, rental);
                oID = task.getResult().getString("owner_id");

                //---------------------------------------Owner Cardview-----------------------//
                fStore.collection("Users").document(oID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            String firstname = task.getResult().getString("fname");
                            String lastname = task.getResult().getString("lname");
                            String userImage = task.getResult().getString("image_url");
                            String thumb = task.getResult().getString("image_thumb");
                            UserImage = findViewById(R.id.ownerImage);
                            FName = findViewById(R.id.fname);
                            LName = findViewById(R.id.lname);
                            cardView = findViewById(R.id.notificationCard);

                            FName.setText(firstname);
                            LName.setText(lastname);
                            RequestOptions placeholderOption = new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL);

                            placeholderOption.placeholder(R.drawable.hdpi);
                            Glide.with(displayPropertyTen.this).applyDefaultRequestOptions(placeholderOption)
                                    .load(userImage)
                                    .thumbnail(Glide.with(displayPropertyTen.this).load(thumb))
                                    .into(UserImage);

                            cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(displayPropertyTen.this, userOwnerDetails.class);
                                    intent.putExtra("oID", oID);
                                    startActivity(intent);
                                }
                            });

                        } else {

                            //Firebase Exception
                        }
                    }
                });
                //-------------------------Access via Likes---------------------------//
                if (getIntent().hasExtra("likes")) {

                    fStore.collection("Properties").document(pID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            oID = task.getResult().getString("owner_id");

                            fStore.collection("Users").document(oID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()) {

                                        String firstname = task.getResult().getString("fname");
                                        String lastname = task.getResult().getString("lname");
                                        String userImage = task.getResult().getString("image_url");

                                        UserImage = findViewById(R.id.ownerImage);
                                        FName = findViewById(R.id.fname);
                                        LName = findViewById(R.id.lname);
                                        cardView = findViewById(R.id.notificationCard);

                                        FName.setText(firstname);
                                        LName.setText(lastname);
                                        RequestOptions placeholderOption = new RequestOptions();

                                        placeholderOption.placeholder(R.drawable.hdpi);
                                        Glide.with(displayPropertyTen.this).applyDefaultRequestOptions(placeholderOption)
                                                .load(userImage).into(UserImage);

                                        cardView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(displayPropertyTen.this, userOwnerDetails.class);
                                                intent.putExtra("oID", oID);
                                                startActivity(intent);
                                            }
                                        });

                                    } else {

                                        //Firebase Exception
                                    }
                                }
                            });
                            rent.setVisibility(View.INVISIBLE);
                            like.setVisibility(View.INVISIBLE);
                        }
                    });
                }            //-------------------------------End of contract Notification--------------------------------//
                if (getIntent().getExtras().containsKey("contract")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(displayPropertyTen.this);
                    builder.setMessage("Would you like to extend your contract?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    final Map<String, Object> notificationMessage = new HashMap<>();

                                    final String message = getString(R.string.notification12);

                                    notificationMessage.put("message", message);
                                    notificationMessage.put("sendeeID", sendeeID);
                                    notificationMessage.put("property_id", pID);

                                    fStore.collection("Users").document(oID).collection("Notifications")
                                            .add(notificationMessage).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            displayPropertyTen.this.finish();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    final Map<String, Object> notificationMessage = new HashMap<>();
                                    fStore.collection("Occupants").document(sendeeID).update("Tenure", "");

                                    final String message = getString(R.string.notification13);

                                    notificationMessage.put("message", message);
                                    notificationMessage.put("sendeeID", sendeeID);
                                    notificationMessage.put("property_id", pID);
                                    fStore.collection("Users").document(oID).collection("Notifications")
                                            .add(notificationMessage).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            displayPropertyTen.this.finish();
                                        }
                                    });
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                } else if (getIntent().getExtras().containsKey("contract1")) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(displayPropertyTen.this);
                    builder1.setTitle("Time to move");
                    builder1.setMessage("Oh no, looks like time is up and the owner wants their property back. We suggest you to go through our property list and build your like list.");
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

                //-----------------------set Amenities---------------------//

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

                final String lid = "" + pID + "" + sendeeID;
                like = findViewById(R.id.like);

                fStore.collection("Likes").document(lid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()) {
                            like.setImageDrawable(getDrawable(R.drawable.ic_favorite_black_30dp));
                        } else {
                            like.setImageDrawable(getDrawable(R.drawable.ic_favorite_black_36dp));
                        }

                    }
                });

                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        like.setEnabled(isInternetOn());

                        if (like.isEnabled()) {
                            fStore.collection("Likes").document(lid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.getResult().exists()) {

                                        fStore.collection("Likes").document(lid).delete();

                                    } else {
                                        Map<String, Object> likes = new HashMap<>();
                                        likes.put("timestamp", FieldValue.serverTimestamp());
                                        likes.put("sendee_id", sendeeID);
                                        likes.put("property_id", pID);

                                        fStore.collection("Likes").document(lid).set(likes);
                                    }
                                }
                            });
                        }else{
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(displayPropertyTen.this);
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

                rent = findViewById(R.id.rentbtn);
                contact = fStore.collection("Occupants").document(sendeeID);
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    String stat = task.getResult().getString("status");
                                    String property_id = task.getResult().getString("property_id");
                                    if (stat.equals("overdue") && property_id.equals(pID)) {
                                        rent.setEnabled(true);
                                        rent.setText("Pay Rent");
                                        rent.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(displayPropertyTen.this, Payment.class);
                                                intent.putExtra("rent", "");
                                                intent.putExtra("property_id", pID);
                                                intent.putExtra("from_user_id", oID);
                                                startActivity(intent);
                                            }
                                        });
                                    } else {
                                        rent.setEnabled(false);
                                        rent.setBackground(getResources().getDrawable(R.drawable.buttongrad));
                                        rent.setTextColor(getResources().getColor(R.color.text2));
                                    }
                                }
                            });
                        } else {
                            rent.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rent.setEnabled(isInternetOn());

                                    if(rent.isEnabled()) {

                                        rent.setEnabled(false);
                                        rent.setBackground(getResources().getDrawable(R.drawable.buttongrad));
                                        rent.setTextColor(getResources().getColor(R.color.text2));
                                        rent.setText("Request Sent");
                                        message = getResources().getString(R.string.notification1);

                                        if (!TextUtils.isEmpty(message)) {

                                            Map<String, Object> notificationMessage = new HashMap<>();
                                            notificationMessage.put("message", message);
                                            notificationMessage.put("sendeeID", sendeeID);
                                            notificationMessage.put("property_id", pID);

                                            fStore.collection("Users/" + oID + "/Notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(displayPropertyTen.this, "Request Sent.", Toast.LENGTH_LONG).show();

                                                    if (getIntent().hasExtra("declined")) {

                                                        rent.setEnabled(true);
                                                        rent.setBackground(getResources().getDrawable(R.drawable.buttongrad2));
                                                        rent.setTextColor(getResources().getColor(R.color.inputText));
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(displayPropertyTen.this, "Error: " + e, Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }else{
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(displayPropertyTen.this);
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

    private void setdata(String imageURL, String imageThumb, String propertyname, String house, String tenure, String prior, String rules, String bedrooms,
                         String bathrooms /*, String occupants*/, String secDeposit, String keyDeposit,
                         String advanceRental, String rental) {

        ImageView image = findViewById(R.id.p1);
        Glide.with(this)
                .load(imageURL)
                .thumbnail(Glide.with(this).load(imageThumb))
                .into(image);

        TextView name = findViewById(R.id.propertynametxt);
        name.setText(propertyname);

        TextView housetype = findViewById(R.id.house);
        housetype.setText(house);

        TextView tenurep = findViewById(R.id.tenure);
        tenurep.setText("Tenure Period: " + tenure);

        TextView prion = findViewById(R.id.notif);
        prion.setText("Notify " + prior + " in advance before moving out");

        TextView arules = findViewById(R.id.rules);
        arules.setText(rules);

        TextView bed = findViewById(R.id.Bedroom);
        bed.setText(bedrooms + " Bedrooms");

        TextView bath = findViewById(R.id.bathroom);
        bath.setText(bathrooms + " Bathrooms");

        /*TextView occ = findViewById(R.id.occupants);
        occ.setText(occupants);*/

        TextView sec = findViewById(R.id.Sec_dep);
        sec.setText("Security Deposit: RM " + secDeposit);

        TextView key = findViewById(R.id.key_dep);
        key.setText("Key Deposit: RM " + keyDeposit);

        TextView adv = findViewById(R.id.adv_rent);
        adv.setText("Advance Rental: RM " + advanceRental);

        TextView rent = findViewById(R.id.month_rent);
        rent.setText("Monthly Rental: RM " + rental);
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
