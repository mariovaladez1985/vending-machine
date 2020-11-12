package com.unosquare.vendingmachine.beans;

import org.apache.commons.lang3.math.NumberUtils;

public class Product {
	private String itemName;
	private String code;
	private double cost;
	
	public static Product getProduct(String stringProduct) throws IllegalArgumentException
	{
		if(stringProduct == null || stringProduct.trim().isEmpty() || stringProduct.split(",").length < 3)
		{
			throw new IllegalArgumentException("Invalid input detected: " + stringProduct + ", the input must be of the type [Item name, code, cost]");
		}

		String [] arrayProductParameters =  stringProduct.split(",");
		Product product = new Product();
		product.setItemName(arrayProductParameters[0].trim());
		product.setCode(arrayProductParameters[1].trim());
		
		if(NumberUtils.isCreatable(arrayProductParameters[2].replace("$", "").trim()))
		{
			product.setCost(Double.parseDouble(arrayProductParameters[2].replace("$", "").trim()));
		}
		else {
			throw new IllegalArgumentException("Invalid input detected: " + stringProduct + ", the third comma separated input must be of the type [$NUMBER]");
		}
		
		return product;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final Product other = (Product) obj;
        if (code == other.getCode()) {
            return false;
        }
        return true;
    }
	
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	
}
