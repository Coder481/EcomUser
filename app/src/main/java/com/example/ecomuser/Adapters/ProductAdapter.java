package com.example.ecomuser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecomuser.Controllers.MultipleVBPAndWBPViewBinder;
import com.example.ecomuser.Controllers.SingleVBPViewBinder;
import com.example.ecomuser.MainActivity;
import com.example.ecomuser.Models.Cart;
import com.example.ecomuser.Models.Product;
import com.example.ecomuser.databinding.ProductItemSingleVbBinding;
import com.example.ecomuser.databinding.ProductItemWbOrMultiVbBinding;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int PRODUCT_TYPE_SINGLE_VB=0 , PRODUCT_TYPE_MULTI_VB_OR_WB=1;
    private final List<Product> allProducts;
    private Context context;
    private Cart cart;

    public ProductAdapter(Context context, List<Product> products, Cart cart) {
        this.context=context;
        this.allProducts = products;
        this.cart=cart;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==PRODUCT_TYPE_MULTI_VB_OR_WB){
            ProductItemWbOrMultiVbBinding b = ProductItemWbOrMultiVbBinding.inflate(
                    LayoutInflater.from(context)
                    ,parent,false);
            return new MultiVbOrWbVH(b);
        }else{
            ProductItemSingleVbBinding b = ProductItemSingleVbBinding.inflate(
                    LayoutInflater.from(context)
                    ,parent,false);
            return new SingleVbVH(b);
        }

    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Product product = allProducts.get(position);

        if (getItemViewType(position) == PRODUCT_TYPE_MULTI_VB_OR_WB){
            MultiVbOrWbVH vh = ((MultiVbOrWbVH)holder);

            vh.b.name.setText(product.name);
            vh.b.price.setText(product.type == Product.WEIGHT_BASED ? "Rs. "+product.pricePerkg+"/kg":product.variantsString());

            // Binding Buttons for WBP and Multiple VBP
            new MultipleVBPAndWBPViewBinder().bind(vh.b,product,cart,position);
        }
        else{

            SingleVbVH vh = (SingleVbVH)holder;

            vh.b.name.setText(product.name + " " + product.variants.get(0).name);
            vh.b.price.setText(product.variantsString());

            // Binding Buttons for Single VBP
            new SingleVBPViewBinder().bind(vh.b,product,cart);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Product product = allProducts.get(position);
        if (product.type == Product.WEIGHT_BASED|| product.variants.size()>1){
            return PRODUCT_TYPE_MULTI_VB_OR_WB;
        }

        return PRODUCT_TYPE_SINGLE_VB;
    }

    @Override
    public int getItemCount() {
        return allProducts.size();
    }


    /** Multiple VBP or WBP view holder **/
    public static class MultiVbOrWbVH extends RecyclerView.ViewHolder{

        ProductItemWbOrMultiVbBinding b;
        public MultiVbOrWbVH(@NonNull ProductItemWbOrMultiVbBinding b) {
            super(b.getRoot());
            this.b=b;
        }
    }

    /** Single VBP view holder **/
    public static class SingleVbVH extends RecyclerView.ViewHolder{

        ProductItemSingleVbBinding b;
        public SingleVbVH(@NonNull ProductItemSingleVbBinding b) {
            super(b.getRoot());
            this.b=b;
        }
    }
}
