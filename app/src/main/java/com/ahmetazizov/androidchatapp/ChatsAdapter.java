package com.ahmetazizov.androidchatapp;

//import static com.ahmetazizov.androidchatapp.Message.LAYOUT_RECEIVER;
//import static com.ahmetazizov.androidchatapp.Message.LAYOUT_SENDER;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatsAdapter extends RecyclerView.Adapter {

    Context context;

    ArrayList<Message> list;


    public ChatsAdapter(Context context, ArrayList<Message> list) {
        this.list = list;
        this.context = context;
    }


    @Override
    public int getItemViewType(int position) {
        int CASE;
        String currentUser = MainActivity.username;

        if (list.get(position).getSender().equals(MainActivity.username)) CASE = 1;
        else CASE = 2;



        switch (CASE) {
            case 1: return 1;
            case 2: return 2;

            default: return -1;
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
            default: return null;
        }
    }




    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int CASE;
        final String currentUser = MainActivity.username;

        if (list.get(position).getSender().equals(MainActivity.username)) CASE = 1;
        else CASE = 2;

        switch (CASE) {
            case 1:
                String senderMessage = list.get(position).getContent();
                String senderTimeSent = list.get(position).getTime();

                ((SenderMessageViewHolder) holder).senderMessageContent.setText(senderMessage);
                ((SenderMessageViewHolder) holder).senderTimeSent.setText(senderTimeSent);

                break;


            case 2:
                String receiverMessage = list.get(position).getContent();
                String receiverTimeSent = list.get(position).getTime();

                ((ReceiverMessageViewHolder) holder).receiverMessageContent.setText(receiverMessage);
                ((ReceiverMessageViewHolder) holder).receiverTimeSent.setText(receiverTimeSent);

                break;
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }




    class SenderMessageViewHolder extends RecyclerView.ViewHolder {

        private final TextView senderMessageContent;
        private final TextView senderTimeSent;

        public SenderMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageContent = itemView.findViewById(R.id.senderMessageContent);
            senderTimeSent = itemView.findViewById(R.id.senderTimeSent);
        }
    }


    class ReceiverMessageViewHolder extends RecyclerView.ViewHolder {

        private final TextView receiverMessageContent;
        private final TextView receiverTimeSent;

        public ReceiverMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverMessageContent = itemView.findViewById(R.id.receiverMessageContent);
            receiverTimeSent = itemView.findViewById(R.id.receiverTimeSent);
        }
    }
}
