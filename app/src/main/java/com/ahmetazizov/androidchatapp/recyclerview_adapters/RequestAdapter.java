package com.ahmetazizov.androidchatapp.recyclerview_adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.models.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {

    public final static String TAG = "RequestAdapter";
    Context context;
    ArrayList<Request> requestList;


    public RequestAdapter(Context context, ArrayList<Request> requestList) {
        this.context = context;
        this.requestList = requestList;
    }



    @NonNull
    @Override
    public RequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.request_row, parent, false);
        return new RequestAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glide.with(context)
                .load(requestList.get(position).getImageURL())
                .override(300, 300)
                .centerCrop()
                .into(holder.profileImage);

        holder.username.setText(requestList.get(position).getUsername());

        Timestamp exactTime = requestList.get(position).getRequestTime();

        long timeMilli = exactTime.toDate().getTime();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String time = timeFormat.format(timeMilli);

        holder.timeSent.setText(time);
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView username, timeSent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            username = itemView.findViewById(R.id.username);
            timeSent = itemView.findViewById(R.id.timeSent);
        }
    }
}
