package com.hbernabe.loots.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hbernabe.loots.Interface.ItemClickListener;
import com.hbernabe.loots.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName, txtProductDescription, txtProductPrice;
    public ImageView imageView;
    public ItemClickListener listner;


    public ProductViewHolder(View itemView)
    {
        super(itemView);


        imageView = (ImageView) itemView.findViewById(R.id.prodImage);
        txtProductName = (TextView) itemView.findViewById(R.id.prodName);
        txtProductDescription = (TextView) itemView.findViewById(R.id.prodDescription);
        txtProductPrice = (TextView) itemView.findViewById(R.id.prodPrice);
    }

    public void setItemClickListner(ItemClickListener listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onClick(view, getAdapterPosition(), false);
    }
}

