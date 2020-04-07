package com.zxjk.moneyspace.bean.response;

public class GetParentSymbolBean {

    /**
     * coinType : 1
     * symbol : USDT
     * parentSymbol : ETH
     * walletName : ETH wallet有钱
     * importMethod : 2
     * contractAddress : 0xdac17f958d2ee523a2206206994597c13d831ec7
     * tokenDecimal : 6
     * logo : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571020793185.png?Expires=1886380789&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=40XcvkX5AftUJF8CLBtDrMWPlJE%3D
     * walletAddress : 0x35dfce803e429fc15f98961a0baa04e433c6cd81
     */

    private String coinType;
    private String symbol;
    private String parentSymbol;
    private String walletName;
    private String importMethod;
    private String contractAddress;
    private String tokenDecimal;
    private String logo;
    private String walletAddress;

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getParentSymbol() {
        return parentSymbol;
    }

    public void setParentSymbol(String parentSymbol) {
        this.parentSymbol = parentSymbol;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getTokenDecimal() {
        return tokenDecimal;
    }

    public void setTokenDecimal(String tokenDecimal) {
        this.tokenDecimal = tokenDecimal;
    }

    public String getImportMethod() {
        return importMethod;
    }

    public void setImportMethod(String importMethod) {
        this.importMethod = importMethod;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
}
