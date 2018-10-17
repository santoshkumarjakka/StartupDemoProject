package com.example.demo.main_activity;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.demo.custom.FilterDialog;
import com.example.demo.model.NoticeList;
import com.example.demo.product_activity.AddProductActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by bpn on 12/7/17.
 */

public class MainPresenterImpl implements MainContract.presenter, MainContract.GetNoticeIntractor.OnFinishedListener {

    private MainContract.MainView mainView;
    private MainContract.GetNoticeIntractor getNoticeIntractor;

    public MainPresenterImpl(MainContract.MainView mainView, MainContract.GetNoticeIntractor getNoticeIntractor) {
        this.mainView = mainView;
        this.getNoticeIntractor = getNoticeIntractor;
    }

    @Override
    public void onDestroy() {

        mainView = null;

    }

    @Override
    public void onButtonClick() {

        if(mainView != null){
            ((MainActivity) mainView.getContext()).startActivityForResult(new Intent(mainView.getContext(), AddProductActivity.class),200);
        }


    }

    @Override
    public void requestDataFromServer() {
        if(mainView != null){
            mainView.showProgress();
        }
        getNoticeIntractor.getNoticeArrayList(this);
    }


    @Override
    public void onFinished(ArrayList<NoticeList> noticeArrayList) {
        if(mainView != null){

            Collections.sort(noticeArrayList, new Comparator<NoticeList>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public int compare(NoticeList sp1, NoticeList sp2) {
                    int firstValue= Integer.parseInt(sp1.getProductPrice().replaceAll("[^-?0-9]",""));
                    int secondValue= Integer.parseInt(sp2.getProductPrice().replaceAll("[^-?0-9]",""));
                    return Integer.compare(firstValue, secondValue);
                }
            });
            mainView.setDataToRecyclerView(noticeArrayList);
            mainView.hideProgress();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        if(mainView != null){
            mainView.onResponseFailure(t);
            mainView.hideProgress();
        }
    }
    @Override
    public void onOpenFilterDialog(Context context,ArrayList<NoticeList> noticeList) {
        String maxmiumValue = noticeList.get(noticeList.size() - 1).getProductPrice();
        String mimumValue = noticeList.get(0).getProductPrice().replaceAll("[^-?0-9]","");
        Set<String> removeDuplicateList = new LinkedHashSet<>();
        for (NoticeList value : noticeList) {
            removeDuplicateList.add(value.getProductBrand());
        }
        ArrayList<String> brandList= new ArrayList<>(removeDuplicateList);
        Collections.sort(brandList);

        new FilterDialog(context,brandList,noticeList, maxmiumValue, mimumValue);
    }

}
