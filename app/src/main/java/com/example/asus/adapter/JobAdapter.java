package com.example.asus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.asus.model.Job;
import com.example.asus.quanlycongviec.R;

import java.util.List;

public class JobAdapter extends BaseAdapter {

    Context context;
    int layout;
    List<Job> jobList;

    public JobAdapter(Context context, int layout, List<Job> jobList) {
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
        return jobList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    class ViewHolder{
        TextView txtName,txtContent,txtLocation,txtDate,txtHour;

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);

            holder = new ViewHolder();
            holder.txtName = view.findViewById(R.id.txtTenCV);
            holder.txtContent = view.findViewById(R.id.txtNoiDungCV);
            holder.txtLocation = view.findViewById(R.id.txtDiaDiemCV);
            holder.txtDate = view.findViewById(R.id.txtNgay);
            holder.txtHour = view.findViewById(R.id.txtGio);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        final Job job = jobList.get(i);
        holder.txtName.setText(job.getName());
        holder.txtContent.setText(job.getContent());
        holder.txtLocation.setText(job.getLocation());
        holder.txtDate.setText(job.getDate());
        holder.txtHour.setText(job.getHour());

        return view;
    }
}
