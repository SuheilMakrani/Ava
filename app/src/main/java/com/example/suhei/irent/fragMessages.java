package com.example.suhei.irent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class fragMessages extends Fragment {

	private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
	private FirebaseAuth mAuth = FirebaseAuth.getInstance();
	private RecyclerView mConvList;
	private FirebaseFirestore fStore = FirebaseFirestore.getInstance();

	private DatabaseReference mConvDatabase;
	private DatabaseReference mMessageDatabase;
	private DatabaseReference mUsersDatabase;

	private String mCurrent_user_id;

	private View mMainView;


	public fragMessages() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.activity_messages_fragment, container, false);

		mConvList = mMainView.findViewById(R.id.messageList);

		mCurrent_user_id = mAuth.getCurrentUser().getUid();

		mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);

		mConvDatabase.keepSynced(true);
		mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
		mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(mCurrent_user_id);
		mUsersDatabase.keepSynced(true);

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
		linearLayoutManager.setReverseLayout(true);
		linearLayoutManager.setStackFromEnd(true);

		//mConvList.setHasFixedSize(true);
		mConvList.setLayoutManager(linearLayoutManager);

		// Inflate the layout for this fragment
		return mMainView;
	}
	@Override
	public void onStart() {
		super.onStart();

		databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Online").setValue(true);
		com.google.firebase.database.Query conversationQuery = mConvDatabase.orderByChild("timestamp");

		FirebaseRecyclerOptions<postChat> options =
				new FirebaseRecyclerOptions.Builder<postChat>()
						.setQuery(conversationQuery, postChat.class)
						.build();

		FirebaseRecyclerAdapter firebaseConvAdapter = new FirebaseRecyclerAdapter<postChat, ConvViewHolder>(options) {
			@NonNull
			@Override
			public ConvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
				View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_chat, parent, false);
				return new ConvViewHolder(view);
			}

			@Override
			protected void onBindViewHolder(@NonNull final ConvViewHolder convViewHolder, int i, @NonNull final postChat conv) {

				final String list_user_id = getRef(i).getKey();

				com.google.firebase.database.Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

				lastMessageQuery.addChildEventListener(new ChildEventListener() {
					@Override
					public void onChildAdded(DataSnapshot dataSnapshot, String s) {

						String data = dataSnapshot.child("message").getValue().toString();

						if (data != null){
							convViewHolder.setMessage(data, conv.isSeen());
							fStore.collection("Users").document(list_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
								@Override
								public void onComplete(@NonNull Task<DocumentSnapshot> task) {
									final String fname = task.getResult().getString("fname");
									final String lname = task.getResult().getString("lname");
									final String userThumb = task.getResult().getString("image_thumb");
									final String image = task.getResult().getString("image_url");

									mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
										@Override
										public void onDataChange(DataSnapshot dataSnapshot) {

											if(dataSnapshot.hasChild("Online")) {
												String userOnline = dataSnapshot.child("Online").getValue().toString();
												convViewHolder.setUserOnline(userOnline);
											}
											if(getActivity()!=null) {
												convViewHolder.setName(fname + " " + lname);
												convViewHolder.setUserImage(image, userThumb, getContext());

												convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
													@Override
													public void onClick(View view) {
														Intent chatIntent = new Intent(getContext(), ChatActivity.class);
														chatIntent.putExtra("user_id", list_user_id);
														chatIntent.putExtra("user_name", fname + " " + lname);
														startActivity(chatIntent);
													}
												});
											}
										}
										@Override
										public void onCancelled(DatabaseError databaseError) {

										}
									});

								}
							});
						}else{
							convViewHolder.mView.setVisibility(View.GONE);
						}
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
		};
		firebaseConvAdapter.startListening();
		mConvList.setAdapter(firebaseConvAdapter);
	}

	public static class ConvViewHolder extends RecyclerView.ViewHolder {

		View mView;

		public ConvViewHolder(View itemView) {
			super(itemView);
			mView = itemView;
		}

		public void setMessage(String message, boolean isSeen){

			TextView userStatusView = mView.findViewById(R.id.user_single_status);
			userStatusView.setText(message);

			if(!isSeen){
				userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
			} else {
				userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
			}

		}

		public void setName(String name){

			TextView userNameView = mView.findViewById(R.id.user_single_name);
			userNameView.setText(name);

		}

		public void setUserImage(String Image,String thumb_image, Context ctx){

			CircleImageView userImageView =mView.findViewById(R.id.user_single_image);
			RequestOptions Ro = new RequestOptions()
					.diskCacheStrategy(DiskCacheStrategy.ALL);
			Ro.placeholder(R.drawable.ic_hdpi120b);
			//Load thumbnail in case the real image doesn't load

			Glide.with(ctx).applyDefaultRequestOptions(Ro).load(Image)
					.thumbnail(Glide.with(ctx).load(thumb_image))
					.into(userImageView);

		}

		public void setUserOnline(String online_status) {

			ImageView userOnlineView = mView.findViewById(R.id.user_single_online_icon);

			if(online_status.equals("true")){

				userOnlineView.setVisibility(View.VISIBLE);

			} else {

				userOnlineView.setVisibility(View.INVISIBLE);

			}
		}

	}
	@Override
	public void onPause () {
		super.onPause();
		if(mAuth.getCurrentUser() !=null){
			databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Online").setValue(ServerValue.TIMESTAMP);
		}
	}

}
