package com.example.suhei.irent;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class adapterTenantImage extends RecyclerView.Adapter<adapterTenantImage.ViewHolder> {

    public Context context;
    public List<postTenantImage> postTenantImages;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    public adapterTenantImage(Context context, List<postTenantImage> postTenantImages) {
        this.context = context;
        this.postTenantImages = postTenantImages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tenthead, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, context, postTenantImages);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String userID = postTenantImages.get(position).getUser_id();

        fStore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String img = task.getResult().getString("image_url");
                String thumb = task.getResult().getString("image_thumb");

                holder.setUserImg(img, thumb);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postTenantImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        List<postTenantImage> postTenantImages;
        Context context;
        View mView;
        private CircleImageView circleImageView;

        public ViewHolder(View itemView, Context context, List<postTenantImage> postTenantImages) {
            super(itemView);
            this.postTenantImages = postTenantImages;
            this.context = context;
            itemView.setOnClickListener(this);
            mView = itemView;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            postTenantImage tentImgPosts = this.postTenantImages.get(position);
            Intent tenant = new Intent(context, userTenantDetails1.class);
            tenant.putExtra("from_user_id", tentImgPosts.getUser_id());
            tenant.putExtra("property_id", tentImgPosts.getProperty_id());
            this.context.startActivity(tenant);
        }
        public void setUserImg(String image, String thumb){
            circleImageView = mView.findViewById(R.id.tentImage);
            RequestOptions Ro = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Ro.placeholder(R.drawable.hdpi);
            //Load thumbnail in case the real image doesn't load
            Glide.with(context).applyDefaultRequestOptions(Ro).load(image)
                    .thumbnail(Glide.with(context).load(thumb)).into(circleImageView);
        }
    }
}
