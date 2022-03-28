package com.techelevator.VendingMachine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class VendingMachine {

    private Map<String, VendingItem> inventoryMap = new HashMap<>();


    private final int STARTING_INVENTORY_LEVEL = 5;
    private BigDecimal balance = BigDecimal.ZERO;
    private final int QUARTER_VALUE_IN_PENNIES = 25;
    private final int DIME_VALUE_IN_PENNIES = 10;
    private final int NICKEL_VALUE_IN_PENNIES = 5;

    private  Map<String,String> snackSounds = new HashMap<>() {{
        put("Chip","Crunch Crunch, Yum!");
        put("Gum","Chew, Chew, Yum!");
        put("Drink","Glug Glug, Yum!");
        put("Candy","Munch Munch, Yum!");
    }};

    private BigDecimal totalSales = BigDecimal.ZERO;

    public BigDecimal getTotalSales() {return totalSales;}

    private void addToTotalSales(BigDecimal transactionAmount){
        totalSales = totalSales.add(transactionAmount);
    }

    public BigDecimal getBalance() throws BalanceZeroException{
        if(balance.compareTo(BigDecimal.ZERO) == 0){
            throw new BalanceZeroException("Balance is zero. Please feed money before selecting product.");
        }
        return balance;
    }

    public VendingMachine(String fileName){
        List<String[]> inventory = new ArrayList<>();
        File inputFile = new File(fileName);
        try(Scanner reader = new Scanner(inputFile)){
            while (reader.hasNextLine()){
                String line = reader.nextLine();
                String[] item = line.split("\\|");
                String k = item[0];

                //todo maybe we don't need the location, since it is the key
                VendingItem itemForMap = new VendingItem(item[1], item[2], item[3], STARTING_INVENTORY_LEVEL);
                inventoryMap.put(k, itemForMap);
            }
        }catch (Exception e){
            System.out.println("File not found load inventory method");
            System.out.println(e.getMessage());
        }

    }

    // added this so we could get this value when testing
    public int getQuantitySold(String location){
        return inventoryMap.get(location).getQuantitySold();
    }

    public String displayInventory(){
        String returnString = "";
        Set keySet = inventoryMap.keySet();
        List<String> keyArray = new ArrayList<>(keySet);

        Collections.sort(keyArray);

        String previousRowLetter = "A";
        for (int i = 0; i <keyArray.size() ; i++) {
            String keyValue = keyArray.get(i);
            String rowLetter = keyValue.substring(0,1);
            if(!previousRowLetter.equals(rowLetter)){
                returnString += "\n";
            }else {
                if(i > 0) {
                    returnString += " | ";
                }
            }
                returnString +=  keyValue+ " "+ inventoryMap.get(keyValue);
            previousRowLetter = keyValue.substring(0,1);
        }


        return returnString;
    }

    private List<String[]> loadInventory(String fileName) {
        List<String[]> inventory = new ArrayList<>();
        File inputFile = new File(fileName);
        try(Scanner reader = new Scanner(inputFile)){
            while (reader.hasNextLine()){
                String line = reader.nextLine();
                String[] item = line.split("\\|");

                //todo create the objects directly here and add to out
                inventory.add(item);
            }
        }catch (Exception e){
            System.out.println("File not found load inventory method");
            System.out.println(e.getMessage());
        }
        return inventory;
    }
    public boolean addToBalance(String amountToAdd){
        try{
            int amount = Integer.parseInt(amountToAdd);
            if(amount <0){
                throw new NumberFormatException();
            }
            BigDecimal convertedAmountToAdd = new BigDecimal(amount);
            balance = balance.add(convertedAmountToAdd);
            // call appendToAuditFile
            appendLogFile("FEED MONEY", convertedAmountToAdd);

            return true;
        }catch(NumberFormatException e) {
            System.out.println("Invalid input please enter a whole number greater than zero.");
            return false;
        }

        }

        public String getPrettyBalance() {
        String returnString = "";
        returnString = "Current balance is $"+ convertBDtoCurrency(balance);

        return returnString;
        }

    private void subtractFromBalance(BigDecimal amountToSubtract){
        balance = balance.subtract(amountToSubtract);
    }

    private boolean sufficientFunds(BigDecimal itemCost){
        boolean hasSufficientFunds = false;
        if (itemCost.compareTo(balance)<=0) {
            hasSufficientFunds = true;
        }
        return hasSufficientFunds;
    }

    private BigDecimal convertBDtoCurrency(BigDecimal number){
        return number.setScale(2, RoundingMode.DOWN);
    }

    private void appendLogFile(String transactionType, BigDecimal value){
        //produce Date Time
        //Append
        File logFile = new File("ExampleFiles\\vm_log.txt");
        try(PrintWriter pw = new PrintWriter(new FileOutputStream(logFile, true))){
           // LocalDate.now();

            String timeStamp = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa").format(Calendar.getInstance().getTime());

            pw.println(timeStamp + " " + transactionType +" $"+convertBDtoCurrency(value)+ " $" + convertBDtoCurrency(balance));
        }catch( FileNotFoundException e){
            System.out.println("Something went wrong");
            System.out.println("*** Unable to open log file: " + logFile.getAbsolutePath());
        }

    }

    public String makePurchase(String userSelection) {
        String purchaseMessage ="";
        if(inventoryMap.containsKey(userSelection)){
            //is item quantity > 0 ?
            VendingItem currentItem = inventoryMap.get(userSelection);
            int quantity = currentItem.getQuantity();

            if(quantity>0){
                // CHECK if the balance is sufficient
                if(sufficientFunds(currentItem.getPrice())){
                    // make the purchase : update balance & decrease quantity
                    subtractFromBalance(currentItem.getPrice());
                    currentItem.setQuantity(quantity-1);
                    // add to our running total sales
                    addToTotalSales((currentItem).getPrice());
                    //increase quantity sold by 1 for sales report
                    currentItem.incrementQuantitySold();

                    appendLogFile(currentItem.getProduct() + " "+ userSelection,currentItem.getPrice());
                    purchaseMessage = getItemSnackSound(currentItem.getType());
                } else {
                    purchaseMessage = "***Insufficient funds for selected item.*** \n Please add additional funds or select a different product.";
                }
            }else{ // quantity is 0, give the user feedback ?
                //todo replace this temporary console print message in an OOP way
                purchaseMessage = "Out of stock.";
            }
        } else{
           purchaseMessage = "Invalid selection, no crunch crunch for you!";
        }
        return purchaseMessage;
    }
    public int getInventoryQuantity(String location){
        return inventoryMap.get(location).getQuantity();
    }


    public String finishTransaction (){
        //dispense change
        String returnString = "Your change is $"+ convertBDtoCurrency(balance);
        BigDecimal currentBalance = balance;
        String coins = getCoinString();
        returnString += "\nYour coins: " + coins;
        balance = BigDecimal.ZERO;
        appendLogFile("GIVE CHANGE", currentBalance);

        return returnString;
    }



    private String getCoinString() {
        int pennyBalance = balance.multiply(new BigDecimal("100")).intValue();
        int quarters = pennyBalance / QUARTER_VALUE_IN_PENNIES;
        int dimes = (pennyBalance % QUARTER_VALUE_IN_PENNIES) / DIME_VALUE_IN_PENNIES;
        int nickels = ((pennyBalance % QUARTER_VALUE_IN_PENNIES) % DIME_VALUE_IN_PENNIES) / NICKEL_VALUE_IN_PENNIES;
        return "Quarters: " + quarters + " | Dimes: " + dimes + " | Nickels: " + nickels;
    }
    public String getItemSnackSound (String type){
        return snackSounds.get(type);
    }


    public void generateSalesReport() {
        File outputFileName = new File("ExampleFiles\\OurSalesReport.txt");
        try(PrintWriter pw = new PrintWriter(outputFileName)){
            for(String key : inventoryMap.keySet()){
                VendingItem item = inventoryMap.get(key);
                pw.println(item.getProduct() + " | "+ item.getQuantitySold());
            }
            pw.println();
            pw.println("**TOTAL SALES** $" + getTotalSales().setScale(2, RoundingMode.DOWN));
        }catch (Exception e){
            System.out.println("Destination file error.");
        }
    }
}
