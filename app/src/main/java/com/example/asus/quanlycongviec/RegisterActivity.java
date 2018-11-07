package com.example.asus.quanlycongviec;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.model.CheckAll;
import com.example.asus.model.Error;
import com.example.asus.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

// Mô tả            : Form đăng ký
// Tác giả          : Ngô Hoàng Quốc Bảo
// Email            : n14dccn067@student.ptithcm.edu.vn
// Ngày cập nhật    : 5/11/2018
public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = RegisterActivity.class.getSimpleName();
    DatabaseReference mData;
    CheckAll checkAll;
    Error error;

    EditText edtId,edtName;
    Button btnRegister,btnBack;
    TextInputEditText edtPass,edtPassConfirm;
    TextInputLayout usernameWrapper,passwordWrapper;

    String strNodeUsername = "username";    // node username trong firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setControls();
        addEvents();
    }
    // Tên hàm : addEvents
    // Mô tả   : gán sự kiện cho các control
    private void addEvents() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // (2) Xử lý đăng ký
                //  1. Nhấn nút Đăng ký thì thực hiện xử lý.
                registerUser();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //(3) Xử lý trở lại
                // 1. Nhấn nút Trở lại hoặc nút Back (trên smartphone) thì quay trở lại màn hình Đăng nhập
                back();
            }
        });
        //(4) Xử lý password confirm
        // 1. Xử lý check khi đang nhập
        edtPassConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // lấy dữ liệu từ edittext Pass
                String pass = edtPass.getText().toString().trim();
                // lấy dữ liệu từ edittext Pass confirm
                String pass_confirm = edtPassConfirm.getText().toString().trim();
                if (pass.equals(pass_confirm) || pass_confirm.length() == 0)
                {
                    edtPassConfirm.setBackgroundColor(Color.WHITE);
                    passwordWrapper.setError(null);
                    btnRegister.setEnabled(true);
                }
                else
                {
                    btnRegister.setEnabled(false);
                    passwordWrapper.setError(error.REGISTER_E004);
                }
            }
        });
    }

    // Tên hàm : registerUser
    // Mô tả   : lấy dữ liệu trong 3 ô edittext để đăng ký
    private void registerUser() {
        // lấy dữ liệu từ Edittext Tên đăng nhập
        String strId;
        // lấy dữ liệu từ Edittext Mật khẩu
        String strPass;
        // lấy dữ liệu từ Edittext Họ tên
        String strName;
        strId = edtId.getText().toString().trim();
        strPass = edtPass.getText().toString().trim();
        strName = edtName.getText().toString().trim();
        // (2) Xử lý đăng ký
        // 2. Xử lý check:
        // a. Check hạng mục 1 2 3
        if (checkAll.checkEmpty(strId,strPass,strName)) {
            Toast.makeText(this, error.REGISTER_E001, Toast.LENGTH_SHORT).show();
        }
        // (2) Xử lý đăng ký
        // 2. Xử lý check:
        // a. Check hạng mục 4
        else if (checkAll.checkSpecial(strId)) {
            Toast.makeText(this, error.REGISTER_E003, Toast.LENGTH_SHORT).show();
        }
        else {
            //(2) Xử lý đăng ký
            // 3. Xử lý add
            addUser(strId,strPass,strName);
        }
    }


    // Tên hàm : addUser
    // Mô tả   : kiểm tra xem Username nhập vào có tồn tại chưa? Nếu chưa thì thêm mới vào,
    // nếu rồi thì báo lỗi
    // Tham số : String strId   (chứa tên đăng nhập)
    // Tham số : String strPass (chứa mật khẩu)
    // Tham số : String strName (chứa tên)
    private void addUser(final String strId, final String strPass, final String strName){
        Query query = mData.child(MainActivity.strUser).orderByChild(strNodeUsername).equalTo(strId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(RegisterActivity.this, error.REGISTER_E002,
                            Toast.LENGTH_SHORT).show();
                } else {
                    mData.child(MainActivity.strUser).push().setValue(new User(strId,strPass,strName))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                Toast.makeText(RegisterActivity.this, error.REGISTER_SUCCESS,
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, error.REGISTER_E005,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
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
        edtId = findViewById(R.id.edtId);
        edtPass = findViewById(R.id.edtPass);
        edtPassConfirm = findViewById(R.id.edtPassConfirm);
        edtName = findViewById(R.id.edtName);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);
        usernameWrapper = findViewById(R.id.usernameWrapper);
        passwordWrapper = findViewById(R.id.passwordWrapper);

        mData = FirebaseDatabase.getInstance().getReference();
        checkAll = new CheckAll();
        error = new Error();
    }
    // Tên hàm: onBackPressed
    // Mô tả: khi bấm nút Back trên smartphone thì sẽ thực hiện hàm này
    @Override
    public void onBackPressed() {
        //(3) Xử lý trở lại
        // 1. Nhấn nút Trở lại hoặc nút Back (trên smartphone) thì quay trở lại màn hình Đăng nhập
        back();
    }

    // Tên hàm: back
    // Mô tả: Quay lại màn hình trước đó
    private void back(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
