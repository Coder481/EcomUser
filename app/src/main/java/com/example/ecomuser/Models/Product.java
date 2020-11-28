package com.example.ecomuser.Models;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Product implements Serializable {
    public static final int WEIGHT_BASED=0,VARIANTS_BASED=1;

    // Compulsary
    public  String name;
    public  int type;

    // WeightBased
    public int pricePerkg;
    public float minQty;

    // VariantsBased
    public List<Variant> variants = new ArrayList<>();

    public Product(){
    }

    //WeightBased
    public  Product(String name,int pricePerkg,float minQty){
        type=WEIGHT_BASED;
        this.name=name;
        this.pricePerkg=pricePerkg;
        this.minQty=minQty;
    }

    // VariantsBased
    public  Product(String name){
        type=VARIANTS_BASED;
        this.name=name;
    }

    public Product(String name,List<Variant> variants){
        type=VARIANTS_BASED;
        this.name=name;
        this.variants=variants;
    }

    // Extracts & sets variants from String[]
    public  void fromVariantStrings(String[] vs){
        variants=new ArrayList<>();
        for(String s : vs){
            String[] v = s.split(",");
            v[1]=v[1].trim();
            variants.add(new Variant(v[0],Integer.parseInt(v[1])));
        }
    }

    public  String minQtyToString(){
        // float (2.0) -> String (2kg)
        // float (0.050) -> String (50g)

        if (minQty< 1){
            int g = (int) (minQty*1000);
            return g+"g";
        }
        return ((int) minQty)+"kg";
    }


    @Override
    public String toString() {
        return "Product {"+
                "Name= "+name+'\''
                +", Type "+type
                +", PricePerkg= "+pricePerkg
                +", MinQty= "+minQty
                +", Variants= "+variants
                +'}';
    }

    public  String variantsString(){
        String variantsString = variants.toString();
        return variantsString
                .replaceFirst("\\[","")
                .replaceFirst("]","")
                .replaceAll(",",", ");
    }

    public void initWeightBasedProduct(String name, int pricePerkg, float minQty) {
        type=WEIGHT_BASED;
        this.name=name;
        this.pricePerkg=pricePerkg;
        this.minQty=minQty;
    }

    public void initVarientBasedProduct(String name) {
        type=VARIANTS_BASED;
        this.name=name;
    }
}