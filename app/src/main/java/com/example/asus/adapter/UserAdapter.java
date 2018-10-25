package com.example.asus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.asus.model.Job;
import com.example.asus.model.User;
import com.example.asus.quanlycongviec.MainActivity;
import com.example.asus.quanlycongviec.MenuActivity;
import com.example.asus.quanlycongviec.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends BaseAdapter {

    DatabaseReference mData;
    int count = 0;

    Context context;
    int layout;
    List<User> userList;

    public UserAdapter(Context context, int layout, List<User> userList) {
        this.context = context;
        this.layout = layout;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    class ViewHolder {
        TextView txtName, txtUsername, txtCountJob;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);

            holder = new ViewHolder();
            holder.txtName = view.findViewById(R.id.txtName);
            holder.txtUsername = view.findViewById(R.id.txtUsername);
            holder.txtCountJob = view.findViewById(R.id.txtCountJob);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        User user = userList.get(i);
        holder.txtName.setText(user.getName());
        holder.txtUsername.setText(user.getUsername());

        mData = FirebaseDatabase.getInstance().getReference();
        getKeyUser(holder.txtCountJob,user.getUsername());

        return view;
    }

    private void getKeyUser(final TextView txtCountJob, final String username) {
        mData.child(MainActivity.strUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot dataUser : dataSnapshot.getChildren())
                    {
                        User user = dataUser.getValue(User.class);
                        if (user.getUsername().equals(username))
                        {

                            getCountJob(txtCountJob, dataUser.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getCountJob(final TextView txtCountJob, String key) {


        mData.child(MainActivity.strCongViec).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    count = (int) dataSnapshot.getChildrenCount();
                    txtCountJob.setText(count+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
