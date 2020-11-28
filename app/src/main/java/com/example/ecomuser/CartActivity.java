package com.example.ecomuser;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.example.ecomuser.Models.Cart;
import com.example.ecomuser.Models.CartItem;
import com.example.ecomuser.Models.Product;
import com.example.ecomuser.databinding.ActivityCartBinding;
import com.example.ecomuser.databinding.CartItemViewBinding;

import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding b;
    private Cart cart;
    Product product;

    // Intent
    public static final String EXTRA_REPLY = "com.example.android.ecomuser.extra.REPLY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // Intent
        Intent intent = getIntent();
        cart = (Cart) intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE);
        setupCartItemView(cart);


    }

    private void setupCartItemView(Cart cart) {
        for (Map.Entry<String, CartItem> map : cart.allCartItemsMap.entrySet()){
            CartItemViewBinding ib = CartItemViewBinding.inflate(getLayoutInflater());

            ib.cartItemName.setText(map.getValue().name);

            if (!(map.getValue().name.contains(" ")) ){
                ib.cartItemQuantiity.setText(map.getValue().qty+" X Rs."+(map.getValue().price)/map.getValue().qty);
                ib.cartItemPrice.setText("Rs. "+map.getValue().price);
            }else{
                ib.cartItemQuantiity.setText(map.getValue().qty+" X Rs."+map.getValue().price);
                ib.cartItemPrice.setText("Rs. "+map.getValue().qty*map.getValue().price);
            }

            // Setting up delete button
            setupDeleteBtn(ib,cart,map.getKey());
            b.cartItemLayout.addView(ib.getRoot());

        }
    }

    private void setupDeleteBtn(CartItemViewBinding ib, Cart cart, String key) {
        ib.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.cartItemLayout.removeView(ib.getRoot());
                cart.allCartItemsMap.remove(key);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY,cart);
        setResult(RESULT_OK,replyIntent);
        finish();
    }

    // To remove up navigation  button in cart activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        return super.onCreateOptionsMenu(menu);
    }
}