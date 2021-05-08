package com.example.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.PreValent.Prevalent;
import com.example.ecommerce.R;
import com.example.ecommerce.Seller.SellerHomeActivity;
import com.example.ecommerce.Seller.SellerRegestraitionActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinNowButton , loginButton ;
    private ProgressDialog loadingBar;
    private String parentName = "Users";
    private TextView sellerLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinNowButton = findViewById(R.id.main_join_now_btn);
        loginButton = findViewById(R.id.main_login_btn);
        sellerLogin = findViewById(R.id.seller_login);

        //checkbox ****************
        Paper.init(this);
        loadingBar = new ProgressDialog(this);

        // Login And Creation Methods
        Login();
        Register();
        SellerLogin();
        // CheckBox *************************************************************

        String userPhoneKey = Paper.book().read(Prevalent.userPhoneKey);
        String userPasswordKey = Paper.book().read(Prevalent.userPasswordKey);

        if(userPhoneKey != "" && userPasswordKey != "")
        {
            if( !TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey))
            {
                AllowAccess(userPhoneKey , userPasswordKey ) ;
                loadingBar.setTitle(" Already Logged In ");
                loadingBar.setMessage(" Please Wait... ");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

            }
        }

        // *********************************************************************************

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null)
        {
            Intent intent = new Intent(MainActivity.this , SellerHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }

    public void SellerLogin()
    {
        sellerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , SellerRegestraitionActivity.class);
                startActivity(intent);
            }
        });
    }


    public void Login()
        {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this , LoginActivity.class);
                    startActivity(intent);
                }
            });
       }

       public void Register()
       {
           joinNowButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(MainActivity.this , RegisterActivity.class);
                   startActivity(intent);
               }
           });
       }

    private void AllowAccess(final String phone, final String password)
    {
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
                            Toast.makeText(MainActivity.this, " Successful ", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(MainActivity.this , HomeActivity.class);
                            Prevalent.CurrentOnlineUser = usersdata;
                            startActivity(intent);

                        }
                    }

                }
                else
                {
                    Toast.makeText(MainActivity.this, " This Account Not Found", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





}