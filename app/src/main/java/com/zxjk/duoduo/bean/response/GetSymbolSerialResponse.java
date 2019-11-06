package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class GetSymbolSerialResponse implements Parcelable {

    /**
     * symbolSerialDTOS : [{"income":"0.03000","expenditure":"0","month":"2019.11","serialList":[{"id":"90","serialNumber":"6116808975361229","amount":"0.01000","serialType":"0","symbol":"ETH","createTime":"1572614063365","source":"0","serialTitle":"红包","month":"2019.11","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571729863640.png?Expires=1887089855&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=l8UqEGla8JIQDRtGl5HExztPQ1I%3D"},{"id":"18","serialNumber":"7978597998573057","amount":"0.02000","serialType":"0","symbol":"ETH","createTime":"1572602053720","source":"14","serialTitle":"签到","month":"2019.11","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571730297884.png?Expires=1887090289&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=bPy9HiwHDeTJkw0LVCqYKivrgs8%3D"}]}]
     * balanceAddress : 0x8eb9612ee45cfa8a8b679e3fad6a1759e8f7d4cb
     */

    private String balanceAddress;
    private List<SymbolSerialDTOSBean> symbolSerialDTOS;

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
         * income : 0.03000
         * expenditure : 0
         * month : 2019.11
         * serialList : [{"id":"90","serialNumber":"6116808975361229","amount":"0.01000","serialType":"0","symbol":"ETH","createTime":"1572614063365","source":"0","serialTitle":"红包","month":"2019.11","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571729863640.png?Expires=1887089855&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=l8UqEGla8JIQDRtGl5HExztPQ1I%3D"},{"id":"18","serialNumber":"7978597998573057","amount":"0.02000","serialType":"0","symbol":"ETH","createTime":"1572602053720","source":"14","serialTitle":"签到","month":"2019.11","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571730297884.png?Expires=1887090289&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=bPy9HiwHDeTJkw0LVCqYKivrgs8%3D"}]
         */

        private String income;
        private String expenditure;
        private String month;
        private List<SerialListBean> serialList;

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

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public List<SerialListBean> getSerialList() {
            return serialList;
        }

        public void setSerialList(List<SerialListBean> serialList) {
            this.serialList = serialList;
        }

        public static class SerialListBean implements Parcelable {
            /**
             * id : 90
             * serialNumber : 6116808975361229
             * amount : 0.01000
             * serialType : 0
             * symbol : ETH
             * createTime : 1572614063365
             * source : 0
             * serialTitle : 红包
             * month : 2019.11
             * logo : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571729863640.png?Expires=1887089855&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=l8UqEGla8JIQDRtGl5HExztPQ1I%3D
             */

            private String id;
            private String serialNumber;
            private String amount;
            private String serialType;
            private String symbol;
            private String createTime;
            private String source;
            private String serialTitle;
            private String logo;

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

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
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
                dest.writeString(this.logo);
            }

            public SerialListBean() {
            }

            protected SerialListBean(Parcel in) {
                this.id = in.readString();
                this.serialNumber = in.readString();
                this.amount = in.readString();
                this.serialType = in.readString();
                this.symbol = in.readString();
                this.createTime = in.readString();
                this.source = in.readString();
                this.serialTitle = in.readString();
                this.logo = in.readString();
            }

            public static final Creator<SerialListBean> CREATOR = new Creator<SerialListBean>() {
                @Override
                public SerialListBean createFromParcel(Parcel source) {
                    return new SerialListBean(source);
                }

                @Override
                public SerialListBean[] newArray(int size) {
                    return new SerialListBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.income);
            dest.writeString(this.expenditure);
            dest.writeString(this.month);
            dest.writeList(this.serialList);
        }

        public SymbolSerialDTOSBean() {
        }

        protected SymbolSerialDTOSBean(Parcel in) {
            this.income = in.readString();
            this.expenditure = in.readString();
            this.month = in.readString();
            this.serialList = new ArrayList<SerialListBean>();
            in.readList(this.serialList, SerialListBean.class.getClassLoader());
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.balanceAddress);
        dest.writeList(this.symbolSerialDTOS);
    }

    public GetSymbolSerialResponse() {
    }

    protected GetSymbolSerialResponse(Parcel in) {
        this.balanceAddress = in.readString();
        this.symbolSerialDTOS = new ArrayList<SymbolSerialDTOSBean>();
        in.readList(this.symbolSerialDTOS, SymbolSerialDTOSBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<GetSymbolSerialResponse> CREATOR = new Parcelable.Creator<GetSymbolSerialResponse>() {
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
