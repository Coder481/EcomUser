package com.example.ecomuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.ecomuser.Adapters.ProductAdapter;
import com.example.ecomuser.Models.Cart;
import com.example.ecomuser.Models.CartItem;
import com.example.ecomuser.Models.Inventory;
import com.example.ecomuser.Models.Order;
import com.example.ecomuser.Models.Product;
import com.example.ecomuser.fcmsender.FCMSender;
import com.example.ecomuser.fcmsender.MsgFormatter;
import com.example.ecomuser.databinding.ActivityMainBinding;
import com.example.ecomuser.databinding.ProductItemSingleVbBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.google.firebase.Timestamp;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;
    private Cart cart = new Cart();

    public static final String EXTRA_MESSAGE = "com.example.android.ecomuser.extra.MESSAGE";

    // Shared Preferences
    private SharedPreferences mSharedPref;
    private final String MY_DATA = "myData";

    Product product;
    private CartItem cartItem;
    private MyApp app;
    private List<Product> products;
    private String username;
    private String myEmailId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        app = (MyApp) getApplicationContext();

        Intent signInIntent = getIntent();
        myEmailId = signInIntent.getStringExtra("MyId");
        username = signInIntent.getStringExtra("Username");

        setupTopic();
        loadSavedData();
        setupCheckout();

    }

    private void setupTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("users");
    }


    /** Load Data From Firebase **/
    private void loadSavedData(){
        if (app.isOffline()){
            app.showToast(this,"You are offline \n Check your connectivity");
            return;
        }

        app.showLoadingDialog(this);

        app.db.collection("Inventory").document("Products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            Inventory inventory = documentSnapshot.toObject(Inventory.class);
                            products = inventory.products;
                        }else{
                            products = new ArrayList<>();
                        }
                        setupProductsList();
                        app.hideLoadingDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        app.showToast(MainActivity.this,"Failed to get Data!!");
                        app.hideLoadingDialog();
                    }
                });



        /*Gson gson = new Gson();
        mSharedPref = getSharedPreferences("product_data",MODE_PRIVATE);
        String json = mSharedPref.getString(MY_DATA,null);
        if (json!=null) {
            setupViewsSharedPref(gson, json);
        } else
            cart.allCartItemsMap = new HashMap<>();*/
    }


    /** Product **/
    private void setupProductsList() {

        // Create adapter object
        ProductAdapter adapter = new ProductAdapter(this,products,cart);

        // Set the adapter & LayoutManager to recyclerView
        b.recyclerView.setAdapter(adapter);
        b.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Divider Line
        b.recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));


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

    private void setupIntent() {
        Intent intent = new Intent(this, CartActivity.class);


        intent.putExtra(EXTRA_MESSAGE, cart).putExtra("myEmailId",myEmailId).putExtra("user",username);

        startActivity(intent);
    }  // Intent (Cart Activity)





    /** Options Menu For SignOut **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_options_menu,menu);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("Hi "+username);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signout_btn){
            askForConfirmation();

        }

        return super.onOptionsItemSelected(item);
    }

    private void askForConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure to sign out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeIdFromSharedPref();
                        setupSignOut();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        app.showToast(MainActivity.this,"Cancelled!");
                    }
                })
                .show();
    }

    private void removeIdFromSharedPref() {
        mSharedPref = getSharedPreferences("signInId",MODE_PRIVATE);
        mSharedPref.edit().remove(SignInActivity.MY_ID)
                .remove("username")
                .apply();
    }

    private void setupSignOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        app.showToast(MainActivity.this,"SIGNED OUT!");
                        finish();
                    }
                });
    }

    private void moveToHomeScreen() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // TODO :
        startActivity(a);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Do you want to sign-out or just leave the app?")
                .setPositiveButton("Leave App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveToHomeScreen();
                    }
                })
                .setNegativeButton("Sign Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        askForConfirmation();
                    }
                })
                .show();
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



}