package com.example.ecomuser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ecomuser.Adapters.ProductAdapter;
import com.example.ecomuser.Controllers.SingleVBPViewBinder;
import com.example.ecomuser.Models.Cart;
import com.example.ecomuser.Models.CartItem;
import com.example.ecomuser.Models.Product;
import com.example.ecomuser.Models.Variant;
import com.example.ecomuser.databinding.ActivityMainBinding;
import com.example.ecomuser.databinding.ProductItemSingleVbBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;
    private Cart cart = new Cart();

    public static final String EXTRA_MESSAGE = "com.example.android.ecomuser.extra.MESSAGE";

    // Shared Preferences
    private SharedPreferences mSharedPref;
    private final String MY_DATA = "myData";

    Product product;
    private CartItem cartItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        //loadSavedData();

        setupProductsList();

        setupCheckout();
    }

    /** Shared Preferences **/
    /*@Override
    protected void onPause() {
        super.onPause();
        saveData();
    }*/

    private void saveData() {
        mSharedPref = getSharedPreferences("product_data",MODE_PRIVATE);
        Gson gson = new Gson();
        mSharedPref.edit()
                .putString(MY_DATA,gson.toJson(cart.allCartItemsMap))
                .putInt("singleVBPQty",(int)(cart.allCartItemsMap.get(product.name+" "+product.variants.get(0)).qty))
                .putString("totalVariantsQtyMap",gson.toJson(cart.totalVariantsQtyMap))
                .putInt("totalPrice",cart.totalPrice)
                .putInt("totalNoOfItems",cart.totalNoOfItems)
                .apply();
    }

    private void loadSavedData(){
        Gson gson = new Gson();
        mSharedPref = getSharedPreferences("product_data",MODE_PRIVATE);
        String json = mSharedPref.getString(MY_DATA,null);
        if (json!=null) {
            setupViewsSharedPref(gson, json);
        } else
            cart.allCartItemsMap = new HashMap<>();
    }

    private void setupViewsSharedPref(Gson gson, String json) {

        ProductItemSingleVbBinding b = ProductItemSingleVbBinding.inflate(getLayoutInflater());
        cart.allCartItemsMap = gson.fromJson(json,new TypeToken<Map<String, CartItem>>(){}.getType());
        //String key = cartItem.name;
        int qty = mSharedPref.getInt("singleVBPQty",0);
        cart.totalNoOfItems = mSharedPref.getInt("totalNoOfItems",0);
        cart.totalPrice = mSharedPref.getInt("totalPrice",0);

        b.addBtn.setVisibility(View.GONE);
        b.qtyGroup.setVisibility(View.VISIBLE);
        b.quantity.setText(qty+"");
        Context context = b.getRoot().getContext(); // We can get context from binding using any view (here we take root)
        if(context instanceof MainActivity){
            ((MainActivity) context).updateCartSummary();
        }else{
            Toast.makeText(context, "Something went Wrong!!", Toast.LENGTH_SHORT).show();
        }

    }


    /** Product **/
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


    /** Intent (New Activity)**/
    private void setupIntent() {
        Intent intent = new Intent(this, CartActivity.class);


        intent.putExtra(EXTRA_MESSAGE, cart);

        startActivity(intent);
    }


    /** Cart Summary **/
    private void setupCheckout() {
        b.checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Cart Summary")
                        .setMessage(cart.allCartItemsMap.toString())
                        .show();*/
                setupIntent();
            }
        });
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