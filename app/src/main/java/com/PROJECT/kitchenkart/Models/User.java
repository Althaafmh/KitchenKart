// src/main/java/com/PROJECT/kitchenkart/Models/User.java
package com.PROJECT.kitchenkart.Models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.room.Ignore; // Import the Ignore annotation

/** @noinspection ALL*/
@Entity(tableName = "users")
public class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "phone")
    private String phone;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "profile_picture_url")
    private String profilePictureUrl;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name = "profile_picture_blob")
    private byte[] profilePictureBlob;

    // Use the @Ignore annotation to tell Room to skip this constructor.
    // This constructor is specifically for Firebase Firestore's object deserialization.
    @Ignore
    public User() {
        // Required for Firebase Firestore
    }

    public User(@NonNull String uid, String name, String email, String phone, String address, String profilePictureUrl, byte[] profilePictureBlob) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.profilePictureUrl = profilePictureUrl;
        this.profilePictureBlob = profilePictureBlob;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public byte[] getProfilePictureBlob() {
        return profilePictureBlob;
    }

    public void setProfilePictureBlob(byte[] profilePictureBlob) {
        this.profilePictureBlob = profilePictureBlob;
    }
}
