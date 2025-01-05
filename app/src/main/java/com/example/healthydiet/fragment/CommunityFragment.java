package com.example.healthydiet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthydiet.R;
import com.example.healthydiet.manager.UserManager;
import com.example.healthydiet.activity.AddPostActivity;
import com.example.healthydiet.adapter.PostListAdapter;
import com.example.healthydiet.entity.Post;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CommunityFragment extends Fragment {
    private WebSocketManager webSocketManager;
    private List<Post> PostList;
    private RecyclerView recyclerView;
    private PostListAdapter adapter;
    private AppCompatImageButton add_post;
    public CommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        recyclerView = view.findViewById(R.id.post_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // 使用垂直线性布局

        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.logConnectionStatus();  // 记录连接状态

        // 注册食物记录列表回调
        webSocketManager.registerCallback(WebSocketMessageType.GET_POST, message -> {
            Log.d("PostList", "Received post list response: " + message);
            try {
                JSONArray postLists = new JSONArray(message);
                PostList = new ArrayList<>();

                for (int i = 0; i < postLists.length(); i++) {
                    JSONObject postJson = postLists.getJSONObject(i);
                    Post post = new Post(
                         postJson.getInt("postId"),postJson.getInt("userId"),
                            postJson.getString("title"),postJson.getString("content"),
                            postJson.getString("tags"),postJson.getString("timestamp"),
                            postJson.getInt("isOffending")
                    );

                    PostList.add(post);
                }
                // Sort the list to put "管理员公告" posts at the front
                Collections.sort(PostList, new Comparator<Post>() {
                    @Override
                    public int compare(Post p1, Post p2) {
                        // If both posts are "管理员公告", return 0 (no change)
                        if (p1.getTags().equals("管理员公告") && p2.getTags().equals("管理员公告")) {
                            return 0;
                        }
                        // If p1 is "管理员公告", it should come before p2
                        if (p1.getTags().equals("管理员公告")) {
                            return -1;
                        }
                        // If p2 is "管理员公告", it should come before p1
                        if (p2.getTags().equals("管理员公告")) {
                            return 1;
                        }
                        // Otherwise, keep their order (return 0)
                        return 0;
                    }
                });

                // 在主线程更新UI
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        adapter = new PostListAdapter(PostList);
                        recyclerView.setAdapter(adapter);
                    });
                }
            } catch (Exception e) {
                Log.e("PostList", "Error processing post list: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // 确保WebSocket已连接后再发送请求
        if (!webSocketManager.isConnected()) {
            Log.d("PostList", "WebSocket not connected, attempting to reconnect...");
            webSocketManager.reconnect();
        }
        User user = UserManager.getInstance().getUser();
        String getPost = "getVisiblePosts:";
        webSocketManager.sendMessage(getPost);

        int is_blocked = user.getIsblocked();
        add_post = view.findViewById(R.id.circularButton);
        add_post.setOnClickListener(v -> {
            if (is_blocked == 0) {
                // 如果未被封禁，执行跳转
                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                startActivity(intent);
            } else {
                // 如果被封禁，显示提示信息
                Toast.makeText(getActivity(), "当前正在封禁中，不能发帖", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

}
