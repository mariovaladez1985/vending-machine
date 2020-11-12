package com.unosquare.vendingmachine.beans;

import java.util.ArrayList;
import java.util.List;

public class VendingMachine {
	private List<Product> lstProduct = new ArrayList<Product>();
	
	private List<Product> lstPurchasedProduct = new ArrayList<Product>();
	
	private double cashBalance;

	public List<Product> getLstProduct() {
		return lstProduct;
	}

	public void setLstProduct(List<Product> lstProduct) {
		this.lstProduct = lstProduct;
	}

	public double getCashBalance() {
		return cashBalance;
	}

	public void setCashBalance(double cashBalance) {
		this.cashBalance = cashBalance;
	}

	public List<Product> getLstPurchasedProduct() {
		return lstPurchasedProduct;
	}

	public void setLstPurchasedProduct(List<Product> lstPurchasedProduct) {
		this.lstPurchasedProduct = lstPurchasedProduct;
	}

	public enum DENOMINATIONS {
		CENT_1("1 Cent", .01), CENT_5("5 Cent", .05), CENT_10("10 Cent", .10), CENT_25("25 Cent", .25), CENT_50("50 Cent", .50), NOTE_1("1 Dollar", 1), NOTE_2("2 Dollar",2);

		DENOMINATIONS(String label, double value) {
			this.label = label;
			this.value = value;
		}
		
		private double value;
		private String label;
		
		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}
		
		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}
	}
}
