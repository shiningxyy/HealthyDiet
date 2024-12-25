package com.example.healthydiet.activity;

import android.os.Bundle;
import android.os.Handler;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthydiet.R;

import com.example.healthydiet.UserManager;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

public class AddFoodRecordActivity extends AppCompatActivity {
    private WebSocketManager webSocketManager;
    private Handler handler;
    private TextInputEditText weightEditText;
    private Button addButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfoodrecord);

        // 初始化 Handler，用于在主线程更新 UI
        handler = new Handler(getMainLooper());

        // 获取传递的 Intent 和数据
        AtomicReference<Intent> intent = new AtomicReference<>(getIntent());
        int food_id = intent.get().getIntExtra("food_id",0);
        String foodName = intent.get().getStringExtra("food_name");  // 获取食物名称
        int foodCalories = intent.get().getIntExtra("food_calories",0);
        double foodFat= intent.get().getDoubleExtra("food_fat",0.0);
        double foodProtein= intent.get().getDoubleExtra("food_protein",0.0);
        double foodCarbohydrates= intent.get().getDoubleExtra("food_carbohydrates",0.0);
        double foodDietaryFiber= intent.get().getDoubleExtra("food_DietaryFiber",0.0);
        double foodPotassium= intent.get().getDoubleExtra("food_Potassium",0.0);
        double foodSodium= intent.get().getDoubleExtra("food_Sodium",0.0);

        // 使用获取的数据，例如在 UI 中显示
        TextView foodNameTextView = findViewById(R.id.foodNameTextView);
        TextView foodCaloriesTextView = findViewById(R.id.caloriesTextView);
        TextView foodFatTextView=findViewById(R.id.fatTextView);
        TextView proteinTextView=findViewById(R.id.proteinTextView);
        TextView carbohydratesTextView=findViewById(R.id.carbohydratesTextView);

        foodNameTextView.setText(foodName);
        foodCaloriesTextView.setText( foodCalories + "千卡/100克");
        foodFatTextView.setText( "脂肪："+foodFat + "克/100克");
        proteinTextView.setText( "蛋白质："+foodProtein + "克/100克");
        carbohydratesTextView.setText( "碳水化合物："+foodCarbohydrates + "克/100克");


        // 获取按钮并设置点击事件
        addButton = findViewById(R.id.add_button);

        addButton.setOnClickListener(v -> {
            weightEditText = findViewById(R.id.food_weight);
            double weight = Double.parseDouble(weightEditText.getText().toString().trim());
            int sumCalories= (int) (foodCalories*weight/100);
            double sumFat=foodFat*weight/100;
            double sumProtein=foodProtein*weight/100;
            double sumCarbohydrates=foodCarbohydrates*weight/100;
            double sumDietaryFiber=foodDietaryFiber*weight/100;
            double sumPotassium=foodPotassium*weight/100;
            double sumSodium=foodSodium*weight/100;

                if (weight<=0) {
                    Toast.makeText(AddFoodRecordActivity.this, "请输入食物克数", Toast.LENGTH_SHORT).show();
                } else {
                    // 获取当前日期和时间
                    Calendar calendar = Calendar.getInstance();
                    // 创建 SimpleDateFormat 对象，定义日期时间的格式
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    // 格式化当前日期和时间
                    String formattedDateTime = sdf.format(calendar.getTime());
                    User user = UserManager.getInstance().getUser();
                    String addMessage = "addFoodRecord:{" +
                            "\"recordTime\": \"" + formattedDateTime + "\"," +
                            "\"userId\": \"" + user.getUserId() + "\"," +
                            "\"foodId\": " + food_id + "," +
                            "\"foodWeight\": " + weight + "," +
                            "\"calories\": " + sumCalories + "," +
                            "\"fat\": " + sumFat + "," +
                            "\"protein\": " + sumProtein + "," +
                            "\"carbohydrates\": " + sumCarbohydrates + "," +
                            "\"sodium\": " + sumSodium + "," +
                            "\"potassium\": " + sumPotassium + "," +
                            "\"dietaryFiber\": \"" + sumDietaryFiber + "\"" +
                            "}";
                    Log.d("addFoodRecord", "Sending register message: " + addMessage);



                    if (!webSocketManager.isConnected()) {
                        Log.d("RegisterActivity", "WebSocket not connected, attempting to reconnect...");
                        webSocketManager.reconnect();
                    }

                        Log.d("AddFoodRecord", "WebSocket connected successfully");
                        webSocketManager.sendMessage(addMessage);


                }
        });


        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.logConnectionStatus();  // 记录连接状态
        Log.d("AddFoodRecord", "注册回调之前");

        webSocketManager.registerCallback(WebSocketMessageType.FOOD_RECORD_ADD, message -> {
            Log.d("AddFoodRecord", "注册回调");
            try {
                JSONObject response = new JSONObject(message);
                Log.d("AddFoodRecord", "message:"+message);
                if (response.optInt("status") == 200) {
                    Log.d("AddFoodRecord", "Add successful");
                    Toast.makeText(AddFoodRecordActivity.this, "Add food record successful", Toast.LENGTH_SHORT).show();
                    intent.set(new Intent(AddFoodRecordActivity.this, HomeActivity.class));
                    startActivity(intent.get());

                    finish();
                } else {
                    Log.d("AddFoodRecord", "返回的不是200");
                    runOnUiThread(() ->
                            Toast.makeText(AddFoodRecordActivity.this, "返回的不是200", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                Log.e("AddFoodRecord", "Error processing add response: " + e.getMessage());
                e.printStackTrace();
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.unregisterCallback(WebSocketMessageType.FOOD_RECORD_ADD);
    }
}
