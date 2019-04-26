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
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class adapterLike extends RecyclerView.Adapter<adapterLike.ViewHolder> {
    private List<postLike> likeList;
    private Context context;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private String userID, pID;


    public adapterLike(Context context, List<postLike> likeList){
        this.likeList = likeList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tentlist, parent, false);
        context = parent.getContext();
        ViewHolder viewHolder = new ViewHolder(view, context, likeList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        pID = likeList.get(position).getProperty_id();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        fStore.setFirestoreSettings(settings);

        fStore.collection("Properties").document(pID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String rent = task.getResult().getString("monthly_rental");
                holder.setRental(rent);
                String img = task.getResult().getString("image_url");
                holder.setPropertyImage(img);
                String name = task.getResult().getString("property_name");
                holder.setPropertyName(name);

                final String ownID = task.getResult().getString("owner_id");
                if(ownID!=null) {
                    fStore.collection("Users").document(ownID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String fownName = task.getResult().getString("fname");
                            String lownName = task.getResult().getString("lname");
                            String ownimg = task.getResult().getString("image_url");

                            holder.setUserData(fownName, lownName, ownimg);
                        }
                    });
                }
            }
        });
        try {
            long millisecond = likeList.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);
        } catch (Exception e) {
            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return likeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private List<postLike> postLikes;
        private View mview;
        private TextView PropertyName;
        private TextView Rent;
        private Context context1;
        private ImageView propImage;
        private TextView date;
        private TextView FName;
        private TextView LName;
        private CircleImageView UserImage;
        public ViewHolder(View itemView, Context context, List<postLike> likeList) {
            super(itemView);
            this.postLikes = likeList;
            this.context1 = context;
            itemView.setOnClickListener(this);
            mview = itemView;
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            postLike postLikes = this.postLikes.get(position);
            Intent display = new Intent(context1, displayPropertyTen.class);
            display.putExtra("likes", "");
            display.putExtra("property_id", postLikes.getProperty_id());
            this.context1.startActivity(display);
        }
        public void setPropertyName(String name){
            PropertyName = mview.findViewById(R.id.propName);
            PropertyName.setText(name);
        }
        public void setRental(String rental){
            Rent = mview.findViewById(R.id.Rental);
            Rent.setText(rental);
        }
        public void setPropertyImage(String download_URI){

            propImage = mview.findViewById(R.id.property_image);
            //Hold Image in Place
            RequestOptions Ro = new RequestOptions();
            Ro.placeholder(R.drawable.image_placeholder);
            //Load thumbnail in case the real image doesn't load
            Glide.with(context1).applyDefaultRequestOptions(Ro).load(download_URI).into(propImage);
        }
        public void setUserData(String fname, String lname, String image){

            UserImage = mview.findViewById(R.id.tentImage);
            FName = mview.findViewById(R.id.fname);
            LName = mview.findViewById(R.id.lname);

            FName.setText(fname);
            LName.setText(lname);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.hdpi);
            Glide.with(context1).applyDefaultRequestOptions(placeholderOption).load(image).into(UserImage);

        }
        public void setTime(String date1) {

            date = mview.findViewById(R.id.date);
            date.setText(date1);

        }
    }
}
