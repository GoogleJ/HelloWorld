package com.zxjk.duoduo.bean.response;

public class MarketsResponse {


    /**
     * logo : https://appserversrc.8btc.com/coin_info/img/BTC-Bitcoin.svg
     * coin : BTC/USDT
     * lastRMB : 82581.29
     * change : 0.95%
     * exchange : Huobi
     * lastDollar : 11882.20
     */

    private String logo;
    private String coin;
    private String lastRMB;
    private String change;
    private String exchange;
    private String lastDollar;
    private String totalValue;

    public String getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(String totalValue) {
        this.totalValue = totalValue;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getLastRMB() {
        return lastRMB;
    }

    public void setLastRMB(String lastRMB) {
        this.lastRMB = lastRMB;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getLastDollar() {
        return lastDollar;
    }

    public void setLastDollar(String lastDollar) {
        this.lastDollar = lastDollar;
    }
}
