package com.zxjk.moneyspace.bean.request;

public class OperateAssetsRequest {

    /**
     * customerId : 1
     * contractAddress : 0x3830f7af866fae79e4f6b277be17593bf96bee3b
     * symbol : FST
     * isDelete : 1
     * parentSymbol : ETH
     */

    private String customerId;
    private String contractAddress;
    private String symbol;
    private String isDelete;
    private String parentSymbol;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getParentSymbol() {
        return parentSymbol;
    }

    public void setParentSymbol(String parentSymbol) {
        this.parentSymbol = parentSymbol;
    }
}
