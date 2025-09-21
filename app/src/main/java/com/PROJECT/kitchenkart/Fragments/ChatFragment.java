package com.PROJECT.kitchenkart.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PROJECT.kitchenkart.Activities.ChatActivity;
import com.PROJECT.kitchenkart.Adapters.RecentChatsAdapter;
import com.PROJECT.kitchenkart.Models.Chat;
import com.PROJECT.kitchenkart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass to display a list of recent chats.
 * @noinspection FieldCanBeLocal
 */
public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";

    // UI elements
    private RecyclerView rvRecentChats;
    private TextView tvNoChats;

    // Data and adapter
    private RecentChatsAdapter adapter;
    private List<Chat> chatList;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            return view;
        }

        rvRecentChats = view.findViewById(R.id.rv_recent_chats);
        tvNoChats = view.findViewById(R.id.tv_no_chats);

        chatList = new ArrayList<>();
        adapter = new RecentChatsAdapter(chatList, chat -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_RECEIVER_ID, chat.getOtherUserId(currentUser.getUid()));
            String otherUserName = "Chat User";
            intent.putExtra(ChatActivity.EXTRA_RECEIVER_NAME, otherUserName);
            startActivity(intent);
        });

        rvRecentChats.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecentChats.setAdapter(adapter);

        loadRecentChats();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentUser != null) {
            loadRecentChats();
        }
    }

    /**
     * Loads recent chats for the current user from Firestore.
     */
    @SuppressLint("SetTextI18n")
    private void loadRecentChats() {
        db.collection("chats")
                .whereArrayContains("users", currentUser.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error loading chats: " + error.getMessage());

                        // Check if the error is due to insufficient permissions
                        if (error.getCode() == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                            tvNoChats.setText("Failed to load chats due to security settings. Please check Firebase Security Rules.");
                        } else {
                            tvNoChats.setText("Error loading chats. Please check your internet connection.");
                        }

                        tvNoChats.setVisibility(View.VISIBLE);
                        rvRecentChats.setVisibility(View.GONE);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        chatList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Chat chat = doc.toObject(Chat.class);
                            chatList.add(chat);
                        }
                        adapter.notifyDataSetChanged();
                        tvNoChats.setVisibility(View.GONE);
                        rvRecentChats.setVisibility(View.VISIBLE);
                    } else {
                        tvNoChats.setText("No recent chats found.");
                        tvNoChats.setVisibility(View.VISIBLE);
                        rvRecentChats.setVisibility(View.GONE);
                    }
                });
    }
}