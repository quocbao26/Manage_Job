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

    Button btnChonNgay,btnChonGio,btnThem;
    EditText edtTen_add,edtNoiDung_add,edtDiaDiem_add,edtNgay_add,edtGio_add;
    Spinner spUuTien;
    ArrayList<String> arrUuTien;
    ArrayAdapter<String> adapterUuTien;

    Calendar calendar;
    SimpleDateFormat sdfNgay,sdfGio;

    String strKeyUuTien = "";
    String strTenUuTien = "";
    String strUuTien = "priority";
    String strCongViec = "jobs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_cong_viec);
        setControls();
        getPriority();
        spDefault();
        addEvents();
    }

    private void getPriority() {
        mData.child(strUuTien).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot valuePriority : dataSnapshot.getChildren())
                {
                    String uuTien = valuePriority.getValue(String.class);
                    arrUuTien.add(uuTien);
                }
                adapterUuTien.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addEvents() {
        btnChonNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });
        btnChonGio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectHour();
            }
        });
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addJob();
            }
        });
    }

    private void addJob() {
        String strTen,strNoiDung,strDiaDiem,strNgay,strGio;
        strTen = edtTen_add.getText().toString().trim();
        strNoiDung = edtNoiDung_add.getText().toString().trim();
        strDiaDiem = edtDiaDiem_add.getText().toString().trim();
        strNgay = edtNgay_add.getText().toString().trim();
        strGio = edtGio_add.getText().toString().trim();
        HashMap<String, String> job = new HashMap<>();
        job.put("ten",strTen);
        job.put("noidung",strNoiDung);
        job.put("diadiem",strDiaDiem);
        job.put("ngay",strNgay);
        job.put("gio",strGio);
        if (checkAll.checkEmptyJob(job))
        {
            Toast.makeText(this, error.INSERT_E001, Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            processAddJob(job);
        }
    }

    private void processAddJob(final HashMap job) {

        mData.child(strUuTien).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot duLieuUuTien : dataSnapshot.getChildren())
                {
                    String uuTien = duLieuUuTien.getValue(String.class);
                    if (strTenUuTien.equals(uuTien))
                    {
                        strKeyUuTien = duLieuUuTien.getKey();
                    }
                }
                String id = mData.child(strCongViec).push().getKey(); // id riêng từng công việc
                Job jobTmp = new Job(id,(String)job.get("ten"),
                                                (String)job.get("noidung"),
                                                (String)job.get("diadiem"),
                                                (String)job.get("ngay"),
                                                (String)job.get("gio"),
                                                strKeyUuTien,false);

                mData.child(strCongViec).child(MainActivity.strKeyUser)
                        .child(id).setValue(jobTmp).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(InsertJobActivity.this, error.INSERT_SUCCESS, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(InsertJobActivity.this, MenuActivity.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InsertJobActivity.this, error.INSERT_E002, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void spDefault() {
        spUuTien.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strTenUuTien = (String) adapterView.getItemAtPosition(i);
                Log.e("ThemCongViecActivity",strTenUuTien);
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
                edtGio_add.setText(sdfGio.format(calendar.getTime()));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(InsertJobActivity.this,
                callBack,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        edtGio_add.setText(sdfGio.format(calendar.getTime()));
        timePickerDialog.show();
    }

    private void selectDate() {
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR,i);
                calendar.set(Calendar.MONTH,i1);
                calendar.set(Calendar.DAY_OF_MONTH,i2);
                edtNgay_add.setText(sdfNgay.format(calendar.getTime()));
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(InsertJobActivity.this,callback,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE));
        edtNgay_add.setText(sdfNgay.format(calendar.getTime()));
        dialog.show();
    }

    private void setControls() {
        btnThem = findViewById(R.id.btnThem);
        btnChonGio = findViewById(R.id.btnChonGio_add);
        btnChonNgay = findViewById(R.id.btnChonNgay_add);
        edtTen_add = findViewById(R.id.edtTenCV_add);
        edtNoiDung_add = findViewById(R.id.edtNoiDungCV_add);
        edtDiaDiem_add = findViewById(R.id.edtDiaDiemCV_add);
        edtGio_add = findViewById(R.id.edtGioCV_add);
        edtNgay_add = findViewById(R.id.edtNgayCV_add);

        spUuTien = findViewById(R.id.spUuTien);
        arrUuTien = new ArrayList<>();
        adapterUuTien = new ArrayAdapter<>(InsertJobActivity.this,android.R.layout.simple_spinner_item,arrUuTien);
        adapterUuTien.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUuTien.setAdapter(adapterUuTien);

        calendar = Calendar.getInstance();
        sdfNgay = new SimpleDateFormat("dd/MM/yyyy");
        sdfGio = new SimpleDateFormat("HH:mm");
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

                startActivity(new Intent(InsertJobActivity.this,MenuActivity.class));
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
