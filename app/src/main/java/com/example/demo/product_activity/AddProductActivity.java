package com.example.demo.product_activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.demo.R;
import com.example.demo.custom.Utility;
import com.example.demo.main_activity.GetNoticeIntractorImpl;
import com.example.demo.model.NoticeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity implements ProductContract.MainView, View.OnClickListener {
    private int select_photo = 1;
    ArrayList<ImageView> viewArrayList = new ArrayList<>();
    private LinearLayout llImages;
    private HashMap<Integer, String> stringStringHashMap = new HashMap<>();
    private ProductPresenterImpl productPresenter;
    private ProgressBar progressBar;
    private EditText etProducrName, etProducrBrand, etProducrPrice;
    private Spinner sp_no_of_images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        productPresenter = new ProductPresenterImpl(this, new GetNoticeIntractorImpl("insert"));
        initProgressBar();
        initView();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    productPresenter.onClickImage(productPresenter.getViewImage(), select_photo);
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == select_photo)
                onSelectFromGalleryResult(data);

        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {

            if (stringStringHashMap.containsKey(select_photo)) {
                stringStringHashMap.remove(select_photo);
                stringStringHashMap.put(select_photo, productPresenter.getRealPathFromURI(data.getData(), AddProductActivity.this));
            } else {
                stringStringHashMap.put(select_photo, productPresenter.getRealPathFromURI(data.getData(), AddProductActivity.this));
            }
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        productPresenter.getViewImage().setImageBitmap(bm);

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
    public void setAdapter(ArrayAdapter<Integer> adapter) {
        sp_no_of_images.setAdapter(adapter);

    }


    @Override
    public void onResponseFailure(Throwable throwable) {
        Toast.makeText(AddProductActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean checkNetworkConection() {
        ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() == true) {
            return true;
        } else {
            Toast.makeText(AddProductActivity.this, "Network Not Available", Toast.LENGTH_LONG).show();

            return false;
        }
    }

    @Override
    public Context getContext() {
        return AddProductActivity.this;
    }

    public void initProgressBar() {
        progressBar = new ProgressBar(AddProductActivity.this, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setGravity(Gravity.CENTER);
        relativeLayout.addView(progressBar);

        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        progressBar.setVisibility(View.INVISIBLE);

        this.addContentView(relativeLayout, params);
    }

    public void initView() {
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sp_no_of_images = findViewById(R.id.spImageCount);
        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<>(AddProductActivity.this, android.R.layout.simple_list_item_1, new Integer[]{0, 1, 2, 3});
        this.setAdapter(arrayAdapter);
        sp_no_of_images.setOnItemSelectedListener(onItemSelectedListener);
        Button addProduct = findViewById(R.id.btnAdd);
        addProduct.setOnClickListener(this);
        etProducrName = findViewById(R.id.etProducrName);
        etProducrBrand = findViewById(R.id.etProducrBrand);
        etProducrPrice = findViewById(R.id.etProducrPrice);
        llImages = findViewById(R.id.llImages);
        findViewById(R.id.ivUploadImage).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAdd) {
            String productBrand = etProducrBrand.getText().toString();
            String productName = etProducrName.getText().toString();
            String productPrice = etProducrPrice.getText().toString();

            if (!TextUtils.isEmpty(productBrand) && !TextUtils.isEmpty(productName) && !TextUtils.isEmpty(productPrice)) {
                if (stringStringHashMap.size() != 0) {
                    if (checkNetworkConection()) {
                        Collection<String> values = stringStringHashMap.values();
                        productPresenter.requestDataFromServer(productName, productBrand, productPrice, new ArrayList<String>(values));
                    }
                } else {
                    Toast.makeText(AddProductActivity.this, "Add Product Image", Toast.LENGTH_LONG).show();
                }
            } else {
                if (TextUtils.isEmpty(etProducrName.getText().toString())) {
                    etProducrName.setError("Enter Product Name");
                } else if (TextUtils.isEmpty(etProducrBrand.getText().toString())) {
                    etProducrBrand.setError("Enter Brand Name");
                } else if (TextUtils.isEmpty(etProducrPrice.getText().toString())) {
                    etProducrPrice.setError("Enter Selling Price");
                }
            }
        } else if (v.getId() == R.id.ivUploadImage) {
            productPresenter.onClickImage((ImageView) findViewById(R.id.ivUploadImage), select_photo);
        }
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int imagesize = (int) parent.getSelectedItem();
            llImages.removeAllViews();
            for (int i = 0; i < imagesize; i++) {
                final ImageView imgView = new ImageView(AddProductActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(300, 300);
                lp.setMargins(8, 8, 8, 8);
                imgView.setLayoutParams(lp);
                llImages.addView(imgView, llImages.getChildCount() - 1);
                imgView.setId(i);
                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utility.checkPermission(AddProductActivity.this)) {
                            select_photo = select_photo + 1;
                            productPresenter.onClickImage(imgView, select_photo);
                        }
                    }
                });
                imgView.setImageResource(R.drawable.ic_upload_image);
                viewArrayList.add(imgView);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

}
