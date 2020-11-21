package com.example.ecomuser.Models;

import android.os.Parcelable;

import com.example.ecomuser.Models.CartItem;
import com.example.ecomuser.Models.Product;
import com.example.ecomuser.Models.Variant;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Cart implements Serializable {

    // Map to save all CartItems by their name as key
    public Map<String,CartItem> allCartItemsMap = new HashMap<>();

    // Map to save total quantity of Variant's item
    public Map<String,Integer> totalVariantsQtyMap = new HashMap<>();

    public int totalNoOfItems, totalPrice;



    /** Variant Based Products **/

    public int addVBPToCart(Product product,Variant variant){
        String key = product.name+" " + variant.name;

        // Adding variantBased Product to Cart
        if(allCartItemsMap.containsKey(key)){
            allCartItemsMap.get(key).qty++;
        }else {
            //product.variants.add(variant);
            allCartItemsMap.put(key,new CartItem(key,variant.price));
        }


        totalNoOfItems++;
        totalPrice += variant.price;

        // Add vbProduct quantity to Map
        if(totalVariantsQtyMap.containsKey(product.name)){
            totalVariantsQtyMap.put(product.name,totalVariantsQtyMap.get(product.name)+1);
        }else {
            totalVariantsQtyMap.put(product.name,1);
        }

        return (int) allCartItemsMap.get(key).qty;
    }

    public int removeVBPFromCart(Product product,Variant variant){
        String key = product.name+" "+variant.name;

        // Remove item from cart
        allCartItemsMap.get(key).qty--;
        // checking for completely removal of item from cart
        if(allCartItemsMap.get(key).qty == 0){
            //product.variants.remove(variant);
            allCartItemsMap.remove(key);
        }



        totalNoOfItems--;
        totalPrice -= variant.price;

        // Remove vbProduct quantity from map
        int qty = totalVariantsQtyMap.get(product.name)-1;
        totalVariantsQtyMap.put(product.name,qty);
        // checking for completely removal
        if (totalVariantsQtyMap.get(product.name)==0){
            totalVariantsQtyMap.remove(key);
        }

        return allCartItemsMap.containsKey(key)? (int) allCartItemsMap.get(key).qty : 0;}

    // Remove all the vbProduct items from cart (when "Remove All" button is pressed)
    public void removeAllVariantsFromCart(Product product){

        for (Variant variant: product.variants){
            String key = product.name+" "+variant.name;

            if (allCartItemsMap.containsKey(key)){
                totalNoOfItems -= allCartItemsMap.get(key).qty;
                totalPrice -= (allCartItemsMap.get(key).price)*(allCartItemsMap.get(key).qty);
                allCartItemsMap.remove(key);
            }
        }

        if (totalVariantsQtyMap.containsKey(product.name)){
            totalVariantsQtyMap.remove(product.name);
        }
    }



    /** Weight Based Products **/

    public void updateWBPQty(Product product, float qty){
        // Calculate new price
        int newPrice = (int) (product.pricePerkg*qty);

        // Subtract previous price
        if (allCartItemsMap.containsKey(product.name))
            totalPrice -= allCartItemsMap.get(product.name).price;
        else
            totalNoOfItems++;

        // Overwrite previous data
        allCartItemsMap.put(product.name,new CartItem(product.name,newPrice,qty));
        totalPrice += newPrice;
    }

    public void removeWBPFromCart(Product product){
        if (allCartItemsMap.containsKey(product.name)){
            totalPrice -= allCartItemsMap.get(product.name).price;
            totalNoOfItems--;
            allCartItemsMap.remove(product.name);
        }
    }

    public int getVariantQty(Product product, Variant variant) {
        String key = product.name+" "+variant.name;

        if (allCartItemsMap.containsKey(key)){
            return (int) allCartItemsMap.get(key).qty;
        }
        return 0;
    }
}
