package com.ahmetazizov.androidchatapp.recyclerview_adapters;

//import static com.ahmetazizov.androidchatapp.models.TextMessage.LAYOUT_RECEIVER;
//import static com.ahmetazizov.androidchatapp.models.TextMessage.LAYOUT_SENDER;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.models.ImageMessage;
import com.ahmetazizov.androidchatapp.models.Message;
import com.ahmetazizov.androidchatapp.models.TextMessage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter {

    private final String TAG = "ChatsAdapter";

    RecyclerView recyclerView;
    Context context;
    ArrayList<Message> list;
    FirebaseFirestore db;
    Toolbar selectionOptions;
    TextView selectionCount;
    List<Message> selectionList = new ArrayList<>();


    public ChatsAdapter(Context context, ArrayList<Message> list, RecyclerView recyclerView, Toolbar selectionOptions, TextView selectionCount) {
        this.list = list;
        this.context = context;
        this.recyclerView = recyclerView;
        this.selectionOptions = selectionOptions;
        this.selectionCount = selectionCount;
    }


    @Override
    public int getItemViewType(int position) {
        String currentUser = Constants.currentUser;
        int CASE;

        if (!list.isEmpty()) {
            if (list.get(position).getMessageType().equals("text")) {
                CASE = (list.get(position).getSender().equals(currentUser)) ? 1 : 2;
            } else {
                CASE = (list.get(position).getSender().equals(currentUser)) ? 3 : 4;
            }

            return CASE;
        } else {
            return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View layoutSender = LayoutInflater.from(context).inflate(R.layout.sender_chat_row, parent, false);
                return new SenderMessageViewHolder(layoutSender);
            case 2:
                View layoutReceiver = LayoutInflater.from(context).inflate(R.layout.receiver_chat_row, parent, false);
                return new ReceiverMessageViewHolder(layoutReceiver);
            case 3:
                View layoutSenderImage = LayoutInflater.from(context).inflate(R.layout.sender_image_row, parent, false);
                return new SenderImageViewHolder(layoutSenderImage);
            case 4:
                View layoutReceiverImage = LayoutInflater.from(context).inflate(R.layout.receiver_image_row, parent, false);
                return new ReceiverImageViewHolder(layoutReceiverImage);
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        db = FirebaseFirestore.getInstance();

        final String currentUser = Constants.currentUser;
        int CASE;

        if (!list.isEmpty()) {
            if (list.get(position).getMessageType().equals("text")) {
                CASE = (list.get(position).getSender().equals(currentUser)) ? 1 : 2;
            } else {
                CASE = (list.get(position).getSender().equals(currentUser)) ? 3 : 4;
            }
        } else {
            CASE = -1;
        }

        switch (CASE) {
            case 1:
                TextMessage textMessage = (TextMessage) list.get(position);

                String senderMessage = textMessage.getContent();
                String senderTimeSent = textMessage.getTime();

                ((SenderMessageViewHolder) holder).senderMessageContent.setText(senderMessage);
                ((SenderMessageViewHolder) holder).senderTimeSent.setText(senderTimeSent);
                break;
            case 2:
                TextMessage textMessage2 = (TextMessage) list.get(position);

                String receiverMessage = textMessage2.getContent();
                String receiverTimeSent = textMessage2.getTime();

                ((ReceiverMessageViewHolder) holder).receiverMessageContent.setText(receiverMessage);
                ((ReceiverMessageViewHolder) holder).receiverTimeSent.setText(receiverTimeSent);
                break;
            case 3:
                ImageMessage imageMessage = (ImageMessage) list.get(position);

                String url = imageMessage.getUrl();

                Glide.with(context)
                        .load(url)
                        .override(500, 500)
                        .centerCrop()
                        .into(((SenderImageViewHolder) holder).senderImageContainer);
                break;
            case 4:
                ImageMessage imageMessage2 = (ImageMessage) list.get(position);

                String url2 = imageMessage2.getUrl();

                Glide.with(context)
                        .load(url2)
                        .override(500, 500)
                        .centerCrop()
                        .into(((ReceiverImageViewHolder) holder).receiverImageContainer);
                break;

            default: break;
        }

        holder.itemView.setOnLongClickListener(v -> {
            selectionOptions.setVisibility(View.VISIBLE);
            selectionOptions.animate().alpha(1.0f).setDuration(300).setListener(null);

            checkSelectionList(position);

            return true;
        });


        holder.itemView.setOnClickListener(v -> {
            if (!selectionList.isEmpty()) {
                checkSelectionList(position);
            }
        });


        if (selectionList.contains(list.get(position))) {
            holder.itemView.setBackgroundColor(Color.rgb(200, 150,  150));
        }
        else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class SenderMessageViewHolder extends RecyclerView.ViewHolder {

        private final TextView senderMessageContent;
        private final TextView senderTimeSent;

        public SenderMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageContent = itemView.findViewById(R.id.senderMessageContent);
            senderTimeSent = itemView.findViewById(R.id.senderTimeSent);
        }
    }


    static class ReceiverMessageViewHolder extends RecyclerView.ViewHolder {

        private final TextView receiverMessageContent;
        private final TextView receiverTimeSent;

        public ReceiverMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverMessageContent = itemView.findViewById(R.id.receiverMessageContent);
            receiverTimeSent = itemView.findViewById(R.id.receiverTimeSent);
        }
    }


    static class SenderImageViewHolder extends RecyclerView.ViewHolder {

        ImageView senderImageContainer;

        public SenderImageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderImageContainer = itemView.findViewById(R.id.imageContainer);
        }
    }

    static class ReceiverImageViewHolder extends RecyclerView.ViewHolder {

        ImageView receiverImageContainer;

        public ReceiverImageViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverImageContainer = itemView.findViewById(R.id.imageContainer);
        }
    }



    private void checkSelectionList(int position) {
        if (selectionList.contains(list.get(position))) {
            selectionList.remove(list.get(position));

            if (selectionList.isEmpty()) {
                closeSelectionList();
            }

        } else {
            selectionList.add(list.get(position));
        }

        selectionCount.setText(String.valueOf(selectionList.size()));

        notifyDataSetChanged();
    }


    public void closeSelectionList() {
        selectionOptions.animate().alpha(0.0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                selectionOptions.setVisibility(View.GONE);
            }
        });

        selectionList.clear();

        notifyDataSetChanged();
    }

    public List<Message> getSelectionList() {
        return selectionList;
    }
}
