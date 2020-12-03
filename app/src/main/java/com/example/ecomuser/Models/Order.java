package com.example.ecomuser.Models;

import com.google.firebase.Timestamp;
import java.util.List;

public class Order {

    public int status;
    public String orderId;
    public Timestamp orderPlacedTs;
    public String userName, userPhoneNo , userAddress;

    public List<CartItem> cartItemList;
    public int totlItems , totlPrice;


    public static class OrderStatus {
        public static final int PLACED = 0 // Initially(U)
                , DELIVERED = 1 , DECLINED = -1;  // (A)
    }

}
