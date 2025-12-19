package com.example.myapplication.database;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface JsonPlaceholderApi {

    //"http://10.0.2.2:3000/"
    @GET("api/send_expenses")
    Call<List<Post>> getData();

    @POST("api/add_expenses")
    Call<Post> postData(@Body Post post);

}
