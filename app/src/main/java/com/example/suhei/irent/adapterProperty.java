package com.example.suhei.irent;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class adapterProperty extends RecyclerView.Adapter<adapterProperty.ViewHolder> {
    public FirebaseFirestore fStore;
    public Context context;
    public List<postProperty> postPropertyList;

    public adapterProperty(List<postProperty> propertyPostlist, Context context){
        this.postPropertyList = propertyPostlist;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.proplist, parent, false);
        context = parent.getContext();
        fStore = FirebaseFirestore.getInstance();
        ViewHolder viewHolder = new ViewHolder(view, context, postPropertyList);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        fStore.setFirestoreSettings(settings);

        final String addData = postPropertyList.get(position).getProperty_name();
        holder.setPropertyName(addData);
        final String rental = postPropertyList.get(position).getMonthly_rental();
        holder.setRental(rental);
        final String imageUrl = postPropertyList.get(position).getImage_url();
        String Thumb = postPropertyList.get(position).getImage_thumb();
        holder.setPropertyImage(imageUrl, Thumb);

        final String pid = postPropertyList.get(position).getProperty_id();
        final Task<QuerySnapshot> query = fStore.collection("Occupants").whereEqualTo("property_id", pid).get();
         query.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
             @RequiresApi(api = Build.VERSION_CODES.KITKAT)
             @Override
             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                 for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                     if (document.exists()) {
                         String status = "Occupied";
                         holder.setStatus(status);
                     }else{
                         String status = "Not Occupied";
                         holder.setStatus(status);
                     }
                 }
             }
         });
    }

    @Override
    public int getItemCount() {
        return postPropertyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener

    {
        private final List<postProperty> postPropertyList;
        private View mview;
        private TextView PropertyName;
        private TextView Rent;
        private TextView status;
        private Context context;
        private ImageView propImage;

        public ViewHolder(View itemView,  Context context, List<postProperty> postPropertyList) {
            super(itemView);
            this.postPropertyList = postPropertyList;
            this.context = context;
            itemView.setOnClickListener(this);
            mview = itemView;
        }
        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            postProperty postPropertyList = this.postPropertyList.get(position);
            Intent pdisplay = new Intent(context,displayProperty.class);
            pdisplay.putExtra("property_id", postPropertyList.getProperty_id());
            this.context.startActivity(pdisplay);
        }

        public void setPropertyName(String name){
            PropertyName = mview.findViewById(R.id.propnametxt);
            PropertyName.setText(name);
        }
        public void setRental(String rental){
            Rent = mview.findViewById(R.id.Rental);
            Rent.setText(rental);
        }
        public void setPropertyImage(String download_URI, String downloadThumbURI){

            propImage = mview.findViewById(R.id.propImage);
            //Hold Image in Place
            RequestOptions Ro = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Ro.placeholder(R.drawable.image_placeholder);
            //Load thumbnail in case the real image doesn't load
            Glide.with(context).applyDefaultRequestOptions(Ro).load(download_URI).thumbnail
                    (Glide.with(context).load(downloadThumbURI)).into(propImage);
        }
        public void setStatus(String stat){
            status = mview.findViewById(R.id.status);
            status.setTextColor(mview.getResources().getColor(R.color.ontime));
            status.setText(stat);
        }
    }
}
