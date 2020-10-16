package com.example.employeeslist.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.employeeslist.R;
import com.example.employeeslist.models.EmployeesDetailsModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployeesListAdapter extends RecyclerView.Adapter<EmployeesListAdapter.MyViewHolder> {

    private List<EmployeesDetailsModel> mEmployeesDetailsModelList;
    private Context mContext;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_employees_list_item, parent, false);
        return new MyViewHolder(view);
    }

    public EmployeesListAdapter(List<EmployeesDetailsModel> mEmployeesDetailsModelList, Context mContext) {
        this.mEmployeesDetailsModelList = mEmployeesDetailsModelList;
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mEmployeesDetailsModelList.get(position).getProfileImage())
                .placeholder(R.drawable.splash_theme)
                .into(holder.mCircleImageView);

        holder.mTextView.setText(mContext.getString(R.string.api_response_result,
                mEmployeesDetailsModelList.get(position).getEmployeeName(),
                mEmployeesDetailsModelList.get(position).getEmployeeSalary(),
                mEmployeesDetailsModelList.get(position).getEmployeeAge()));

    }

    @Override
    public int getItemCount() {
        return mEmployeesDetailsModelList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mCircleImageView;
        private TextView mTextView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mCircleImageView = itemView.findViewById(R.id.circleImageViewCustomEmployeesListItem);
            mTextView = itemView.findViewById(R.id.textViewCustomEmployeesListItem);
        }
    }
}
