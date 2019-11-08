package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class GetSymbolSerialResponse implements Parcelable {

    /**
     * symbolSerialDTOS : [{"id":"312","serialNumber":"1573182569137639","amount":"0.02000","serialType":"0","symbol":"USDT","createTime":"1573182569137","source":"14","serialTitle":"签到","month":"2019.11","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571730297884.png?Expires=1887090289&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=bPy9HiwHDeTJkw0LVCqYKivrgs8%3D","income":"117.84350","expenditure":"175.24367"},{"id":"307","serialNumber":"1573178906231614","amount":"5.00000","serialType":"1","symbol":"USDT","createTime":"1573178906231","source":"0","serialTitle":"红包","month":"2019.11","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571729863640.png?Expires=1887089855&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=l8UqEGla8JIQDRtGl5HExztPQ1I%3D","income":"117.84350","expenditure":"175.24367"},{"id":"298","serialNumber":"1573123336335688","amount":"0.01000","serialType":"0","symbol":"USDT","createTime":"1573123336335","source":"14","serialTitle":"签到","month":"2019.11","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571730297884.png?Expires=1887090289&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=bPy9HiwHDeTJkw0LVCqYKivrgs8%3D","income":"117.84350","expenditure":"175.24367"},{"id":"245","serialNumber":"157312087019390425","amount":"1.00200","serialType":"1","symbol":"USDT","createTime":"1573120870226","source":"9","serialTitle":"提币","month":"2019.11","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571730147208.png?Expires=1887090143&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=10KdF42Vjv1Wp%2FGEvC%2FtdWG%2BWVs%3D","income":"117.84350","expenditure":"175.24367"},{"id":"243","serialNumber":"157312036889664141","amount":"2.00200","serialType":"1","symbol":"USDT","createTime":"1573120368926","source":"9","serialTitle":"提币","month":"2019.11","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571730147208.png?Expires=1887090143&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=10KdF42Vjv1Wp%2FGEvC%2FtdWG%2BWVs%3D","income":"117.84350","expenditure":"175.24367"},{"id":"237","serialNumber":"157312021883635643","amount":"1.00200","serialType":"1","symbol":"USDT","createTime":"1573120218862","source":"9","serialTitle":"提币","month":"2019.11","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571730147208.png?Expires=1887090143&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=10KdF42Vjv1Wp%2FGEvC%2FtdWG%2BWVs%3D","income":"117.84350","expenditure":"175.24367"},{"id":"227","serialNumber":"157311907084366932","amount":"1.00200","serialType":"1","symbol":"USDT","createTime":"1573119070874","source":"9","serialTitle":"提币","month":"2019.11","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571730147208.png?Expires=1887090143&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=10KdF42Vjv1Wp%2FGEvC%2FtdWG%2BWVs%3D","income":"117.84350","expenditure":"175.24367"},{"id":"216","serialNumber":"6526364673050368","amount":"0.01000","serialType":"0","symbol":"USDT","createTime":"1573028845704","source":"0","serialTitle":"红包","month":"2019.11","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571729863640.png?Expires=1887089855&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=l8UqEGla8JIQDRtGl5HExztPQ1I%3D","income":"117.84350","expenditure":"175.24367"},{"id":"215","serialNumber":"1541014242741589","amount":"0.01000","serialType":"1","symbol":"USDT","createTime":"1573027164689","source":"0","serialTitle":"红包","month":"2019.11","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571729863640.png?Expires=1887089855&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=l8UqEGla8JIQDRtGl5HExztPQ1I%3D","income":"117.84350","expenditure":"175.24367"},{"id":"206","serialNumber":"1481834675195082","amount":"9.48900","serialType":"0","symbol":"USDT","createTime":"1572924637297","source":"21","serialTitle":"商城消费","month":"2019.11","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571730223132.png?Expires=1887090221&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=6CyhVZCkNOvnGMVo3C%2Bb2BvaQxo%3D","income":"117.84350","expenditure":"175.24367"}]
     * balanceAddress : 21312312312
     */

    private String balanceAddress;
    private String canTransfer;
    private List<SymbolSerialDTOSBean> symbolSerialDTOS;

    public String getCanTransfer() {
        return canTransfer;
    }

    public void setCanTransfer(String canTransfer) {
        this.canTransfer = canTransfer;
    }

    public String getBalanceAddress() {
        return balanceAddress;
    }

    public void setBalanceAddress(String balanceAddress) {
        this.balanceAddress = balanceAddress;
    }

    public List<SymbolSerialDTOSBean> getSymbolSerialDTOS() {
        return symbolSerialDTOS;
    }

    public void setSymbolSerialDTOS(List<SymbolSerialDTOSBean> symbolSerialDTOS) {
        this.symbolSerialDTOS = symbolSerialDTOS;
    }

    public static class SymbolSerialDTOSBean implements Parcelable {
        /**
         * id : 312
         * serialNumber : 1573182569137639
         * amount : 0.02000
         * serialType : 0
         * symbol : USDT
         * createTime : 1573182569137
         * source : 14
         * serialTitle : 签到
         * month : 2019.11
         * logo : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571730297884.png?Expires=1887090289&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=bPy9HiwHDeTJkw0LVCqYKivrgs8%3D
         * income : 117.84350
         * expenditure : 175.24367
         */

        private String id;
        private String serialNumber;
        private String amount;
        private String serialType;
        private String symbol;
        private String createTime;
        private String source;
        private String serialTitle;
        private String month;
        private String logo;
        private String income;
        private String expenditure;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getSerialType() {
            return serialType;
        }

        public void setSerialType(String serialType) {
            this.serialType = serialType;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getSerialTitle() {
            return serialTitle;
        }

        public void setSerialTitle(String serialTitle) {
            this.serialTitle = serialTitle;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getIncome() {
            return income;
        }

        public void setIncome(String income) {
            this.income = income;
        }

        public String getExpenditure() {
            return expenditure;
        }

        public void setExpenditure(String expenditure) {
            this.expenditure = expenditure;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.serialNumber);
            dest.writeString(this.amount);
            dest.writeString(this.serialType);
            dest.writeString(this.symbol);
            dest.writeString(this.createTime);
            dest.writeString(this.source);
            dest.writeString(this.serialTitle);
            dest.writeString(this.month);
            dest.writeString(this.logo);
            dest.writeString(this.income);
            dest.writeString(this.expenditure);
        }

        public SymbolSerialDTOSBean() {
        }

        protected SymbolSerialDTOSBean(Parcel in) {
            this.id = in.readString();
            this.serialNumber = in.readString();
            this.amount = in.readString();
            this.serialType = in.readString();
            this.symbol = in.readString();
            this.createTime = in.readString();
            this.source = in.readString();
            this.serialTitle = in.readString();
            this.month = in.readString();
            this.logo = in.readString();
            this.income = in.readString();
            this.expenditure = in.readString();
        }

        public static final Creator<SymbolSerialDTOSBean> CREATOR = new Creator<SymbolSerialDTOSBean>() {
            @Override
            public SymbolSerialDTOSBean createFromParcel(Parcel source) {
                return new SymbolSerialDTOSBean(source);
            }

            @Override
            public SymbolSerialDTOSBean[] newArray(int size) {
                return new SymbolSerialDTOSBean[size];
            }
        };
    }

    public GetSymbolSerialResponse() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.balanceAddress);
        dest.writeString(this.canTransfer);
        dest.writeTypedList(this.symbolSerialDTOS);
    }

    protected GetSymbolSerialResponse(Parcel in) {
        this.balanceAddress = in.readString();
        this.canTransfer = in.readString();
        this.symbolSerialDTOS = in.createTypedArrayList(SymbolSerialDTOSBean.CREATOR);
    }

    public static final Creator<GetSymbolSerialResponse> CREATOR = new Creator<GetSymbolSerialResponse>() {
        @Override
        public GetSymbolSerialResponse createFromParcel(Parcel source) {
            return new GetSymbolSerialResponse(source);
        }

        @Override
        public GetSymbolSerialResponse[] newArray(int size) {
            return new GetSymbolSerialResponse[size];
        }
    };
}
