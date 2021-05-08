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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SellerRegestraitionActivity extends AppCompatActivity {

    private EditText sellerName , sellerPhone,sellerEmail,sellerPassword,sellerAddress;
    private Button sellerRegistr , sellerHaveAccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_regestraition);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);


        sellerName = findViewById(R.id.seller_name);
        sellerPhone = findViewById(R.id.seller_phone);
        sellerEmail = findViewById(R.id.seller_email);
        sellerPassword = findViewById(R.id.seller_passwprd);
        sellerAddress = findViewById(R.id.seller_address);

        sellerRegistr = findViewById(R.id.seller_register);
        sellerHaveAccount = findViewById(R.id.already_have_account_Seller);

        sellerRegistr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SellerRegister();
            }
        });

        sellerHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerRegestraitionActivity.this , SellerLoginActivity.class);
                startActivity(intent);
            }
        });



    }

    private void SellerRegister()
    {
        String nameS = sellerName.getText().toString();
        String phoneS = sellerPhone.getText().toString();
        String emailS = sellerEmail.getText().toString();
        String passwordS = sellerPassword.getText().toString();
        String addressS = sellerAddress.getText().toString();

        if (!nameS.equals("") && !phoneS.equals("") && !emailS.equals("") && !passwordS.equals("") && !addressS.equals(""))
        {
            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage(" Waiting .. ");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            mAuth.createUserWithEmailAndPassword(emailS,passwordS)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {
                                final DatabaseReference ref_seller ;
                                ref_seller = FirebaseDatabase.getInstance()
                                        .getReference();

                                String uid = mAuth.getCurrentUser().getUid();

                                HashMap<String , Object> sellerMap = new HashMap<>();
                                sellerMap.put("sid" , uid);
                                sellerMap.put("phone" , phoneS);
                                sellerMap.put("email" , emailS);
                                sellerMap.put("address" , addressS);
                                sellerMap.put("name" , nameS);

                                ref_seller.child("Sellers").child(uid).updateChildren(sellerMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) 
                                            {


                                                    loadingBar.dismiss();
                                                    Toast.makeText(SellerRegestraitionActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent(SellerRegestraitionActivity.this , SellerHomeActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);



                                            }
                                        });

                            }
                            else
                            {
                                Toast.makeText(SellerRegestraitionActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
        }
        else
        {
            Toast.makeText(this, "Please Complete Previous Form ", Toast.LENGTH_SHORT).show();

        }



    }
}