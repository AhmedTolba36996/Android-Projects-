package com.example.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button creatAccountButton ;
    private EditText inputName , inputPhoneNumber,inputPassword;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        creatAccountButton = findViewById(R.id.register_btn);
        inputName = findViewById(R.id.register_username_input);
        inputPhoneNumber = findViewById(R.id.register_phone_number_input);
        inputPassword = findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);

        creatAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatAccount();
            }
        });

    }

    private void CreatAccount()
    {
        String name = inputName.getText().toString();
        String phone = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Enter Your Name ..", Toast.LENGTH_SHORT).show();
        }
        else if ( TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Enter Your Number Phone", Toast.LENGTH_SHORT).show();
        }
        else if ( TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Enter Your Password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creat Account");
            loadingBar.setMessage(" Waiting .. ");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateNumberPhone(name,phone,password);

        }
    }

    private void ValidateNumberPhone(String name, String phone, String password)
    {
        final DatabaseReference ref ;
        ref = FirebaseDatabase.getInstance().getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if( !(snapshot.child("Users").child(phone).exists()) )
                {
                    HashMap<String , Object > usersDataMAps = new HashMap<>();

                    usersDataMAps.put("phone" , phone);
                    usersDataMAps.put("password" , password);
                    usersDataMAps.put("name" , name);

                    ref.child("Users").child(phone).updateChildren(usersDataMAps)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this , LoginActivity.class);
                                        startActivity(intent);
                                    }

                                    else
                                        {
                                            loadingBar.dismiss();
                                            Toast.makeText(RegisterActivity.this, "Error , try again", Toast.LENGTH_SHORT).show();
                                        }

                                }
                            });
                }
                else
                    {
                        Toast.makeText(RegisterActivity.this, "This number" + phone +"Already Exist !", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                    }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}