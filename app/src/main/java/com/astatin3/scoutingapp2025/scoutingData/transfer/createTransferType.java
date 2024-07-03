package com.astatin3.scoutingapp2025.scoutingData.transfer;

public class createTransferType extends transferType {
    public transferValue getType() {return transferValue.CREATE;}
    public createTransferType(String name){
        super(name);
    }
}