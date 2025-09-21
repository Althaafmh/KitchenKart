// src/main/java/com/PROJECT/kitchenkart/Database/UserDao.java
package com.PROJECT.kitchenkart.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.PROJECT.kitchenkart.Models.User;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Use REPLACE to update if user exists
    void insertUser(User user);

    @Query("SELECT * FROM users WHERE uid = :userId")
    User getUserById(String userId);

    @Update
    void updateUser(User user);

    @Query("DELETE FROM users WHERE uid = :userId")
    void deleteUser(String userId); // Optional: if you need to delete a user
}