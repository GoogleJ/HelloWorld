package com.zxjk.moneyspace.bean.response;

import java.util.List;

public class GetUserInfo {

    /**
     * nick : 喵喵喵
     * headPortrait : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/4qnihrkux4nk4cqw3wq5.jpg
     * balanceWallet : [{"symbol":"USDT","walletAddress":"0xdab86ba12e90d6fa97aae76a3408990f6f0f2270"}]
     * blockchainWallet : []
     */

    private String nick;
    private String headPortrait;
    private List<BalanceWalletBean> balanceWallet;
    private List<?> blockchainWallet;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public List<BalanceWalletBean> getBalanceWallet() {
        return balanceWallet;
    }

    public void setBalanceWallet(List<BalanceWalletBean> balanceWallet) {
        this.balanceWallet = balanceWallet;
    }

    public List<?> getBlockchainWallet() {
        return blockchainWallet;
    }

    public void setBlockchainWallet(List<?> blockchainWallet) {
        this.blockchainWallet = blockchainWallet;
    }

    public static class BalanceWalletBean {
        /**
         * symbol : USDT
         * walletAddress : 0xdab86ba12e90d6fa97aae76a3408990f6f0f2270
         */

        private String symbol;
        private String walletAddress;

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getWalletAddress() {
            return walletAddress;
        }

        public void setWalletAddress(String walletAddress) {
            this.walletAddress = walletAddress;
        }
    }
}
