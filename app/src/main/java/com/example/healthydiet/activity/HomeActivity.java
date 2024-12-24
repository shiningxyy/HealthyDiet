package com.example.healthydiet.activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.healthydiet.R;
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

        // 获取WebSocketManager实例
        webSocketManager = WebSocketManager.getInstance();

        // 获取底部导航栏
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 从 Intent 中获取传递的数据
        Intent intent = getIntent();
        int exerciseId = intent.getIntExtra("exerciseId", -1);
        int caloriesPerHour = intent.getIntExtra("caloriesPerHour", 0);
        String exerciseName = intent.getStringExtra("name");
        int duration = intent.getIntExtra("duration", 0);
        int burnedCalories = intent.getIntExtra("burnedCalories", 0);
        String date = intent.getStringExtra("date");

        // 创建 Bundle 并传递数据
        Bundle bundle = new Bundle();
        bundle.putInt("exerciseId", exerciseId);
        bundle.putInt("caloriesPerHour", caloriesPerHour);
        bundle.putString("name", exerciseName);
        bundle.putInt("duration", duration);
        bundle.putInt("burnedCalories", burnedCalories);
        bundle.putString("date", date);

        // 默认显示 DietFragment
        if (savedInstanceState == null) {
            currentFragment = new DietFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, currentFragment)
                    .commit();
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
