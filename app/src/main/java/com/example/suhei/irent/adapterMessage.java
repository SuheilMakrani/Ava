package com.example.suhei.irent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class adapterMessage extends RecyclerView.Adapter<adapterMessage.MessageViewHolder>{


    private List<postMessages> mMessageList;
    private DatabaseReference mUserDatabase;
    private Context context;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public adapterMessage(List<postMessages> mMessageList, Context context) {

        this.mMessageList = mMessageList;
        this.context = context;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msglist1 ,parent, false);
        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView leftText,rightText, time1, time2;
        android.support.v7.widget.CardView left, right;
        private ImageView limage, rimage;

        public MessageViewHolder(View itemView) {
            super(itemView);
            leftText = itemView.findViewById(R.id.leftText);
            rightText = itemView.findViewById(R.id.rightText);
            time1 = itemView.findViewById(R.id.time_text_layout1);
            time2 = itemView.findViewById(R.id.time_text_layout2);
            left = itemView.findViewById(R.id.left);
            right = itemView.findViewById(R.id.right);
            limage = itemView.findViewById(R.id.imageLeft);
            rimage = itemView.findViewById(R.id.imageRight);
        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        String user = mMessageList.get(i).getFrom();
        String text = mMessageList.get(i).getMessage();
        Long time = mMessageList.get(i).getTime();

        if(user == null){
            user = mAuth.getCurrentUser().getUid();
        }

        if (user.equals(mAuth.getCurrentUser().getUid())) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("H:m");
            String time11 = format.format(new Date(time));

            viewHolder.leftText.setText(text);
            viewHolder.time1.setText(time11);
            viewHolder.left.setVisibility(View.VISIBLE);
            viewHolder.right.setVisibility(View.INVISIBLE);

        }
        else {
            SimpleDateFormat format = new SimpleDateFormat("H:m");
            String time11 = format.format(new Date(time));

            viewHolder.rightText.setText(text);
            viewHolder.time2.setText(time11);
            viewHolder.right.setVisibility(View.VISIBLE);
            viewHolder.left.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}