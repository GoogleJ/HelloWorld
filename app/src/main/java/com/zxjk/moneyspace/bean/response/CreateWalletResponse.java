package com.zxjk.moneyspace.bean.response;

public class CreateWalletResponse {

    /**
     * customerId :
     * balanceHk :
     * balanceHkb :
     * payPwd :
     * walletAddress :
     * walletPrivateKey :
     * walletKeystore :
     * walletMnemonic :
     * duoduoId :
     * balanceEth :
     * exchange : 0.0
     * exchangeHkb : 0.0
     * rate :
     * id :
     * updateTime :
     * createTime :
     * createBy :
     * updateBy :
     * page :
     * isDelete :
     */

    private String customerId;
    private String balanceHk;
    private String balanceHkb;
    private String payPwd;
    private String walletAddress;
    private String walletPrivateKey;
    private String walletKeystore;
    private String walletMnemonic;
    private String duoduoId;
    private String balanceEth;
    private double exchange;
    private double exchangeHkb;
    private String rate;
    private String id;
    private String updateTime;
    private String createTime;
    private String createBy;
    private String updateBy;
    private String page;
    private String isDelete;
    private String balanceMoToken;
    private String balanceMot;

    public String getBalanceMot() {
        return balanceMot;
    }

    public void setBalanceMot(String balanceMot) {
        this.balanceMot = balanceMot;
    }

    public String getBalanceMoToken() {
        return balanceMoToken;
    }

    public void setBalanceMoToken(String balanceMoToken) {
        this.balanceMoToken = balanceMoToken;
    }

    @Override
    public String toString() {
        return "CreateWalletResponse{" +
                "customerId='" + customerId + '\'' +
                ", balanceHk='" + balanceHk + '\'' +
                ", balanceHkb='" + balanceHkb + '\'' +
                ", payPwd='" + payPwd + '\'' +
                ", walletAddress='" + walletAddress + '\'' +
                ", walletPrivateKey='" + walletPrivateKey + '\'' +
                ", walletKeystore='" + walletKeystore + '\'' +
                ", walletMnemonic='" + walletMnemonic + '\'' +
                ", duoduoId='" + duoduoId + '\'' +
                ", balanceEth='" + balanceEth + '\'' +
                ", exchange=" + exchange +
                ", exchangeHkb=" + exchangeHkb +
                ", rate='" + rate + '\'' +
                ", id='" + id + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", createTime='" + createTime + '\'' +
                ", createBy='" + createBy + '\'' +
                ", updateBy='" + updateBy + '\'' +
                ", page='" + page + '\'' +
                ", isDelete='" + isDelete + '\'' +
                '}';
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getBalanceHk() {
        return balanceHk;
    }

    public void setBalanceHk(String balanceHk) {
        this.balanceHk = balanceHk;
    }

    public String getBalanceHkb() {
        return balanceHkb;
    }

    public void setBalanceHkb(String balanceHkb) {
        this.balanceHkb = balanceHkb;
    }

    public String getPayPwd() {
        return payPwd;
    }

    public void setPayPwd(String payPwd) {
        this.payPwd = payPwd;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getWalletPrivateKey() {
        return walletPrivateKey;
    }

    public void setWalletPrivateKey(String walletPrivateKey) {
        this.walletPrivateKey = walletPrivateKey;
    }

    public String getWalletKeystore() {
        return walletKeystore;
    }

    public void setWalletKeystore(String walletKeystore) {
        this.walletKeystore = walletKeystore;
    }

    public String getWalletMnemonic() {
        return walletMnemonic;
    }

    public void setWalletMnemonic(String walletMnemonic) {
        this.walletMnemonic = walletMnemonic;
    }

    public String getDuoduoId() {
        return duoduoId;
    }

    public void setDuoduoId(String duoduoId) {
        this.duoduoId = duoduoId;
    }

    public String getBalanceEth() {
        return balanceEth;
    }

    public void setBalanceEth(String balanceEth) {
        this.balanceEth = balanceEth;
    }

    public double getExchange() {
        return exchange;
    }

    public void setExchange(double exchange) {
        this.exchange = exchange;
    }

    public double getExchangeHkb() {
        return exchangeHkb;
    }

    public void setExchangeHkb(double exchangeHkb) {
        this.exchangeHkb = exchangeHkb;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }
}
