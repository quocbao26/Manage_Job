package com.example.asus.quanlycongviec;

import android.content.Intent;
import android.support.annotation.NonNull;
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
        setContentView(R.layout.activity_doi_mat_khau);
        setControls();
        addEvents();
    }

    private void addEvents() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strMKNew = edtPassNew.getText().toString().trim();
                String strMKNewConfirm = edtPassNewConfirm.getText().toString().trim();
                String strMKOld = edtPassOld.getText().toString().trim();

                if (checkAll.checkEmpty(strMKNew, strMKNewConfirm, strMKOld)) {
                    Toast.makeText(ChangePasswordActivity.this, error.CHANGEPASS_E001, Toast.LENGTH_SHORT).show();
                } else if (!(strMKNew.equals(strMKNewConfirm))) {
                    Toast.makeText(ChangePasswordActivity.this, error.CHANGEPASS_E002, Toast.LENGTH_SHORT).show();
                } else {
                    getMKUserFromKeyUser(strMKOld, strMKNew);
                }
            }
        });
    }

    private void getMKUserFromKeyUser(final String strMKOld, final String strMKNew) {
        mData.child(MainActivity.strUser).child(MainActivity.strKeyUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            User user = dataSnapshot.getValue(User.class);
                            if (user.getPassword().equals(strMKOld))
                            {
                                startChangePassword(user, strMKNew);
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

    private void startChangePassword(User user, String strMKNew) {
        User userTmp = new User(user.getUsername(), strMKNew, user.getName());
        mData.child(MainActivity.strUser).child(MainActivity.strKeyUser).setValue(userTmp)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                MainActivity.strKeyUser = "";
                finish();
                Toast.makeText(ChangePasswordActivity.this, error.CHANGEPASS_SUCCESS, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ChangePasswordActivity.this,MainActivity.class));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChangePasswordActivity.this, error.CHANGEPASS_FAIL, Toast.LENGTH_SHORT).show();
            }
        });
    }

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
}
