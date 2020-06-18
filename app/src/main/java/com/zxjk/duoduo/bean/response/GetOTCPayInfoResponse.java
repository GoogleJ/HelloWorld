package com.zxjk.duoduo.bean.response;

import java.util.List;

public class GetOTCPayInfoResponse {

    /**
     * payTypeList : [{"payType":"ALIPAY","open":1},{"payType":"WEIXIN","open":1},{"payType":"EBANK","open":1}]
     * trade : 1
     */

    private int trade;
    private List<PayTypeListBean> payTypeList;

    public int getTrade() {
        return trade;
    }

    public void setTrade(int trade) {
        this.trade = trade;
    }

    public List<PayTypeListBean> getPayTypeList() {
        return payTypeList;
    }

    public void setPayTypeList(List<PayTypeListBean> payTypeList) {
        this.payTypeList = payTypeList;
    }

    public static class PayTypeListBean {
        /**
         * payType : ALIPAY
         * open : 1
         */

        private String payType;
        private int open;

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }

        public int getOpen() {
            return open;
        }

        public void setOpen(int open) {
            this.open = open;
        }
    }
}
