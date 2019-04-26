package com.example.suhei.irent;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class adapterProperty1 extends RecyclerView.Adapter<adapterProperty1.ViewHolder> {

	public FirebaseFirestore fStore;
	public Context context1;
	public List<postProperty> postPropertyList1;

	public adapterProperty1(List<postProperty> propertyPostlist1, Context context1) {
		this.postPropertyList1 = propertyPostlist1;
		this.context1 = context1;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tentlist, parent, false);
		context1 = parent.getContext();
		fStore = FirebaseFirestore.getInstance();
		FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
				.setPersistenceEnabled(true)
				.build();
		fStore.setFirestoreSettings(settings);
		return new ViewHolder(view, context1, postPropertyList1);
	}

	@Override
	public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
		holder.setIsRecyclable(false);

		final String addData = postPropertyList1.get(position).getProperty_name();
		holder.setPropertyName(addData);
		final String rental = postPropertyList1.get(position).getMonthly_rental();
		holder.setRental(rental);
		final String imageUrl = postPropertyList1.get(position).getImage_url();
		String Thumb = postPropertyList1.get(position).getImage_thumb();
		holder.setPropertyImage(imageUrl, Thumb);

		String owner_id = postPropertyList1.get(position).getOwner_id();
		//User Data will be retrieved here...
		fStore.collection("Users").document(owner_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {

				if(task.isSuccessful()){

					String firstname = task.getResult().getString("fname");
					String lastname = task.getResult().getString("lname");
					String userImage = task.getResult().getString("image_url");
					String thumb = task.getResult().getString("image_thumb");
					holder.setUserData(firstname, lastname, userImage, thumb);

				} else {

					//Firebase Exception
				}
			}
		});
		try {
			long millisecond = postPropertyList1.get(position).getDate_posted().getTime();
			String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
			holder.setTime(dateString);
		} catch (Exception e) {

			Toast.makeText(context1, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

		}
	}
	@Override
	public int getItemCount() {
		return postPropertyList1.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		private List<postProperty> postPropertyList1;
		private View mview;
		private TextView PropertyName;
		private TextView Rent;
		private Context context1;
		private ImageView propImage;
		private TextView date;
		private TextView FName;
		private TextView LName;
		private CircleImageView UserImage;

		public ViewHolder(View itemView, Context context1, List<postProperty> postPropertyList1) {
			super(itemView);
			this.postPropertyList1 = postPropertyList1;
			this.context1 = context1;
			itemView.setOnClickListener(this);
			mview = itemView;

		}

		@Override
		public void onClick(View view) {
			int position = getAdapterPosition();
			postProperty postPropertyList1 = this.postPropertyList1.get(position);
			Intent pdisplay = new Intent(context1,displayPropertyTen.class);
			pdisplay.putExtra("property_id", postPropertyList1.getProperty_id());

			this.context1.startActivity(pdisplay);
		}

		public void setPropertyName(String name){
			PropertyName = mview.findViewById(R.id.propName);
			PropertyName.setText(name);
		}
		public void setRental(String rental){
			Rent = mview.findViewById(R.id.Rental);
			Rent.setText(rental);
		}
		public void setPropertyImage(String download_URI, String downloadThumbURI){

			propImage = mview.findViewById(R.id.property_image);
			//Hold Image in Place
			RequestOptions Ro = new RequestOptions()
					.diskCacheStrategy(DiskCacheStrategy.ALL);
			Ro.placeholder(R.drawable.image_placeholder);
			//Load thumbnail in case the real image doesn't load
			Glide.with(context1).applyDefaultRequestOptions(Ro).load(download_URI).thumbnail
					(Glide.with(context1).load(downloadThumbURI)).into(propImage);
		}
		public void setUserData(String fname, String lname, String image, String thumb){

			UserImage = mview.findViewById(R.id.tentImage);
			FName = mview.findViewById(R.id.fname);
			LName = mview.findViewById(R.id.lname);

			FName.setText(fname);
			LName.setText(lname);

			RequestOptions placeholderOption = new RequestOptions()
					.diskCacheStrategy(DiskCacheStrategy.ALL);
			placeholderOption.placeholder(R.drawable.hdpi);
			Glide.with(context1).applyDefaultRequestOptions(placeholderOption).load(image)
					.thumbnail(Glide.with(context1).applyDefaultRequestOptions(placeholderOption).load(thumb)).into(UserImage);

		}
		public void setTime(String date1) {

			date = mview.findViewById(R.id.date);
			date.setText(date1);

		}
	}
}
