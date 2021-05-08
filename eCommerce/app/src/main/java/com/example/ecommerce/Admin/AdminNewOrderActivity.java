package com.example.ecommerce.Admin;

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

import com.example.ecommerce.Model.AdminOrders;
import com.example.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrderActivity extends AppCompatActivity {

    private RecyclerView orderListrecycler;
    private DatabaseReference ref_order;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);

        ref_order = FirebaseDatabase.getInstance().getReference().child("Orders");
        orderListrecycler = findViewById(R.id.recycler_order);
        orderListrecycler.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {

        FirebaseRecyclerOptions<AdminOrders> options = new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ref_order,AdminOrders.class)
                .build();
        FirebaseRecyclerAdapter<AdminOrders , AdminOrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrderViewHolder holder, int i, @NonNull AdminOrders adminOrders) {

                        holder.username.setText("Name : " + adminOrders.getName());
                        holder.userdatetime.setText("Order At : " + adminOrders.getDate() + " " + adminOrders.getTime()  );
                        holder.userphone.setText("Phone : " + adminOrders.getPhone());
                        holder.usershippenaddress.setText("Address : " +adminOrders.getCity() +" "+adminOrders.getHome());
                        holder.usertotalprice.setText("Total Pice = $" + adminOrders.getTotalAmount());

                        holder.showorderbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String uID = getRef(i).getKey();
                                Intent intent = new Intent(AdminNewOrderActivity.this , AdminUserProductsActivity.class);
                                intent.putExtra("uid" , uID);


                                startActivity(intent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrderActivity.this);
                                builder.setTitle("Have You Shipped order Product ? ");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(which == 0)
                                        {
                                            String uID = getRef(i).getKey();
                                            RemoveOreder(uID);
                                            Toast.makeText(AdminNewOrderActivity.this, "Item Removed", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            finish();
                                        }

                                    }
                                });

                                builder.show();

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout , parent , false);

                        return new AdminOrderViewHolder(view);

                    }
                };
        orderListrecycler.setAdapter(adapter);
        adapter.startListening();



        super.onStart();
    }

    public static class AdminOrderViewHolder extends RecyclerView.ViewHolder
    {
        public TextView username , userphone , usertotalprice , userdatetime ,usershippenaddress ;
        public Button showorderbutton;

        public AdminOrderViewHolder(@NonNull View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.order_user_name);
            userphone = itemView.findViewById(R.id.order_phone_number);
            usertotalprice = itemView.findViewById(R.id.order_total_price);
            userdatetime = itemView.findViewById(R.id.order_date_time);
            usershippenaddress = itemView.findViewById(R.id.order_city_address);
            showorderbutton = itemView.findViewById(R.id.show_order_product);



        }
    }


    private void RemoveOreder(String uID)
    {
    ref_order.child(uID).removeValue();


    }


}