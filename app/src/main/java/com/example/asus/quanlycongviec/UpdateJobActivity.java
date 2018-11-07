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
    ImageView imgDate, imgHour;
    Button btnUpdate, btnBack;
    EditText edtName_Edit, edtContent_Edit, edtLocation_Edit, edtDate_Edit, edtHour_Edit;

    Calendar calendar;
    SimpleDateFormat sdfDate,sdfHour;
    CheckAll checkAll;
    Error error;
    DatabaseReference mData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_job);
        setControls();

        Intent intent = getIntent();  // nhận dữ liệu từ MenuActivity qua (Intent)
        Job job = (Job) intent.getSerializableExtra("job");

        addEvents(job);
    }

    private void addEvents(Job job) {
        final String idCV = job.getId();
        final int keyPriority = job.getKeyPri();
        edtName_Edit.setText(job.getName());
        edtContent_Edit.setText(job.getContent());
        edtLocation_Edit.setText(job.getLocation());
        edtDate_Edit.setText(job.getDate());
        edtHour_Edit.setText(job.getHour());
        imgDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate();
            }
        });
        imgHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedHour();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processEditJob(idCV,keyPriority);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
    }

    private void processEditJob(String idCV, int keyPriority) {
        String strName,strContent,strLocation,strDate,strHour;
        strName = edtName_Edit.getText().toString().trim();
        strContent = edtContent_Edit.getText().toString().trim();
        strLocation = edtLocation_Edit.getText().toString().trim();
        strDate = edtDate_Edit.getText().toString().trim();
        strHour = edtHour_Edit.getText().toString().trim();
        HashMap<String, String> job = new HashMap<>();
        job.put("id",idCV);
        job.put("keyPriority",String.valueOf(keyPriority));
        job.put("name",strName);
        job.put("content",strContent);
        job.put("location",strLocation);
        job.put("date",strDate);
        job.put("hour",strHour);
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
        Job congViec = new Job((String)job.get("id"),(String)job.get("name"),
                (String)job.get("content"),
                (String)job.get("location"),
                (String)job.get("date"),
                (String)job.get("hour"),
                Integer.parseInt((String) job.get("keyPriority")),false);
        mData.child(MainActivity.strJob + "/" + MainActivity.strKeyUser + "/" + congViec.getId())
                .setValue(congViec)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) task.isComplete();
                {
                    Toast.makeText(UpdateJobActivity.this, error.UPDATE_SUCCESS,
                            Toast.LENGTH_SHORT).show();
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
                edtHour_Edit.setText(sdfHour.format(calendar.getTime()));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateJobActivity.this,
                callBack,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
                edtHour_Edit.setText(sdfHour.format(calendar.getTime()));
        timePickerDialog.show();
    }

    private void selectedDate() {
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR,i);
                calendar.set(Calendar.MONTH,i1);
                calendar.set(Calendar.DAY_OF_MONTH,i2);
                edtDate_Edit.setText(sdfDate.format(calendar.getTime()));
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(UpdateJobActivity.this,callback,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE));
                edtDate_Edit.setText(sdfDate.format(calendar.getTime()));
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
        imgDate             = findViewById(R.id.imgNgay);
        imgHour             = findViewById(R.id.imgGio);
        btnUpdate           = findViewById(R.id.btnSua);
        btnBack             = findViewById(R.id.btnTroLai);
        edtName_Edit        = findViewById(R.id.edtTenCV_edit);
        edtContent_Edit     = findViewById(R.id.edtNoiDungCV_edit);
        edtLocation_Edit    = findViewById(R.id.edtDiaDiemCV_edit);
        edtDate_Edit        = findViewById(R.id.edtNgayCV_edit);
        edtHour_Edit       = findViewById(R.id.edtGioCV_edit);
        calendar = Calendar.getInstance();
        sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        sdfHour = new SimpleDateFormat("HH:mm");
        checkAll = new CheckAll();
        mData = FirebaseDatabase.getInstance().getReference();
        error = new Error();
    }

    @Override
    public void onBackPressed() {
        back();
    }
}
