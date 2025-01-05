package com.example.healthydiet.fragment;

import static retrofit2.Response.error;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.healthydiet.R;
import com.example.healthydiet.manager.UserManager;
import com.example.healthydiet.activity.MainActivity;
import com.example.healthydiet.activity.ModifyInfoActivity;
import com.example.healthydiet.activity.NotificationListActivity;
import com.example.healthydiet.activity.ReminderActivity;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private Button logout;
    private Button reminderSettingsButton;
    private Button modify_info;
    private Button notification;
    private WebSocketManager webSocketManager;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 101;
    private static final int REQUEST_CODE_GALLERY = 100;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        webSocketManager = WebSocketManager.getInstance();

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
// 将头像设置为按钮并添加点击事件

        profileImageView.setOnClickListener(v -> openGallery());

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
            if (!webSocketManager.isConnected()) {
                Log.d("ImageDetails", "WebSocket not connected, attempting to reconnect...");
                webSocketManager.reconnect();
            }
            webSocketManager.sendMessage("identify:"+base64Image);            // 添加图片前缀，确保正确解码
            return base64Image;
        } catch (IOException e) {
            Log.e("ImageDetails", "Error converting image to Base64: " + e.getMessage());
            return null;
        }
    }

}
