package com.PROJECT.kitchenkart.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.PROJECT.kitchenkart.Models.CartItem;
import com.PROJECT.kitchenkart.Models.Product;
import com.PROJECT.kitchenkart.Models.User;
import com.PROJECT.kitchenkart.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity to display product details and handle interactions such as adding to cart and initiating a chat.
 * @noinspection ALL
 */
public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT = "extra_product";

    // UI elements
    private ImageView ivProductImage;
    private TextView tvProductName, tvProductPrice, tvSellerName, tvProductDescription, tvAvailableQuantity;
    private Button btnAddToCart;
    private TextView btnMakeOffer, btnIsAvailable, btnLastPrice;
    private TextView etMessage;
    private Button btnStartChat, btnCall;

    // Firebase instances
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Product currentProduct;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Product Details");
        }

        // Initialize all UI elements
        ivProductImage = findViewById(R.id.iv_product_image);
        tvProductName = findViewById(R.id.tv_product_name);
        tvProductPrice = findViewById(R.id.tv_product_price);
        tvSellerName = findViewById(R.id.tv_seller_name);
        tvProductDescription = findViewById(R.id.tv_description);
        tvAvailableQuantity = findViewById(R.id.tv_available_quantity);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnMakeOffer = findViewById(R.id.btn_make_offer);
        btnIsAvailable = findViewById(R.id.btn_is_available);
        btnLastPrice = findViewById(R.id.btn_last_price);
        etMessage = findViewById(R.id.et_message_box);
        btnStartChat = findViewById(R.id.btn_start_chat);
        btnCall = findViewById(R.id.btn_call);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_PRODUCT)) {
            currentProduct = (Product) intent.getSerializableExtra(EXTRA_PRODUCT);
            if (currentProduct != null) {
                populateUI(currentProduct);
                loadSellerDetails(currentProduct.getSellerId());
            }
        } else {
            Toast.makeText(this, "Product data not found.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up click listeners for all interactive elements
        btnAddToCart.setOnClickListener(v -> {
            if (currentUser != null && currentProduct != null) {
                addCartItem(currentProduct);
            } else {
                Toast.makeText(this, "Please log in to add items to cart.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        btnMakeOffer.setOnClickListener(v -> startChatWithInitialMessage("Is this available? I would like to make an offer."));
        btnIsAvailable.setOnClickListener(v -> startChatWithInitialMessage("Is this available?"));
        btnLastPrice.setOnClickListener(v -> startChatWithInitialMessage("What is the last price?"));

        btnStartChat.setOnClickListener(v -> {
            String customMessage = etMessage.getText().toString();
            if (!TextUtils.isEmpty(customMessage)) {
                startChatWithInitialMessage(customMessage);
            } else {
                Toast.makeText(this, "Please enter a message to start the chat.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void populateUI(@NonNull Product product) {
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(this).load(product.getImageUrl()).into(ivProductImage);
        } else {
            ivProductImage.setImageResource(R.drawable.ic_product_placeholder);
        }

        tvProductName.setText(product.getName());
        tvProductPrice.setText(String.format("Rs. %.2f", product.getPrice()));
        tvProductDescription.setText(product.getDescription());
        tvAvailableQuantity.setText(String.format("Available Quantity: %d", product.getQuantity()));
    }

    private void loadSellerDetails(@NonNull String sellerId) {
        DocumentReference sellerRef = db.collection("users").document(sellerId);
        sellerRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User seller = documentSnapshot.toObject(User.class);
                if (seller != null) {
                    tvSellerName.setText(String.format("Seller: %s", seller.getName()));

                    // Add the call button functionality here
                    btnCall.setOnClickListener(v -> {
                        if (seller.getPhone() != null && !seller.getPhone().isEmpty()) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + seller.getPhone()));
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Seller's phone number is not available.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                tvSellerName.setText("Seller details not found.");
            }
        }).addOnFailureListener(e -> {
            tvSellerName.setText("Error loading seller details.");
        });
    }

    private void addCartItem(@NonNull Product product) {
        // Create a new CartItem object from the product data
        CartItem cartItem = new CartItem();
        cartItem.setProductId(product.getId());
        cartItem.setProductName(product.getName());
        cartItem.setPrice(product.getPrice());
        cartItem.setQuantity(1); // Default quantity to 1 when adding
        cartItem.setProductImageUrl(product.getImageUrl());

        // Add the new item to the user's cart in Firestore
        db.collection("users").document(currentUser.getUid()).collection("cart")
                .add(cartItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Added to cart successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void startChatWithInitialMessage(@NonNull String initialMessage) {
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to start a chat.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        String sellerId = currentProduct.getSellerId();
        if (sellerId == null || sellerId.isEmpty()) {
            Toast.makeText(this, "Seller information is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        String senderId = currentUser.getUid();

        if (senderId.equals(sellerId)) {
            Toast.makeText(this, "You cannot chat with yourself.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique chat ID by sorting the two user IDs
        String chatId = (senderId.compareTo(sellerId) < 0) ? senderId + "_" + sellerId : sellerId + "_" + senderId;

        // Check if a chat already exists
        db.collection("chats").document(chatId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Chat already exists, just send the message
                        sendMessageAndStartChat(chatId, senderId, sellerId, initialMessage);
                    } else {
                        // Chat doesn't exist, create it first
                        Map<String, Object> chatData = new HashMap<>();
                        chatData.put("users", Arrays.asList(senderId, sellerId));
                        chatData.put("lastMessage", initialMessage);
                        chatData.put("timestamp", new Date());

                        db.collection("chats").document(chatId)
                                .set(chatData, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    sendMessageAndStartChat(chatId, senderId, sellerId, initialMessage);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to initiate chat: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking for existing chat: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    private void sendMessageAndStartChat(@NonNull String chatId, @NonNull String senderId, @NonNull String receiverId, @NonNull String text) {
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "Message cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("senderId", senderId);
        messageData.put("receiverId", receiverId);
        messageData.put("text", text);
        messageData.put("timestamp", new Date());

        db.collection("chats").document(chatId).collection("messages")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    // Update the lastMessage and timestamp of the parent chat document
                    Map<String, Object> chatUpdates = new HashMap<>();
                    chatUpdates.put("lastMessage", text);
                    chatUpdates.put("timestamp", new Date());
                    db.collection("chats").document(chatId).update(chatUpdates);

                    Intent chatIntent = new Intent(this, ChatActivity.class);
                    chatIntent.putExtra(ChatActivity.EXTRA_RECEIVER_ID, receiverId);
                    String sellerName = tvSellerName.getText().toString().replace("Seller: ", "");
                    chatIntent.putExtra(ChatActivity.EXTRA_RECEIVER_NAME, sellerName);
                    startActivity(chatIntent);

                    // Clear the message box after sending
                    if (etMessage != null) {
                        etMessage.setText("");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
