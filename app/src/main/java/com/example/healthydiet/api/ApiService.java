package com.example.healthydiet.api;

import com.example.healthydiet.entity.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("users/register")  // 后端接口的完整 URL 是 http://localhost:8080/api/api/users/register
    Call<ResponseBody> registerUser(@Body User user);
}
