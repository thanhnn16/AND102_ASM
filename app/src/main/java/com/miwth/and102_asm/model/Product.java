package com.miwth.and102_asm.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


public class Product implements Parcelable {
    private int productID;
    private String productName;
    private float productPrice;
    private int productQuantity;
    private int productCategoryId;
    private String productDes;

    public Product() {
        // Default constructor required for Firebase deserialization
    }

    public Product(int productID, String productName, float productPrice, int productQuantity, int productCategoryId, String productDes) {
        this.productID = productID;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.productCategoryId = productCategoryId;
        this.productDes = productDes;
    }

    public int getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    protected Product(Parcel in) {
        productID = in.readInt();
        productName = in.readString();
        productPrice = in.readFloat();
        productQuantity = in.readInt();
        productCategoryId = in.readInt();
        productDes = in.readString();
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public float getProductPrice() {
        return productPrice;
    }

    public int getProductCategoryId() {
        return productCategoryId;
    }

    public String getProductCategoryName() {
        int id = getProductCategoryId();
        String[] categories = {"Vegetables", "Fruits", "Dairy", "Bread", "Eggs", "Mushroom", "Oats", "Rice", "Others"};
        switch (id) {
            case 1:
                return categories[0];
            case 2:
                return categories[1];
            case 3:
                return categories[2];
            case 4:
                return categories[3];
            case 5:
                return categories[4];
            case 6:
                return categories[5];
            case 7:
                return categories[6];
            case 8:
                return categories[7];
            default:
                return categories[8];
        }
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getProductDes() {
        return productDes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(productID);
        dest.writeString(productName);
        dest.writeFloat(productPrice);
        dest.writeInt(productQuantity);
        dest.writeInt(productCategoryId);
        dest.writeString(productDes);
    }
}
