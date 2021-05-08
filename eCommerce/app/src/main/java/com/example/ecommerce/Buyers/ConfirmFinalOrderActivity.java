package com.example.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerce.PreValent.Prevalent;
import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditText , phoneEditText , homeEditText , cityEditText ;
    private Button conformDataOrder ;

    private String totalAmount ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price = " + totalAmount, Toast.LENGTH_SHORT).show();

        nameEditText = findViewById(R.id.nameConfirmFinalOrder);
        phoneEditText = findViewById(R.id.numbperPhoneConfirmFinalOrder);
        homeEditText = findViewById(R.id.adressHomeConfirmFinalOrder);
        cityEditText = findViewById(R.id.addressCityConfirmFinalOrder);

        conformDataOrder = findViewById(R.id.confirm_final_order_btn);

        conformDataOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check();
            }
        });
    }

    private void Check()
    {
        if(TextUtils.isEmpty(nameEditText.getText().toString()))
        {
            Toast.makeText(this, "Enter Your Name ..", Toast.LENGTH_SHORT).show();
        }
        else if ( TextUtils.isEmpty(phoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Enter Your Number Phone", Toast.LENGTH_SHORT).show();
        }
        else if ( TextUtils.isEmpty(homeEditText.getText().toString()))
        {
            Toast.makeText(this, "Enter Your Home Address", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cityEditText.getText().toString()))
        {
            Toast.makeText(this, "Enter Your City", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder()
    {
        Calendar calendar = Calendar.getInstance();

        final String saveCurrentDate , saveCurrentTime;

        SimpleDateFormat currentData = new SimpleDateFormat("MMM dd , yyyy");
        saveCurrentDate = currentData.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat(" HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        final DatabaseReference ref_order = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.CurrentOnlineUser.getPhone());

        HashMap<String , Object> orderHashMap = new HashMap<>();
        orderHashMap.put("totalAmount" , totalAmount);
        orderHashMap.put("name" , nameEditText.getText().toString());
        orderHashMap.put("phone" , phoneEditText.getText().toString());
        orderHashMap.put("home" , homeEditText.getText().toString());
        orderHashMap.put("city" , cityEditText.getText().toString());
        orderHashMap.put("date" , saveCurrentDate);
        orderHashMap.put("time" , saveCurrentTime);
        orderHashMap.put("state" , "not shipped");

        ref_order.updateChildren(orderHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View")
                            .child(Prevalent.CurrentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Order Has been Successfully", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this , HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }

            }
        });


    }

}