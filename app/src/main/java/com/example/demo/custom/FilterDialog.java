package com.example.demo.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.R;
import com.example.demo.main_activity.GetNoticeIntractorImpl;
import com.example.demo.main_activity.MainActivity;
import com.example.demo.main_activity.MainContract;
import com.example.demo.main_activity.MainPresenterImpl;
import com.example.demo.model.NoticeList;
import com.example.demo.my_interface.OnRangeSeekbarFinalValueListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FilterDialog extends AlertDialog.Builder implements MainContract.MainView, MainContract.DialogView {
    private final ProgressBar progressBar;
    private AlertDialog closeDialog;
    private List<String> brand;
    private ArrayList<NoticeList> noticeLists;
    private Context context;

    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    public FilterDialog(@NonNull final Context context, List<String> brand, final ArrayList<NoticeList> noticeLists, final String maxmiumValue, String mimumValue) {
        super(context);
        this.context = context;
        this.noticeLists = noticeLists;
        this.brand = brand;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.dialog_filter, null, false);
        final TextView textMin = (TextView) viewDialog.findViewById(R.id.textMin);
        final TextView textMax = (TextView) viewDialog.findViewById(R.id.textMax);

        final HashMap<String, String> hashMap = new HashMap<>();
        Button btnSubmit = (Button) viewDialog.findViewById(R.id.btnSubmit);
        ((Button) viewDialog.findViewById(R.id.btnRefresh)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog.dismiss();
                ((MainActivity) context).setDataToRecyclerView(noticeLists);
            }
        });
        MultiSelectionSpinner mySpinner = (MultiSelectionSpinner) viewDialog.findViewById(R.id.mySpinner);
        RangeSeekbar rangeSeekbar = (RangeSeekbar) viewDialog.findViewById(R.id.rangeSeekbar);
        rangeSeekbar.setMaxValue(Float.parseFloat(maxmiumValue));
        rangeSeekbar.setMinValue(Float.parseFloat(mimumValue));
        rangeSeekbar.setMaxStartValue(Float.parseFloat(maxmiumValue));
        rangeSeekbar.setMinStartValue(Float.parseFloat(mimumValue));

        textMax.setText(maxmiumValue);
        textMin.setText(mimumValue);
        progressBar = ((MainActivity) context).initProgressBar(this.context);
        rangeSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                textMax.setText(maxValue.toString());
                textMin.setText(minValue.toString());

            }
        });

        TextView tvCloseDialog=(TextView)viewDialog.findViewById(R.id.tvTitle);
        tvCloseDialog.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(tvCloseDialog) {
            @Override
            public boolean onDrawableClick() {
                closeDialog.dismiss();
                return false;
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hashMap.put("priceMax", textMax.getText().toString());
                hashMap.put("priceMin", textMin.getText().toString());
                if (((MainActivity) context).checkNetworkConection()) {
                    addValues(hashMap);
                }

            }
        });
        mySpinner.setItems(brand);
        mySpinner.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
            @Override
            public void selectedIndices(List<Integer> indices) {

            }

            @Override
            public void selectedStrings(List<String> strings) {
                hashMap.clear();
                hashMap.put("brand", android.text.TextUtils.join(",", strings));
            }
        });
        this.setCancelable(false);
        this.setView(viewDialog);
        closeDialog = this.create();
        closeDialog.show();

    }

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
        ((MainActivity) context).setDataToRecyclerView(noticeArrayList);
        closeDialog.dismiss();
    }

    @Override
    public void onResponseFailure(Throwable throwable) {
        Toast.makeText(context, context.getResources().getString(R.string.error_msg), Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean checkNetworkConection() {
        return false;
    }


    @Override
    public void addValues(HashMap<String, String> map) {
        new MainPresenterImpl(this, new GetNoticeIntractorImpl("list", map)).requestDataFromServer();
    }
}
