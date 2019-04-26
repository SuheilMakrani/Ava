package com.example.suhei.irent;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class adapterTenant extends RecyclerView.Adapter<adapterTenant.ViewHolder> {

    public FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    public List<postTenant> postTenantList;
    public Context context;

    public adapterTenant(Context context, List<postTenant> postTenantList){
        this.postTenantList = postTenantList;
        this.context = context;
    }

    @NonNull
    @Override
    public adapterTenant.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tenantlist, parent, false);
        context = parent.getContext();
        return new ViewHolder(view, context, postTenantList);
    }

    @Override
    public void onBindViewHolder(@NonNull final adapterTenant.ViewHolder holder, int position) {
        String user_id = postTenantList.get(position).getUser_id();
        String property_id = postTenantList.get(position).getProperty_id();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        fStore.setFirestoreSettings(settings);

        fStore.collection("Users").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String firstname = documentSnapshot.getString("fname");
                String lastname = documentSnapshot.getString("lname");
                String userImage = documentSnapshot.getString("image_url");
                String thumb = documentSnapshot.getString("image_thumb");

                holder.setUserData(firstname, lastname, userImage, thumb);
            }
        });
        fStore.collection("Properties").document(property_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String propername = documentSnapshot.getString("property_name");
                holder.setPropertyData(propername);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postTenantList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View mview;
        private CircleImageView UserImage;
        private TextView FName, LName, propname;
        List<postTenant> postTenantList;
        Context context;

        public ViewHolder(View itemView, Context context, List<postTenant> postTenantList) {
            super(itemView);
            this.postTenantList = postTenantList;
            this.context = context;
            itemView.setOnClickListener(this);
            mview = itemView;
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
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image)
                    .thumbnail(Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(thumb))
                    .into(UserImage);
        }
        public void setPropertyData(String name){

            propname = mview.findViewById(R.id.propname);
            propname.setText(name);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            postTenant postTenantList = this.postTenantList.get(position);
            Intent tenant = new Intent(context, userTenantDetails1.class);
            tenant.putExtra("from_user_id", postTenantList.getUser_id());
            tenant.putExtra("property_id", postTenantList.getProperty_id());
            this.context.startActivity(tenant);
        }
    }
}
