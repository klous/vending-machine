package com.techelevator.VendingMachine;

import java.math.BigDecimal;

public class VendingItem {

    private String product;
    private BigDecimal price;
    private String type;
    private int quantity;

    private int quantitySold = 0;
    public int getQuantitySold() {return quantitySold;}
    public void incrementQuantitySold() {
        quantitySold++;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProduct() {
        return product;
    }

    public String getType() {
        return type;
    }


    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    //todo updating this to pass in a string and convert it to BigDecimal here
    public VendingItem(String product, String price, String type, int quantity){
        this.product = product;
        this.price = new BigDecimal(price);
        this.type = type;
        this.quantity = quantity;
    }

    //todo consider adding an @Override toString method
    @Override
    public String toString(){
        String returnStr = "";
        if(quantity==0){
            returnStr = "Out of Stock";
        }else {
            returnStr += product + " $" + price;
        }
        return returnStr;
    }


}
