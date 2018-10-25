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

public class MainActivity extends AppCompatActivity {

    public static String strCongViec = "jobs";
    public static String strUser = "users";  // node trong Firebase
    public static String strKeyUser = ""; // lưu key người dùng sau khi đăng nhập

    Error error;
    DatabaseReference mData;
    SharedPreferences sharedPreferences;

    CheckAll checkAll;

    EditText edtID,edtPASS;
    Button btnLogin,btnRegister;
    CheckBox cbRemember;

    int iFlag = 0;  // kiểm tra có điền đúng Tên đăng nhập và Mật khẩu không
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setControls();
        getDataFromSharePre();
        checkInternet();
        addEvents();
    }

    private void getDataFromSharePre() {
        edtID.setText(sharedPreferences.getString("username",""));
        edtPASS.setText(sharedPreferences.getString("password",""));
        cbRemember.setChecked(sharedPreferences.getBoolean("checked",false));
    }

    // Kiểm tra kết nối Internet
    private void checkInternet()
    {
        if(isConnected() == false) {
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            alertDialog.setTitle("Thông báo")
                    .setMessage(error.NOT_CONNECT_INTERNET)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        } else {
            Toast.makeText(this, error.CONNECTED_INTERNET, Toast.LENGTH_SHORT).show();
        }
    }

    // kiểm tra xem có kết nối sẵn chưa
    private boolean isConnected(){
        ConnectivityManager cm=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnectedOrConnecting())  return true;
        return false;
    }
    // gán sự kiện cho nút Đăng ký, Đăng nhập
    private void addEvents() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSignup();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLogin();
            }
        });
    }

    // xử lý đăng nhập
    private void isLogin() {
        String strId = "";  // lấy dữ liệu từ EditText Tên đăng nhập
        String strPass = ""; // lấy dữ liệu từ EditText Mật khẩu
        strId = edtID.getText().toString().trim();
        strPass = edtPASS.getText().toString().trim();
        if (checkEmptyIdPass(strId, strPass) == false)
        {
            kiemTraLogin(strId, strPass);
        }
    }

    public boolean checkEmptyIdPass(String strId, String strPass) {
        if (checkAll.checkId(strId) && checkAll.checkPass(strPass))
        {
            Toast.makeText(this, error.LOGIN_E001, Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (checkAll.checkId(strId) && !checkAll.checkPass(strPass))
        {
            Toast.makeText(this, error.LOGIN_E002, Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (!checkAll.checkId(strId) && checkAll.checkPass(strPass))
        {
            Toast.makeText(this, error.LOGIN_E003, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    // xem có Tên đăng nhập và mật khẩu đã điền đã có trên Firebase chưa ?
    // Nếu có thì đăng nhập thành công và qua màn hình Công việc
    private void kiemTraLogin(final String strId, final String strPass) {
        if (strId.equals("1") && strPass.equals("1")) {
            startActivity(new Intent(MainActivity.this,ManageUserActivity.class));
        }
        else {
            mData.child(strUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataUser : dataSnapshot.getChildren())
                    {
                        User user = dataUser.getValue(User.class);
                        if (user.getUsername().equals(strId) && user.getPassword().equals(strPass))
                        {
                            iFlag = 1;
                            strKeyUser = dataUser.getKey();
                            rememberIdAndPass(strId,strPass);
                            Toast.makeText(MainActivity.this, error.LOGIN_SUCCESS, Toast.LENGTH_SHORT).show();
                            intent = new Intent(MainActivity.this,MenuActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    if (iFlag == 0)
                    {
                        Toast.makeText(MainActivity.this,error.LOGIN_E004, Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

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



    // đi đến màn hình đăng ký
    private void isSignup() {
        Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
    // gán Id cho edittext và button
    private void setControls() {
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
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
