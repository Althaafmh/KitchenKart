package com.PROJECT.kitchenkart.DataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.PROJECT.kitchenkart.Models.User;

// Increment version from 1 to 2
@Database(entities = {User.class}, version = 2, exportSchema = false) // <--- Changed version to 2
public abstract class AppDatabase extends RoomDatabase {

    // --Commented out by Inspection (2025-08-05 16.15):public abstract UserDao UserDao();

    private static volatile AppDatabase INSTANCE;

// --Commented out by Inspection START (2025-08-05 16.15):
//    public static AppDatabase getDatabase(final Context context) {
//        if (INSTANCE == null) {
//            synchronized (AppDatabase.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                                    AppDatabase.class, "kitchenkart_database")
//                            .fallbackToDestructiveMigration() // <--- This will wipe old data on upgrade
//                            .build();
//                }
//            }
//        }
//        return INSTANCE;
//    }
// --Commented out by Inspection STOP (2025-08-05 16.15)
}