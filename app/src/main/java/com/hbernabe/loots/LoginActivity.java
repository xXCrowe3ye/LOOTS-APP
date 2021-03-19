package com.hbernabe.loots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbernabe.loots.Model.Users;
import com.hbernabe.loots.PaperLoc.paperDb;

public class LoginActivity extends AppCompatActivity
{
    private EditText editTextUser, editTextPass;
    private Button loginBtn;
    private ProgressDialog loadingBar;
    private TextView adminUse, normalUse;

    private String parentDbName = "Users";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        loginBtn = (Button) findViewById(R.id.loginBtn);
        editTextPass = (EditText) findViewById(R.id.loginPasswordInput);
        editTextUser = (EditText) findViewById(R.id.loginUserNameInput);
        adminUse = (TextView) findViewById(R.id.adminLogIn);
        normalUse = (TextView) findViewById(R.id.userLogIn);
        loadingBar = new ProgressDialog(this);




        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                userLog();
            }
        });

        adminUse.setOnClickListener(new View.OnClickListener() { //changes login button in login layout to Admin login
            @Override
            public void onClick(View view)
            {
                loginBtn.setText("Login Admin");
                adminUse.setVisibility(View.INVISIBLE);
                normalUse.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        normalUse.setOnClickListener(new View.OnClickListener() { //returns to normal user login
            @Override
            public void onClick(View view)
            {
                loginBtn.setText("Login");
                adminUse.setVisibility(View.VISIBLE);
                normalUse.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });
    }



    private void userLog()
    {
        String username = editTextUser.getText().toString();
        String password = editTextPass.getText().toString();

        if (TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "Enter username", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            checkCredentials(username, password);
        }
    }



    private void checkCredentials(final String username, final String password)
    {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(parentDbName).child(username).exists())
                {
                    Users usersData = dataSnapshot.child(parentDbName).child(username).getValue(Users.class);

                    if (usersData.getUsername().equals(username))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            if (parentDbName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this, "Admin logged in", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AddProdActivity.class);
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this, "logged in Successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, HomeNavigationActivity.class);
                                paperDb.currentUser = usersData;
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Account with this " + username + " username do not exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}