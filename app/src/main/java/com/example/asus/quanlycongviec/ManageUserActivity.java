package com.example.asus.quanlycongviec;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.asus.adapter.UserAdapter;
import com.example.asus.model.Error;
import com.example.asus.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageUserActivity extends AppCompatActivity {

    DatabaseReference mData;
    Error error;

    ListView lvUser;
    ArrayList<User> arrUser;
    UserAdapter adapterUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        setControls();
        addEvents();
    }

    private void addEvents() {
        getUsers();
        lvUser.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final User user = new User(arrUser.get(i).getUsername(),
                        arrUser.get(i).getPassword()
                        ,arrUser.get(i).getName());
                AlertDialog.Builder dialogDelete = new AlertDialog.Builder(ManageUserActivity.this);
                dialogDelete.setMessage("Bạn có muốn xóa tài khoản "+user.getUsername()+" không ?");
                dialogDelete.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser(user);
                    }
                });
                dialogDelete.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialogDelete.show();
                return false;
            }
        });
    }

    private void deleteUser(final User user) {
        mData.child(MainActivity.strUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot dataUser : dataSnapshot.getChildren())
                    {
                        User userTmp = dataUser.getValue(User.class);
                        if (user.getUsername().equals(userTmp.getUsername()))
                        {
                            mData.child(MainActivity.strJob).child(dataUser.getKey()).removeValue();
                            mData.child(MainActivity.strUser).child(dataUser.getKey()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ManageUserActivity.this,
                                                    error.MANAGER_SUCCESS, Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ManageUserActivity.this,
                                                    error.MANAGER_E001, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUsers() {
        arrUser.clear();
        mData.child(MainActivity.strUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot dataUser : dataSnapshot.getChildren())
                    {
                        User user = dataUser.getValue(User.class);
                        arrUser.add(user);
                    }
                }
                adapterUser.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setControls() {
        mData = FirebaseDatabase.getInstance().getReference();
        error = new Error();

        lvUser = findViewById(R.id.lvUser);
        arrUser = new ArrayList<>();
        adapterUser = new UserAdapter(ManageUserActivity.this,R.layout.item_user,arrUser);
        lvUser.setAdapter(adapterUser);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ManageUserActivity.this);
        builder.setMessage("Bạn chắc chắn muốn thoát ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.strKeyUser = "";
                //startActivity(new Intent(ManageUserActivity.this, MainActivity.class));
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
