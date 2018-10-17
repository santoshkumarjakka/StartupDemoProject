package com.example.demo.main_activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.demo.R;
import com.example.demo.adapter.NoticeAdapter;
import com.example.demo.model.NoticeList;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainContract.MainView, View.OnClickListener {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private MainContract.presenter presenter;
    private LinearLayout llErrorEnable;
    private ArrayList<NoticeList> noticeLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainPresenterImpl(this, new GetNoticeIntractorImpl("list"));
        initializeToolbarAndRecyclerView();
        initProgressBar(MainActivity.this);
        getPresentCall();

    }

    private void getPresentCall() {
        if (checkNetworkConection()) {
            presenter.requestDataFromServer();
        }
    }


    /**
     * Initializing Toolbar and RecyclerView
     */
    private void initializeToolbarAndRecyclerView() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view_list);
        llErrorEnable = findViewById(R.id.llErrorEnable);
        FloatingActionButton floatingActionButton=findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onButtonClick();
            }
        });

        Button btnSubmit = findViewById(R.id.btnFilter);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noticeLists != null) {
                    presenter.onOpenFilterDialog(MainActivity.this, noticeLists);
                }
            }
        });
        findViewById(R.id.btnRetry).setOnClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);


    }


    /**
     * Initializing progressbar programmatically
     */
    public ProgressBar initProgressBar(Context context) {
        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setGravity(Gravity.CENTER);
        relativeLayout.addView(progressBar);

        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        progressBar.setVisibility(View.INVISIBLE);

        ((Activity) context).addContentView(relativeLayout, params);
        return progressBar;
    }


    /**
     * RecyclerItem click event listener
     */
    private RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener() {
        @Override
        public void onItemClick(NoticeList notice) {

            Toast.makeText(MainActivity.this,
                    "List title:  " + notice.getProductName(),
                    Toast.LENGTH_LONG).show();

        }
    };


    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    public void setDataToRecyclerView(ArrayList<NoticeList> noticeArrayList) {
        if (noticeLists.size() == 0 && noticeArrayList.size()!=0) {

            noticeLists.addAll(noticeArrayList);
        } else if (noticeArrayList.size() == 0) {
            llErrorEnable.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tvErrorMsg)).setText(R.string.no_data_found);
        }
        NoticeAdapter adapter = new NoticeAdapter(MainActivity.this,noticeArrayList, recyclerItemClickListener);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onResponseFailure(Throwable throwable) {

        llErrorEnable.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tvErrorMsg)).setText(R.string.error_msg);

        findViewById(R.id.btnRetry).setOnClickListener(this);
    }

    @Override
    public boolean checkNetworkConection() {
        ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() == true) {

            return true;
        } else {
            Toast.makeText(MainActivity.this, "Network Not Available", Toast.LENGTH_LONG).show();

            return false;
        }
    }

    @Override
    public Context getContext() {
        return MainActivity.this;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }




    @Override
    public void onClick(View v) {
        getPresentCall();
        llErrorEnable.setVisibility(View.GONE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 200) {
                if (checkNetworkConection()) {
                    presenter.requestDataFromServer();
                }
            }

        }
    }
}

