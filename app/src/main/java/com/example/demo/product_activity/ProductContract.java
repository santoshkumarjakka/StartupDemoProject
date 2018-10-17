package com.example.demo.product_activity;

import android.content.Context;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.demo.model.NoticeList;

import java.util.ArrayList;

/**
 * Created by bpn on 12/6/17.
 */

public interface ProductContract {

    /**
     * Call when user interact with the view and other when view OnDestroy()
     */
    interface presenter {

        void onDestroy();

        void onClickImage(ImageView view, int requestCode);

        ImageView getViewImage();

        String getRealPathFromURI(Uri contentUri, Context context);

        void requestDataFromServer(String productName, String productBrand, String productPrice, ArrayList<String> fileUrl);

    }

    /**
     * showProgress() and hideProgress() would be used for displaying and hiding the progressBar
     * while the setDataToRecyclerView and onResponseFailure is fetched from the GetNoticeInteractorImpl class
     **/
    interface MainView {

        void showProgress();

        void hideProgress();

        void setAdapter(ArrayAdapter<Integer> adapter);

        void onResponseFailure(Throwable throwable);

        boolean checkNetworkConection();

        Context getContext();


    }

    /**
     * Intractors are classes built for fetching data from your database, web services, or any other data source.
     **/
    interface GetNoticeIntractor {

        interface OnUploadFinishedListener {
            void onFinished(String noticeArrayList);

            void onFailure(Throwable t);
        }

        void getUploadImage(OnUploadFinishedListener onFinishedListener, String productName, String productBrand, String productPrice, ArrayList<String> fileUrl);
    }
}
