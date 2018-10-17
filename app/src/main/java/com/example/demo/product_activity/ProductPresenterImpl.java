package com.example.demo.product_activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.demo.R;
import com.example.demo.custom.Utility;

import java.util.ArrayList;

/**
 * Created by bpn on 12/7/17.
 */

public class ProductPresenterImpl implements ProductContract.presenter, ProductContract.GetNoticeIntractor.OnUploadFinishedListener {

    private ProductContract.MainView mainView;
    private ProductContract.GetNoticeIntractor getNoticeIntractor;
    private ImageView currentView;

    public ProductPresenterImpl(ProductContract.MainView mainView, ProductContract.GetNoticeIntractor getNoticeIntractor) {
        this.mainView = mainView;
        this.getNoticeIntractor = getNoticeIntractor;
    }

    @Override
    public void onDestroy() {

        mainView = null;

    }

    @Override
    public void onClickImage(ImageView view, int requestCode) {
        if (mainView != null) {
            currentView = view;
            if (Utility.checkPermission(mainView.getContext())) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                ((Activity) mainView.getContext()).startActivityForResult(Intent.createChooser(galleryIntent, "Select File"), requestCode);
            }
        }


    }

    @Override
    public ImageView getViewImage() {
        return currentView;
    }

    @Override
    public void requestDataFromServer(String productName, String productBrand, String productPrice, ArrayList<String> fileUrl) {
        if (mainView != null) {
            mainView.showProgress();
        }
        getNoticeIntractor.getUploadImage(this, productName, productBrand, productPrice, fileUrl);
    }


    @Override
    public void onFinished(String noticeArrayList) {
        if (mainView != null) {
            Toast.makeText(mainView.getContext(), noticeArrayList, Toast.LENGTH_LONG).show();
            Intent i = new Intent();
            mainView.hideProgress();
            ((Activity) mainView.getContext()).finish();
            ((Activity) mainView.getContext()).setResult(200, i);
        }
    }

    @Override
    public void onFailure(Throwable t) {
        if (mainView != null) {
            mainView.onResponseFailure(t);
            mainView.hideProgress();
        }
    }

    @Override
    public String getRealPathFromURI(Uri contentUri, Context context) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

}
