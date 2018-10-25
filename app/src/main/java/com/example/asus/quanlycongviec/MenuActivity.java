package com.example.asus.quanlycongviec;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.adapter.JobAdapter;
import com.example.asus.model.Job;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class MenuActivity extends AppCompatActivity {
    public static final String TAG = MenuActivity.class.getSimpleName();
    DatabaseReference mData;
    TabHost tabHost;

    Calendar calendar;
    Toolbar toolbar;
    TextView txtTitle;
    ImageButton imgAdd;

    Spinner spSapXep;
    ArrayList<String> arrSapXep;
    ArrayAdapter<String> adapterSapXep;

    ListView lvCVCanLam;
    ArrayList<Job> arrCVCanLam;
    JobAdapter adapterCVCanLam;

    ListView lvCVDaLam;
    ArrayList<Job> arrCVDaLam;
    JobAdapter adapterCVDaLam;

    String strKhong = "Không", strMucUuTien = "Mức ưu tiên", strDeadline = "Deadline";


    String strSapXep = "";
    int iSoNgay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setControls();
        getData();
        Log.e(TAG, MainActivity.strKeyUser);
        addEvents();
    }

    private void getData() {
        getJobNeed();
        getJobDone();
    }

    private void getJobDone() {
        Log.e(TAG,"Load Job Done");
        mData.child(MainActivity.strCongViec).child(MainActivity.strKeyUser)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrCVDaLam.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot valueCongViec : dataSnapshot.getChildren()) {
                        Job jobDaLam = valueCongViec.getValue(Job.class);
                        if (jobDaLam.isTrangThai()) {
                            arrCVDaLam.add(jobDaLam);
                            adapterCVDaLam.notifyDataSetChanged();
                        } else {
                            adapterCVDaLam.notifyDataSetChanged();
                        }
                    }
                } else {
                    adapterCVDaLam.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getJobNeed() {
        // lấy Tên sắp xếp từ spinner
        spSapXep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strSapXep = (String) adapterView.getItemAtPosition(i);
                Log.e(TAG, strSapXep + "\n" + strKhong);
                if (strSapXep.equals(strKhong)) {
                    Log.e(TAG, "Sắp xếp KHÔNG");
                    loadAllJob();
                } else if (strSapXep.equals(strMucUuTien)) {
                    Log.e(TAG, "Sắp xếp ƯU TIÊN");
                    loadJobPriority();
                } else {
                    Log.e(TAG, "Sắp xếp Deadline");
                    loadSortDeadline();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadSortDeadline() {
        Log.e(TAG,"Deadline");
        mData.child(MainActivity.strCongViec).child(MainActivity.strKeyUser)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrCVCanLam.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot canLam : dataSnapshot.getChildren()) {
                        Job job = canLam.getValue(Job.class);
                        if (job.isTrangThai() == false) {
                            arrCVCanLam.add(job);
                        }
                    }
                    sortDeadline();
                } else {
                    adapterCVCanLam.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sortDeadline() {
        final List<Integer> arrOrder = new ArrayList<>();         // số thứ tự
        final ArrayList<Integer> arraySoNgay = new ArrayList<>(); // số ngày cách thời điểm hiện tại
        for (int i = 0; i < arrCVCanLam.size(); i++) {
            String dateItem = arrCVCanLam.get(i).getNgay();
            Log.e(TAG,"Ngày: "+dateItem);
            int soNgay = convertDate(dateItem);
            if (soNgay >= 0) {
                arraySoNgay.add(soNgay);
                arrOrder.add(i);
            }
        }
        Log.e(TAG, "Chua sort: " + arraySoNgay.toString());  // 0 5 3
        Log.e(TAG, "Chua sort: " + arrOrder.toString());     // 0 2 3

        mData.child(MainActivity.strCongViec).child(MainActivity.strKeyUser)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrCVCanLam.clear();
                int dem = 0, i = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren())
                    {
                        Job job = data.getValue(Job.class);
                        if (job.isTrangThai() == false)
                        {
                            if (arrOrder.get(dem) == i) {
                                arrCVCanLam.add(job);
                                dem++;
                            }
                            i++;
                            if (dem == arrOrder.size()) break;
                        }
                    }
                    for (int m = 0; m < arraySoNgay.size() - 1; m++)
                    {
                        for (int n = m + 1; n < arraySoNgay.size(); n++) {
                            if (arraySoNgay.get(m) > arraySoNgay.get(n)) {
                                Collections.swap(arraySoNgay, m,n); Log.e(TAG,"Đã sort: "+arraySoNgay.toString());
                                Collections.swap(arrOrder, m, n);   Log.e(TAG,"Đã sort: "+arrOrder.toString());
                                Collections.swap(arrCVCanLam,m,n);
                            }
                        }
                    }
                    adapterCVCanLam.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private int convertDate(String dateInArray) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(calendar.getTime());
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date mDate1 = sdf.parse(date);
            Date mDate2 = sdf2.parse(dateInArray);
            long timeInMillisecond1 = mDate1.getTime();
            long timeInMillisecond2 = mDate2.getTime();
            Log.e(TAG, "Ngày nhập " + timeInMillisecond1 + " - " +
                    "Ngày máy: " + timeInMillisecond2 +" = "
                    +((timeInMillisecond2 - timeInMillisecond1) / (1000 * 60 * 60 * 24)));
            // 1s = 1000 millisecond, 60s, 60p, 24h
            iSoNgay = (int) ((timeInMillisecond2 - timeInMillisecond1) / (1000 * 60 * 60 * 24));
            return iSoNgay;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void loadJobPriority() {
        Log.e(TAG,"Priority");
        final String keyCao = "-LOpiRYdc8QrWKv2CEQA";
        final String keyTrungBinh = "-LOpiRYdc8QrWKv2CEQ9";
        final String keyThap = "-LOpiRYWWSB17RtBx2Gi";
        String keyUuTien = "keyUuTien";
        arrCVCanLam.clear();
        addHigh(keyUuTien, keyCao);
        addMedium(keyUuTien, keyTrungBinh);
        addLow(keyUuTien, keyThap);
    }

    private void addLow(String keyUuTien, String keyThap) {
        Query query = mData.child(MainActivity.strCongViec).child(MainActivity.strKeyUser)
                .orderByChild(keyUuTien).equalTo(keyThap);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataCV : dataSnapshot.getChildren()) {
                        Job job = dataCV.getValue(Job.class);
                        if (job.isTrangThai() == false) {
                            arrCVCanLam.add(job);
                            adapterCVCanLam.notifyDataSetChanged();
                        } else {
                            adapterCVCanLam.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addMedium(String keyUuTien, String keyTrungBinh) {
        Query query = mData.child(MainActivity.strCongViec).child(MainActivity.strKeyUser)
                .orderByChild(keyUuTien).equalTo(keyTrungBinh);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataCV : dataSnapshot.getChildren()) {
                        Job job = dataCV.getValue(Job.class);
                        if (job.isTrangThai() == false) {
                            arrCVCanLam.add(job);
                            adapterCVCanLam.notifyDataSetChanged();
                        } else
                        {
                            adapterCVCanLam.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addHigh(String keyUuTien, String keyCao) {
        Log.e(TAG, "addCao");
        Query query = mData.child(MainActivity.strCongViec).child(MainActivity.strKeyUser)
                .orderByChild(keyUuTien).equalTo(keyCao);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataCV : dataSnapshot.getChildren()) {
                        Job job = dataCV.getValue(Job.class);
                        if (job.isTrangThai() == false) {
                            arrCVCanLam.add(job);
                            adapterCVCanLam.notifyDataSetChanged();
                        }
                        else
                        {
                            adapterCVCanLam.notifyDataSetChanged();
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadAllJob() {
        Log.e(TAG,"AllJob");
        mData.child(MainActivity.strCongViec).child(MainActivity.strKeyUser)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrCVCanLam.clear();
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot valueCongViec : dataSnapshot.getChildren()) {
                        Job jobCanLam = valueCongViec.getValue(Job.class);
                        if (jobCanLam.isTrangThai() == false) {
                            arrCVCanLam.add(jobCanLam);
                            adapterCVCanLam.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addEvents() {
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, InsertJobActivity.class));
            }
        });
        lvCVCanLam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Job jobEdit = new Job(arrCVCanLam.get(i).getId(),
                        arrCVCanLam.get(i).getTenCongViec(),
                        arrCVCanLam.get(i).getNoiDung(),
                        arrCVCanLam.get(i).getDiaDiem(),
                        arrCVCanLam.get(i).getNgay(),
                        arrCVCanLam.get(i).getGio(),
                        arrCVCanLam.get(i).getKeyUuTien(),
                        arrCVCanLam.get(i).isTrangThai());


                Intent intent = new Intent(MenuActivity.this, UpdateJobActivity.class);
                intent.putExtra("congviec", jobEdit);
                startActivity(intent);
            }
        });
        lvCVCanLam.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final Job jobTmp = new Job(arrCVCanLam.get(i).getId(),
                        arrCVCanLam.get(i).getTenCongViec(),
                        arrCVCanLam.get(i).getNoiDung(),
                        arrCVCanLam.get(i).getDiaDiem(),
                        arrCVCanLam.get(i).getNgay(),
                        arrCVCanLam.get(i).getGio(),
                        arrCVCanLam.get(i).getKeyUuTien(),
                        arrCVCanLam.get(i).isTrangThai());
                CharSequence[] item = {"Xong công việc", "Xóa công việc"};
                android.app.AlertDialog.Builder builderOption = new android.app.AlertDialog.Builder
                        (MenuActivity.this);
                builderOption.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int itemSelect) {

                        if (itemSelect == 0) {
                            processJobComplete(jobTmp);
                        } else if (itemSelect == 1) {
                            deleteJob(jobTmp);
                        }

                    }

                });
                builderOption.show();
                return true;
            }
        });
        lvCVDaLam.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final Job jobTmp = new Job(arrCVDaLam.get(i).getId(),
                        arrCVDaLam.get(i).getTenCongViec(),
                        arrCVDaLam.get(i).getNoiDung(),
                        arrCVDaLam.get(i).getDiaDiem(),
                        arrCVDaLam.get(i).getNgay(),
                        arrCVDaLam.get(i).getGio(),
                        arrCVDaLam.get(i).getKeyUuTien(),
                        arrCVDaLam.get(i).isTrangThai());

                CharSequence[] item = {"Hồi lại công việc", "Xóa công việc"};
                android.app.AlertDialog.Builder builderOption = new android.app.AlertDialog.Builder
                        (MenuActivity.this);
                builderOption.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int itemSelect) {

                        if (itemSelect == 0) {
                            processJobReturn(jobTmp);
                        } else if (itemSelect == 1) {
                            deleteJob(jobTmp);
                        }

                    }

                });
                builderOption.show();
                return true;
            }
        });


    }

    private void processJobReturn(final Job job) {
        mData.child(MainActivity.strCongViec).child(MainActivity.strKeyUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot daLam : dataSnapshot.getChildren()) {
                            Job jobNode = daLam.getValue(Job.class);
                            if (job.getId().equals(jobNode.getId())) {
                                jobNode.setTrangThai(false);
                                mData.child(MainActivity.strCongViec).child(MainActivity.strKeyUser)
                                        .child(daLam.getKey())
                                        .setValue(jobNode);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void processJobComplete(final Job job) {
        mData.child(MainActivity.strCongViec).child(MainActivity.strKeyUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot canLam : dataSnapshot.getChildren()) {
                            Job jobNode = canLam.getValue(Job.class);
                            if (job.getId().equals(jobNode.getId())) {
                                job.setTrangThai(true);
                                mData.child(MainActivity.strCongViec).child(MainActivity.strKeyUser)
                                        .child(canLam.getKey())
                                        .setValue(job);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void deleteJob(final Job job) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MenuActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Bạn có muốn xóa " + job.getTenCongViec() + " không ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mData.child(MainActivity.strCongViec).child(MainActivity.strKeyUser)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot valueCV : dataSnapshot.getChildren()) {
                                    Job cv = valueCV.getValue(Job.class);
                                    if (job.getId().equals(cv.getId())) {
                                        mData.child(MainActivity.strCongViec).child(MainActivity.strKeyUser)
                                                .child(valueCV.getKey()).removeValue();
                                        Toast.makeText(MenuActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }


    private void setControls() {
        tabHost = findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("t1");
        tab1.setContent(R.id.tab1);
        tab1.setIndicator("Các việc cần làm");
        tabHost.addTab(tab1);
        TabHost.TabSpec tab2 = tabHost.newTabSpec("t2");
        tab2.setContent(R.id.tab2);
        tab2.setIndicator("Các việc đã làm");
        tabHost.addTab(tab2);
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.WHITE);
        }
        toolbar = findViewById(R.id.myToolbar);
        txtTitle = findViewById(R.id.txtTitle);
        setSupportActionBar(toolbar);
        txtTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        imgAdd = findViewById(R.id.imgAdd);

        spSapXep = findViewById(R.id.spUuTien);
        arrSapXep = new ArrayList<>();
        addDataUuTien();
        adapterSapXep = new ArrayAdapter<>(MenuActivity.this, android.R.layout.simple_spinner_item, arrSapXep);
        adapterSapXep.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSapXep.setAdapter(adapterSapXep);

        lvCVCanLam = findViewById(R.id.lvCVCanLam);
        arrCVCanLam = new ArrayList<>();
        adapterCVCanLam = new JobAdapter(MenuActivity.this, R.layout.item_congviec, arrCVCanLam);
        lvCVCanLam.setAdapter(adapterCVCanLam);

        lvCVDaLam = findViewById(R.id.lvCVDaLam);
        arrCVDaLam = new ArrayList<>();
        adapterCVDaLam = new JobAdapter(MenuActivity.this, R.layout.item_congviec, arrCVDaLam);
        lvCVDaLam.setAdapter(adapterCVDaLam);

        mData = FirebaseDatabase.getInstance().getReference();
        calendar = Calendar.getInstance();
    }

    private void addDataUuTien() {
        arrSapXep.add(strKhong);
        arrSapXep.add(strMucUuTien);
        arrSapXep.add(strDeadline);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout:
            {
                MainActivity.strKeyUser = "";
                finish();
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
                break;
            }
            case R.id.change_password:
            {
                startActivity(new Intent(MenuActivity.this,ChangePasswordActivity.class));
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
        builder.setMessage("Bạn chắc chắn muốn thoát ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.strKeyUser = "";
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
                finish();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
