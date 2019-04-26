package com.example.suhei.irent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class adapterAva extends RecyclerView.Adapter<adapterAva.ViewHolder>  {

    public Context context;
    public List<postAva> chatMessages;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public adapterAva(Context context, List<postAva>chatMessages){
        this.chatMessages = chatMessages;
        this.context= context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msglist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String user = chatMessages.get(position).getMsgUser();
        final String text = chatMessages.get(position).getMsgText();

        String current = mAuth.getCurrentUser().getUid();

        if (user.equals("user")) {
            holder.leftText.setText(text);
            holder.right.setVisibility(View.GONE);
            holder.leftText.setVisibility(View.VISIBLE);
        }
        else{
            String search  = "propertyID";
            String search2 = "userID";
            String s = "I am sorry we do not have";
            holder.right.setVisibility(View.VISIBLE);

            if (text.toLowerCase().contains(search.toLowerCase())) {
                holder.leftText.setVisibility(View.INVISIBLE);
                if(!text.toLowerCase().contains(s.toLowerCase())) {
                    String text2 = text.split("\\.")[0];
                    final String part3 = text.split("\\.")[1];

                    fStore.collection("Properties").document(part3).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String name = task.getResult().getString("property_name");
                            holder.rightText.setText("Property Name: " + name);
                            holder.right.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, displayPropertyTen.class);
                                    intent.putExtra("property_id", part3);
                                    context.startActivity(intent);
                                }
                            });
                        }
                    });
                }else if (text.toLowerCase().contains(search2.toLowerCase())){
                    String text2 = text.split("\\.")[0];
                    final String part3 = text.split("\\.")[1];
                    String text1 = text.split("\\.")[2];
                    final String part1 = text.split("\\.")[3];

                    fStore.collection("Users").document(part3).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String name = task.getResult().getString("fname");
                            String lname = task.getResult().getString("lname");
                            holder.rightText.setText("Tenant Name: " + name+" "+lname);
                            holder.right.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, userTenantDetails1.class);
                                    intent.putExtra("from_user_id", part3);
                                    intent.putExtra("property_id", part1);
                                    context.startActivity(intent);
                                }
                            });
                        }
                    });


                }else{
                    holder.leftText.setVisibility(View.INVISIBLE);

                    final String part1 = text.split("\\.")[0];
                    String part3 = text.split("\\.")[1];

                    final String text2 = part3.replace(search, "");
                    fStore.collection("Properties").document(text2).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String name = task.getResult().getString("property_name");
                            holder.rightText.setText(part1+".\n\nProperty Name: "+name);

                            holder.right.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent (context, displayPropertyTen.class);
                                    intent.putExtra("property_id", text2);
                                    context.startActivity(intent);
                                }
                            });
                        }
                    });
                }

            } else{
                holder.rightText.setText(text);
                holder.rightText3.setVisibility(View.INVISIBLE);
                holder.leftText.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView leftText,rightText,rightText3;
        CardView right;
        public ViewHolder(View itemView) {
            super(itemView);
            leftText = itemView.findViewById(R.id.leftText);
            rightText = itemView.findViewById(R.id.rightText);
            rightText3 = itemView.findViewById(R.id.rightText3);
            right = itemView.findViewById(R.id.rightcard);
        }
    }
    public void clear() {
        final int size = chatMessages.size();
        chatMessages.clear();
        notifyItemRangeRemoved(0, size);
    }
}