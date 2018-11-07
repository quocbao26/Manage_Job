package com.example.asus.quanlycongviec;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.model.CheckAll;
import com.example.asus.model.Error;
import com.example.asus.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
// Mô tả            : Form đăng nhập
// Tác giả          : Ngô Hoàng Quốc Bảo
// Email            : n14dccn067@student.ptithcm.edu.vn
// Ngày cập nhật    : 5/11/2018
public class MainActivity extends AppCompatActivity {

    public static String strPriority = "priority"; // node priority trong Firebase
    public static String strJob = "jobs";     // node jobs trong Firebase
    public static String strUser = "users";  // node users trong Firebase
    public static String strKeyUser = ""; // lưu key người dùng sau khi đăng nhập

    Error error;
    DatabaseReference mData;
    SharedPreferences sharedPreferences;

    CheckAll checkAll;

    EditText edtID,edtPASS;
    Button btnLogin,btnRegister;
    CheckBox cbRemember;

    boolean iFlag = false;  // kiểm tra có điền đúng Tên đăng nhập và Mật khẩu không
    Intent intent;

    // (1) Hiển thị ban đầu
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setControls();
        // 2. Setup dữ liệu ban đầu
        getDataFromSharePre();
        // 3.Hàm check Internet
        checkInternet();
        addEvents();
    }

    // Tên hàm : getDataFromSharePre
    // Mô tả   : cài đặt dữ liệu ban đầu khi lần trước đã tích nhớ mật khẩu
    private void getDataFromSharePre() {
        edtID.setText(sharedPreferences.getString("username",""));
        edtPASS.setText(sharedPreferences.getString("password",""));
        cbRemember.setChecked(sharedPreferences.getBoolean("checked",false));
    }


    // Tên hàm: checkInternet
    // Mô tả: Kiểm tra kết nối Internet
    private void checkInternet() {
        if(isConnected() == false) {
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            alertDialog.setTitle("Thông báo")
                    .setMessage(error.NOT_CONNECT_INTERNET)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkInternet();
                        }
                    })
                    .show();
        } else {
            Toast.makeText(this, error.CONNECTED_INTERNET, Toast.LENGTH_SHORT).show();
        }
    }

    // Tên hàm: isConnected
    // Mô tả: kiểm tra xem có kết nối sẵn chưa
    // Kiểu trả về: True, False
    private boolean isConnected(){
        ConnectivityManager cm=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnectedOrConnecting())  return true;
        return false;
    }
    // Tên hàm: addEvents
    // Mô tả: gán sự kiện cho các control
    private void addEvents() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // (3) Đăng ký
                isSignup();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //(2) Xử lý đăng nhập
                // 1. Nhấn nút Đăng nhập thì thực hiện xử lý.
                isLogin();
            }
        });
    }

    // Tên hàm: isLogin
    // Mô tả: xử lý đăng nhập
    // (2) Xử lý đăng nhập
    private void isLogin() {
        String strId = "";  // lấy dữ liệu từ EditText Tên đăng nhập
        String strPass = ""; // lấy dữ liệu từ EditText Mật khẩu
        strId = edtID.getText().toString().trim();
        strPass = edtPASS.getText().toString().trim();
        // 2. Xử lý check:
        // a. Check hạng mục 1 2 3
        if (checkEmptyIdPass(strId, strPass) == false)
        {
            // 4.Hàm đăng nhập
            processLogin(strId, strPass);
        }
    }

    // Tên hàm : checkEmptyIdPass
    // Mô tả   : kiểm tra 2 giá trị Id, Pass có rỗng không ?
    // Kiểu trả về : true / false
    // Tham số : String strId   (chứa tên đăng nhập)
    // Tham số : String strPass (chứa mật khẩu)
    public boolean checkEmptyIdPass(String strId, String strPass) {
        // a. Check hạng mục 1
        if (checkAll.checkId(strId) && checkAll.checkPass(strPass)){
            Toast.makeText(this, error.LOGIN_E001, Toast.LENGTH_SHORT).show();
            return true;
        }
        // a. Check hạng mục 2
        else if (checkAll.checkId(strId) && !checkAll.checkPass(strPass)) {
            Toast.makeText(this, error.LOGIN_E002, Toast.LENGTH_SHORT).show();
            return true;
        }
        // a. Check hạng mục 3
        else if (!checkAll.checkId(strId) && checkAll.checkPass(strPass)) {
            Toast.makeText(this, error.LOGIN_E003, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    // Tên hàm: processLogin
    // Mô tả: nếu đúng Id, Pass thì chuyển sang màn hình khác
    // Tham số : String strId   (chứa tên đăng nhập)
    // Tham số : String strPass (chứa mật khẩu)
    private void processLogin(final String strId, final String strPass) {
        if (strId.equals("1") && strPass.equals("1")) {
            startActivity(new Intent(MainActivity.this,ManageUserActivity.class));
        }
        else {
            mData.child(strUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataUser : dataSnapshot.getChildren()) {
                        User user = dataUser.getValue(User.class);
                        boolean ch = user.getUsername().equals(strId);
                        Log.e("MainActivity","Equal: "+ch);
                        if (user.getUsername().equals(strId) && user.getPassword().equals(strPass)) {
                            iFlag = true;
                            strKeyUser = dataUser.getKey();
                            // (2) Xử lý đăng nhập
                            // 3.Hàm lưu tài khoản, mật khẩu
                            rememberIdAndPass(strId,strPass);
                            Toast.makeText(MainActivity.this, error.LOGIN_SUCCESS,
                                    Toast.LENGTH_SHORT).show();
                            intent = new Intent(MainActivity.this,JobNeedActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    if (iFlag == false) {
                        Toast.makeText(MainActivity.this,error.LOGIN_E004, Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    // Tên hàm: rememberIdAndPass
    // Mô tả: nếu có tick nhớ mật khẩu thì lưu lại
    // Tham số : String strId   (chứa tên đăng nhập)
    // Tham số : String strPass (chứa mật khẩu)
    private void rememberIdAndPass(String strId, String strPass) {
        if (cbRemember.isChecked()){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username",strId);
            editor.putString("password",strPass);
            editor.putBoolean("checked",true);
            editor.commit();
        }else{
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("username");
            editor.remove("password");
            editor.remove("checked");
            editor.commit();
        }
    }


    // Tên hàm: isSignup
    // Mô tả: đi đến màn hình đăng ký
    private void isSignup() {
        Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
    // Tên hàm: setControls
    // Mô tả: gán Id cho các control
    private void setControls() {
        edtID       = findViewById(R.id.edtID);
        edtPASS     = findViewById(R.id.edtPASS);
        btnLogin    = findViewById(R.id.btnLogin);
        cbRemember  = findViewById(R.id.cbRemember);
        btnRegister = findViewById(R.id.btnRegister);
        mData       = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("dataLogin",MODE_PRIVATE);
        checkAll = new CheckAll();
        error = new Error();
    }
}
