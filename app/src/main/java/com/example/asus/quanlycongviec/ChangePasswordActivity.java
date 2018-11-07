package com.example.asus.quanlycongviec;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.model.CheckAll;
import com.example.asus.model.Error;
import com.example.asus.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
// Mô tả            : Form đổi mật khẩu
// Tác giả          : Ngô Hoàng Quốc Bảo
// Email            : n14dccn067@student.ptithcm.edu.vn
// Ngày cập nhật    : 6/11/2018
public class ChangePasswordActivity extends AppCompatActivity {

    public final static String TAG = ChangePasswordActivity.class.getSimpleName();

    Button btnConfirm, btnBack;
    EditText edtPassNew, edtPassNewConfirm, edtPassOld;

    DatabaseReference mData;
    CheckAll checkAll;
    Error error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setControls();
        addEvents();
    }

    // Tên hàm: addEvents
    // Mô tả: gán sự kiện cho các control
    private void addEvents() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //(3) Trở lại
                back();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // chứa pass mới
                String strPassNew = edtPassNew.getText().toString().trim();
                // chứa pass mới nhập lại
                String strPassNewConfirm = edtPassNewConfirm.getText().toString().trim();
                // chứa pass cũ
                String strPassOld = edtPassOld.getText().toString().trim();
                //(2) Xử lý đổi mật khẩu
                // 1. Nhấn nút Xác nhận thì thực hiện xử lý.
                // 2. Xử lý check:
                // a. Check hạng mục 1 2 3
                if (checkAll.checkEmpty(strPassNew, strPassNewConfirm, strPassOld)) {
                    Toast.makeText(ChangePasswordActivity.this, error.CHANGEPASS_E001,
                            Toast.LENGTH_SHORT).show();
                }
                // a. Check hạng mục 4
                else if (!(strPassNew.equals(strPassNewConfirm))) {
                    Toast.makeText(ChangePasswordActivity.this, error.CHANGEPASS_E002,
                            Toast.LENGTH_SHORT).show();
                } else {
                    // (2) Xử lý đổi mật khẩu
                    // 3.So sánh mật khẩu lúc login và mật khẩu cũ mới nhập
                    getMKUserFromKeyUser(strPassOld, strPassNew);
                }
            }
        });
    }

    // Tên hàm: getMKUserFromKeyUser
    // Mô tả: vào node KeyUser lúc đầu Login lấy pass và so sánh với pass cũ đã nhập vào
    // Tham số:  String strPassOld   (chứa mật khẩu cũ)
    // Tham số : String strPassNew   (chứa mật khẩu mới)
    private void getMKUserFromKeyUser(final String strPassOld, final String strPassNew) {
        mData.child(MainActivity.strUser + "/" + MainActivity.strKeyUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            User user = dataSnapshot.getValue(User.class);
                            if (user.getPassword().equals(strPassOld))
                            {
                                // (2) Xử lý đổi mật khẩu
                                // 4.Đổi mật khẩu
                                startChangePassword(user, strPassNew);
                            }
                            else
                            {
                                Toast.makeText(ChangePasswordActivity.this, error.CHANGEPASS_E003,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    // Tên hàm: startChangePassword
    // Mô tả: Thực hiện đổi mật khẩu
    // Tham số: Object User (chứa User hiện tại đang login)
    // Tham số: String strPassNew (chứa mật khẩu mới)
    private void startChangePassword(User user, String strPassNew) {
        User userTmp = new User(user.getUsername(), strPassNew, user.getName());
        mData.child(MainActivity.strUser + "/" + MainActivity.strKeyUser).setValue(userTmp)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                MainActivity.strKeyUser = "";
                finish();
                Toast.makeText(ChangePasswordActivity.this, error.CHANGEPASS_SUCCESS,
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ChangePasswordActivity.this,MainActivity.class));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChangePasswordActivity.this, error.CHANGEPASS_FAIL,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Tên hàm: setControls
    // Mô tả: gán Id cho các control
    private void setControls() {
        btnConfirm = findViewById(R.id.btnConfirm);
        btnBack = findViewById(R.id.btnBack);
        edtPassNew = findViewById(R.id.edtPassNew);
        edtPassNewConfirm = findViewById(R.id.edtPassNewConfirm);
        edtPassOld = findViewById(R.id.edtPassOld);

        mData = FirebaseDatabase.getInstance().getReference();
        checkAll = new CheckAll();
        error = new Error();
    }

    // Tên hàm: onBackPressed
    // Mô tả: khi bấm nút Back trên smartphone thì sẽ thực hiện hàm này
    @Override
    public void onBackPressed() {
        // (3) Trở lại
        back();
    }

    // Tên hàm: back
    // Mô tả: Đi đến màn hình Các công việc
    private void back(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
        builder.setMessage("Bạn chắc chắn muốn thoát ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                startActivity(new Intent(ChangePasswordActivity.this,JobNeedActivity.class));
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
