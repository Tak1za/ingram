package com.Tak1za.ingram.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<DocumentReference> followers;
    private List<DocumentReference> following;

    public User(String firstName, String lastName, String email, Timestamp createdAt, Timestamp updatedAt, List<DocumentReference> followers, List<DocumentReference> following) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.followers = followers;
        this.following = following;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public List<DocumentReference> getFollowers() {
        return followers;
    }

    public void setFollowers(List<DocumentReference> followers) {
        this.followers = followers;
    }

    public List<DocumentReference> getFollowing() {
        return following;
    }

    public void setFollowing(List<DocumentReference> following) {
        this.following = following;
    }
}