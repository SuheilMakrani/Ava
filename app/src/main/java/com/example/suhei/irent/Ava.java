package com.example.suhei.irent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.ResponseMessage;
import ai.api.model.Result;

import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Ava extends AppCompatActivity implements AIListener {
    RecyclerView recyclerView;
    EditText editText;
    ImageButton addBtn;
    DatabaseReference ref;
    Boolean flagFab = true;
    private AIService aiService;
    List<postAva> chatMessages;
    adapterAva adapter;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore= FirebaseFirestore.getInstance();
    private String uID = mAuth.getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_susan);

        editText = findViewById(R.id.editText);
        ref = FirebaseDatabase.getInstance().getReference();
        ref.keepSynced(true);
        addBtn =  findViewById(R.id.addBtn);

        chatMessages = new ArrayList<>();
        recyclerView = findViewById(R.id.chats);
        adapter = new adapterAva(this, chatMessages);

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        final AIConfiguration config = new AIConfiguration("e59ea496ab614716b7348058b0e84932",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        final AIDataService aiDataService = new AIDataService(config);

        final AIRequest aiRequest = new AIRequest();

        addBtn.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                addBtn.setEnabled(isInternetOn());
                if(addBtn.isEnabled()){
                    final String message = editText.getText().toString().trim();

                    if (!message.equals("")) {

                        fStore.collection("Users").document(uID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                String type = task.getResult().getString("user_type");

                                if (type.equals("HouseOwner")) {

                                    String own = "looking for property";
                                    String own3 = "looking for a property";
                                    String own1 = "searching for property";
                                    String own2 = "can i look at properties";

                                    if (message.toLowerCase().contains(own.toLowerCase()) || message.toLowerCase().contains(own1.toLowerCase()) ||
                                            message.toLowerCase().contains(own2.toLowerCase())||message.toLowerCase().contains(own3.toLowerCase())) {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(Ava.this);
                                        builder1.setTitle("Ava");
                                        builder1.setMessage("I am sorry but this feature is only available for the tenants");
                                        builder1.setCancelable(true);
                                        builder1.setNeutralButton(android.R.string.ok,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });

                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();
                                    } else {

                                        postAva chatMessage = new postAva(message, "user");
                                        ref.child("chat").child(uID).push().setValue(chatMessage);
                                        aiRequest.setQuery(message);

                                        new AsyncTask<AIRequest,Void,AIResponse>(){

                                            @SuppressLint("WrongThread")
                                            @Override
                                            protected AIResponse doInBackground(AIRequest... aiRequests) {
                                                final AIRequest request = aiRequests[0];
                                                try {
                                                    return aiDataService.request(aiRequest);
                                                } catch (AIServiceException e) {

                                                }
                                                return null;
                                            }
                                            @Override
                                            protected void onPostExecute(AIResponse response) {
                                                if (response != null) {
                                                    Result result = response.getResult();
                                                    int messageCount = result.getFulfillment().getMessages().size();
                                                    if (messageCount == 1) {
                                                        String reply = result.getFulfillment().getSpeech();
                                                        postAva chatmessage = new postAva(reply, "bot");
                                                        ref.child("chat").child(uID).push().setValue(chatmessage);
                                                    } else if (messageCount > 1) {
                                                        for (int i = 1; i < messageCount; i++) {
                                                            String reply = result.getFulfillment().getSpeech();
                                                            postAva chatmessage = new postAva(reply, "bot");
                                                            ref.child("chat").child(uID).push().setValue(chatmessage);
                                                        }
                                                    }
                                                }
                                            }
                                        }.execute(aiRequest);

                                    }
                                } else if (type.equals("Tenant")) {

                                    String own = "who has not paid rent";
                                    String own1 = "who hasn't paid rent";
                                    String own2 = "who hasnot paid rent";
                                    String own3 = "who hasnt paid rent";

                                    if (message.toLowerCase().contains(own.toLowerCase()) || message.toLowerCase().contains(own1.toLowerCase()) || message.toLowerCase().contains(own2.toLowerCase()) || message.toLowerCase().contains(own3.toLowerCase())) {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(Ava.this);
                                        builder1.setTitle("Ava");
                                        builder1.setMessage("I am sorry but this feature is only available for the House Owners");
                                        builder1.setCancelable(true);
                                        builder1.setNeutralButton(android.R.string.ok,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });

                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();
                                    } else {
                                        postAva chatMessage = new postAva(message, "user");
                                        ref.child("chat").child(uID).push().setValue(chatMessage);
                                        aiRequest.setQuery(message);

                                        new AsyncTask<AIRequest,Void,AIResponse>(){

                                            @SuppressLint("WrongThread")
                                            @Override
                                            protected AIResponse doInBackground(AIRequest... aiRequests) {
                                                final AIRequest request = aiRequests[0];
                                                try {
                                                    return aiDataService.request(aiRequest);
                                                } catch (AIServiceException e) {

                                                }
                                                return null;
                                            }
                                            @Override
                                            protected void onPostExecute(AIResponse response) {
                                                if (response != null) {
                                                    Result result = response.getResult();
                                                    int messageCount = result.getFulfillment().getMessages().size();
                                                    if (messageCount == 1) {
                                                        String reply = result.getFulfillment().getSpeech();
                                                        postAva chatmessage = new postAva(reply, "bot");
                                                        ref.child("chat").child(uID).push().setValue(chatmessage);
                                                    } else if (messageCount > 1) {
                                                        for (int i = 1; i < messageCount; i++) {
                                                            String reply = result.getFulfillment().getSpeech();
                                                            postAva chatmessage = new postAva(reply, "bot");
                                                            ref.child("chat").child(uID).push().setValue(chatmessage);
                                                        }
                                                    }
                                                }
                                            }
                                        }.execute(aiRequest);
                                    }
                                }
                            }
                        });
                    }
                    else {
                        aiService.startListening();
                    }
                    editText.setText("");
                }else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Ava.this);
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

        Query query = ref.child("chat").child(uID);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                postAva chat = dataSnapshot.getValue(postAva.class);
                chatMessages.add(chat);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int msgCount = adapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (msgCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
                recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v,
                                               int left, int top, int right, int bottom,
                                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if (bottom < oldBottom) {
                            recyclerView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.smoothScrollToPosition(
                                            recyclerView.getAdapter().getItemCount() - 1);
                                }
                                }, 100);
                            }
                        }
                    });
                }
        });

        recyclerView.setAdapter(adapter);
    }
    public void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

    @Override
    public void onResult(ai.api.model.AIResponse response) {

        final Result result = response.getResult();

        String message = result.getResolvedQuery();
        postAva chatmessage0 = new postAva(message, "user");
        ref.child("chat").child(uID).push().setValue(chatmessage0);

        String reply = result.getFulfillment().getSpeech();
        postAva chatmessage = new postAva(reply, "bot");
        ref.child("chat").child(uID).push().setValue(chatmessage);
    }

    @Override
    public void onError(ai.api.model.AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

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