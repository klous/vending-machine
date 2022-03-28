package com.techelevator;

import com.techelevator.VendingMachine.BalanceZeroException;
import com.techelevator.VendingMachine.VendingItem;
import com.techelevator.VendingMachine.VendingMachine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static com.techelevator.VendingMachineCLI.FILE_NAME;

public class VendingMachineTest {
    private VendingMachine vm = null;

    @Before
    public void vendingMachineCreationForTests(){
        vm = new VendingMachine(FILE_NAME);

    }
    @Test
    public void test_adding_four_dollars_to_vending_machine(){
        vm.addToBalance("4");
        BigDecimal expectedBalance =new BigDecimal("4");
        try {
            BigDecimal actualBalance = vm.getBalance();

            Assert.assertEquals(expectedBalance, actualBalance);
        } catch (BalanceZeroException e) {}
    }

    @Test
    public void test_adding_four_and_a_half_dollars_to_vending_machine(){
        vm.addToBalance("4.5");
        BigDecimal expectedBalance = BigDecimal.ZERO;
       try {
           BigDecimal actualBalance = vm.getBalance();

        Assert.assertEquals(expectedBalance, actualBalance);
       } catch (BalanceZeroException e){}
    }

    @Test
    public void test_adding_negative_two_doesnt_change_balance () {
        vm.addToBalance("-2");
        BigDecimal expectedBalance = new BigDecimal("0");
        try {
            BigDecimal actualBalance = vm.getBalance();

            Assert.assertEquals("When adding $-2, balance should remain the same",expectedBalance, actualBalance);
        } catch (BalanceZeroException e) {
        }
    }
    @Test
    public void test_adding_negative_two_to_existing_balance_of_3 () {
        vm.addToBalance("3");
        vm.addToBalance("-2");
        BigDecimal expectedBalance = new BigDecimal("3");
        try {
            BigDecimal actualBalance = vm.getBalance();

            Assert.assertEquals("When trying to add $-2 balance to an existing balance of 3, we should keep the balance of 3",expectedBalance, actualBalance);
        } catch (BalanceZeroException e) {
        }
    }
    @Test
    public void test_adding_the_string_four_to_a_balance_of_0 () {
        vm.addToBalance("four");
        BigDecimal expectedBalance = new BigDecimal("0");
        try {
            BigDecimal actualBalance = vm.getBalance();

            Assert.assertEquals("When trying to add a 'four' to balance, the value should not change",expectedBalance, actualBalance);
        } catch (BalanceZeroException e) {
        }
    }
    @Test
    public void test_makePurchase_happy_path () {
        vm.addToBalance("5");
        vm.makePurchase("A3");
        int expectedItemQuantity = 4;
        BigDecimal expectedBalance = new BigDecimal("2.25");
        int actualItemQuantity = vm.getInventoryQuantity("A3");
        try {
            BigDecimal actualBalance = vm.getBalance();

            Assert.assertEquals("Balance should go down $2.75 and return 2.25", expectedBalance, actualBalance);
        } catch (BalanceZeroException e) {

        }
        Assert.assertEquals("started at 5 quantity, should be 4", expectedItemQuantity, actualItemQuantity);
    }

    @Test
    public void test_makePurchase_with_insufficient_funds () {
        vm.addToBalance("3");
        vm.makePurchase("A4");
        int expectedItemQuantity = 5;
        BigDecimal expectedBalance = new BigDecimal("3");
        int actualItemQuantity = vm.getInventoryQuantity("A4");
        try {
            BigDecimal actualBalance = vm.getBalance();

            Assert.assertEquals("Balance should remain the same 3", expectedBalance, actualBalance);
        } catch (BalanceZeroException e) {

        }
        Assert.assertEquals("started at 5 quantity, should stay the same", expectedItemQuantity, actualItemQuantity);
    }

    @Test
    public void test_addToTotalSales_adding_to_totalSales_when_purchasing_items () {
        vm.addToBalance("5");
        vm.makePurchase("D4");
        vm.makePurchase("C1");
        vm.makePurchase("D2");
        BigDecimal expectedTotalSales = new BigDecimal("2.95");

        BigDecimal actualTotalSales = vm.getTotalSales();

        Assert.assertEquals("Total Dollar sales should be 2.95", expectedTotalSales, actualTotalSales);

    }

    @Test
    public void test_incrementQuantity_multiple_purchase_for_same_item () {
        vm.addToBalance("20");
        vm.makePurchase("D4");
        vm.makePurchase("D4");
        vm.makePurchase("C1");
        vm.makePurchase("C1");
        vm.makePurchase("C1");



        int expectedQuantitySoldsOfD4 = 2;
        int expectedQuantitySoldsOfC1 = 3;

        int actualQuantitySoldsOfD4 = vm.getQuantitySold("D4");
        int actualQuantitySoldsOfC1 = vm.getQuantitySold("C1");


        Assert.assertEquals("D4 should have sold 2 items", expectedQuantitySoldsOfD4, actualQuantitySoldsOfD4);
        Assert.assertEquals("C1 should have sold 3 items", expectedQuantitySoldsOfC1, actualQuantitySoldsOfC1);

    }

    @Test
    public void test_makePurchase_for_insufficient_inventory_does_not_change_balance () {
        vm.addToBalance("10");
        vm.makePurchase("D4");
        vm.makePurchase("D4");
        vm.makePurchase("D4");
        vm.makePurchase("D4");
        vm.makePurchase("D4");
        vm.makePurchase("D4");
        int expectedItemQuantity = 0;
        BigDecimal expectedBalance = new BigDecimal("6.25");
        int actualItemQuantity = vm.getInventoryQuantity("D4");
        try {
            BigDecimal actualBalance = vm.getBalance();

            Assert.assertEquals("Balance should go down 3.75 and end at 6.25", expectedBalance, actualBalance);
        } catch (BalanceZeroException e) {

        }
        Assert.assertEquals("started at 5 quantity, should be 0", expectedItemQuantity, actualItemQuantity);


    }
    @Test
    public void test_finishTransaction_happy_path () {
        vm.addToBalance("1");
        vm.makePurchase("D4");

        try {
            vm.finishTransaction();
            BigDecimal actualBalance = vm.getBalance();
            Assert.assertEquals(BigDecimal.ZERO, actualBalance );

        } catch (BalanceZeroException e) {

        }

    }

}
