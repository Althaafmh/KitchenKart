package com.PROJECT.kitchenkart.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

/**
 * DBHelper class to manage the SQLite database for the Kitchen Kart application.
 * This class handles database creation, versioning, and provides methods for basic
 * CRUD operations on all tables. This schema is based on the E-commerce Order and
 * Cart Flow Diagram provided.
 */
public class DBHelper extends SQLiteOpenHelper {

    // --- Database information ---
    public static final String DATABASE_NAME = "KitchenKart.db";
    public static final int DATABASE_VERSION = 1;

    // --- Table and column names as public constants ---
    // User Table (acts as base for Buyer and Seller)
    public static final String TABLE_USERS = "User";
    public static final String COLUMN_USER_ID = "UserId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_DOB = "dob";
    public static final String COLUMN_NIC = "nic";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_POSTAL_CODE = "postalCode";
    public static final String COLUMN_PHONE_NUMBER = "phoneNumber";
    public static final String COLUMN_DIVISION = "division";
    public static final String COLUMN_USER_TYPE = "userType"; // 'Buyer' or 'Seller'

    // FoodItem Table
    public static final String TABLE_FOOD_ITEMS = "FoodItem";
    public static final String COLUMN_FOOD_ITEM_ID = "FoodItemId";
    public static final String COLUMN_FOOD_SELLER_ID = "SellerId"; // Foreign Key to User
    public static final String COLUMN_FOOD_NAME = "foodName";
    public static final String COLUMN_FOOD_DESCRIPTION = "description";
    public static final String COLUMN_FOOD_PRICE = "price";
    public static final String COLUMN_FOOD_IMAGE_URL = "imageUrl";

    // Cart Table
    public static final String TABLE_CART = "Cart";
    public static final String COLUMN_CART_ID = "CartId";
    public static final String COLUMN_CART_USER_ID = "UserId"; // Foreign Key to User

    // CartItem Table
    public static final String TABLE_CART_ITEMS = "CartItem";
    public static final String COLUMN_CART_ITEM_ID = "CartItemId";
    public static final String COLUMN_CART_ITEMS_CART_ID = "CartId"; // Foreign Key to Cart
    public static final String COLUMN_CART_ITEMS_FOOD_ITEM_ID = "FoodItemId"; // Foreign Key to FoodItem
    public static final String COLUMN_CART_ITEMS_QUANTITY = "quantity";

    // Order Table
    public static final String TABLE_ORDERS = "Order";
    public static final String COLUMN_ORDER_ID = "OrderId";
    public static final String COLUMN_ORDER_USER_ID = "UserId"; // Foreign Key to User
    public static final String COLUMN_ORDER_DATE = "orderDate";
    public static final String COLUMN_ORDER_STATUS = "orderStatus";
    public static final String COLUMN_ORDER_TOTAL = "totalPrice";

    // OrderItem Table
    public static final String TABLE_ORDER_ITEMS = "OrderItem";
    public static final String COLUMN_ORDER_ITEM_ID = "OrderItemId";
    public static final String COLUMN_ORDER_ITEMS_ORDER_ID = "OrderId"; // Foreign Key to Order
    public static final String COLUMN_ORDER_ITEMS_FOOD_ITEM_ID = "FoodItemId"; // Foreign Key to FoodItem
    public static final String COLUMN_ORDER_ITEMS_QUANTITY = "quantity";

    // Review Table
    public static final String TABLE_REVIEW = "Review";
    public static final String COLUMN_REVIEW_ID = "ReviewId";
    public static final String COLUMN_REVIEW_RATING_ID = "RatingId";
    public static final String COLUMN_REVIEW_TEXT = "reviewText";
    public static final String COLUMN_REVIEW_DATE = "reviewDate";

    // Rating Table
    public static final String TABLE_RATING = "Rating";
    public static final String COLUMN_RATING_ID = "RatingId";
    public static final String COLUMN_RATING_VALUE = "ratingValue";
    public static final String COLUMN_RATING_USER_ID = "UserId";
    public static final String COLUMN_RATING_SELLER_ID = "SellerId";

    // SellerOrders Table
    public static final String TABLE_SELLER_ORDERS = "SellerOrders";
    public static final String COLUMN_SELLER_ORDER_ID = "SellerOrderId";
    public static final String COLUMN_SELLER_ORDER_SELLER_ID = "SellerId";
    public static final String COLUMN_SELLER_ORDER_ORDER_ID = "OrderId";
    public static final String COLUMN_SELLER_ORDER_STATUS = "status";


    // --- SQL CREATE statements for all tables ---
    private static final String SQL_CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " TEXT PRIMARY KEY," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_EMAIL + " TEXT," +
                    COLUMN_PASSWORD + " TEXT," +
                    COLUMN_GENDER + " TEXT," +
                    COLUMN_DOB + " TEXT," +
                    COLUMN_NIC + " TEXT," +
                    COLUMN_ADDRESS + " TEXT," +
                    COLUMN_POSTAL_CODE + " TEXT," +
                    COLUMN_PHONE_NUMBER + " TEXT," +
                    COLUMN_DIVISION + " TEXT," +
                    COLUMN_USER_TYPE + " TEXT)";

    private static final String SQL_CREATE_TABLE_FOOD_ITEMS =
            "CREATE TABLE " + TABLE_FOOD_ITEMS + " (" +
                    COLUMN_FOOD_ITEM_ID + " TEXT PRIMARY KEY," +
                    COLUMN_FOOD_SELLER_ID + " TEXT," +
                    COLUMN_FOOD_NAME + " TEXT," +
                    COLUMN_FOOD_DESCRIPTION + " TEXT," +
                    COLUMN_FOOD_PRICE + " REAL," +
                    COLUMN_FOOD_IMAGE_URL + " TEXT," +
                    "FOREIGN KEY(" + COLUMN_FOOD_SELLER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";

    private static final String SQL_CREATE_TABLE_CART =
            "CREATE TABLE " + TABLE_CART + " (" +
                    COLUMN_CART_ID + " TEXT PRIMARY KEY," +
                    COLUMN_CART_USER_ID + " TEXT," +
                    "FOREIGN KEY(" + COLUMN_CART_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";

    private static final String SQL_CREATE_TABLE_CART_ITEMS =
            "CREATE TABLE " + TABLE_CART_ITEMS + " (" +
                    COLUMN_CART_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_CART_ITEMS_CART_ID + " TEXT," +
                    COLUMN_CART_ITEMS_FOOD_ITEM_ID + " TEXT," +
                    COLUMN_CART_ITEMS_QUANTITY + " INTEGER," +
                    "FOREIGN KEY(" + COLUMN_CART_ITEMS_CART_ID + ") REFERENCES " + TABLE_CART + "(" + COLUMN_CART_ID + ")," +
                    "FOREIGN KEY(" + COLUMN_CART_ITEMS_FOOD_ITEM_ID + ") REFERENCES " + TABLE_FOOD_ITEMS + "(" + COLUMN_FOOD_ITEM_ID + "))";

    private static final String SQL_CREATE_TABLE_ORDERS =
            String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY,%s TEXT,%s TEXT,%s TEXT,%s REAL,FOREIGN KEY(%s) REFERENCES %s(%s))", TABLE_ORDERS, COLUMN_ORDER_ID, COLUMN_ORDER_USER_ID, COLUMN_ORDER_DATE, COLUMN_ORDER_STATUS, COLUMN_ORDER_TOTAL, COLUMN_ORDER_USER_ID, TABLE_USERS, COLUMN_USER_ID);

    private static final String SQL_CREATE_TABLE_ORDER_ITEMS =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT,%s TEXT,%s INTEGER,FOREIGN KEY(%s) REFERENCES %s(%s),FOREIGN KEY(%s) REFERENCES %s(%s))", TABLE_ORDER_ITEMS, COLUMN_ORDER_ITEM_ID, COLUMN_ORDER_ITEMS_ORDER_ID, COLUMN_ORDER_ITEMS_FOOD_ITEM_ID, COLUMN_ORDER_ITEMS_QUANTITY, COLUMN_ORDER_ITEMS_ORDER_ID, TABLE_ORDERS, COLUMN_ORDER_ID, COLUMN_ORDER_ITEMS_FOOD_ITEM_ID, TABLE_FOOD_ITEMS, COLUMN_FOOD_ITEM_ID);

    private static final String SQL_CREATE_TABLE_RATING =
            "CREATE TABLE " + TABLE_RATING + " (" +
                    COLUMN_RATING_ID + " TEXT PRIMARY KEY," +
                    COLUMN_RATING_VALUE + " INTEGER," +
                    COLUMN_RATING_USER_ID + " TEXT," +
                    COLUMN_RATING_SELLER_ID + " TEXT," +
                    "FOREIGN KEY(" + COLUMN_RATING_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")," +
                    "FOREIGN KEY(" + COLUMN_RATING_SELLER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";

    private static final String SQL_CREATE_TABLE_REVIEW =
            "CREATE TABLE " + TABLE_REVIEW + " (" +
                    COLUMN_REVIEW_ID + " TEXT PRIMARY KEY," +
                    COLUMN_REVIEW_RATING_ID + " TEXT," +
                    COLUMN_REVIEW_TEXT + " TEXT," +
                    COLUMN_REVIEW_DATE + " TEXT," +
                    "FOREIGN KEY(" + COLUMN_REVIEW_RATING_ID + ") REFERENCES " + TABLE_RATING + "(" + COLUMN_RATING_ID + "))";

    private static final String SQL_CREATE_TABLE_SELLER_ORDERS =
            String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY,%s TEXT,%s TEXT,%s TEXT,FOREIGN KEY(%s) REFERENCES %s(%s),FOREIGN KEY(%s) REFERENCES %s(%s))", TABLE_SELLER_ORDERS, COLUMN_SELLER_ORDER_ID, COLUMN_SELLER_ORDER_SELLER_ID, COLUMN_SELLER_ORDER_ORDER_ID, COLUMN_SELLER_ORDER_STATUS, COLUMN_SELLER_ORDER_SELLER_ID, TABLE_USERS, COLUMN_USER_ID, COLUMN_SELLER_ORDER_ORDER_ID, TABLE_ORDERS, COLUMN_ORDER_ID);

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_USERS);
        db.execSQL(SQL_CREATE_TABLE_FOOD_ITEMS);
        db.execSQL(SQL_CREATE_TABLE_CART);
        db.execSQL(SQL_CREATE_TABLE_CART_ITEMS);
        db.execSQL(SQL_CREATE_TABLE_ORDERS);
        db.execSQL(SQL_CREATE_TABLE_ORDER_ITEMS);
        db.execSQL(SQL_CREATE_TABLE_RATING);
        db.execSQL(SQL_CREATE_TABLE_REVIEW);
        db.execSQL(SQL_CREATE_TABLE_SELLER_ORDERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART_ITEMS);
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_ORDERS));
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEW);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SELLER_ORDERS);
        // Create new tables
        onCreate(db);
    }

    /**
     * Inserts a new user into the database.
     * @param values The ContentValues object containing the user's data.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long insertUser(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = db.insert(TABLE_USERS, null, values);
        db.close();
        return newRowId;
    }
}
