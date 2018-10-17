package com.example.demo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.demo.R;
import com.example.demo.main_activity.RecyclerItemClickListener;
import com.example.demo.model.NoticeList;
import com.example.demo.network.RetrofitInstance;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.EmployeeViewHolder> {

    private ArrayList<NoticeList> dataList;
    private RecyclerItemClickListener recyclerItemClickListener;
    private Context context;

    public NoticeAdapter(Context context, ArrayList<NoticeList> dataList, RecyclerItemClickListener recyclerItemClickListener) {
        this.dataList = dataList;
        this.context = context;
        this.recyclerItemClickListener = recyclerItemClickListener;
    }


    @Override
    public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_view_row, parent, false);
        return new EmployeeViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(EmployeeViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.txtProductTitle.setText(dataList.get(position).getProductName());
        holder.txtProductBrand.setText(dataList.get(position).getProductBrand());
        holder.txtNProductPrice.setText("U+20B9" + dataList.get(position).getProductPrice());
        Glide.with(context).load(RetrofitInstance.BASE_URL + dataList.get(position).getProductImage()).placeholder(R.drawable.loading).error(R.drawable.no_image_avalible).into(holder.ivProductImage);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class EmployeeViewHolder extends RecyclerView.ViewHolder {

        TextView txtProductTitle, txtProductBrand, txtNProductPrice;
        ImageView ivProductImage;

        EmployeeViewHolder(View itemView) {
            super(itemView);
            txtProductTitle = itemView.findViewById(R.id.txt_notice_title);
            txtProductBrand = itemView.findViewById(R.id.txt_notice_brief);
            txtNProductPrice = itemView.findViewById(R.id.txt_notice_file_path);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);

        }
    }

}