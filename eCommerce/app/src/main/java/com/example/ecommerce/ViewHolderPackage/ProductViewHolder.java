package com.example.ecommerce.ViewHolderPackage;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.InterFace.ItemClickListner;
import com.example.ecommerce.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName , txtProductDescription , txtProductPrice ;
    public ImageView imageViewProduct ;
    public ItemClickListner listner;

    public ProductViewHolder(@NonNull View itemView)
    {
        super(itemView);

        txtProductName = itemView.findViewById(R.id.product_name);
        txtProductDescription = itemView.findViewById(R.id.product_description);
        txtProductPrice = itemView.findViewById(R.id.product_price);
        imageViewProduct = itemView.findViewById(R.id.product_image);
    }

    public void setItemClickListener(ItemClickListner listener)
    {
        this.listner = listener;
    }

    @Override
    public void onClick(View v)
    {

        listner.onClick(v , getAdapterPosition() , false);

    }


}
