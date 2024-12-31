package com.example.healthydiet.activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.healthydiet.R;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.fragment.CommunityFragment;
import com.example.healthydiet.fragment.DietFragment;
import com.example.healthydiet.fragment.HealthyFragment;
import com.example.healthydiet.fragment.ProfileFragment;
import com.example.healthydiet.websocket.WebSocketManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity{
    private BottomNavigationView bottomNavigationView;
    private Fragment currentFragment;
    private WebSocketManager webSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        String fragmentKey = getIntent().getStringExtra("fragment_key");

        // 根据标识加载相应的 Fragment

        // 获取WebSocketManager实例
        webSocketManager = WebSocketManager.getInstance();

        // 获取底部导航栏
        bottomNavigationView = findViewById(R.id.bottom_navigation);


        // 默认显示 DietFragment
        if (savedInstanceState == null) {
            currentFragment = new DietFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, currentFragment)
                    .commit();
        }
        if ("HealthyFragment".equals(fragmentKey)) {
            currentFragment = new HealthyFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, currentFragment)
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_healthy);

        }
        else if ("DietFragment".equals(fragmentKey)) {
            currentFragment = new DietFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, currentFragment)
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_diet);

        }
        // 设置导航栏项选择监听器
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();  // 获取当前点击的菜单项的ID


                if(id==R.id.nav_diet){
                    if (!(currentFragment instanceof DietFragment)) {
                        currentFragment = new DietFragment();
                        switchFragment(currentFragment);
                    }
                    return true;
                }
                else if(id==R.id.nav_healthy){
                    if (!(currentFragment instanceof HealthyFragment)) {
                        currentFragment = new HealthyFragment();
                        switchFragment(currentFragment);
                    }
                    return true;
                }
                else if(id==R.id.nav_community) {
                    if (!(currentFragment instanceof CommunityFragment)) {
                        currentFragment = new CommunityFragment();
                        switchFragment(currentFragment);
                    }
                    return true;
                }
                else if(id==R.id.nav_profile) {
                    if (!(currentFragment instanceof ProfileFragment)) {
                        currentFragment = new ProfileFragment();
                        switchFragment(currentFragment);
                    }
                    return true;
                }
            return false;
        });
    }

    private void switchFragment(Fragment fragment) {
//        // 将 user 对象传递给 Fragment
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("user", user);  // 传递 user 对象
//        fragment.setArguments(bundle);  // 将 Bundle 设置为 Fragment 的参数

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)  // 替换当前Fragment
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 如果需要在Activity销毁时关闭WebSocket连接
        // webSocketManager.closeConnection();
    }

}
