package com.example.suhei.irent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Payment extends AppCompatActivity {

    private String pID, user_id, oID;
    private Toolbar mainToolbar;
    private Button Pay;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    private TextView monthrent, secdep, advrent, keydep, total;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        fstore.setFirestoreSettings(settings);

        mainToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mainToolbar);
        mainToolbar.setTitleTextColor(getResources().getColor(R.color.inputText));
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month = month_date.format(c.getTime());
        getSupportActionBar().setTitle(month+" "+year);

        user_id = mAuth.getCurrentUser().getUid();
        Pay = findViewById(R.id.pay);
        monthrent = findViewById(R.id.month_rent2);
        secdep = findViewById(R.id.sec_dep);
        advrent = findViewById(R.id.adv_rent2);
        keydep = findViewById(R.id.key_dep2);
        total = findViewById(R.id.rent2);
        final Bundle extras = getIntent().getExtras();

        if(getIntent().getStringExtra("from_user_id").equals(user_id)){
            fstore.collection("Occupants").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    oID = task.getResult().getString("owner_id");
                }
            });
        }else{
            oID = getIntent().getStringExtra("from_user_id");
        }

        pID = getIntent().getStringExtra("property_id");
        progress = new ProgressDialog(this);

        fstore.collection("Properties").document(pID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String mrent = task.getResult().getString("monthly_rental");
                String sdep = task.getResult().getString("security_deposit");
                String arent = task.getResult().getString("advance_rental");
                String kdep = task.getResult().getString("key_deposit");

                if (extras.containsKey("rent")) {

                    secdep.setText("-");
                    advrent.setText("-");
                    keydep.setText("-");
                    monthrent.setText("RM "+mrent);
                    total.setText("RM "+mrent);
                }else{
                    secdep.setText("RM "+sdep);
                    advrent.setText("RM "+arent);
                    keydep.setText("RM "+kdep);
                    monthrent.setText("RM "+mrent);
                    int q = Integer.parseInt(sdep);
                    int w = Integer.parseInt(arent);
                    int e = Integer.parseInt(kdep);
                    int t = Integer.parseInt(mrent);
                    int fin = q + w +e+t;
                    String tot = String.valueOf(fin);
                    total.setText("RM "+tot);
                }

            }
        });

        Pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pay.setEnabled(isInternetOn());
                if(Pay.isEnabled()) {

                    progress.setTitle("Payment");
                    progress.setMessage("Please wait while we process your payment.");
                    progress.setCanceledOnTouchOutside(false);
                    progress.setCancelable(false);
                    progress.show();

                    if (extras.containsKey("rent")) {

                        final String message = getString(R.string.notification7);

                        if (!TextUtils.isEmpty(message)) {

                            Map<String, Object> notificationMessage = new HashMap<>();
                            notificationMessage.put("message", message);
                            notificationMessage.put("sendeeID", user_id);
                            notificationMessage.put("property_id", pID);

                            fstore.collection("Users/" + oID + "/Notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Map<String, Object> add = new HashMap<>();
                                    add.put("status", "paid");

                                    fstore.collection("Occupants").document(user_id).update(add).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progress.dismiss();
                                            startTimer();
                                            Toast.makeText(Payment.this, "Rent has been paid", Toast.LENGTH_LONG).show();
                                            Intent pay = new Intent(Payment.this, homeTenant.class);
                                            startActivity(pay);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Payment.this, "Error: " + e, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Payment.this, "Error: " + e, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        final String message = getString(R.string.notification4);

                        if (!TextUtils.isEmpty(message)) {

                            Map<String, Object> notificationMessage = new HashMap<>();
                            notificationMessage.put("message", message);
                            notificationMessage.put("sendeeID", user_id);
                            notificationMessage.put("property_id", pID);

                            fstore.collection("Users/" + oID + "/Notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    Map<String, Object> add = new HashMap<>();
                                    add.put("user_id", user_id);
                                    add.put("property_id", pID);
                                    add.put("owner_id", oID);
                                    add.put("status", "paid");
                                    add.put("Tenure", "");

                                    fstore.collection("Occupants").document(user_id).set(add).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startTimer();
                                        startTimer1();
                                        startTimer2();
                                            Toast.makeText(Payment.this, "Payment Made", Toast.LENGTH_LONG).show();
                                            Intent pay = new Intent(Payment.this, homeTenant.class);
                                            startActivity(pay);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Payment.this, "Error: " + e, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Payment.this, "Error: " + e, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }else{
                    android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(Payment.this);
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

    private void startTimer(){

        TimerTask task = new TimerTask() {
            public void run() {
                fstore.collection("Occupants").document(user_id).update("status", "overdue");

                String message = getString(R.string.notification5);
                final Map<String, Object> notificationMessage = new HashMap<>();

                notificationMessage.put("message", message);
                notificationMessage.put("sendeeID", user_id);
                notificationMessage.put("property_id", pID);

                fstore.collection("Users/" + oID + "/Notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        String message1 = getString(R.string.notification6);

                        notificationMessage.put("message", message1);
                        notificationMessage.put("sendeeID", user_id);
                        notificationMessage.put("property_id", pID);

                        fstore.collection("Users/" + user_id + "/Notifications").add(notificationMessage);
                    }
                });
            }
        };
        Timer timer = new Timer("Timer");
        long delay = 60000;
        timer.schedule(task, delay);
    }
    private void startTimer1(){

        final TimerTask task = new TimerTask() {
            public void run() {

                fstore.collection("Occupants").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String tenure = task.getResult().getString("Tenure");
                        if(tenure.equals("renew")){
                            startTimer1();
                            startTimer2();
                        }else{
                            final String message = getString(R.string.notification9);

                            final Map<String, Object> notificationMessage = new HashMap<>();
                            notificationMessage.put("message", message);
                            notificationMessage.put("sendeeID", user_id);
                            notificationMessage.put("property_id", pID);

                            fstore.collection("Users/" + oID + "/Notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    final String message1 = getString(R.string.notification15);

                                    final Map<String, Object> notificationMessage1 = new HashMap<>();
                                    notificationMessage1.put("message", message1);
                                    notificationMessage1.put("sendeeID", oID);
                                    notificationMessage1.put("property_id", pID);

                                    fstore.collection("Users/" + user_id + "/Notifications").add(notificationMessage1). addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            fstore.collection("Occupants").document(user_id).delete();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        };
        Timer timer = new Timer("Timer");
        long delay = 180000;
        timer.schedule(task, delay);
    }
    private void startTimer2(){

        TimerTask task = new TimerTask() {
            public void run() {
                fstore.collection("Occupants").document(user_id).update("Tenure", "almost");

                final String message = getString(R.string.notification8);

                final Map<String, Object> notificationMessage = new HashMap<>();
                notificationMessage.put("message", message);
                notificationMessage.put("sendeeID", user_id);
                notificationMessage.put("property_id", pID);

                fstore.collection("Users/" + oID + "/Notifications").add(notificationMessage);
            }
        };
        Timer timer = new Timer("Timer");
        long delay = 120000;
        timer.schedule(task, delay);
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
