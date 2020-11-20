package com.example.ecomuser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import com.example.ecomuser.Adapters.ProductAdapter;
import com.example.ecomuser.Models.Cart;
import com.example.ecomuser.Models.Product;
import com.example.ecomuser.Models.Variant;
import com.example.ecomuser.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;
    private Cart cart = new Cart();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setupProductsList();

        setupCheckout();
    }

    private void setupCheckout() {
        b.checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Cart Summary")
                        .setMessage(cart.allCartItemsMap.toString())
                        .show();
            }
        });
    }

    private void setupProductsList() {
        // Create DataSet
        List<Product> products = getProducts();
        // Create adapter object
        ProductAdapter adapter = new ProductAdapter(this,products,cart);

        // Set the adapter & LayoutManager to recyclerView
        b.recyclerView.setAdapter(adapter);
        b.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Divider Line
        b.recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));


    }

    private List<Product> getProducts() {
        return Arrays.asList(
                new Product("Bread",Arrays.asList(
                        new Variant("Small",25)
                        ,new Variant("Medium",35)
                        ,new Variant("Big",45)
                ))
                , new Product("Mango",80,2.270f)
                ,new Product("Kiwi",Arrays.asList(
                        new Variant("500g",100)
                ))
        );
    }

    public void updateCartSummary() {
        if (cart.totalNoOfItems == 0){
            b.checkout.setVisibility(View.GONE);
        }else{
            b.checkout.setVisibility(View.VISIBLE);
        }
        b.cartSummary.setText("Total Rs. "+cart.totalPrice+"\nTotal items: "+cart.totalNoOfItems);
    }

}