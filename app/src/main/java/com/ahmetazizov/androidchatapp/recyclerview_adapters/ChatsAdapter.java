package com.ahmetazizov.androidchatapp.recyclerview_adapters;

//import static com.ahmetazizov.androidchatapp.models.Message.LAYOUT_RECEIVER;
//import static com.ahmetazizov.androidchatapp.models.Message.LAYOUT_SENDER;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.MainActivity;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.fragments.ChatFragment;
import com.ahmetazizov.androidchatapp.models.Message;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class ChatsAdapter extends RecyclerView.Adapter {

    private final String TAG = "ChatsAdapter";

    ImageView deleteButton;
    RecyclerView recyclerView;
    Context context;
    ArrayList<Message> list;
    FirebaseFirestore db;
    CardView selectionOptions;
    TextView selectionCount;

    List<Message> deleteList = new ArrayList<>();


    public ChatsAdapter(Context context, ArrayList<Message> list, RecyclerView recyclerView, ImageView deleteButton, CardView selectionOptions, TextView selectionCount) {
        this.list = list;
        this.context = context;
        this.recyclerView = recyclerView;
        this.deleteButton = deleteButton;
        this.selectionOptions = selectionOptions;
        this.selectionCount = selectionCount;
    }


    @Override
    public int getItemViewType(int position) {
        int CASE;
        String currentUser = Constants.currentUser;

        if (list.get(position).getSender().equals(currentUser)) CASE = 1;
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

        db = FirebaseFirestore.getInstance();
        DocumentReference deleteDocRef = db.collection("chats").
                document(list.get(position).getChatRef()).collection("messages").document(list.get(position).getId());

        int CASE;
        final String currentUser = Constants.currentUser;

        if (list.get(position).getSender().equals(currentUser)) CASE = 1;
        else CASE = 2;

        switch (CASE) {
            case 1:
                String senderMessage = list.get(position).getContent();
                String senderTimeSent = list.get(position).getTime();

                ((SenderMessageViewHolder) holder).senderMessageContent.setText(senderMessage);
                ((SenderMessageViewHolder) holder).senderTimeSent.setText(senderTimeSent);


                if (deleteList.contains(list.get(position))) {
                    holder.itemView.setBackgroundColor(Color.rgb(200, 150,  150));
                }
                else {
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                }


                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        selectionOptions.setVisibility(View.VISIBLE);
                        selectionOptions.animate().alpha(1.0f).setDuration(300).setListener(null);

                        checkDeleteList(holder.getAdapterPosition());

                        return true;
                    }
                });


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!deleteList.isEmpty()) {
                            checkDeleteList(holder.getAdapterPosition());
                        }
                    }
                });


                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteButton.animate().translationX(dpToPixels(60)).setDuration(300).setListener(null);
                    }
                });

//                ((SenderMessageViewHolder) holder).deleteButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (v.getVisibility() != View.GONE) {
//
//                            deleteDocRef.delete()
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            // Document successfully deleted
//                                            Log.d(TAG, "Document deleted successfully!");
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            // Handle errors
//                                            Log.e(TAG, "Error deleting document: " + e.getMessage());
//                                        }
//                                    });
//
//
//                        }
//                    }
//                });

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












    private void deleteButtonVisibility(float alpha) {
        if (alpha == 1.0f) deleteButton.setVisibility(View.VISIBLE);

        deleteButton.animate().alpha(alpha).setDuration(700).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (alpha == 0.0f) deleteButton.setVisibility(View.GONE);
            }
        });
    }


    public void clearDeleteButton() {
        deleteButtonVisibility(0.0f);
        deleteButton.animate().translationX(dpToPixels(60)).setDuration(300).setListener(null);

//        for (int i = 0; i < getItemCount(); i++) {
//            if (list.get(i).getSender().equals(Constants.currentUser)) {
//
//                ChatsAdapter.SenderMessageViewHolder holder = (ChatsAdapter.SenderMessageViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
//
//                if (holder != null) {
//
//                    holder.itemView.setBackgroundColor(Color.TRANSPARENT);
//
//                }
//            }
//        }

        deleteList.clear();

        notifyDataSetChanged();
    }



    private void checkDeleteList(int position) {
        if (deleteList.contains(list.get(position))) {
            deleteList.remove(list.get(position));

            if (deleteList.isEmpty()) {
                selectionOptions.animate().alpha(0.0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        selectionOptions.setVisibility(View.GONE);
                    }
                });
            }

        } else {
            deleteList.add(list.get(position));
        }

        String currentCount = String.valueOf(deleteList.size());
        selectionCount.setText(currentCount);

        notifyDataSetChanged();
    }


    private int dpToPixels(int dps) {
        float density = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * density);
        return pixels;
    }


    public List<Message> getDeleteList() {
        return deleteList;
    }
}
