package com.techelevator;

import com.techelevator.VendingMachine.BalanceZeroException;
import com.techelevator.VendingMachine.VendingItem;
import com.techelevator.VendingMachine.VendingMachine;
import com.techelevator.view.Menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class VendingMachineCLI {

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String MAIN_MENU_SALES_REPORT = "Sales Report";
	private static final String FEED_MONEY = "Feed Money";
	private static final String SELECT_PRODUCT = "Select Product";
	private static final String FINISH_TRANSACTION = "Finish Transaction";
		private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE, MAIN_MENU_OPTION_EXIT, MAIN_MENU_SALES_REPORT };
private static final String[] PURCHASE_ITEMS_SUB_MENU_OPTIONS = { FEED_MONEY,SELECT_PRODUCT, FINISH_TRANSACTION };
	//add exit here add exit to while loop
	public static final String FILE_NAME = "ExampleFiles\\VendingMachine.txt";

	private Menu menu;

	private Scanner userInput = new Scanner(System.in);

	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
	}

	public void run() {

		//Keep in mind VendingMachineCLI should ONLY be the interface
		VendingMachine vm = new VendingMachine(FILE_NAME);

		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				// display vending machine items
				System.out.println(vm.displayInventory());

			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				while (true) {
					System.out.println(vm.getPrettyBalance());
					String subChoice = (String) menu.getChoiceFromOptions(PURCHASE_ITEMS_SUB_MENU_OPTIONS);
						if (subChoice.equals(FEED_MONEY)) {
						boolean validInput = false;
						while (!validInput) {
							System.out.print("Enter the amount to deposit in whole dollars: $");
							String balanceInput = userInput.nextLine();
							validInput = vm.addToBalance(balanceInput);
						}
						// prompt user to input money
					} else if (subChoice.equals(SELECT_PRODUCT)) {
						try {
							vm.getBalance();
							System.out.print("What do you want to buy? enter the slot: ");
							String userSelection = userInput.nextLine().toUpperCase(Locale.ROOT);
							System.out.println(vm.makePurchase(userSelection));
						} catch (BalanceZeroException e) {
							System.out.println(e.getMessage());
						}
					}else if (subChoice.equals(FINISH_TRANSACTION)) {

						System.out.println(vm.finishTransaction());
						break;
				}
			}
			}else if (choice.equals(MAIN_MENU_OPTION_EXIT)){
				break;
			}else if(choice.equals(MAIN_MENU_SALES_REPORT)){
				vm.generateSalesReport();
			}
		}
	}



	public static void main(String[] args) {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}




}
