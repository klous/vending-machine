package com.techelevator.VendingMachine;

public class BalanceZeroException extends Exception{
    public BalanceZeroException(String message) {
        super(message);
    }

}
