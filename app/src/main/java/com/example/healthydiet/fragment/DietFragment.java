package com.example.healthydiet.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.healthydiet.R;
import com.example.healthydiet.UserManager;
import com.example.healthydiet.activity.DietAnalysisActivity;
import com.example.healthydiet.activity.FoodlistActivity;
import com.example.healthydiet.activity.MainActivity;
import com.example.healthydiet.activity.RegisterActivity;
import com.example.healthydiet.activity.ViewFoodRecordActivity;
import com.example.healthydiet.adapter.FoodRecordAdapter;
import com.example.healthydiet.entity.FoodRecord;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// DietFragment.java
public class DietFragment extends Fragment {
    private ProgressBar circularProgressBar;
    private TextView caloriesTextView;  // 用来显示摄入千卡数
    private Button breakfast_button;
    private Button lunch_button;
    private Button dinner_button;

    private Button circularButton;
    private Button view_record;
    private Button diet_analysis;
    private WebSocketManager webSocketManager;
    private List<FoodRecord> TodayRecordList;
    private int total_calories=0;
    private double total_fat=0;
    private double total_protein=0;
    private double total_carbohydrates=0;
    private double total_sodium=0;
    private double total_potassium=0;
    private double total_dietaryFiber=0;
    private  double generation;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 101;
    private static final int REQUEST_CODE_GALLERY = 100;
    //private User user;
    public DietFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diet, container, false);

        circularProgressBar = view.findViewById(R.id.circularProgressBar);
        caloriesTextView = view.findViewById(R.id.caloriesTextView);

        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.logConnectionStatus();  // 记录连接状态

        // 注册食物记录列表回调
        webSocketManager.registerCallback(WebSocketMessageType.FOOD_RECORD_GET, message -> {
            Log.d("FoodRecordList", "Received food record list response: " + message);
            try {
                JSONArray foodLists = new JSONArray(message);
                TodayRecordList = new ArrayList<>();

                for (int i = 0; i < foodLists.length(); i++) {
                    JSONObject foodJson = foodLists.getJSONObject(i);
                    String record_time=foodJson.getString("recordTime");
                    if(istoday(record_time)){
                        int calories= foodJson.getInt("calories");
                        int food_record_id=foodJson.getInt("foodRecordId");
                        String food_name=foodJson.getString("foodname");
                        int user_id=foodJson.getInt("userId");
                        int food_id=foodJson.getInt("foodId");
                        double food_weight=foodJson.getDouble("foodWeight");
                        double fat=foodJson.getDouble("fat");
                        double protein=foodJson.getDouble("protein");
                        double carbohydrates=foodJson.getDouble("carbohydrates");
                        double sodium=foodJson.getDouble("sodium");
                        double potassium=foodJson.getDouble("potassium");
                        double dietaryFiber=foodJson.getDouble("dietaryFiber");
                        FoodRecord today_record=new FoodRecord(food_record_id,food_name,record_time,
                                user_id,food_id, food_weight,calories,fat,protein,carbohydrates,sodium,
                                potassium,dietaryFiber);
                        TodayRecordList.add(today_record);
                        total_calories+=calories;
                        total_potassium+=potassium;
                        total_protein+=protein;
                        total_carbohydrates+=carbohydrates;
                        total_dietaryFiber+=dietaryFiber;
                        total_fat+=fat;
                        total_sodium+=sodium;
                        System.out.println("fat："+fat);
                        System.out.println("total_fat："+total_fat);
                    }
                }

                User user = UserManager.getInstance().getUser();
                int gender= user.getGender();
                int weight= user.getWeight();
                double activity_factor= user.getActivity_factor();

                if(gender==0){
                    generation=(48.5*weight+2954.7)/4.184;
                    generation*=activity_factor;
                }
                else{
                    generation=(41.9*weight+2869.1)/4.184;
                    generation*=activity_factor;
                }
                updateProgressBar(total_calories, (int) generation);
                updateCaloriesText(total_calories);

            } catch (Exception e) {
                Log.e("FoodRecordList", "Error processing food record list: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // 确保WebSocket已连接后再发送请求
        if (!webSocketManager.isConnected()) {
            Log.d("FoodRecordList", "WebSocket not connected, attempting to reconnect...");
            webSocketManager.reconnect();
        }
        User user = UserManager.getInstance().getUser();
        String getRecord="getAllFoodRecord:" +user.getUserId();
        webSocketManager.sendMessage(getRecord);


        // 初始化跳转按钮
        breakfast_button = view.findViewById(R.id.breakfastButton);
        // 设置按钮点击事件，跳转到另一个 Activity
        breakfast_button.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(getActivity(), FoodlistActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            // 将 user 对象传递给目标 Activity
          //  intent.putExtra("user", user); // 将 user 对象作为 Extra 传递
            startActivity(intent);
        });

        // 初始化跳转按钮
        lunch_button = view.findViewById(R.id.lunchButton);
        // 设置按钮点击事件，跳转到另一个 Activity
        lunch_button.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(getActivity(), FoodlistActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            startActivity(intent);
        });

        // 初始化跳转按钮
        dinner_button = view.findViewById(R.id.dinnerButton);
        // 设置按钮点击事件，跳转到另一个 Activity
        dinner_button.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(getActivity(), FoodlistActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            startActivity(intent);
        });

        // 初始化跳转按钮
        view_record = view.findViewById(R.id.viewRecordButton);
        // 设置按钮点击事件，跳转到另一个 Activity
        view_record.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(getActivity(), ViewFoodRecordActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            startActivity(intent);
        });

        diet_analysis = view.findViewById(R.id.details_button);
        // 设置按钮点击事件，跳转到另一个 Activity
        diet_analysis.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(getActivity(), DietAnalysisActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            // 将 List 传递到目标 Activity
            intent.putExtra("TodayRecordList", (Serializable) TodayRecordList);  // 传递序列化的 List
            intent.putExtra("total_calories",total_calories);
            intent.putExtra("total_fat",total_fat);
            intent.putExtra("total_sodium",total_sodium);
            intent.putExtra("total_dietaryFiber",total_dietaryFiber);
            intent.putExtra("total_carbohydrates",total_carbohydrates);
            intent.putExtra("total_protein",total_protein);
            intent.putExtra("total_potassium",total_potassium);
            intent.putExtra("generation",generation);
            startActivity(intent);
        });
// 请求权限
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        } else {
            // 权限已授予，执行相册操作
            openGallery();
        }
        circularButton = view.findViewById(R.id.circularButton);
        circularButton.setOnClickListener(v -> openGallery());
        return view;
    }
    // 权限请求结果回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限授予，打开相册
                openGallery();
            } else {
                // 权限拒绝，提示用户
                Toast.makeText(getActivity(), "权限被拒绝，无法访问相册", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 打开相册选择器
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    // 处理返回的图片信息
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();  // 获取选中的图片 URI
            if (selectedImageUri != null) {
                getImageDetails(selectedImageUri);
            }
        }
    }

    // 获取图片的详细信息
    private void getImageDetails(Uri imageUri) {
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE
        };

        try (Cursor cursor = getActivity().getContentResolver().query(imageUri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                int nameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                int sizeColumn = cursor.getColumnIndex(MediaStore.Images.Media.SIZE);
                int dateAddedColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                int mimeTypeColumn = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE);

                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                long size = cursor.getLong(sizeColumn);
                long dateAdded = cursor.getLong(dateAddedColumn);
                String mimeType = cursor.getString(mimeTypeColumn);

                Log.d("ImageDetails", "ID: " + id);
                Log.d("ImageDetails", "Name: " + name);
                Log.d("ImageDetails", "Size: " + size + " bytes");
                Log.d("ImageDetails", "Date Added: " + dateAdded);
                Log.d("ImageDetails", "Mime Type: " + mimeType);

                // 将图片转为 Base64 字符串
                String base64Image = convertImageToBase64(imageUri);
                Log.d("ImageDetails", "Base64: " + base64Image); // 打印 Base64 编码的图片


            }
        }
    }
    private Bitmap resizeBitmap(Bitmap original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();
        float ratioBitmap = Math.min((float) maxWidth / width, (float) maxHeight / height);
        int widthFinal = (int) (width * ratioBitmap);
        int heightFinal = (int) (height * ratioBitmap);
        return Bitmap.createScaledBitmap(original, widthFinal, heightFinal, true);
    }
    // 这个方法用来将 Base64 字符串写入到文件中
    private void saveBase64ToFile(String base64String) {
        // 定义文件路径
        String fileName = getContext().getFilesDir() + "/image_base64.txt";
        FileOutputStream fos = null;
        try {
            // 打开文件输出流
            fos = new FileOutputStream(fileName);
            // 将 Base64 字符串写入文件
            fos.write(base64String.getBytes());
            fos.close();
            Log.d("ImageDetails", "Base64 data saved to: " + fileName);
        } catch (IOException e) {
            Log.e("ImageDetails", "Error writing Base64 to file: " + e.getMessage());
        }
    }
    private void sendBase64InChunks(String base64Image) {
        final int MAX_SIZE = 5000;  // 每段的最大大小，单位：字符
        int length = base64Image.length();
        int chunkCount = (int) Math.ceil((double) length / MAX_SIZE); // 计算分段数

        // 遍历每个段
        for (int i = 0; i < chunkCount; i++) {
            // 计算当前段的起始和结束位置
            int start = i * MAX_SIZE;
            int end = Math.min(start + MAX_SIZE, length);

            // 获取当前段的 Base64 字符串
            String chunk = base64Image.substring(start, end);

            // 构建消息，包含分段信息
            JSONObject message = new JSONObject();
            try {
                message.put("chunkIndex", i); // 当前段的索引
                message.put("totalChunks", chunkCount); // 总段数
                message.put("chunkData", chunk); // 当前段数据
            } catch (Exception e) {
                e.printStackTrace();
            }
// 确保WebSocket已连接后再发送请求
            if (!webSocketManager.isConnected()) {
                Log.d("ImageDetails", "WebSocket not connected, attempting to reconnect...");
                webSocketManager.reconnect();
            }
            webSocketManager.sendMessage("identify:"+message.toString());
            // 发送 WebSocket 消息
        }
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            // 获取 ContentResolver
            ContentResolver contentResolver = getActivity().getContentResolver();

            // 打开输入流获取图片文件
            InputStream inputStream = contentResolver.openInputStream(imageUri);

            // 将图片内容读入字节数组
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }

            // 关闭输入流
            inputStream.close();

            // 获取字节数组
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // 将字节数组转换为 Bitmap
            Bitmap originalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            // 压缩 Bitmap 图片
            Bitmap compressedBitmap = resizeBitmap(originalBitmap, 800, 800); // 你可以根据需求调整尺寸

            // 将压缩后的 Bitmap 转为字节数组
            ByteArrayOutputStream compressedByteArrayOutputStream = new ByteArrayOutputStream();
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, compressedByteArrayOutputStream); // 80 表示压缩质量

            // 获取压缩后的字节数组
            byte[] compressedImageBytes = compressedByteArrayOutputStream.toByteArray();

            // 使用 Base64 编码字节数组并返回编码后的字符串
            String base64Image = Base64.encodeToString(compressedImageBytes, Base64.NO_WRAP);  // NO_WRAP 去除换行符
            saveBase64ToFile(base64Image);
            sendBase64InChunks(base64Image);
            // 添加图片前缀，确保正确解码
            return base64Image;
        } catch (IOException e) {
            Log.e("ImageDetails", "Error converting image to Base64: " + e.getMessage());
            return null;
        }
    }


    // 更新环形进度条
    private void updateProgressBar(int x,int max) {
        // 计算进度百分比
        int progress = x; // 转换为百分比进度
        System.out.println("计算进度");
        // 在主线程中更新 UI
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("进度：");
                    System.out.println(progress);
                    circularProgressBar.setProgress(progress);  // 设置进度
                    circularProgressBar.setMax(max);
                }
            });
        }
    }
    // 更新摄入千卡数的文本
    private void updateCaloriesText(int x) {
        // 更新TextView的文本，显示摄入的千卡数
        String caloriesText = "今日已摄入 " + x + " 千卡";
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    caloriesTextView.setText(caloriesText);  // 更新文本内容
                }
            });
        }
    }
public boolean istoday(String day) throws ParseException {
    // 定义日期格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 解析给定的日期字符串为 Date 对象
    Date givenDate = sdf.parse(day);

    // 获取今天的日期
    Date today = new Date();

    // 比较日期（只比较年月日，不考虑时分秒）
    SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
    String givenDateStr = dayFormat.format(givenDate);
    String todayStr = dayFormat.format(today);

    if (givenDateStr.equals(todayStr)) {
       return true;
    } else {
       return false;
    }
}
}

