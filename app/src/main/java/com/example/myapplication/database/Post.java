package com.example.myapplication.database;

import android.net.Uri;

public class Post {

    public String id;
    public int amount;
    public String currency;
    public String category;
    public String remark;
    public String createdBy;
    public String createdDate;
    public Uri uri;

    public String getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCategory() {
        return category;
    }

    public String getRemark() {
        return remark;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }
    public Uri getUri() {
        if (uri != null && !uri.toString().isEmpty()) {
            return uri;
        } else {
            return null;
        }
    }

}
