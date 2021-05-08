package com.example.ecommerce.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SellerLoginActivity extends AppCompatActivity {

    private EditText sellerEmailLogin , sellerPasswordLogin ;
    private Button sellerLogin;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        sellerEmailLogin = findViewById(R.id.seller_email_login);
        sellerPasswordLogin = findViewById(R.id.seller_passwprd_login);
        sellerLogin = findViewById(R.id.login_seller);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);


        sellerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginSellerWithEmailAndPassword();

            }
        });

    }

    private void LoginSellerWithEmailAndPassword()
    {
        String emailL = sellerEmailLogin.getText().toString();
        String passwordL = sellerPasswordLogin.getText().toString();

        if (!emailL.equals("") && !passwordL.equals("")) {
            loadingBar.setTitle("Logging");
            loadingBar.setMessage(" Waiting .. ");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(emailL , passwordL)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            loadingBar.dismiss();
                            Toast.makeText(SellerLoginActivity.this, "Logging Successfully", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SellerLoginActivity.this , SellerHomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }
                    });

        }
        else
        {
            Toast.makeText(this, "Enter Email and Password", Toast.LENGTH_SHORT).show();
        }

        }
}