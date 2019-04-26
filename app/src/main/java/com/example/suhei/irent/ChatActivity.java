package com.example.suhei.irent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String uID, fname, lname, image, thumb, currentUser;
    private android.support.v7.widget.Toolbar chatToolbar;
    private FirebaseFirestore fStore;
    private DatabaseReference databaseReference;
    private StorageReference mImageStorage;
    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayout;
    private final List<postMessages> messagesList = new ArrayList<>();
    private adapterMessage adapter;
    private static final int TOTAL_ITEMS_TO_LOAD = 15;
    private int currentpage = 1;
    private SwipeRefreshLayout refreshLayout;
    private int itemPos = 0;

    private String mLastKey = "";
    private String mPrevKey = "";
    private static final int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        fStore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        uID = getIntent().getStringExtra("user_id");
        currentUser = mAuth.getCurrentUser().getUid();

        chatToolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(chatToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        chatToolbar = findViewById(R.id.chat_app_bar);
        LayoutInflater inflater = (LayoutInflater) ChatActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        mTitleView = findViewById(R.id.custom_bar_title);
        mLastSeenView = findViewById(R.id.custom_bar_seen);
        mProfileImage = findViewById(R.id.custom_bar_image);

        mChatSendBtn = findViewById(R.id.chat_send_btn);
        mChatMessageView = findViewById(R.id.chat_message_view);

        adapter = new adapterMessage(messagesList, this);
        recyclerView = findViewById(R.id.messages_list);
        refreshLayout = findViewById(R.id.message_swipe_layout);
        linearLayout = new LinearLayoutManager(this);
        linearLayout.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayout);
        recyclerView.setAdapter(adapter);

        loadMessages();

        fStore.collection("Users").document(uID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                fname = task.getResult().getString("fname");
                lname = task.getResult().getString("lname");
                image = task.getResult().getString("image_url");
                thumb = task.getResult().getString("image_thumb");

                mTitleView.setText(fname+" "+lname);

                RequestOptions placeholderOption = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(ChatActivity.this).applyDefaultRequestOptions(placeholderOption)
                        .load(image)
                        .thumbnail(Glide.with(ChatActivity.this).load(thumb))
                        .into(mProfileImage);
            }
        });
        databaseReference.child("Users").child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("Online").getValue().toString();

                if(online.equals("true")) {
                    mLastSeenView.setText("Online");
                } else {
                    getTimeAgo GetTimeAgo = new getTimeAgo();
                    long lastTime = Long.parseLong(online);
                    String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, getApplicationContext());
                    mLastSeenView.setText(lastSeenTime);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //-------------------------------Send Message-----------------------//
        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChatSendBtn.setEnabled(isInternetOn());
                if(mChatSendBtn.isEnabled()) {
                    //-------------------------Conversations-----------------------//
                    databaseReference.child("Chat").child(currentUser).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(!dataSnapshot.hasChild(uID)){

                                Map chatAdd = new HashMap();
                                chatAdd.put("seen", false);
                                chatAdd.put("timestamp", ServerValue.TIMESTAMP);

                                Map chatUser = new HashMap();
                                chatUser.put("Chat/"+currentUser+"/"+uID, chatAdd);
                                chatUser.put("Chat/"+uID+"/"+currentUser, chatAdd);

                                databaseReference.updateChildren(chatUser, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if(databaseError !=null){

                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    sendMsg();

                    final String message = getString(R.string.notification14);

                    if (!TextUtils.isEmpty(message)) {

                        Map<String, Object> notificationMessage = new HashMap<>();
                        notificationMessage.put("message", message);
                        notificationMessage.put("sendeeID", currentUser);
                        notificationMessage.put("property_id", uID);

                        fStore.collection("Users/" + uID + "/Notifications").add(notificationMessage);
                    }
                }else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ChatActivity.this);
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
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentpage++;
                itemPos = 0;
                loadMore();
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
    private void loadMore(){

        DatabaseReference messageRef = databaseReference.child("Messages").child(currentUser).child(uID);

        com.google.firebase.database.Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                postMessages message = dataSnapshot.getValue(postMessages.class);
                String messageKey = dataSnapshot.getKey();
                if(!mPrevKey.equals(messageKey)){
                    messagesList.add(itemPos++, message);
                } else {
                    mPrevKey = mLastKey;
                }
                if(itemPos == 1) {
                    mLastKey = messageKey;
                }
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
                linearLayout.scrollToPositionWithOffset(10, 0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadMessages() {

        DatabaseReference messageref = databaseReference.child("Messages").child(currentUser).child(uID);
        com.google.firebase.database.Query messageq = messageref.limitToLast(currentpage * TOTAL_ITEMS_TO_LOAD);

        messageq.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                postMessages messages = dataSnapshot.getValue(postMessages.class);
                itemPos++;
                if(itemPos == 1){
                    String messageKey = dataSnapshot.getKey();
                    mLastKey = messageKey;
                    mPrevKey = messageKey;
                }
                messagesList.add(messages);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messagesList.size()-1);
                refreshLayout.setRefreshing(false);
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
    }

    private void sendMsg() {
        String msg = mChatMessageView.getText().toString();

        if(!TextUtils.isEmpty(msg)){

            String currentUserRef = "Messages/"+currentUser+"/"+uID;
            String chatUserRef = "Messages/"+uID+"/"+currentUser;

            DatabaseReference user_message_push = databaseReference.child("messages")
                    .child(currentUser).child(uID).push();

            String push_id = user_message_push.getKey();

            Map message1 = new HashMap();
            message1.put("message", msg);
            message1.put("seen", false);
            message1.put("type", "text");
            message1.put("time", ServerValue.TIMESTAMP);
            message1.put("from", currentUser);

            Map messageUser = new HashMap();

            messageUser.put(currentUserRef +"/"+push_id, message1);
            messageUser.put(chatUserRef+"/"+push_id, message1);

            mChatMessageView.setText("");

            databaseReference.updateChildren(messageUser, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                    if(databaseError != null){

                    }
                }
            });
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Online").setValue(true);
    }
    @Override
    protected void onPause () {
        super.onPause();
        if(mAuth.getCurrentUser() !=null){
            databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
