package com.zxjk.moneyspace.bean.response;

import java.util.List;

public class GetRedPackageRecordResponse {
    /**
     * redpackageList : [{"nick":"阳","redPackageId":111,"money":4,"createTime":"1554679683111","message":"4","TYPE":"2","redPackageType":"2"},{"nick":"阳","redPackageId":110,"money":44,"createTime":"1554679551337","message":"44","TYPE":"2","redPackageType":"2"},{"nick":"阳","redPackageId":109,"money":6,"createTime":"1554679409990","message":"6","TYPE":"2","redPackageType":"2"},{"nick":"阳","redPackageId":108,"money":4,"createTime":"1554679054536","message":"4","TYPE":"2","redPackageType":"2"},{"nick":"阳","redPackageId":107,"money":7,"createTime":"1554678685772","message":"7","TYPE":"2","redPackageType":"2"},{"nick":"阳","redPackageId":106,"money":3,"createTime":"1554678481607","message":"3","TYPE":"2","redPackageType":"2"},{"nick":"阳","redPackageId":105,"money":55,"createTime":"1554677443994","message":"55","TYPE":"2","redPackageType":"2"},{"nick":"阳","redPackageId":104,"money":5,"createTime":"1554653710803","message":"5","TYPE":"2","redPackageType":"2"},{"nick":"阳","redPackageId":103,"money":8,"createTime":"1554653515398","message":"8","TYPE":"2","redPackageType":"2"},{"nick":"阳","redPackageId":102,"money":55,"createTime":"1554653173553","message":"55","TYPE":"2","redPackageType":"2"},{"nick":"阳","redPackageId":101,"money":6,"createTime":"1554652835029","message":"6","TYPE":"2","redPackageType":"2"},{"nick":"阳","redPackageId":100,"money":1,"createTime":"1554539101125","message":"0.00","TYPE":"2","redPackageType":"2"}]
     * totalRecord : {"count":12,"total_money":198}
     */

    private TotalRecordBean totalRecord;
    private List<RedpackageListBean> redpackageList;

    public TotalRecordBean getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(TotalRecordBean totalRecord) {
        this.totalRecord = totalRecord;
    }

    public List<RedpackageListBean> getRedpackageList() {
        return redpackageList;
    }

    public void setRedpackageList(List<RedpackageListBean> redpackageList) {
        this.redpackageList = redpackageList;
    }

    public static class TotalRecordBean {
        /**
         * count : 12
         * total_money : 198.0
         */

        private int count;
        private double total_money;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public double getTotal_money() {
            return total_money;
        }

        public void setTotal_money(double total_money) {
            this.total_money = total_money;
        }
    }

    public static class RedpackageListBean {
        /**
         * nick : 阳
         * redPackageId : 111
         * money : 4.0
         * createTime : 1554679683111
         * message : 4
         * TYPE : 2
         * redPackageType : 2
         */

        private String nick;
        private int redPackageId;
        private double money;
        private String createTime;
        private String message;
        private String TYPE;
        private String redPackageType;

        @Override
        public String toString() {
            return "RedpackageListBean{" +
                    "nick='" + nick + '\'' +
                    ", redPackageId=" + redPackageId +
                    ", money=" + money +
                    ", createTime='" + createTime + '\'' +
                    ", message='" + message + '\'' +
                    ", TYPE='" + TYPE + '\'' +
                    ", redPackageType='" + redPackageType + '\'' +
                    '}';
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public int getRedPackageId() {
            return redPackageId;
        }

        public void setRedPackageId(int redPackageId) {
            this.redPackageId = redPackageId;
        }

        public double getMoney() {
            return money;
        }

        public void setMoney(double money) {
            this.money = money;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTYPE() {
            return TYPE;
        }

        public void setTYPE(String TYPE) {
            this.TYPE = TYPE;
        }

        public String getRedPackageType() {
            return redPackageType;
        }

        public void setRedPackageType(String redPackageType) {
            this.redPackageType = redPackageType;
        }
    }
}
