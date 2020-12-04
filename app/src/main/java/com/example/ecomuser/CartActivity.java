package com.example.ecomuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.ecomuser.Models.Cart;
import com.example.ecomuser.Models.CartItem;
import com.example.ecomuser.Models.Order;
import com.example.ecomuser.Models.Product;
import com.example.ecomuser.databinding.ActivityCartBinding;
import com.example.ecomuser.databinding.CartItemViewBinding;
import com.example.ecomuser.fcmsender.FCMSender;
import com.example.ecomuser.fcmsender.MsgFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding b;
    private Cart cart;
    Product product;

    // Intent
    public static final String EXTRA_REPLY = "com.example.android.ecomuser.extra.REPLY";
    private MyApp app;
    private String myEmail;
    private Order order;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        app = (MyApp) getApplicationContext();

        // Intent
        Intent intent = getIntent();
        cart = (Cart) intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE);
        myEmail = intent.getStringExtra("myEmailId");
        username = intent.getStringExtra("user");
        setupCartItemView(cart);

        setupOrderTextview(cart);
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
            setupDeleteBtn(ib,cart,map);
            b.cartItemLayout.addView(ib.getRoot());

        }

    }

    private void setupDeleteBtn(CartItemViewBinding ib, Cart cart, Map.Entry<String,CartItem> map) {
        ib.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.cartItemLayout.removeView(ib.getRoot());
                cart.allCartItemsMap.remove(map.getKey());

                if (!(map.getValue().name.contains(" ")) ){
                    cart.totalPrice -= map.getValue().price;
                    cart.totalNoOfItems -= 1;
                } else{
                    cart.totalPrice -= map.getValue().qty*map.getValue().price;
                    cart.totalNoOfItems -= map.getValue().qty;
                }
                setupOrderTextview(cart);
            }
        });

    }

    private void setupOrderTextview(Cart cart) {
        b.orderTextview.setText("     Items:"+cart.totalNoOfItems+"     Price: Rs."+cart.totalPrice);
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY,cart);
        setResult(RESULT_OK,replyIntent);
        finish();
    }*/

    // To remove up navigation  button in cart activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_cart_options_menu,menu);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.orderBtn){
            setupOrderBtn();
        }

        return super.onOptionsItemSelected(item);
    }



    /** Order Button **/
    private void setupOrderBtn() {
        new AlertDialog.Builder(CartActivity.this)
                .setTitle("Are you sure to make order?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        makeOrder(myEmail);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(CartActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void makeOrder(String myEmailId) {

        List<CartItem> cartItems = new ArrayList<>(cart.allCartItemsMap.values());
        Timestamp timestamp = new Timestamp(new Date());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String format = simpleDateFormat.format(new Date());


        order = new Order();
        order.totlItems  = cart.totalNoOfItems;
        order.totlPrice = cart.totalPrice;
        order.userAddress = myEmailId;
        order.cartItemList = cartItems;
        order.userName = username;
        order.orderId = format+"\n"+order.userAddress;
        order.orderPlacedTs = timestamp;

        /*orderMap.put("customerId",myEmailId);



        orderMap.put("Items Ordered",cartItems);
        orderMap.put("Total items",cart.totalNoOfItems);
        orderMap.put("Total price",cart.totalPrice);*/


        app.db.collection("Orders").document(format+"\n"+myEmailId)
                .set(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendNotification();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        app.showToast(CartActivity.this,"Error!  Order cannot be saved..");
                    }
                });
    }

    private void sendNotification() {

        String message = MsgFormatter.getSampleMessage("admin","New Order","From:"+order.userAddress);

        new FCMSender().send(message
                , new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CartActivity.this, "Failure!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(CartActivity.this)
                                        .setTitle("Order Successfully placed!")
                                        .setMessage(response.toString())
                                        .show();
                            }
                        });
                    }
                });
    }
}