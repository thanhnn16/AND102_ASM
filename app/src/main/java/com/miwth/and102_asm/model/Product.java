package com.miwth.and102_asm.model;

import java.io.Serializable;

public class Product implements Serializable {
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
}
