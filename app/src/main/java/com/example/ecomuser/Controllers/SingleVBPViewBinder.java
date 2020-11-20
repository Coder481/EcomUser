package com.example.ecomuser.Controllers;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.ecomuser.MainActivity;
import com.example.ecomuser.Models.Cart;
import com.example.ecomuser.Models.Product;
import com.example.ecomuser.databinding.ProductItemSingleVbBinding;

public class SingleVBPViewBinder {
    private ProductItemSingleVbBinding b;

    public void bind(ProductItemSingleVbBinding b,final Product product, final Cart cart){
        this.b=b;

        b.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cart.addVBPToCart(product,product.variants.get(0));
                updateQtyView(1);
            }
        });

        b.incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = cart.addVBPToCart(product,product.variants.get(0));
                updateQtyView(qty);
            }
        });

        b.decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = cart.removeVBPFromCart(product,product.variants.get(0));
                updateQtyView(qty);
            }
        });
    }

    private void updateQtyView(int qty) {
        if (qty==0){
            b.addBtn.setVisibility(View.VISIBLE);
            b.qtyGroup.setVisibility(View.GONE);
        }else if (qty==1){
            b.addBtn.setVisibility(View.GONE);
            b.qtyGroup.setVisibility(View.VISIBLE);
        }

        b.quantity.setText(qty+"");
        updateCheckoutSummary();
    }

    private void updateCheckoutSummary() {
        Context context = b.getRoot().getContext(); // We can get context from binding using any view (here we take root)
        if(context instanceof MainActivity){
            ((MainActivity) context).updateCartSummary();
        }else{
            Toast.makeText(context, "Something went Wrong!!", Toast.LENGTH_SHORT).show();
        }
    }

}
