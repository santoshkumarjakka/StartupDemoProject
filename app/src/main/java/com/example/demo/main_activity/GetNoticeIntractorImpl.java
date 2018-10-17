package com.example.demo.main_activity;

import android.content.CursorLoader;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;


import com.example.demo.model.NoticeList;
import com.example.demo.model.NoticeResponse;
import com.example.demo.my_interface.GetNoticeDataService;
import com.example.demo.network.RetrofitInstance;
import com.example.demo.product_activity.ProductContract;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bpn on 12/7/17.
 */

public class GetNoticeIntractorImpl implements MainContract.GetNoticeIntractor, ProductContract.GetNoticeIntractor {
    private String request;

    private HashMap<String, String> parmeter;

    public GetNoticeIntractorImpl(String request) {
        this.request = request;
    }

    public GetNoticeIntractorImpl(String request, HashMap<String, String> parmeter) {
        this.request = request;
        this.parmeter = parmeter;
    }

    @Override
    public void getNoticeArrayList(final OnFinishedListener onFinishedListener) {


        /** Create handle for the RetrofitInstance interface*/
        GetNoticeDataService service = RetrofitInstance.getRetrofitInstance().create(GetNoticeDataService.class);

        /** Call the method with parameter in the interface to get the notice data*/
        Call<ArrayList<NoticeList>> call;
        if (isNullOrEmpty(parmeter)) {
            call = service.getNoticeData(request);
        } else {
            call = service.getFilterNoticeData(request, parmeter);
        }

        /**Log the URL called*/
        Log.wtf("URL Called", call.request().url() + "");

        call.enqueue(new Callback<ArrayList<NoticeList>>() {
            @Override
            public void onResponse(Call<ArrayList<NoticeList>> call, Response<ArrayList<NoticeList>> response) {
                onFinishedListener.onFinished(response.body());

            }

            @Override
            public void onFailure(Call<ArrayList<NoticeList>> call, Throwable t) {
                onFinishedListener.onFailure(t);
            }
        });

    }

    public boolean isNullOrEmpty(final Map<?, ?> m) {
        return m == null || m.isEmpty();
    }

    @Override
    public void getUploadImage(final OnUploadFinishedListener onFinishedListener, String productName, String productBrand, String productPrice, ArrayList<String> fileUrl) {

        GetNoticeDataService service = RetrofitInstance.getRetrofitInstance().create(GetNoticeDataService.class);

        RequestBody pbName = RequestBody.create(MediaType.parse("text/plain"), productName);
        RequestBody pbBrand = RequestBody.create(MediaType.parse("text/plain"), productBrand);
        RequestBody pbPrice = RequestBody.create(MediaType.parse("text/plain"), productPrice);
        RequestBody fbody = RequestBody.create(MediaType.parse("image/*"), new File(fileUrl.get(0)));
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("product_name", pbName);
        map.put("product_brand", pbBrand);
        map.put("product_price", pbPrice);
        MultipartBody.Part[] parts = new MultipartBody.Part[fileUrl.size()];

        for (int i = 0; i < fileUrl.size(); i++) {
            File file = new File(String.valueOf(Uri.parse(fileUrl.get(i))));
            parts[i] = MultipartBody.Part.createFormData("product_image", file.getName(), fbody);
        }

        Call<NoticeResponse>
                call = service.UploadImageData(request, parts, map);


        call.enqueue(new Callback<NoticeResponse>() {
            @Override
            public void onResponse(Call<NoticeResponse> call, Response<NoticeResponse> response) {
                onFinishedListener.onFinished(response.body().getMsg());
            }

            @Override
            public void onFailure(Call<NoticeResponse> call, Throwable t) {
                onFinishedListener.onFailure(t);

            }
        });

    }


    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, String fileUri) {
        File file = new File(fileUri);
        RequestBody filename = RequestBody.create(MediaType.parse("image/*"), file.getName());

        return MultipartBody.Part.create(filename);
    }
//
}
