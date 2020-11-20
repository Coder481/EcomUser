package com.example.ecomuser.Pickers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.example.ecomuser.Models.Cart;
import com.example.ecomuser.Models.Product;
import com.example.ecomuser.Models.Variant;
import com.example.ecomuser.databinding.VariantItemBinding;
import com.example.ecomuser.databinding.VariantPickerDialogLayoutBinding;

public class VariantPicker {
    private VariantPickerDialogLayoutBinding b;
    private Cart cart;
    private Product product;
    private Context context;


    public void show(Context context, final Product product, final Cart cart,final OnVariantPickedListener listener){
        b = VariantPickerDialogLayoutBinding.inflate(LayoutInflater.from(context));
        this.cart=cart;
        this.context=context;
        this.product=product;


        new AlertDialog.Builder(context)
                .setTitle(product.name)
                .setCancelable(false)
                .setView(b.getRoot())
                .setPositiveButton("SELECT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int qty = cart.totalVariantsQtyMap.get(product.name);
                        if (qty>0)
                            listener.onQtyUpdated(qty);
                        else
                            listener.onRemoved();
                    }
                })
                .setNegativeButton("REMOVE ALL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cart.removeAllVariantsFromCart(product);
                        listener.onRemoved();
                    }
                })
                .show();
        showVariants();
    }

    private void showVariants() {
        for (Variant variant : product.variants){
            // Inflate
            VariantItemBinding ib = VariantItemBinding.inflate(
                    /*Inflater*/LayoutInflater.from(context)
                    , /*Parent*/b.getRoot()
                    ,/*attach to Parent*/true);

            // Bind data
            ib.variantName.setText(variant.nameAndPriceString());

            // Setup buttons
            setupButtons(variant,ib);

            // Showing previous Qty
            showPreviousQty(variant,ib);
        }
    }

    private void showPreviousQty(Variant variant, VariantItemBinding ib) {
        int qty = cart.getVariantQty(product,variant);
        if (qty>0){
            ib.variantDecrementBtn.setVisibility(View.VISIBLE);
            ib.variantQuantity.setVisibility(View.VISIBLE);
            ib.variantQuantity.setText(qty+"");
        }
    }

    private void setupButtons(Variant variant, VariantItemBinding ib) {
        ib.variantIncrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update qtyView
                int qty = cart.addVBPToCart(product,variant);
                ib.variantQuantity.setText(qty+"");

                if (qty>=1){
                    ib.variantQuantity.setVisibility(View.VISIBLE);
                    ib.variantDecrementBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        ib.variantDecrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update qtyView
                int qty = cart.removeVBPFromCart(product,variant);
                ib.variantQuantity.setText(qty+"");

                if (qty==0){
                    ib.variantQuantity.setVisibility(View.GONE);
                    ib.variantDecrementBtn.setVisibility(View.GONE);
                }
            }
        });
    }


    public interface OnVariantPickedListener{
        void onQtyUpdated(int qty);
        void onRemoved();
    }
}
