package com.miwth.and102_asm.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


public class Product implements Parcelable {
    private int productID;
    private String productName;
    private String productPrice;
    private int productQuantity;
    private String imgSrc;
    private String useID;

    public Product() {
        // Default constructor required for Firebase deserialization
    }
    public Product(int productID, String productName, String productPrice, int productQuantity, String imgSrc, String useID) {
        this.productID = productID;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.imgSrc = imgSrc;
        this.useID = useID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getUseID() {
        return useID;
    }

    public void setUseID(String useID) {
        this.useID = useID;
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

    protected Product(Parcel in) {
        productID = in.readInt();
        productName = in.readString();
        productPrice = in.readString();
        productQuantity = in.readInt();
        imgSrc = in.readString();
        useID = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(productID);
        dest.writeString(productName);
        dest.writeString(productPrice);
        dest.writeInt(productQuantity);
        dest.writeString(imgSrc);
        dest.writeString(useID);
    }

}
