package com.example.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ecommerce.Model.Products;
import com.example.ecommerce.PreValent.Prevalent;
import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private FloatingActionButton addToCartBtn ;
    private ImageView productImage ;
    private ElegantNumberButton numberButton;
    private TextView productPrice , productDescription , productName;

    private String productID = "";
    private String state = "Normal";
    private String shippingState ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID = getIntent().getStringExtra("pid");

        addToCartBtn = findViewById(R.id.add_product_to_cart_btn);
        productImage = findViewById(R.id.product_image_details);
        numberButton = findViewById(R.id.number_btn);

        productPrice = findViewById(R.id.product_price_details);
        productDescription = findViewById(R.id.product_description_details);
        productName = findViewById(R.id.product_name_details);

        GetProdutDetails(productID);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(state.equals("Order Placed") || state.equals("Order Shipped") )
                {
                    Toast.makeText(ProductDetailsActivity.this, "You Can purchase New Product , Once You recive your first order", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    AddingToCartList();
                }

            }
        });

    }

    private void GetProdutDetails(String productID)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Products");
        ref.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                if(datasnapshot.exists())
                {
                    Products products = datasnapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productDescription.setText(products.getDescription());
                    productPrice.setText(products.getPrice());
                    Picasso.get().load(products.getImage()).into(productImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckStateOrder();
    }

    private void AddingToCartList()
    {

        String saveCurrentDate , saveCurrentTime;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentData = new SimpleDateFormat("MMM dd , yyyy");
        saveCurrentDate = currentData.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat(" HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String , Object> cartMap = new HashMap<>();
        cartMap.put("pid" , productID ) ;
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", numberButton.getNumber());
        cartMap.put("discount", "");

        cartListRef.child("User View").child(Prevalent.CurrentOnlineUser.getPhone())
                .child("Products").child(productID)
                .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    cartListRef.child("Admin View").child(Prevalent.CurrentOnlineUser.getPhone())
                            .child("Products").child(productID)
                            .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                Toast.makeText(ProductDetailsActivity.this, "Added To Cart", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ProductDetailsActivity.this , HomeActivity.class);
                                startActivity(intent);
                            }

                        }
                    });
                }
            }
        });

    }

    private void CheckStateOrder()
    {
        DatabaseReference ref_oreder ;
        ref_oreder = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.CurrentOnlineUser.getPhone());
        ref_oreder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                if(snapshot.child("state").exists())
                {
                    shippingState = snapshot.child("state").getValue().toString();
                    if(snapshot.exists())
                    {

                        if(shippingState.equals("shipped"))
                        {
                            state = "Order Shipped";
                        }
                        else if(shippingState.equals("not shipped"))
                        {
                            state = "Order Placed";
                        }

                    }
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




}