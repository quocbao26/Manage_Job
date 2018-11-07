package com.example.asus.quanlycongviec;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.asus.model.CheckAll;
import com.example.asus.model.Job;
import com.example.asus.model.Error;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class InsertJobActivity extends AppCompatActivity {
    public final static String TAG = InsertJobActivity.class.getSimpleName();
    DatabaseReference mData;
    CheckAll checkAll;
    Error error;

    Button btnDate,btnHour,btnAdd;
    EditText edtName_Add,edtContent_Add,edtLocation_Add,edtDate_Add,edtHour_Add;
    Spinner spPriority;
    ArrayList<String> arrPriority;
    ArrayAdapter<String> adapterPriority;

    Calendar calendar;
    SimpleDateFormat sdfDate,sdfHour;

    //String strKeyUuTien = "";
    String numberPri = "";

    //String strCongViec = "jobs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_job);
        setControls();
        getPriority();
        spDefault();
        addEvents();
    }

    private void getPriority() {
        mData.child(MainActivity.strPriority).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot valuePriority : dataSnapshot.getChildren())
                {
                    int uuTien = valuePriority.getValue(Integer.class);
                    arrPriority.add(uuTien+"");
                }
                adapterPriority.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addEvents() {
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });
        btnHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectHour();
            }
        });

    }

    private void addJob(String numberPri) {
        String strName,strContent,strLocation,strDate,strHour;
        strName = edtName_Add.getText().toString().trim();
        strContent = edtContent_Add.getText().toString().trim();
        strLocation = edtLocation_Add.getText().toString().trim();
        strDate = edtDate_Add.getText().toString().trim();
        strHour = edtHour_Add.getText().toString().trim();
        HashMap<String, String> job = new HashMap<>();
        job.put("name",strName);
        job.put("content",strContent);
        job.put("location",strLocation);
        job.put("date",strDate);
        job.put("hour",strHour);
        if (checkAll.checkEmptyJob(job))
        {
            Toast.makeText(this, error.INSERT_E001, Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            processAddJob(job, numberPri);
        }
    }

    private void processAddJob(final HashMap job, String numberPri) {

        String id = mData.child(MainActivity.strJob).push().getKey(); // id riêng từng công việc
        Job jobTmp = new Job(id,(String)job.get("name"),
                (String)job.get("content"),
                (String)job.get("location"),
                (String)job.get("date"),
                (String)job.get("hour"),
                Integer.parseInt(numberPri),false);

        mData.child(MainActivity.strJob + "/" + MainActivity.strKeyUser)
                .child(id).setValue(jobTmp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(InsertJobActivity.this, error.INSERT_SUCCESS,
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(InsertJobActivity.this, JobNeedActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InsertJobActivity.this, error.INSERT_E002,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void spDefault() {
        spPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                numberPri = (String) adapterView.getItemAtPosition(i);
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addJob(numberPri);
                    }
                });
                Log.e("ThemCongViecActivity",numberPri+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void selectHour() {
        TimePickerDialog.OnTimeSetListener callBack = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY,i);
                calendar.set(Calendar.MINUTE,i1);
                edtHour_Add.setText(sdfHour.format(calendar.getTime()));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(InsertJobActivity.this,
                callBack,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        edtHour_Add.setText(sdfHour.format(calendar.getTime()));
        timePickerDialog.show();
    }

    private void selectDate() {
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR,i);
                calendar.set(Calendar.MONTH,i1);
                calendar.set(Calendar.DAY_OF_MONTH,i2);
                edtDate_Add.setText(sdfDate.format(calendar.getTime()));
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(InsertJobActivity.this,callback,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE));
        edtDate_Add.setText(sdfDate.format(calendar.getTime()));
        dialog.show();
    }

    private void setControls() {
        btnAdd = findViewById(R.id.btnThem);
        btnHour = findViewById(R.id.btnChonGio_add);
        btnDate = findViewById(R.id.btnChonNgay_add);
        edtName_Add = findViewById(R.id.edtTenCV_add);
        edtContent_Add = findViewById(R.id.edtNoiDungCV_add);
        edtLocation_Add = findViewById(R.id.edtDiaDiemCV_add);
        edtHour_Add = findViewById(R.id.edtGioCV_add);
        edtDate_Add = findViewById(R.id.edtNgayCV_add);

        spPriority = findViewById(R.id.spUuTien);
        arrPriority = new ArrayList<>();
        adapterPriority = new ArrayAdapter<>(InsertJobActivity.this,
                android.R.layout.simple_spinner_item,arrPriority);
        adapterPriority.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(adapterPriority);

        calendar = Calendar.getInstance();
        sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        sdfHour = new SimpleDateFormat("HH:mm");
        mData = FirebaseDatabase.getInstance().getReference();
        checkAll = new CheckAll();
        error = new Error();
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(InsertJobActivity.this);
        builder.setMessage("Bạn chắc chắn muốn thoát ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
