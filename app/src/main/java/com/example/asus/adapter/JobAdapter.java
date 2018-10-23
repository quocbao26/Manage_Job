package com.example.asus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.asus.model.Job;
import com.example.asus.quanlycongviec.MenuActivity;
import com.example.asus.quanlycongviec.R;

import java.util.List;

public class JobAdapter extends BaseAdapter {

    MenuActivity context;
    int layout;
    List<Job> jobList;

    public JobAdapter(MenuActivity context, int layout, List<Job> jobList) {
        this.context = context;
        this.layout = layout;
        this.jobList = jobList;
    }

    @Override
    public int getCount() {
        return jobList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    class ViewHolder{
        TextView txtTenCV,txtNoiDungCV,txtDiaDiemCV,txtNgay,txtGio;

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);

            holder = new ViewHolder();
            holder.txtTenCV = view.findViewById(R.id.txtTenCV);
            holder.txtNoiDungCV = view.findViewById(R.id.txtNoiDungCV);
            holder.txtDiaDiemCV = view.findViewById(R.id.txtDiaDiemCV);
            holder.txtNgay = view.findViewById(R.id.txtNgay);
            holder.txtGio = view.findViewById(R.id.txtGio);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        final Job job = jobList.get(i);
        holder.txtTenCV.setText(job.getTenCongViec());
        holder.txtNoiDungCV.setText(job.getNoiDung());
        holder.txtDiaDiemCV.setText(job.getDiaDiem());
        holder.txtNgay.setText(job.getNgay());
        holder.txtGio.setText(job.getGio());

        return view;
    }
}
