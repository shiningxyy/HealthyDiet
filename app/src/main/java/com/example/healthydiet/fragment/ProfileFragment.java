package com.example.healthydiet.fragment;

import static retrofit2.Response.error;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.healthydiet.R;
import com.example.healthydiet.UserManager;
import com.example.healthydiet.activity.MainActivity;
import com.example.healthydiet.activity.ModifyInfoActivity;
import com.example.healthydiet.activity.NotificationListActivity;
import com.example.healthydiet.activity.ViewFoodRecordActivity;
import com.example.healthydiet.activity.ReminderActivity;
import com.example.healthydiet.entity.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private Button logout;
    private Button reminderSettingsButton;
    private Button modify_info;
    private Button notification;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 获取当前用户的信息
        User user = UserManager.getInstance().getUser();
        String profilePicture = user.getProfilePicture();  // 获取用户头像 URL
        String userName = user.getName();  // 获取用户昵称

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 获取视图中的头像和昵称控件
        CircleImageView profileImageView = view.findViewById(R.id.profileImageView);
        TextView nicknameTextView = view.findViewById(R.id.nicknameTextView);

        // 设置昵称
        nicknameTextView.setText(userName);

        // 使用 Glide 或 Picasso 等库加载图片
        Glide.with(this)
                .load(profilePicture)  // 加载用户头像的 URL
                .placeholder(R.drawable.ic_profile1)  // 加载中的占位图
                .error(R.drawable.avater)  // 加载失败时的图片
                .into(profileImageView);  // 设置到头像视图

        // 登出
        logout = view.findViewById(R.id.logoutButton);
        // 设置按钮点击事件，跳转到另一个 Activity
        logout.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(getActivity(), MainActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            startActivity(intent);
        });

        reminderSettingsButton = view.findViewById(R.id.reminderSettingsButton);
        reminderSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReminderActivity.class);
            startActivity(intent);
        });
        modify_info=view.findViewById(R.id.editProfileButton);
        modify_info.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(getActivity(), ModifyInfoActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            startActivity(intent);
        });

        notification=view.findViewById(R.id.notificationButton);
        notification.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(getActivity(), NotificationListActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            startActivity(intent);
        });

        return view;
    }
}
