package com.example.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Admin.AdminHomeActivity;
import com.example.ecommerce.Seller.SellerProductCategoryActivity;
import com.example.ecommerce.Model.Users;
import com.example.ecommerce.PreValent.Prevalent;
import com.example.ecommerce.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText phonenumber , passwordusers ;
    private Button loginButton ;
    private TextView adminLink , notAdminLink , forgetBasswordLink ;
    private ProgressDialog loadingBar;
    private String parentName = "Users";
    // chechkbox
    private CheckBox checkBoxRememberMe ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phonenumber = findViewById(R.id.login_phone_number_input);
        passwordusers = findViewById(R.id.login_password_input);
        loginButton = findViewById(R.id.login_btn);
        loadingBar = new ProgressDialog(this);
        //checkbox
        checkBoxRememberMe = findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        // Admin Link
        adminLink = findViewById(R.id.admin_panel_link);
        notAdminLink = findViewById(R.id.not_admin_panel_link);

        forgetBasswordLink = findViewById(R.id.forget_password_link);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });
        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login Admin");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                parentName = "Admins" ;
            }
        });
        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginButton.setText("Login ");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                parentName = "Users" ;

            }
        });

        forgetBasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this , ResetPasswordActivity.class);
                intent.putExtra("check" , "login");
                startActivity(intent);

            }
        });

    }


    private void LoginUser()
    {
        String phone = phonenumber.getText().toString();
        String password = passwordusers.getText().toString();

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Enter Your Number Phone ..", Toast.LENGTH_SHORT).show();
        }
        else if ( TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Enter Your Password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle(" Logging Account");
            loadingBar.setMessage(" Waiting .. ");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccess(phone,password);
        }

    }

    private void AllowAccess(String phone, String password) {

        // checkbox *******************

        if(checkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.userPhoneKey , phone);
            Paper.book().write(Prevalent.userPasswordKey , password);
        }

        // *****************************


        final DatabaseReference ref ;
        ref = FirebaseDatabase.getInstance().getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(parentName).child(phone).exists())
                {

                    Users usersdata = snapshot.child(parentName).child(phone).getValue(Users.class);
                    if(usersdata.getPhone().equals(phone))
                    {
                        if(usersdata.getPassword().equals(password))
                        {
                            if(parentName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this, " Successful ", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();


                                Intent intent = new Intent(LoginActivity.this , AdminHomeActivity.class);
                                startActivity(intent);
                            }
                            else if (parentName.equals("Users"))
                            {
                                //Toast.makeText(LoginActivity.this, "" + usersdata.getPhone().toString() , Toast.LENGTH_SHORT).show();
                                Toast.makeText(LoginActivity.this, " Successful ", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();


                                Intent intent = new Intent(LoginActivity.this , HomeActivity.class);
                                Prevalent.CurrentOnlineUser = usersdata;
                                startActivity(intent);
                            }
                        }

                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, " This Account Not Found", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}