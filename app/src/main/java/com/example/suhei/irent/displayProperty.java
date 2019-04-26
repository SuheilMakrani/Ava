package com.example.suhei.irent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Empty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class displayProperty extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private com.github.clans.fab.FloatingActionButton delete, edit;
    private String pID, uID, oID, air, bed, study, closet, electric, Wifi, washing
            , dry, gas, freeze, microwave, filter;
    private RecyclerView occs;
    private List<postTenantImage> postTenantImages;
    private adapterTenantImage adapterTenantImage;
    private AppCompatCheckBox ac, beds,table, wardrobe, electricity, wifi, wmachini, dryer, stove, fridge, oven, water;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_property);

        fStore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        fStore.setFirestoreSettings(settings);

        oID = mAuth.getCurrentUser().getUid();
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

                //---------------------------Access via Notification----------------------------//

                if (getIntent().getExtras().containsKey("rent")) {

                    uID = getIntent().getStringExtra("from_user_id");

                    AlertDialog.Builder builder = new AlertDialog.Builder(displayProperty.this);
                    builder.setMessage("Would you like to extend your contract with your Tenant?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    fStore.collection("Occupants").document(uID).update("Tenure", "renew");

                                    final Map<String, Object> notificationMessage = new HashMap<>();

                                    final String message = getString(R.string.notification10);

                                    notificationMessage.put("message", message);
                                    notificationMessage.put("sendeeID", oID);
                                    notificationMessage.put("property_id", pID);
                                    fStore.collection("Users").document(uID).collection("Notifications")
                                            .add(notificationMessage).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            displayProperty.this.finish();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    final Map<String, Object> notificationMessage = new HashMap<>();
                                    final String message = getString(R.string.notification11);

                                    notificationMessage.put("message", message);
                                    notificationMessage.put("sendeeID", oID);
                                    notificationMessage.put("property_id", pID);
                                    fStore.collection("Users").document(uID).collection("Notifications")
                                            .add(notificationMessage).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            displayProperty.this.finish();
                                        }
                                    });
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
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



                delete = findViewById(R.id.delete);
                edit = findViewById(R.id.edit);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete.setEnabled(isInternetOn());

                        if(delete.isEnabled()) {
                            deleteItem();
                        }else{
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(displayProperty.this);
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
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        edit.setEnabled(isInternetOn());

                        if(edit.isEnabled()) {
                            editItem();
                        }else{
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(displayProperty.this);
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

                postTenantImages = new ArrayList<>();
                occs = findViewById(R.id.occs);
                adapterTenantImage = new adapterTenantImage(displayProperty.this, postTenantImages);
                occs.setHasFixedSize(true);
                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(displayProperty.this, LinearLayoutManager.HORIZONTAL, false);
                occs.setLayoutManager(linearLayoutManager);

                Query query = fStore.collection("Occupants").whereEqualTo("property_id", pID);
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                postTenantImage postTenantImage = documentChange.getDocument().toObject(postTenantImage.class);
                                postTenantImages.add(postTenantImage);
                                adapterTenantImage.notifyDataSetChanged();
                            }
                        }
                    }
                });
                occs.setAdapter(adapterTenantImage);
            }
        });
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

    private void deleteItem() {

        if (adapterTenantImage.getItemCount() == 0) {
            Delete();
        } else {
            cant();
        }
    }
    private void editItem() {

        if (adapterTenantImage.getItemCount() == 0) {
            edit();
        } else {
            cant();
        }
    }
    public void Delete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(displayProperty.this);
        builder.setMessage("Are you sure you want to delete your property?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        fStore.collection("Properties").document("" + pID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                fStore.collection("Likes").whereEqualTo("property_id", pID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot document : task.getResult()) {
                                                fStore.collection("Likes").document(document.getId()).delete();
                                            }
                                        }
                                    }
                                });
                                Toast.makeText(displayProperty.this, "Property Deleted", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(displayProperty.this, homeOwner.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(displayProperty.this, "Error Deleting", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void edit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(displayProperty.this);
        builder.setMessage("Are you sure you want to edit your property?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(displayProperty.this, editProperty.class);
                                intent.putExtra("pid", pID);
                                startActivity(intent);
                                finish();
                            }
                    })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void cant(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(displayProperty.this);
        builder1.setTitle("Cant delete or edit");
        builder1.setMessage("Oh no, looks like there is someone staying in the property. You can only delete or edit once contract is over.");
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