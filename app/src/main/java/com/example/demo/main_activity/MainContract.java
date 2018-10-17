package com.example.demo.main_activity;

import android.content.Context;

import com.example.demo.model.NoticeList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bpn on 12/6/17.
 */

public interface MainContract {

    /**
     * Call when user interact with the view and other when view OnDestroy()
     * */
    interface presenter{

        void onDestroy();

        void onButtonClick();
        void onOpenFilterDialog(Context context, ArrayList<NoticeList> noticeList);
        void requestDataFromServer();

    }

    /**
     * showProgress() and hideProgress() would be used for displaying and hiding the progressBar
     * while the setDataToRecyclerView and onResponseFailure is fetched from the GetNoticeInteractorImpl class
     **/
    interface MainView {

        void showProgress();

        void hideProgress();

        void setDataToRecyclerView(ArrayList<NoticeList> noticeArrayList);

        void onResponseFailure(Throwable throwable);

        boolean checkNetworkConection();
        Context getContext();


    }

    interface DialogView{

        void addValues(HashMap<String,String> MAP);}
    /**
     * Intractors are classes built for fetching data from your database, web services, or any other data source.
     **/
    interface GetNoticeIntractor {

        interface OnFinishedListener {
            void onFinished(ArrayList<NoticeList> noticeArrayList);
            void onFailure(Throwable t);
        }

        void getNoticeArrayList(OnFinishedListener onFinishedListener);
    }
}
