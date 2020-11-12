package com.unosquare.vendingmachine.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.unosquare.vendingmachine.beans.Product;
import com.unosquare.vendingmachine.beans.VendingMachine;
import com.unosquare.vendingmachine.beans.VendingMachine.DENOMINATIONS;

@Scope("session")
@Controller
public class VendingMachineController {
	private VendingMachine vendingMachine = new VendingMachine();
	private List<DENOMINATIONS> lstDenomination;

	@RequestMapping(value = "/")
	public String homePage(ModelMap modelMap) {
		lstDenomination = Arrays.asList(DENOMINATIONS.values());

		modelMap.addAttribute("lstDenomination", lstDenomination);
		modelMap.addAttribute("vendingMachine", vendingMachine);
		return "index";
	}

	@RequestMapping(value = "/load-product-list")
	public String loadProductsList(@RequestParam("productsFile") File file, ModelMap modelMap) {
		List<String> lstMessages = new ArrayList<String>();
		List<Product> lstProduct = new ArrayList<Product>();
		boolean allParsedCorrectly = true;
		try {
			if (file.getOriginalFilename() != null && file.getOriginalFilename().toLowerCase().endsWith(".txt")) {
				// Parse File
				String[] arrayProducts = new String(file.getBytes()).split(System.lineSeparator());
				for (String productParameters : arrayProducts) {
					Product newProduct = Product.getProduct(productParameters);
					if (vendingMachine.getLstProduct().indexOf(newProduct) < 0) {
						lstProduct.add(newProduct);
					} else {
						allParsedCorrectly = false;
						lstMessages.add("The product: " + productParameters
								+ " is already in the vending machine, try again with a new code");
					}
				}
			} else {
				lstMessages.add("The file must have a [.TXT] extension");
			}

			if (allParsedCorrectly)
				vendingMachine.getLstProduct().addAll(lstProduct);

		} catch (Exception e) {
			lstMessages.add(e.getMessage());
		} finally {
			modelMap.addAttribute("lstMessages", lstMessages);
			modelMap.addAttribute("lstDenomination", lstDenomination);
			modelMap.addAttribute("vendingMachine", vendingMachine);
			modelMap.addAttribute("hello", "hello");
		}

		return "index";
	}

	@RequestMapping(value = "/add-to-balance")
	@ResponseBody
	public String addToBalance(@RequestParam("denomination") String strDenominacion, ModelMap modelMap) {
		Gson gson = new Gson();
		Map<String, String> mapJson = new HashMap<String, String>();
		
		try
		{
			DENOMINATIONS denomination = DENOMINATIONS.valueOf(strDenominacion);
			vendingMachine.setCashBalance(denomination.getValue());
			mapJson.put("status", "success");
		}
		catch(IllegalArgumentException ex)
		{
			mapJson.put("error", "The denomination is invalid");
		}
		
		return gson.toJson(mapJson).toString();
	}
	
	@RequestMapping(value = "/select-product")
	@ResponseBody
	public String selectProduct(@RequestParam("productCode") String productCode, ModelMap modelMap) {
		Gson gson = new Gson();
		Map<String, String> mapJson = new HashMap<String, String>();
		
		if(productCode != null && productCode.trim().isEmpty())
		{
			mapJson.put("error", "The product code must not be empty");
		}
		else {
			boolean foundProduct = false;
			for(Product availableProduct : vendingMachine.getLstProduct())
			{
				if(productCode.trim().equalsIgnoreCase(availableProduct.getCode().trim()))
				{
					foundProduct = true;
					if(vendingMachine.getCashBalance() >= availableProduct.getCost())
					{
						vendingMachine.getLstPurchasedProduct().add(availableProduct);
						vendingMachine.setCashBalance(vendingMachine.getCashBalance() - availableProduct.getCost());
						mapJson.put("message", "You have purchased [" + availableProduct.getItemName() + "], and have $" +  vendingMachine.getCashBalance() + " available");
						mapJson.put("status", "success");
					}
					else {
						mapJson.put("error", "You need to add " + ((Math.abs(availableProduct.getCost() - vendingMachine.getCashBalance()) * 100) + " cents ") );
					}
					break;
				}
			}
			if(!foundProduct)
			{
				mapJson.put("error", "Product code not found, please select a valid one");
			}
		}
		
		mapJson.put("cashBalance", Double.toString(vendingMachine.getCashBalance()));
	
		return gson.toJson(mapJson).toString();
	}
	
	@RequestMapping(value = "/end-transaction")
	@ResponseBody
	public String endTransaction(ModelMap modelMap) {
		Gson gson = new Gson();
		Map<String, String> mapJson = new HashMap<String, String>();
		
		StringBuilder message = new StringBuilder();
		
		if(vendingMachine.getLstPurchasedProduct().size() > 0 )
		{
			message.append("Please collect your products [" );
			for(Product purchasedProduct : vendingMachine.getLstPurchasedProduct())
			{
				message.append(purchasedProduct.getItemName() + ", ");
				
			}
			message.append("], and your $" +  vendingMachine.getCashBalance() + " dollars change");
		}
		else {
			message.append("You received $" +  vendingMachine.getCashBalance() + " dollars" );
		}
		
		vendingMachine.setLstPurchasedProduct(new ArrayList<Product>());
		vendingMachine.setCashBalance(0.0);
		
		mapJson.put("status", "success");
		mapJson.put("message", message.toString());
		mapJson.put("cashBalance", Double.toString(vendingMachine.getCashBalance()));
	
		return gson.toJson(mapJson).toString();
	}
}
