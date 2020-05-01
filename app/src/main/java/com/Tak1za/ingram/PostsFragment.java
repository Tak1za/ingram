package com.Tak1za.ingram;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.Tak1za.ingram.models.Post;

import java.io.Serializable;
import java.util.List;

public class PostsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    List<Post> userPosts;
    RecyclerView userPostsGridView;

    public PostsFragment() {

    }

    public static PostsFragment newInstance(List<Post> param1) {
        PostsFragment fragment = new PostsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, (Serializable) param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        userPosts = (List<Post>) getArguments().getSerializable(ARG_PARAM1);
        userPostsGridView = view.findViewById(R.id.userPostsGridView);
        
        userPostsGridView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        UserPostsAdapter userPostsAdapter = new UserPostsAdapter(getActivity(), userPosts);
        userPostsGridView.setAdapter(userPostsAdapter);

        return view;
    }
}
