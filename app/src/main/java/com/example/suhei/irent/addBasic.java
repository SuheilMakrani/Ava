package com.example.suhei.irent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class addBasic extends AppCompatActivity {

    private Spinner housechoice;
    private TextView housedesc;
    private EditText propertyName, bedrooms, bathrooms, occupants;
    private String uID, air, bed, study, closet, electric, Wifi, washing, dry, gas, freeze, microwave, filter;
    private AppCompatCheckBox ac, beds,table, wardrobe, electricity, wifi, wmachini, dryer, stove, fridge, oven, water;
    private ProgressDialog progress;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore fStore;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basics);

        Toolbar addProp1 = findViewById(R.id.addProp2);
        setSupportActionBar(addProp1);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Help us identify your property");
        addProp1.setTitleTextColor(getResources().getColor(R.color.inputText));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        fStore.setFirestoreSettings(settings);
        uID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        progress = new ProgressDialog(this);
        propertyName = findViewById(R.id.pname);
        bedrooms = findViewById(R.id.noBedrooms);
        bathrooms = findViewById(R.id.editText4);
        occupants = findViewById(R.id.no_of_occupants);
        housechoice = findViewById(R.id.propertyType);
        housedesc = findViewById(R.id.propertyDescription);
        final Button next = findViewById(R.id.next);
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

        description();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next.setEnabled(isInternetOn());
                if(next.isEnabled()){
                    final String house = housechoice.getSelectedItem().toString();
                    final String bedroom = bedrooms.getText().toString();
                    final String bathroom = bathrooms.getText().toString();
                    final String occupant = occupants.getText().toString();
                    final String pname = propertyName.getText().toString();
                    if(ac.isChecked()){ air = "true"; }else{ air = "false"; }
                    if(beds.isChecked()){ bed = "true"; }else{ bed = "false"; }
                    if(table.isChecked()){ study = "true"; }else{ study = "false"; }
                    if(wardrobe.isChecked()){ closet = "true"; }else{ closet = "false"; }
                    if(electricity.isChecked()){ electric = "true"; }else{ electric = "false"; }
                    if(wifi.isChecked()){ Wifi = "true"; }else{ Wifi = "false"; }
                    if(wmachini.isChecked()){ washing = "true"; }else{ washing = "false"; }
                    if(dryer.isChecked()){ dry = "true"; }else{ dry = "false"; }
                    if(stove.isChecked()){ gas = "true"; }else{ gas = "false"; }
                    if(fridge.isChecked()){ freeze = "true"; }else{ freeze = "false"; }
                    if(oven.isChecked()){ microwave = "true"; }else{ microwave = "false"; }
                    if(water.isChecked()){ filter = "true"; }else{ filter = "false"; }

                    if (!TextUtils.isEmpty(house) && !TextUtils.isEmpty(bedroom) & !TextUtils.isEmpty(bathroom) && !TextUtils.isEmpty(occupant)) {
                        DocumentReference ref = fStore.collection("Properties").document();
                        final String pid = ref.getId();

                        Intent intent = new Intent(addBasic.this, AddProperty.class);
                        intent.putExtra("state", "success");
                        intent.putExtra("owner_id", uID);
                        intent.putExtra("property_name", pname);
                        intent.putExtra("house_type", house);
                        intent.putExtra("no_of_bedrooms", bedroom);
                        intent.putExtra("no_of_bathrooms", bathroom);
                        intent.putExtra("occupants", occupant);
                        intent.putExtra("pId", pid);
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

                        Toast.makeText(addBasic.this, "Property added ", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        finish();

                    }else {
                        Toast.makeText(addBasic.this, "Enter All fields", Toast.LENGTH_LONG).show();
                    }
                }else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(addBasic.this);
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

    private void description(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.housetypes, R.layout.spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        housechoice.setAdapter(adapter);

        housechoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                String selItem = housechoice.getItemAtPosition(arg2).toString();

                switch (selItem) {
                    case "mansion": {
                        String mess = getString(R.string.mansionDesc);
                        housedesc.setText(mess);
                        break;
                    }
                    case "bungalow": {
                        String mess = getString(R.string.bungalowDesc);
                        housedesc.setText(mess);
                        break;
                    }
                    case "villa": {
                        String mess = getString(R.string.villasDesc);
                        housedesc.setText(mess);
                        break;
                    }
                    case "terrace": {
                        String mess = getString(R.string.terraceDesc);
                        housedesc.setText(mess);
                        break;
                    }
                    case "townhouse": {
                        String mess = getString(R.string.townhouseDesc);
                        housedesc.setText(mess);
                        break;
                    }
                    case "apartment": {
                        String mess = getString(R.string.apartmentDesc);
                        housedesc.setText(mess);
                        break;
                    }
                    case "condominium": {
                        String mess = getString(R.string.condominiumDesc);
                        housedesc.setText(mess);
                        break;
                    }
                    case "penthouse": {
                        String mess = getString(R.string.penthouseDesc);
                        housedesc.setText(mess);
                        break;
                    }
                    case "soho": {
                        String mess = getString(R.string.sohoDesc);
                        housedesc.setText(mess);
                        break;
                    }
                    case "shop houses": {
                        String mess = getString(R.string.shophousesDesc);
                        housedesc.setText(mess);
                        break;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
