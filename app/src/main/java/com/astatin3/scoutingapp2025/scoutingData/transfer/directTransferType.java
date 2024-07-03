package com.astatin3.scoutingapp2025.scoutingData.transfer;

public class directTransferType extends transferType {
    public transferValue getType() {return transferValue.DIRECT;}
    public directTransferType(String name){
        super(name);
    }
}
