package com.example.ecommerce.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ecommerce.Buyers.HomeActivity;
import com.example.ecommerce.Buyers.MainActivity;
import com.example.ecommerce.R;
import com.google.firebase.database.DatabaseReference;

public class AdminHomeActivity extends AppCompatActivity {
    private Button logOutAdminBtuuton , checkOrderButton , mainTainProductBtn , checkApproveProduct ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);



        logOutAdminBtuuton = findViewById(R.id.log_out_admin_btn);
        checkOrderButton = findViewById(R.id.check_new_order_btn);
        mainTainProductBtn = findViewById(R.id.maintain_Button_Product);
        checkApproveProduct = findViewById(R.id.check_approve_order_btn);

        mainTainProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this , HomeActivity.class);
                intent.putExtra("Admin" , "Admin");
                startActivity(intent);
            }
        });

        logOutAdminBtuuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this , MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        checkOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this , AdminNewOrderActivity.class);
                startActivity(intent);
            }
        });

        checkApproveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdminHomeActivity.this , AdminCheckAndApproveProductActivity.class);
                startActivity(intent);

            }
        });




    }
}