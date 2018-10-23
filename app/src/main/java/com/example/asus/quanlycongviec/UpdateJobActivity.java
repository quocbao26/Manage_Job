package com.example.asus.quanlycongviec;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.asus.model.CheckAll;
import com.example.asus.model.Job;
import com.example.asus.model.Error;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UpdateJobActivity extends AppCompatActivity {
    ImageView imgNgay, imgGio;
    Button btnSua, btnTroLai;
    EditText edtTen_edit, edtNoiDung_edit, edtDiaDiem_edit, edtNgay_edit, edtGio_edit;

    Calendar calendar;
    SimpleDateFormat sdfNgay,sdfGio;
    CheckAll checkAll;
    Error error;
    DatabaseReference mData;

    String strCongViec = "jobs";
    String idCV = "";
    String keyPriority = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_cong_viec);
        setControls();

        Intent intent = getIntent();  // nhận dữ liệu từ MenuActivity qua (Intent)
        Job job = (Job) intent.getSerializableExtra("congviec");

        addEvents(job);
    }

    private void addEvents(Job job) {
        idCV = job.getId();
        keyPriority = job.getKeyUuTien();
        edtTen_edit.setText(job.getTenCongViec());
        edtNoiDung_edit.setText(job.getNoiDung());
        edtDiaDiem_edit.setText(job.getDiaDiem());
        edtNgay_edit.setText(job.getNgay());
        edtGio_edit.setText(job.getGio());
        imgNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate();
            }
        });
        imgGio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedHour();
            }
        });
        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processEditJob(idCV);
            }
        });
        btnTroLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
    }

    private void processEditJob(String idCV) {
        String strTen,strNoiDung,strDiaDiem,strNgay,strGio;
        strTen = edtTen_edit.getText().toString().trim();
        strNoiDung = edtNoiDung_edit.getText().toString().trim();
        strDiaDiem = edtDiaDiem_edit.getText().toString().trim();
        strNgay = edtNgay_edit.getText().toString().trim();
        strGio = edtGio_edit.getText().toString().trim();
        HashMap<String, String> job = new HashMap<>();
        job.put("id",idCV);
        job.put("keyPriority",keyPriority);
        job.put("ten",strTen);
        job.put("noidung",strNoiDung);
        job.put("diadiem",strDiaDiem);
        job.put("ngay",strNgay);
        job.put("gio",strGio);
        if (checkAll.checkEmptyJob(job))
        {
            Toast.makeText(UpdateJobActivity.this, error.UPDATE_E001, Toast.LENGTH_SHORT).show();
        }
        else
        {
            updateJobInFirebase(job);
        }
    }

    private void updateJobInFirebase(HashMap job) {
        Job congViec = new Job((String)job.get("id"),(String)job.get("ten"),
                (String)job.get("noidung"),
                (String)job.get("diadiem"),
                (String)job.get("ngay"),
                (String)job.get("gio"),
                (String)job.get("keyPriority"),false);
        mData.child(strCongViec).child(MainActivity.strKeyUser).child(idCV).setValue(congViec)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) task.isComplete();
                {
                    Toast.makeText(UpdateJobActivity.this, error.UPDATE_SUCCESS, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateJobActivity.this, error.UPDATE_E002, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectedHour() {
        TimePickerDialog.OnTimeSetListener callBack = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY,i);
                calendar.set(Calendar.MINUTE,i1);
                edtGio_edit.setText(sdfGio.format(calendar.getTime()));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateJobActivity.this,
                callBack,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
                edtGio_edit.setText(sdfGio.format(calendar.getTime()));
        timePickerDialog.show();
    }

    private void selectedDate() {
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR,i);
                calendar.set(Calendar.MONTH,i1);
                calendar.set(Calendar.DAY_OF_MONTH,i2);
                edtNgay_edit.setText(sdfNgay.format(calendar.getTime()));
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(UpdateJobActivity.this,callback,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE));
                edtNgay_edit.setText(sdfNgay.format(calendar.getTime()));
        dialog.show();
    }


    private void back()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(UpdateJobActivity.this);
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


    private void setControls() {
        imgNgay           = findViewById(R.id.imgNgay);
        imgGio            = findViewById(R.id.imgGio);
        btnSua            = findViewById(R.id.btnSua);
        btnTroLai         = findViewById(R.id.btnTroLai);
        edtTen_edit       = findViewById(R.id.edtTenCV_edit);
        edtNoiDung_edit   = findViewById(R.id.edtNoiDungCV_edit);
        edtDiaDiem_edit   = findViewById(R.id.edtDiaDiemCV_edit);
        edtNgay_edit      = findViewById(R.id.edtNgayCV_edit);
        edtGio_edit       = findViewById(R.id.edtGioCV_edit);
        calendar = Calendar.getInstance();
        sdfNgay = new SimpleDateFormat("dd/MM/yyyy");
        sdfGio = new SimpleDateFormat("HH:mm");
        checkAll = new CheckAll();
        mData = FirebaseDatabase.getInstance().getReference();
        error = new Error();
    }

    @Override
    public void onBackPressed() {
        back();
    }
}
