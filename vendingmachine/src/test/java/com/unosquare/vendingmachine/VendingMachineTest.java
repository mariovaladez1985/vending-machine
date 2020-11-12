package com.unosquare.vendingmachine;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.ResourceUtils;

import com.unosquare.vendingmachine.beans.Product;
import com.unosquare.vendingmachine.controller.VendingMachineController;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = VendingMachineController.class)
public class VendingMachineTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	/**
	 * Test the upload a valid file, must load the items in the page
	 * 
	 * @throws Exception
	 */
	public void uploadValidFile() throws Exception {
		File fileVendingMachine = ResourceUtils.getFile("classpath:test/VendingSample.txt");

		List<String> lstLineas = FileUtils.readLines(fileVendingMachine, "UTF-8");
		if (lstLineas.size() > 0) {
			Product firstProduct = Product.getProduct(lstLineas.get(0));

			MockMultipartFile MockMultipartFile = new MockMultipartFile("productsFile", fileVendingMachine.getName(),
					"text/plan", new FileInputStream(fileVendingMachine));

			mockMvc.perform(MockMvcRequestBuilders.multipart("/load-product-list").file(MockMultipartFile))
					.andExpect(status().isOk()).andExpect(view().name("index"))
					.andExpect(content().string(containsString(firstProduct.getItemName())))
					.andDo(MockMvcResultHandlers.print());
			;
		}
	}

	@Test
	/**
	 * Test the upload a invalid file, must print The file must have a [.TXT]
	 * extension
	 * 
	 * @throws Exception
	 */
	public void uploadInvalidFile() throws Exception {
		File fileVendingMachine = ResourceUtils.getFile("classpath:test/VendingInvalid.jpg");

		MockMultipartFile MockMultipartFile = new MockMultipartFile("productsFile", fileVendingMachine.getName(),
				"text/plan", new FileInputStream(fileVendingMachine));

		mockMvc.perform(MockMvcRequestBuilders.multipart("/load-product-list").file(MockMultipartFile))
				.andExpect(status().isOk()).andExpect(view().name("index"))
				.andExpect(content().string(containsString("The file must have a [.TXT] extension")))
				.andDo(MockMvcResultHandlers.print());
		;
	}

	@Test
	/**
	 * insert money using a valid denomination, the service must return the status,
	 * and the incremented amount of cash
	 * 
	 * @throws Exception
	 */
	public void addCashBalanceValid() throws Exception {
		mockMvc.perform(get("/add-to-balance").param("denomination", "NOTE_2")).andExpect(status().isOk())
				.andExpect(content().string(containsString("cashBalance\":\"2.0\",\"status\":\"success\"")))
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	/**
	 * insert money using a invalid denomination, the service must return the
	 * status, and the incremented amount of cash
	 * 
	 * @throws Exception
	 */
	public void addCashBalanceInvalid() throws Exception {
		mockMvc.perform(get("/add-to-balance").param("denomination", "NOTE_5")).andExpect(status().isOk())
				.andExpect(content()
						.string(containsString("cashBalance\":\"0.0\",\"error\":\"The denomination is invalid")))
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	/**
	 * Purchase a product all valid
	 * 
	 * @throws Exception
	 */
	public void selectProductValid() throws Exception {
		File fileVendingMachine = ResourceUtils.getFile("classpath:test/VendingSample.txt");

		List<String> lstLineas = FileUtils.readLines(fileVendingMachine, "UTF-8");
		if (lstLineas.size() > 0) {
			MockHttpSession mockHttpSession = null;

			MockMultipartFile MockMultipartFile = new MockMultipartFile("productsFile", fileVendingMachine.getName(),
					"text/plan", new FileInputStream(fileVendingMachine));

			MvcResult mvcResult = mockMvc
					.perform(MockMvcRequestBuilders.multipart("/load-product-list").file(MockMultipartFile))
					.andExpect(status().isOk()).andReturn();

			mockHttpSession = (MockHttpSession) mvcResult.getRequest().getSession();

			mockMvc.perform(get("/add-to-balance").param("denomination", "NOTE_2").session(mockHttpSession))
					.andExpect(status().isOk());

			mockMvc.perform(get("/select-product").param("productCode", "L3").session(mockHttpSession))
					.andExpect(status().isOk())
					.andExpect(content().string(containsString(
							"\"cashBalance\":\"1.5\",\"message\":\"You have purchased [Nuts], and have $1.5 available\",\"status\":\"success\"")))
					.andDo(MockMvcResultHandlers.print());
		}
	}

	@Test
	/**
	 * Purchase a product entering an invalid product code
	 * 
	 * @throws Exception
	 */
	public void selectProductInvalidProduct() throws Exception {
		File fileVendingMachine = ResourceUtils.getFile("classpath:test/VendingSample.txt");

		List<String> lstLineas = FileUtils.readLines(fileVendingMachine, "UTF-8");
		if (lstLineas.size() > 0) {
			MockHttpSession mockHttpSession = null;

			MockMultipartFile MockMultipartFile = new MockMultipartFile("productsFile", fileVendingMachine.getName(),
					"text/plan", new FileInputStream(fileVendingMachine));

			MvcResult mvcResult = mockMvc
					.perform(MockMvcRequestBuilders.multipart("/load-product-list").file(MockMultipartFile))
					.andExpect(status().isOk()).andReturn();

			mockHttpSession = (MockHttpSession) mvcResult.getRequest().getSession();

			mockMvc.perform(get("/add-to-balance").param("denomination", "NOTE_2").session(mockHttpSession))
					.andExpect(status().isOk());

			mockMvc.perform(get("/select-product").param("productCode", "MC").session(mockHttpSession))
					.andExpect(status().isOk())
					.andExpect(content().string(containsString("Product code not found, please select a valid one")))
					.andDo(MockMvcResultHandlers.print());

		}
	}

	@Test
	/**
	 * Purchase a product when not enough balance is available
	 * 
	 * @throws Exception
	 */
	public void selectProductInvalidNotEnoughBalance() throws Exception {
		File fileVendingMachine = ResourceUtils.getFile("classpath:test/VendingSample.txt");

		List<String> lstLineas = FileUtils.readLines(fileVendingMachine, "UTF-8");
		if (lstLineas.size() > 0) {
			MockHttpSession mockHttpSession = null;

			MockMultipartFile MockMultipartFile = new MockMultipartFile("productsFile", fileVendingMachine.getName(),
					"text/plan", new FileInputStream(fileVendingMachine));

			MvcResult mvcResult = mockMvc
					.perform(MockMvcRequestBuilders.multipart("/load-product-list").file(MockMultipartFile))
					.andExpect(status().isOk()).andReturn();

			mockHttpSession = (MockHttpSession) mvcResult.getRequest().getSession();

			mockMvc.perform(get("/select-product").param("productCode", "L3").session(mockHttpSession))
					.andExpect(status().isOk())
					.andExpect(content().string(containsString("You need to add 50.0 cents")))
					.andDo(MockMvcResultHandlers.print());

		}
	}

	@Test
	/**
	 * Refund the money that is in the balance when there was a purchase
	 * 
	 * @throws Exception
	 */
	public void refundBalanceWithPurchase() throws Exception {
		File fileVendingMachine = ResourceUtils.getFile("classpath:test/VendingSample.txt");

		List<String> lstLineas = FileUtils.readLines(fileVendingMachine, "UTF-8");
		if (lstLineas.size() > 0) {
			MockHttpSession mockHttpSession = null;

			MockMultipartFile MockMultipartFile = new MockMultipartFile("productsFile", fileVendingMachine.getName(),
					"text/plan", new FileInputStream(fileVendingMachine));

			MvcResult mvcResult = mockMvc
					.perform(MockMvcRequestBuilders.multipart("/load-product-list").file(MockMultipartFile))
					.andExpect(status().isOk()).andReturn();
			
			mockHttpSession = (MockHttpSession) mvcResult.getRequest().getSession();
			
			mockMvc.perform(get("/add-to-balance").param("denomination", "NOTE_2").session(mockHttpSession)).andExpect(status().isOk()).andReturn();
			
			mockMvc.perform(get("/select-product").param("productCode", "L3").session(mockHttpSession))
					.andExpect(status().isOk());
			
			mockMvc.perform(get("/endTransaction").session(mockHttpSession))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("{\"cashBalance\":\"0.0\",\"message\":\"Please collect your products [Nuts, ], and your $1.5 dollars change\",\"status\":\"success\"}")))
			.andDo(MockMvcResultHandlers.print());
		}
	}
	
	//
	
	@Test
	/**
	 * Refund the money that is in the balance when there was no purchase
	 * 
	 * @throws Exception
	 */
	public void refundBalanceWithoutPurchase() throws Exception {
		MockHttpSession mockHttpSession = null;
		
		MvcResult mvcResult = mockMvc.perform(get("/add-to-balance").param("denomination", "NOTE_1")).andExpect(status().isOk()).andReturn();
		
		mockHttpSession = (MockHttpSession) mvcResult.getRequest().getSession();

		mockMvc.perform(get("/endTransaction").session(mockHttpSession))
				.andExpect(status().isOk()).andExpect(content().string(containsString("{\"cashBalance\":\"0.0\",\"message\":\"You received $1.0 dollars\",\"status\":\"success\"}")))
				.andDo(MockMvcResultHandlers.print());
	}
	
}
