package com.PROJECT.kitchenkart.Models;

import androidx.annotation.NonNull;

/** @noinspection ALL*/
public class Category {
    private String title;
    private int imageResId;

    // Firebase Firestore සඳහා අවශ්‍ය වන හිස් constructor එක.
    public Category() {
    }

    public Category(String title, int imageResId) {
        this.title = title;
        this.imageResId = imageResId;
    }

    // Getters and setters for Firestore
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
