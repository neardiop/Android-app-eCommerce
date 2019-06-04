package com.emst.cours.ecommerce.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.emst.cours.ecommerce.R;
import com.emst.cours.ecommerce.interfaces.ItemClickListner;

public class CardView extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName , txtProductPrice ,txtProductQuantity;
    private ItemClickListner itemClickListner;

    public CardView(@NonNull View itemView) {
        super(itemView);
        txtProductName = itemView.findViewById(R.id.card_product_name);
        txtProductPrice = itemView.findViewById(R.id.card_product_price);
        txtProductQuantity = itemView.findViewById(R.id.card_product_quantity);
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v, getAdapterPosition() ,false);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }
}