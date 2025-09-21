package com.PROJECT.kitchenkart.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.PROJECT.kitchenkart.Models.Chat;
import com.PROJECT.kitchenkart.Models.User;
import com.PROJECT.kitchenkart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Adapter for displaying a list of recent chats.
 * @noinspection ALL
 */
public class RecentChatsAdapter extends RecyclerView.Adapter<RecentChatsAdapter.ChatViewHolder> {

    private final List<Chat> chatList;
    private final OnChatClickListener listener;
    private final String currentUserId;
    private final FirebaseFirestore db;

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

    public RecentChatsAdapter(List<Chat> chatList, OnChatClickListener listener) {
        this.chatList = chatList;
        this.listener = listener;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChatClick(chat);
            }
        });

        // Fetch the other user's details
        String otherUserId = chat.getOtherUserId(currentUserId);
        if (otherUserId != null) {
            DocumentReference userRef = db.collection("users").document(otherUserId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        holder.tvSenderName.setText(user.getName());
                    }
                }
            });
        }

        holder.tvLastMessage.setText(chat.getLastMessage());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        final TextView tvSenderName;
        final TextView tvLastMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderName = itemView.findViewById(R.id.tv_sender_name);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
        }
    }
}
