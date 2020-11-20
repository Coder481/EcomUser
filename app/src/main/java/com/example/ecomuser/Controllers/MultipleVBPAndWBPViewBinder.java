package com.example.ecomuser.Controllers;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.ecomuser.Adapters.ProductAdapter;
import com.example.ecomuser.MainActivity;
import com.example.ecomuser.Models.Cart;
import com.example.ecomuser.Models.Product;
import com.example.ecomuser.Pickers.VariantPicker;
import com.example.ecomuser.Pickers.WeightPicker;
import android.content.DialogInterface;
import com.example.ecomuser.databinding.ProductItemSingleVbBinding;
import com.example.ecomuser.databinding.ProductItemWbOrMultiVbBinding;

import java.util.ArrayList;

public class MultipleVBPAndWBPViewBinder {
    private ProductItemWbOrMultiVbBinding b;

    private ProductAdapter adapter;
    private Product product;
    private Cart cart;


    public void bind(ProductItemWbOrMultiVbBinding b, final Product product, final Cart cart,int position){
        this.b=b;
        this.product=product;
        this.cart=cart;

        b.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });

        b.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });


    }

    private void showEditDialog() {
        if (product.type == Product.WEIGHT_BASED){
            showWeightPicker();
        }else if (product.type == Product.VARIANTS_BASED){
            showVariantPicker();
        }
    }

    private void showVariantPicker() {
        Context context = b.getRoot().getContext();

        new VariantPicker()
                .show(context, product, cart, new VariantPicker.OnVariantPickedListener() {
                    @Override
                    public void onQtyUpdated(int qty) {
                        b.addBtn.setVisibility(View.GONE);
                        b.qtyGroup.setVisibility(View.VISIBLE);

                        b.quantity.setText(qty+"");

                        updateCheckoutSummary();
                    }

                    @Override
                    public void onRemoved() {
                        b.addBtn.setVisibility(View.VISIBLE);
                        b.qtyGroup.setVisibility(View.GONE);

                        updateCheckoutSummary();
                    }
                });
    }

    private void showWeightPicker() {

        float minQ = product.minQty;
        final int KG = extractCredentialsFromFloat(minQ).get(0);
        final int GM = extractCredentialsFromFloat(minQ).get(1);

        Context context = b.getRoot().getContext();

        new WeightPicker()
                .show(context,cart,product, new WeightPicker.OnWeightPickedListener() {
            @Override
            public void onWeightPicked(int kg, int g) {
                b.addBtn.setVisibility(View.GONE);
                b.qtyGroup.setVisibility(View.VISIBLE);

                b.quantity.setText(kg+"kg "+g+"g");
                cart.updateWBPQty(product,kg+(g/1000f));

                updateCheckoutSummary();
            }

            @Override
            public void onWeightPickerCancelled() {
                cart.removeWBPFromCart(product);
                updateCheckoutSummary();
                b.addBtn.setVisibility(View.VISIBLE);
                b.qtyGroup.setVisibility(View.GONE);
            }
        },adapter,KG,GM);
    }

    private static ArrayList<Integer> extractCredentialsFromFloat(float minQ){
        ArrayList<Integer> cred = new ArrayList<>();
        final int KG ,GM;
        if (minQ<0){
            KG=0;
            cred.add(KG);
            GM=(int)(minQ*1000);
            cred.add(GM);
            return cred;
        }else{
            KG=(int)(minQ);
            cred.add(KG);
            GM=(int)((minQ-KG)*1000);
            cred.add(GM);
            return cred;
        }
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
