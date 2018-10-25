package com.example.asus.quanlycongviec;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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
// Ngày cập nhật    : 9/10/2018
public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = RegisterActivity.class.getSimpleName();
    DatabaseReference mData;
    CheckAll checkAll;
    Error error;

    EditText edtID,edtNAME;
    Button btnRegister,btnBack;
    TextInputEditText edtPASS,edtPASS_XacNhan;
    TextInputLayout usernameWrapper,passwordWrapper;

    String strUser = "users";           // node users trong firebase
    String strNodeUsername = "username";    // node username trong firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);
        setControls();
        addEvents();
    }
    // Tên hàm : addEvents
    // Mô tả   : gán sự kiện cho các nút
    private void addEvents() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        edtPASS_XacNhan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pass = edtPASS.getText().toString().trim();
                String pass_confirm = edtPASS_XacNhan.getText().toString().trim();
                if (pass.equals(pass_confirm) || pass_confirm.length() == 0)
                {
                    edtPASS_XacNhan.setBackgroundColor(Color.WHITE);
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
        String strId;    // lấy dữ liệu từ Edittext Tên đăng nhập
        String strPass;     // lấy dữ liệu từ Edittext Mật khẩu
        String strName; // lấy dữ liệu từ Edittext Họ tên
        strId = edtID.getText().toString().trim();
        strPass = edtPASS.getText().toString().trim();
        strName = edtNAME.getText().toString().trim();

        if (checkAll.checkEmpty(strId,strPass,strName))
        {
            Toast.makeText(this, error.REGISTER_E001, Toast.LENGTH_SHORT).show();
        }
        else if (checkAll.checkSpecial(strId))
        {
            Toast.makeText(this, error.REGISTER_E003, Toast.LENGTH_SHORT).show();
        }
        else
        {
                addUser(strId,strPass,strName);
        }
    }

    // Tên hàm : addUser
    // Mô tả   : kiểm tra xem Username nhập vào có tồn tại chưa? Nếu chưa thì thêm mới vào, nếu rồi thì báo lỗi
    // Tham số : String strId   (chứa tên đăng nhập)
    // Tham số : String strPass (chứa mật khẩu)
    // Tham số : String strName (chứa tên)
    private void addUser(final String strId, final String strPass, final String strName){
        Query query = mData.child(strUser).orderByChild(strNodeUsername).equalTo(strId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    Toast.makeText(RegisterActivity.this, error.REGISTER_E002,
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mData.child(strUser).push().setValue(new User(strId,strPass,strName))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete())
                            {
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
    // Tên hàm : setControls
    // Mô tả   : gán id cho các nút
    private void setControls() {
        edtID = findViewById(R.id.edtID);
        edtPASS = findViewById(R.id.edtPASS);
        edtPASS_XacNhan = findViewById(R.id.edtPASS_XacNhan);
        edtNAME = findViewById(R.id.edtNAME);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);
        usernameWrapper = findViewById(R.id.usernameWrapper);
        passwordWrapper = findViewById(R.id.passwordWrapper);

        mData = FirebaseDatabase.getInstance().getReference();
        checkAll = new CheckAll();
        error = new Error();
    }
}
