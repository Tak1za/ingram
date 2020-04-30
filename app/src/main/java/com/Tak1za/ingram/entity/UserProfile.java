package com.Tak1za.ingram.entity;

import com.Tak1za.ingram.models.Post;
import com.Tak1za.ingram.models.User;

import java.util.List;

public class UserProfile {
    private User user;
    private List<Post> posts;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
