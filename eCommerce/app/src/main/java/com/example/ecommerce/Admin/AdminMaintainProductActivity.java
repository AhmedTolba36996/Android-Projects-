package com.example.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.example.ecommerce.Seller.SellerProductCategoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductActivity extends AppCompatActivity {

    private Button applyChangesBtn , deletProductButton ;
    private EditText name , price , descrption ;
    private ImageView imageView ;
    private String productID = "";
    private DatabaseReference ref_products;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_product);

        applyChangesBtn = findViewById(R.id.product_mainTain_btn_applyChanges);
        name = findViewById(R.id.product_mainTain_name);
        price = findViewById(R.id.product_mainTain_price);
        descrption = findViewById(R.id.product_mainTain_description);
        imageView = findViewById(R.id.product_mainTain_image);
        deletProductButton = findViewById(R.id.product_mainTain_btn_Delet);

        productID = getIntent().getStringExtra("pid");
        ref_products = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        dispalySpecificProductInfo();

        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplyChanges();
            }
        });

        deletProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeletThisProduct();
            }
        });




    }

    private void dispalySpecificProductInfo()
    {
        ref_products.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                if(datasnapshot.exists())
                {
                    String pName = datasnapshot.child("pname").getValue().toString();
                    String pPrice = datasnapshot.child("price").getValue().toString();
                    String pDescription = datasnapshot.child("description").getValue().toString();
                    String pImage = datasnapshot.child("image").getValue().toString();

                    name.setText(pName);
                    price.setText(pPrice);
                    descrption.setText(pDescription);
                    Picasso.get().load(pImage).into(imageView);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void ApplyChanges()
    {
        String pName = name.getText().toString();
        String pPrice = price.getText().toString();
        String pDescription = descrption.getText().toString();

        if(pName.equals(""))
        {
            Toast.makeText(this, "Write Product Name", Toast.LENGTH_SHORT).show();
        }
        else if (pPrice.equals(""))
        {
            Toast.makeText(this, "Write Product Price", Toast.LENGTH_SHORT).show();
        }
        else if (pDescription.equals(""))
        {
            Toast.makeText(this, "Write Product Description", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, Object> productMap = new HashMap<>();

            productMap.put("pid", productID);
            productMap.put("description", pDescription);
            productMap.put("price", pPrice);
            productMap.put("pname", pName);

            ref_products.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful())
                    {
                        Toast.makeText(AdminMaintainProductActivity.this, "Changed Successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AdminMaintainProductActivity.this , SellerProductCategoryActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            });
        }

    }

    private void DeletThisProduct()
    {
        ref_products.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    Intent intent = new Intent(AdminMaintainProductActivity.this , SellerProductCategoryActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(AdminMaintainProductActivity.this, "Removed Succssefully", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



}