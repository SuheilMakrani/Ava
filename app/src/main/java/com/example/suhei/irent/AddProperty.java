package com.example.suhei.irent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Objects;

public class AddProperty extends AppCompatActivity {

	private CardView propertyImage;
	private CardView agreement;
	private String pid,house,bedroom, bathroom, occupant, pname, oID, imageUrl, imageThumb, air, bed
			, study, closet, electric, Wifi, washing, dry, gas, freeze, microwave, filter;
	private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
	private FirebaseAuth mAuth = FirebaseAuth.getInstance();

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_property);

		Toolbar addProp = findViewById(R.id.addProp);
		addProp.setTitleTextColor(getResources().getColor(R.color.inputText));
		setSupportActionBar(addProp);

		Objects.requireNonNull(getSupportActionBar()).setTitle("Lets List Your Property");

		CardView();
	}
	private void CardView(){
		final CardView basics = findViewById(R.id.online);
		propertyImage = findViewById(R.id.images);
		agreement = findViewById(R.id.money);

		propertyImage.setActivated(false);
		agreement.setActivated(false);

		basics.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                basics.setEnabled(isInternetOn());
			    if(basics.isEnabled()){
                    Intent intent = new Intent(AddProperty.this, addBasic.class);
                    startActivity(intent);
				}else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AddProperty.this);
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

		if(getIntent().hasExtra("owner_id")){
			pid = getIntent().getStringExtra("pId");
			oID = getIntent().getStringExtra("owner_id");
			house = getIntent().getStringExtra("house_type");
			bedroom = getIntent().getStringExtra("no_of_bedrooms");
			bathroom = getIntent().getStringExtra("no_of_bathrooms");
			occupant = getIntent().getStringExtra("occupants");
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

		if(getIntent().hasExtra("state")){
			if (getIntent().getStringExtra("state").equals("success")){
				propertyImage.setActivated(true);
			}else{
				propertyImage.setActivated(false);
			}
		}else{
			propertyImage.setActivated(false);
		}
		propertyImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                propertyImage.setEnabled(isInternetOn());
				if (!propertyImage.isActivated()) {
					Toast.makeText(AddProperty.this, "Please finish with the addBasic first", Toast.LENGTH_LONG).show();
				} else {
                    if (propertyImage.isEnabled()) {
                        Intent intent = new Intent(AddProperty.this, addPropertyImages.class);
                        if (getIntent().hasExtra("owner_id")) {
                            pid = getIntent().getStringExtra("pId");
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
                        intent.putExtra("owner_id", oID);
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

                        startActivity(intent);
                        finish();
                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AddProperty.this);
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
			}
		});
		if(getIntent().hasExtra("state1")){
			if (getIntent().getStringExtra("state1").equals("success1")){
				agreement.setActivated(true);
			}else{
				agreement.setActivated(false);
			}
		}else{
			agreement.setActivated(false);
		}

		agreement.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View view) {
                agreement.setEnabled(isInternetOn());

				if (!agreement.isActivated()) {
					Toast.makeText(AddProperty.this, "Please finish with the basic and image uploading first", Toast.LENGTH_LONG).show();
				} else {

                    if (agreement.isEnabled()) {
                        Intent intent = new Intent(AddProperty.this, addPropAgreement.class);
                        if(getIntent().hasExtra("image_url")){
                            pid = getIntent().getStringExtra("pId");
                            oID = getIntent().getStringExtra("owner_id");
                            house = getIntent().getStringExtra("house_type");
                            bedroom = getIntent().getStringExtra("no_of_bedrooms");
                            bathroom = getIntent().getStringExtra("no_of_bathrooms");
                            occupant = getIntent().getStringExtra("occupants");
                            pname = getIntent().getStringExtra("property_name");
                            imageUrl = getIntent().getStringExtra("image_url");
                            imageThumb = getIntent().getStringExtra("image_thumb");
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
                        intent.putExtra("owner_id", oID);
                        intent.putExtra("property_name", pname);
                        intent.putExtra("house_type", house);
                        intent.putExtra("no_of_bedrooms", bedroom);
                        intent.putExtra("no_of_bathrooms", bathroom);
                        intent.putExtra("occupants", occupant);
                        intent.putExtra("pId",pid);
                        intent.putExtra("image_url", imageUrl);
                        intent.putExtra("image_thumb", imageThumb);
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

                        startActivity(intent);
                        finish();
                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AddProperty.this);
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
