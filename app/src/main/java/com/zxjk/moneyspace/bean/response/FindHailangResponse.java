package com.zxjk.moneyspace.bean.response;

public class FindHailangResponse {

    /**
     * payment_type : 2
     * otc_active : {"active_id":1412,"user_id":943652,"symbol":"USDT/CNY","currency":"USDT","currency_type":"CNY","side":2,"type":2,"float_rate":"1.08","price":"7.6","amount":"199.6","total":"1517.6418336","fee":"0.4","fee_rate":"0","quota":"100~1491.01","freezed":"0","executed":"0","state":1,"operate_state":1,"kyc_level":2,"create_time":1582711849672,"update_time":0,"register_time":1582711828788,"remark":"提示：1.请使用实名认证付款方式 2.请按订单金额付款 3.付款时无需备注","role":1,"appeal":false,"pay_time_avg":0,"issue_time_avg":0}
     */

    private int payment_type;
    private OtcActiveBean otc_active;

    public int getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(int payment_type) {
        this.payment_type = payment_type;
    }

    public OtcActiveBean getOtc_active() {
        return otc_active;
    }

    public void setOtc_active(OtcActiveBean otc_active) {
        this.otc_active = otc_active;
    }

    public static class OtcActiveBean {
        /**
         * active_id : 1412
         * user_id : 943652
         * symbol : USDT/CNY
         * currency : USDT
         * currency_type : CNY
         * side : 2
         * type : 2
         * float_rate : 1.08
         * price : 7.6
         * amount : 199.6
         * total : 1517.6418336
         * fee : 0.4
         * fee_rate : 0
         * quota : 100~1491.01
         * freezed : 0
         * executed : 0
         * state : 1
         * operate_state : 1
         * kyc_level : 2
         * create_time : 1582711849672
         * update_time : 0
         * register_time : 1582711828788
         * remark : 提示：1.请使用实名认证付款方式 2.请按订单金额付款 3.付款时无需备注
         * role : 1
         * appeal : false
         * pay_time_avg : 0
         * issue_time_avg : 0
         */

        private int active_id;
        private int user_id;
        private String symbol;
        private String currency;
        private String currency_type;
        private int side;
        private int type;
        private String float_rate;
        private String price;
        private String amount;
        private String total;
        private String fee;
        private String fee_rate;
        private String quota;
        private String freezed;
        private String executed;
        private int state;
        private int operate_state;
        private int kyc_level;
        private String create_time;
        private String update_time;
        private long register_time;
        private String remark;
        private int role;
        private boolean appeal;
        private int pay_time_avg;
        private int issue_time_avg;

        public int getActive_id() {
            return active_id;
        }

        public void setActive_id(int active_id) {
            this.active_id = active_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getCurrency_type() {
            return currency_type;
        }

        public void setCurrency_type(String currency_type) {
            this.currency_type = currency_type;
        }

        public int getSide() {
            return side;
        }

        public void setSide(int side) {
            this.side = side;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getFloat_rate() {
            return float_rate;
        }

        public void setFloat_rate(String float_rate) {
            this.float_rate = float_rate;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getFee_rate() {
            return fee_rate;
        }

        public void setFee_rate(String fee_rate) {
            this.fee_rate = fee_rate;
        }

        public String getQuota() {
            return quota;
        }

        public void setQuota(String quota) {
            this.quota = quota;
        }

        public String getFreezed() {
            return freezed;
        }

        public void setFreezed(String freezed) {
            this.freezed = freezed;
        }

        public String getExecuted() {
            return executed;
        }

        public void setExecuted(String executed) {
            this.executed = executed;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getOperate_state() {
            return operate_state;
        }

        public void setOperate_state(int operate_state) {
            this.operate_state = operate_state;
        }

        public int getKyc_level() {
            return kyc_level;
        }

        public void setKyc_level(int kyc_level) {
            this.kyc_level = kyc_level;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public long getRegister_time() {
            return register_time;
        }

        public void setRegister_time(long register_time) {
            this.register_time = register_time;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getRole() {
            return role;
        }

        public void setRole(int role) {
            this.role = role;
        }

        public boolean isAppeal() {
            return appeal;
        }

        public void setAppeal(boolean appeal) {
            this.appeal = appeal;
        }

        public int getPay_time_avg() {
            return pay_time_avg;
        }

        public void setPay_time_avg(int pay_time_avg) {
            this.pay_time_avg = pay_time_avg;
        }

        public int getIssue_time_avg() {
            return issue_time_avg;
        }

        public void setIssue_time_avg(int issue_time_avg) {
            this.issue_time_avg = issue_time_avg;
        }
    }
}
