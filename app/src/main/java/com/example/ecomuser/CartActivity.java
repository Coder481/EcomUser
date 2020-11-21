package com.example.ecomuser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.ecomuser.Models.Cart;
import com.example.ecomuser.Models.CartItem;
import com.example.ecomuser.databinding.ActivityCartBinding;
import com.example.ecomuser.databinding.CartItemViewBinding;

import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Intent intent = getIntent();
        Cart cart = (Cart) intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE);

        setupCartItemView(cart);
    }

    private void setupCartItemView(Cart cart) {
        for (Map.Entry<String, CartItem> map : cart.allCartItemsMap.entrySet()){
            CartItemViewBinding ib = CartItemViewBinding.inflate(getLayoutInflater());

            ib.cartItemName.setText(map.getValue().name);
            ib.cartItemQuantiity.setText(map.getValue().qty+" X Rs."+map.getValue().price);
            ib.cartItemPrice.setText(map.getValue().qty*map.getValue().price+"");
            b.cartItemLayout.addView(ib.getRoot());
        }
    }
}