package com.example.ecommerce.Buyers;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.InterFace.ItemClickListner;
import com.example.ecommerce.R;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName , txtProductDescription , txtProductPrice , txtProductStates ;
    public ImageView imageView ;
    public ItemClickListner listner;

    public ItemViewHolder(@NonNull View itemView)
    {

        super(itemView);

        imageView = itemView.findViewById(R.id.product_seller_image);
        txtProductName = itemView.findViewById(R.id.product_seller_name);
        txtProductPrice = itemView.findViewById(R.id.product_seller_price);
        txtProductDescription = itemView.findViewById(R.id.product_seller_description);
        txtProductStates = itemView.findViewById(R.id.product_seller_state);

    }


    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }
    @Override
    public void onClick(View v)
    {
        listner.onClick(v , getAdapterPosition(),false);
    }

}
