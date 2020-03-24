package com.zxjk.duoduo.bean.response;

import java.util.List;

public class GetSymbolInfo {

    /**
     * symbolInfo : [{"symbol":"USDT","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571020793185.png?Expires=1886380789&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=40XcvkX5AftUJF8CLBtDrMWPlJE%3D"}]
     * defaultRenegeNumber : 4
     */

    private String defaultRenegeNumber;
    private List<SymbolInfoBean> symbolInfo;

    public String getDefaultRenegeNumber() {
        return defaultRenegeNumber;
    }

    public void setDefaultRenegeNumber(String defaultRenegeNumber) {
        this.defaultRenegeNumber = defaultRenegeNumber;
    }

    public List<SymbolInfoBean> getSymbolInfo() {
        return symbolInfo;
    }

    public void setSymbolInfo(List<SymbolInfoBean> symbolInfo) {
        this.symbolInfo = symbolInfo;
    }

    public static class SymbolInfoBean {
        /**
         * symbol : USDT
         * logo : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571020793185.png?Expires=1886380789&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=40XcvkX5AftUJF8CLBtDrMWPlJE%3D
         */

        private String symbol;
        private String logo;
        private String amountScale;

        public String getAmountScale() {
            return amountScale;
        }

        public void setAmountScale(String amountScale) {
            this.amountScale = amountScale;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }
    }
}
