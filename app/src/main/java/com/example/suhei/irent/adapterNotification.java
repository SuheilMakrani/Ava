package com.example.suhei.irent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class adapterNotification extends RecyclerView.Adapter<adapterNotification.ViewHolder> {

	public FirebaseFirestore fStore;
	public Context context;
	public List<postNotifications> notificationList;

	public adapterNotification(Context context, List<postNotifications> notificationList){
		this.notificationList = notificationList;
		this.context = context;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notificationlist, parent, false);

		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
		fStore = FirebaseFirestore.getInstance();
		FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
				.setPersistenceEnabled(true)
				.build();
		fStore.setFirestoreSettings(settings);
		String sendee =notificationList.get(position).getSendeeID();
		holder.message.setText(notificationList.get(position).getMessage());
		fStore.collection("Users").document(sendee).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
			@Override
			public void onSuccess(DocumentSnapshot documentSnapshot) {
				String firstname = documentSnapshot.getString("fname");
				String lastname = documentSnapshot.getString("lname");
				String userImage = documentSnapshot.getString("image_url");
				String thumb = documentSnapshot.getString("image_thumb");

				holder.setUserData(firstname, lastname, userImage, thumb);
			}
		});
	}
	@Override
	public int getItemCount() {
		return notificationList.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {

		private View mview;
		private CircleImageView UserImage;
		private TextView FName, LName, message;

		public ViewHolder(View itemView) {
			super(itemView);

			mview = itemView;
			message = mview.findViewById(R.id.message);
		}
		public void setUserData(String fname, String lname, String image, String thumb){

			UserImage = mview.findViewById(R.id.ownerImage);
			FName = mview.findViewById(R.id.fname);
			LName = mview.findViewById(R.id.lname);

			FName.setText(fname);
			LName.setText(lname);

			RequestOptions placeholderOption = new RequestOptions()
					.diskCacheStrategy(DiskCacheStrategy.ALL);

			Glide.with(context).applyDefaultRequestOptions(placeholderOption.placeholder(R.drawable.ic_hdpi120b)).load(image)
					.thumbnail(Glide.with(context).load(thumb)).into(UserImage);

		}
	}
}
