package com.example.ecommerce.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Model.Cart;
import com.example.ecommerce.PreValent.Prevalent;
import com.example.ecommerce.R;
import com.example.ecommerce.ViewHolderPackage.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView ;
    private RecyclerView.LayoutManager layoutManager ;
    private Button nextProcessButton ;
    private TextView txtTotalAmount , txtmsg1 ;

    private int overTotalPrice = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        nextProcessButton = findViewById(R.id.next_process_btn);

        txtTotalAmount = findViewById(R.id.total_price);
        txtmsg1 = findViewById(R.id.msg1);

        nextProcessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtTotalAmount.setText("Total Price = $ " + String.valueOf(overTotalPrice));

                Intent intent = new Intent(CartActivity.this , ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price" , String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckStateOrder();

        final DatabaseReference ref_cart = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> option = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(ref_cart.child("User View")
                        .child(Prevalent.CurrentOnlineUser.getPhone())
                        .child("Products") , Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart , CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int i, @NonNull Cart cart) {

            holder.txtProductPrice.setText( cart.getPrice() + "$");
            holder.txtProductQunatity.setText("Quantity = " + cart.getQuantity());
            holder.txtProductName.setText( cart.getPname());

            int oneTypeProductPrice = ((Integer.valueOf(cart.getPrice()))) * Integer.valueOf(cart.getQuantity());

            overTotalPrice = overTotalPrice + oneTypeProductPrice ;

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CharSequence options[] = new CharSequence[]
                            {
                              "Edit",
                              "Remove"
                            };

                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                    builder.setTitle("Cart Options : ");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(which == 0)
                            {
                                Intent intent = new Intent(CartActivity.this , ProductDetailsActivity.class);
                                intent.putExtra("pid" , cart.getPid());
                                startActivity(intent);
                            }

                            if(which == 1)
                            {
                                ref_cart.child("User View").child(Prevalent.CurrentOnlineUser.getPhone())
                                        .child("Products")
                                        .child(cart.getPid()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(CartActivity.this, "Item Removed Successfully", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(CartActivity.this , HomeActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                            }
                        }
                    });
                    builder.show();
                }
            });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout , parent , false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void CheckStateOrder()
    {
      final DatabaseReference ref_oreder ;
        ref_oreder = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.CurrentOnlineUser.getPhone());
        ref_oreder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    String shippingState = snapshot.child("state").getValue().toString();
                    String userName = snapshot.child("name").getValue().toString();

                    if(shippingState.equals("shipped"))
                    {
                        txtTotalAmount.setText("Dear : " +userName +"\n Order is Shipped Successfully .");
                        recyclerView.setVisibility(View.GONE);

                        txtmsg1.setVisibility(View.VISIBLE);
                        txtmsg1.setText("Final Order Has been placed Successully  , Soon it will be verified");
                        nextProcessButton.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, " You Can purchase New Product , Once You recive your first order .", Toast.LENGTH_SHORT).show();

                    }
                    else if(shippingState.equals("not shipped"))
                    {
                        txtTotalAmount.setText("Shipping state is Not Shipped");
                        recyclerView.setVisibility(View.GONE);

                        txtmsg1.setVisibility(View.VISIBLE);
                        nextProcessButton.setVisibility(View.GONE);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}