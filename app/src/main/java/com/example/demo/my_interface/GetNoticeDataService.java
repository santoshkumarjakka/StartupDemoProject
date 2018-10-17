package com.example.demo.my_interface;


import com.example.demo.model.NoticeList;
import com.example.demo.model.NoticeResponse;


import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface GetNoticeDataService {

    @GET("test.php")
    Call<ArrayList<NoticeList>> getNoticeData(@Query("request") String param1);
    @FormUrlEncoded
    @POST("test.php")
    Call<ArrayList<NoticeList>> getFilterNoticeData(@Query("request") String param1, @FieldMap HashMap<String, String> authData);
    @Multipart
    @POST("test.php")
    Call<NoticeResponse> UploadImageData(@Query("request") String param1, @Part MultipartBody.Part[]  authData, @PartMap HashMap<String,RequestBody> productName);
}