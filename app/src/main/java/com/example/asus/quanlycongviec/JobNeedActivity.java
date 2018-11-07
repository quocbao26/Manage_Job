package com.example.asus.quanlycongviec;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.adapter.JobAdapter;
import com.example.asus.model.Error;
import com.example.asus.model.Job;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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


public class JobNeedActivity extends AppCompatActivity {
    public static final String TAG = JobNeedActivity.class.getSimpleName();
    DatabaseReference mData;
    Calendar calendar;
    Toolbar toolbar;
    TextView txtTitle;
    ImageButton imgJob;
    FloatingActionButton fab;
    Spinner spSort;
    ArrayList<String> arrSort;
    ArrayAdapter<String> adapterSort;
    Error error;

    ListView lvCVNeed;
    ArrayList<Job> arrCVNeed;
    JobAdapter adapterCVNeed;

    String strKeyPriority = "keyPri";
    String strNone = "Không";
    String strPriorityLevel = "Mức ưu tiên";
    String strDeadline = "Deadline";


    String strNameofSpin = "";
    int iSoNgay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_need);
        setControls();
        getJobNeed();
        addEvents();
    }


    private void getJobNeed() {
        // lấy Tên sắp xếp từ spinner
        spSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strNameofSpin = (String) adapterView.getItemAtPosition(i);
                if (strNameofSpin.equals(strNone))
                {
                    loadAllJob();
                }
                else if (strNameofSpin.equals(strPriorityLevel))
                {
                    loadJobPriority();
                }
                else
                {
                    loadSortDeadline();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("nothing", "select");
            }
        });
    }

    private void loadSortDeadline() {
        mData.child(MainActivity.strJob).child(MainActivity.strKeyUser)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrCVNeed.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot canLam : dataSnapshot.getChildren()) {
                        Job job = canLam.getValue(Job.class);
                        if (job.isStatus() == false) {
                            arrCVNeed.add(job);
                        }
                    }
                    getCountDay();
                } else {
                    adapterCVNeed.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getCountDay() {
        final List<Integer> arrOrder = new ArrayList<>();         // số thứ tự
        final ArrayList<Integer> arraySoNgay = new ArrayList<>(); // số ngày cách thời điểm hiện tại
        for (int i = 0; i < arrCVNeed.size(); i++) {
            String dateItem = arrCVNeed.get(i).getDate();
            Log.e(TAG,"Ngày: "+dateItem);
            int soNgay = convertDate(dateItem);
            if (soNgay >= 0) {
                arraySoNgay.add(soNgay);
                arrOrder.add(i);
            }
        }
        if (arrOrder.size() == 0 && arraySoNgay.size() == 0)
        {
            arrCVNeed.clear();
            adapterCVNeed.notifyDataSetChanged();
        }
        else
        {
//            Log.e(TAG, "Chua sort: " + arraySoNgay.toString());  // 0 5 3
//            Log.e(TAG, "Chua sort: " + arrOrder.toString());     // 0 2 3

            sortDealline(arrOrder, arraySoNgay);
        }

    }

    private void sortDealline(final List<Integer> arrOrder, final ArrayList<Integer> arraySoNgay) {
        mData.child(MainActivity.strJob).child(MainActivity.strKeyUser)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        arrCVNeed.clear();
                        int dem = 0, i = 0;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren())
                            {
                                Job job = data.getValue(Job.class);
                                if (job.isStatus() == false)
                                {
                                    if (arrOrder.get(dem) == i) {
                                        arrCVNeed.add(job);
                                        Log.e(TAG,"CV Deadline: "+job.getName());
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
                                        Collections.swap(arraySoNgay, m,n);
//                                            Log.e(TAG,"Đã sort: "+arraySoNgay.toString());
                                        Collections.swap(arrOrder, m, n);
//                                            Log.e(TAG,"Đã sort: "+arrOrder.toString());
                                        Collections.swap(arrCVNeed,m,n);
                                    }
                                }
                            }
                            adapterCVNeed.notifyDataSetChanged();
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
//            Log.e(TAG, "Ngày nhập " + timeInMillisecond1 + " - " +
//                    "Ngày máy: " + timeInMillisecond2 +" = "
//                    +((timeInMillisecond2 - timeInMillisecond1) / (1000 * 60 * 60 * 24)));
            // 1s = 1000 millisecond, 60s, 60p, 24h
            iSoNgay = (int) ((timeInMillisecond2 - timeInMillisecond1) / (1000 * 60 * 60 * 24));
            return iSoNgay;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void loadJobPriority() {
        arrCVNeed.clear();
        Log.e(TAG,"keyHigh");
        addSortInPriority(1);
        Log.e(TAG,"keyMedium");
        addSortInPriority(2);
        Log.e(TAG,"keyLow");
        addSortInPriority(3);
//        if (arrCVCanLam.size() == 0)
//        {
//            adapterCVCanLam.notifyDataSetChanged();
//        }
//        adapterCVCanLam.notifyDataSetChanged();
    }


    private void addSortInPriority(final int key) {
        Log.e(TAG,"addSortInPriority");
        Query query = mData.child(MainActivity.strJob + "/" + MainActivity.strKeyUser)
                .orderByChild(strKeyPriority).equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataCV : dataSnapshot.getChildren()){
                    Job job = dataCV.getValue(Job.class);
                    if(!job.isStatus()){
                        Log.e(TAG,"CV: "+job.getName());
                        arrCVNeed.add(job);
                    }
                }
                adapterCVNeed.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadAllJob() {
        Log.d(TAG, "load all job");
        mData.child(MainActivity.strJob + "/" + MainActivity.strKeyUser)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrCVNeed.clear();
                for (DataSnapshot valueCongViec : dataSnapshot.getChildren()) {
                    Job jobCanLam = valueCongViec.getValue(Job.class);
                    if (!jobCanLam.isStatus()) {
                        arrCVNeed.add(jobCanLam);
//                        Log.e(TAG,"CV All: "+jobCanLam.getTenCongViec());
                    }
                }
                adapterCVNeed.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addEvents() {
        imgJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JobNeedActivity.this, JobDoneActivity.class));
                finish();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JobNeedActivity.this,InsertJobActivity.class));
            }
        });
        lvCVNeed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Job jobEdit = new Job(arrCVNeed.get(i).getId(),
                        arrCVNeed.get(i).getName(),
                        arrCVNeed.get(i).getContent(),
                        arrCVNeed.get(i).getLocation(),
                        arrCVNeed.get(i).getDate(),
                        arrCVNeed.get(i).getHour(),
                        arrCVNeed.get(i).getKeyPri(),
                        arrCVNeed.get(i).isStatus());
                Intent intent = new Intent(JobNeedActivity.this, UpdateJobActivity.class);
                intent.putExtra("job", jobEdit);
                startActivity(intent);
            }
        });
        lvCVNeed.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final Job jobTmp = new Job(arrCVNeed.get(i).getId(),
                        arrCVNeed.get(i).getName(),
                        arrCVNeed.get(i).getContent(),
                        arrCVNeed.get(i).getLocation(),
                        arrCVNeed.get(i).getDate(),
                        arrCVNeed.get(i).getHour(),
                        arrCVNeed.get(i).getKeyPri(),
                        arrCVNeed.get(i).isStatus());
                CharSequence[] item = {"Xong công việc", "Xóa công việc"};
                android.app.AlertDialog.Builder builderOption = new android.app.AlertDialog.Builder
                        (JobNeedActivity.this);
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
    }


    private void processJobComplete(final Job job) {
        job.setStatus(true);
        mData.child(MainActivity.strJob + "/" + MainActivity.strKeyUser + "/" + job.getId())
                .setValue(job);
        spSort.setSelection(0);
    }

    private void deleteJob(final Job job) {
        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(JobNeedActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Bạn có muốn xóa " + job.getName() + " không ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mData.child(MainActivity.strJob + "/" + MainActivity.strKeyUser)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot valueCV : dataSnapshot.getChildren()) {
                                    Job cv = valueCV.getValue(Job.class);
                                    if (job.getId().equals(cv.getId())) {
                                        mData.child(MainActivity.strJob).child(MainActivity.strKeyUser)
                                                .child(valueCV.getKey()).removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(JobNeedActivity.this, error.DELETE_SUCCESS,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(JobNeedActivity.this, error.DELETE_E001,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });

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
        fab = findViewById(R.id.fab);

        toolbar = findViewById(R.id.myToolbar);
        txtTitle = findViewById(R.id.txtTitle);
        setSupportActionBar(toolbar);
        txtTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        imgJob = findViewById(R.id.imgJob);
        imgJob.setImageResource(R.drawable.job_done);

        spSort = findViewById(R.id.spUuTien);
        arrSort = new ArrayList<>();
        addDataUuTien();
        adapterSort = new ArrayAdapter<>(JobNeedActivity.this,
                android.R.layout.simple_spinner_item, arrSort);
        adapterSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSort.setAdapter(adapterSort);

        lvCVNeed = findViewById(R.id.lvCVCanLam);
        arrCVNeed = new ArrayList<>();
        adapterCVNeed = new JobAdapter(JobNeedActivity.this, R.layout.item_job, arrCVNeed);
        lvCVNeed.setAdapter(adapterCVNeed);

        mData = FirebaseDatabase.getInstance().getReference();
        calendar = Calendar.getInstance();
        error = new Error();
    }

    private void addDataUuTien() {
        arrSort.add(strNone);
        arrSort.add(strPriorityLevel);
        arrSort.add(strDeadline);
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
                startActivity(new Intent(JobNeedActivity.this, MainActivity.class));
                break;
            }
            case R.id.change_password:
            {
                finish();
                startActivity(new Intent(JobNeedActivity.this,ChangePasswordActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(JobNeedActivity.this);
        builder.setMessage("Bạn chắc chắn muốn thoát ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.strKeyUser = "";
                finish();
                startActivity(new Intent(JobNeedActivity.this, MainActivity.class));
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
