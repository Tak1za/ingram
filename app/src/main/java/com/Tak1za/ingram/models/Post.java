package com.Tak1za.ingram.models;

import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Post implements Serializable {
    private String caption;
    private String imageName;
    private transient Timestamp createdAt;
    private transient Timestamp updatedAt;
    private List<DocumentReference> likes;

    public Post(){

    }

    public Post(String caption, String imageName, Timestamp createdAt, Timestamp updatedAt, List<DocumentReference> likes) {
        this.caption = caption;
        this.imageName = imageName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likes = likes;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<DocumentReference> getLikes() {
        return likes;
    }

    public void setLikes(List<DocumentReference> likes) {
        this.likes = likes;
    }
}


