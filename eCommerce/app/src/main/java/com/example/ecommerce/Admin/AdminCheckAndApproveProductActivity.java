package com.example.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ecommerce.Buyers.ResetPasswordActivity;
import com.example.ecommerce.InterFace.ItemClickListner;
import com.example.ecommerce.Model.Products;
import com.example.ecommerce.R;
import com.example.ecommerce.ViewHolderPackage.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminCheckAndApproveProductActivity extends AppCompatActivity {

    private RecyclerView recyclerView ;
    RecyclerView.LayoutManager layoutManager ;
    private DatabaseReference ref_verifiy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_and_approve_product);

        recyclerView = findViewById(R.id.admin_products_checkList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ref_verifiy = FirebaseDatabase.getInstance().getReference().child("Products");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ref_verifiy.orderByChild("ProductState").equalTo("Not Approved") ,Products.class )
                .build();

        FirebaseRecyclerAdapter<Products , ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull Products model) {

                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Price = " + model.getPrice() + "$");
                        Picasso.get().load(model.getImage()).into(holder.imageViewProduct);


                      holder.itemView.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {

                              final String productID = model.getPid();

                              CharSequence options[] = new CharSequence[]
                                      {
                                              "yes",
                                              "no"
                                      };
                              AlertDialog.Builder builder = new AlertDialog.Builder(AdminCheckAndApproveProductActivity.this );
                              builder.setTitle("Do You Want to Approve this Product ? ");
                              builder.setItems(options, new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int which) {

                                      if(which == 0)
                                      {
                                          ChangeProductState(productID);
                                      }
                                      if(which == 1)
                                      {

                                      }


                                  }
                              });
                              builder.show();

                          }
                      });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout , parent , false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;

                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void ChangeProductState(String productID)
    {
        ref_verifiy.child(productID)
                .child("ProductState").setValue("Approved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(AdminCheckAndApproveProductActivity.this, "Approved , it Product Will Available for sale ", Toast.LENGTH_SHORT).show();

            }
        });
    }
}