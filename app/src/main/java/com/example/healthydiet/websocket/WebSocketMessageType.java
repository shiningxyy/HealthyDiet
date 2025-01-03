package com.example.healthydiet.websocket;

public class WebSocketMessageType {
    public static final String LOGIN = "login";
    public static final String REGISTER = "register";
    public static final String FOOD_LIST = "getAllFood";
    public static final String FOOD_RECORD_ADD = "addFoodRecord";
    public static final String FOOD_RECORD_GET="getAllFoodRecord";
    public static final String FOOD_ITEM_GET="getFoodItemById";
    public static final String EXERCISE_RECORD_GET = "getUserExerciseRecord";
    public static final String EXERCISE_RECORD_ADD = "AddExerciseRecord";
    public static final String EXERCISE_LIST = "getAllExerciseItem";
    public static final String UPDATE_USER="updateUser";

    public static final String FOOD_IDENTIFY="identify";

    public static final String ADD_POST="createPost";
    public static final String GET_POST="getVisiblePosts";
    public static final String ADD_COMMENT="createComment";
    public static final String GET_POSTCOMMENTS="getPostComments";
    // 添加其他消息类型...
} 