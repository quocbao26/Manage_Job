package com.example.asus.quanlycongviec;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.adapter.JobAdapter;
import com.example.asus.model.Job;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JobDoneActivity extends AppCompatActivity {
    DatabaseReference mData;

    ListView lvCVDone;
    ArrayList<Job> arrCVDone;
    JobAdapter adapterCVDone;

    Toolbar toolbar;
    TextView txtTitle;
    ImageButton imgJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_done);
        setControls();
        // (1) Hiển thị ban đầu
        // 1. Lấy thông tin từng sản phẩm từ Firebase thông qua Internet
        getJobDone();
        addEvents();
    }

    // Tên hàm: addEvents
    // Mô tả: gán sự kiện cho các control
    private void addEvents() {
        imgJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // (4) Di chuyển sang màn hình công việc chưa làm
                startActivity(new Intent(JobDoneActivity.this,JobNeedActivity.class));
                finish();
            }
        });
        lvCVDone.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                //(2) Xử lý hồi lại công việc
                // 2. Xử lý hồi lại công việc:
                // a. Lấy thông tin một Object khi click vào item trong listview
                final Job jobTmp = new Job(arrCVDone.get(i).getId(),
                        arrCVDone.get(i).getName(),
                        arrCVDone.get(i).getContent(),
                        arrCVDone.get(i).getLocation(),
                        arrCVDone.get(i).getDate(),
                        arrCVDone.get(i).getHour(),
                        arrCVDone.get(i).getKeyPri(),
                        arrCVDone.get(i).isStatus());
                CharSequence[] item = {"Hồi lại công việc", "Xóa công việc"};
                android.app.AlertDialog.Builder builderOption = new android.app.AlertDialog.Builder
                        (JobDoneActivity.this);
                builderOption.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int itemSelect) {

                        if (itemSelect == 0) {
                            //(2) Xử lý hồi lại công việc
                            // 1. Nhấn giữ 1 item công việc và chọn hồi lại công việc
                            // 2. Xử lý hồi lại công việc
                            // b. Xử lý setup công việc chưa làm
                            processJobReturn(jobTmp);
                        } else if (itemSelect == 1) {
                            //(3) Xử lý xóa công việc
                            // 1. Nhấn giữ 1 item công việc và chọn xóa công việc
                            // 2. Xử lý xóa công việc:
                            // b. Xử lý xóa công việc
                            deleteJob(jobTmp);
                        }
                    }

                });
                builderOption.show();
                return true;
            }
        });
    }

    // Tên hàm: deleteJob
    // Mô tả: thực hiện xóa công việc
    // Tham số: Đối tượng Job
    private void deleteJob(final Job job) {
        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(JobDoneActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Bạn có muốn xóa " + job.getName() + " không ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mData.child(MainActivity.strJob).child(MainActivity.strKeyUser)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot valueCV : dataSnapshot.getChildren()) {
                                    Job cv = valueCV.getValue(Job.class);
                                    if (job.getId().equals(cv.getId())) {
                                        mData.child(MainActivity.strJob).child(MainActivity.strKeyUser)
                                                .child(valueCV.getKey()).removeValue();
                                        Toast.makeText(JobDoneActivity.this, "Đã xóa",
                                                Toast.LENGTH_SHORT).show();
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
    // Tên hàm: processJobReturn
    // Mô tả: thực hiện chuyển lại thành công việc chưa làm
    // Tham số: Đối tượng Job
    private void processJobReturn(final Job jobTmp) {
        mData.child(MainActivity.strJob + "/" + MainActivity.strKeyUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot daLam : dataSnapshot.getChildren()) {
                            Job jobNode = daLam.getValue(Job.class);
                            if (jobTmp.getId().equals(jobNode.getId())) {
                                jobNode.setStatus(false);
                                mData.child(MainActivity.strJob).child(MainActivity.strKeyUser)
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


    // Tên hàm: getJobDone
    // Mô tả: lấy những công việc đã làm từ Firebase về app
    private void getJobDone() {
        mData.child(MainActivity.strJob + "/" + MainActivity.strKeyUser)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        arrCVDone.clear();
                        if (dataSnapshot.exists())
                        {
                            for (DataSnapshot valueCongViec : dataSnapshot.getChildren()) {
                                Job jobDaLam = valueCongViec.getValue(Job.class);
                                if (jobDaLam.isStatus()) {
                                    arrCVDone.add(jobDaLam);
                                    adapterCVDone.notifyDataSetChanged();
                                }
                            }
                            if (arrCVDone.size() == 0) {
                                adapterCVDone.notifyDataSetChanged();
                            }
                        } else {
                            adapterCVDone.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    // Tên hàm: setControls
    // Mô tả: gán Id cho các control
    private void setControls() {
        toolbar = findViewById(R.id.myToolbar);
        txtTitle = findViewById(R.id.txtTitle);
        setSupportActionBar(toolbar);
        txtTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        imgJob = findViewById(R.id.imgJob);
        imgJob.setImageResource(R.drawable.job_need);

        lvCVDone = findViewById(R.id.lvCVDaLam);
        arrCVDone = new ArrayList<>();
        adapterCVDone = new JobAdapter(JobDoneActivity.this, R.layout.item_job, arrCVDone);
        lvCVDone.setAdapter(adapterCVDone);

        mData = FirebaseDatabase.getInstance().getReference();
    }

    // Tên hàm: onCreateOptionsMenu
    // Mô tả: gán id cho menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Tên hàm: onOptionsItemSelected
    // Mô tả: gán sự kiện cho menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
            {
                MainActivity.strKeyUser = "";
                finish();
                startActivity(new Intent(JobDoneActivity.this, MainActivity.class));
                break;
            }
            case R.id.change_password:
            {
                startActivity(new Intent(JobDoneActivity.this,ChangePasswordActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // (5) Trở lại
    // Tên hàm: onBackPressed
    // Mô tả: khi bấm nút Back trên smartphone thì sẽ thực hiện hàm này
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(JobDoneActivity.this);
        builder.setMessage("Bạn chắc chắn muốn thoát ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.strKeyUser = "";
                finish();
                startActivity(new Intent(JobDoneActivity.this, MainActivity.class));

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
