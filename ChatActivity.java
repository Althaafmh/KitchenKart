package com.PROJECT.kitchenkart.Activities;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PROJECT.kitchenkart.Adapters.MessageAdapter;
import com.PROJECT.kitchenkart.Models.Message;
import com.PROJECT.kitchenkart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ChatActivity handles real-time communication between a buyer and a seller.
 * @noinspection ALL
 */
public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_RECEIVER_ID = "extra_receiver_id";
    public static final String EXTRA_RECEIVER_NAME = "extra_receiver_name";

    private RecyclerView rvMessages;
    private EditText etMessageInput;
    private Button btnSendMessage;
    private Toolbar toolbar;
    private TextView tvToolbarTitle;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private String chatId;
    private String receiverId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        receiverId = getIntent().getStringExtra(EXTRA_RECEIVER_ID);
        String receiverName = getIntent().getStringExtra(EXTRA_RECEIVER_NAME);

        if (currentUser == null || receiverId == null) {
            Toast.makeText(this, "Authentication error. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
            tvToolbarTitle.setText(receiverName);
        }

        rvMessages = findViewById(R.id.rv_messages);
        etMessageInput = findViewById(R.id.et_message_input);
        btnSendMessage = findViewById(R.id.btn_send_message);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUser.getUid());
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(messageAdapter);

        setupChat();

        btnSendMessage.setOnClickListener(v -> sendMessage());
    }

    private void setupChat() {
        String senderId = currentUser.getUid();

        // Chat ID එක නිර්මාණය කිරීම
        if (senderId.compareTo(receiverId) > 0) {
            chatId = senderId + "_" + receiverId;
        } else {
            chatId = receiverId + "_" + senderId;
        }

        db.collection("chats").document(chatId).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading messages.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        messageList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Message message = doc.toObject(Message.class);
                            messageList.add(message);
                        }
                        messageAdapter.notifyDataSetChanged();
                        rvMessages.scrollToPosition(messageList.size() - 1); // Scroll to the bottom
                    }
                });
    }

    private void sendMessage() {
        String text = etMessageInput.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            return;
        }

        Message message = new Message(currentUser.getUid(), receiverId, text, new Date());

        db.collection("chats").document(chatId).collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    etMessageInput.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
