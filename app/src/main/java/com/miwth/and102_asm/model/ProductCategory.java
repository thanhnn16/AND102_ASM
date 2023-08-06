package com.miwth.and102_asm.model;

public class ProductCategory {
    private int categoryID;
    private String categoryName;
    private int categoryImage;
    private String categoryDescription;
    private int categoryQuantity;
    private boolean isSelected;

    public ProductCategory() {
    }

    public ProductCategory(int categoryID, String categoryName, int categoryImage) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(int categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public int getCategoryQuantity() {
        return categoryQuantity;
    }

    public void setCategoryQuantity(int categoryQuantity) {
        this.categoryQuantity = categoryQuantity;
    }
}
