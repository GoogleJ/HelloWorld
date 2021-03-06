package com.zxjk.duoduo.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CurrencyInfosByCustomerResponse implements Parcelable {

    /**
     * balanceTotal : 3391.15
     * symbolList : [{"symbol":"ETH","sumBalance":2.599327944,"logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D","symbolInfos":[{"symbol":"ETH","balance":"0","importMethod":"1","contractAddress":"","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D","walletAddress":"0xcba4c84f375d564a6d1c991683e3361fb83f1c1b","balanceToCNY":"0.00"},{"symbol":"ETH","balance":"0","importMethod":"0","contractAddress":"","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D","walletAddress":"0x26014d4b627b40e180c4f28bfce323679bfa7115","balanceToCNY":"0.00"},{"symbol":"ETH","balance":"2.599327944","importMethod":"2","contractAddress":"","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D","walletAddress":"0x424b9f1b44878fcac66ff1ee900721d946176a85","balanceToCNY":"3391.15"},{"symbol":"ETH","balance":"0","importMethod":"3","contractAddress":"","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D","walletAddress":"0x1f8e3f1b94b08fc6e9335749ff0928487f19bbd9","balanceToCNY":"0.00"},{"symbol":"ETH","balance":"0","importMethod":"3","contractAddress":"","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D","walletAddress":"0x429ce3e4d5f7a6adff81c28f2db87ab680da1e5b","balanceToCNY":"0.00"}],"sumBalanceToCny":3391.15}]
     */

    private String balanceTotal;
    private List<SymbolListBean> symbolList;

    public String getBalanceTotal() {
        return balanceTotal;
    }

    public void setBalanceTotal(String balanceTotal) {
        this.balanceTotal = balanceTotal;
    }

    public List<SymbolListBean> getSymbolList() {
        return symbolList;
    }

    public void setSymbolList(List<SymbolListBean> symbolList) {
        this.symbolList = symbolList;
    }

    public static class SymbolListBean implements Parcelable {

        /**
         * symbol : ETH
         * sumBalance : 2.599327944
         * logo : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D
         * symbolInfos : [{"symbol":"ETH","balance":"0","importMethod":"1","contractAddress":"","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D","walletAddress":"0xcba4c84f375d564a6d1c991683e3361fb83f1c1b","balanceToCNY":"0.00"},{"symbol":"ETH","balance":"0","importMethod":"0","contractAddress":"","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D","walletAddress":"0x26014d4b627b40e180c4f28bfce323679bfa7115","balanceToCNY":"0.00"},{"symbol":"ETH","balance":"2.599327944","importMethod":"2","contractAddress":"","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D","walletAddress":"0x424b9f1b44878fcac66ff1ee900721d946176a85","balanceToCNY":"3391.15"},{"symbol":"ETH","balance":"0","importMethod":"3","contractAddress":"","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D","walletAddress":"0x1f8e3f1b94b08fc6e9335749ff0928487f19bbd9","balanceToCNY":"0.00"},{"symbol":"ETH","balance":"0","importMethod":"3","contractAddress":"","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D","walletAddress":"0x429ce3e4d5f7a6adff81c28f2db87ab680da1e5b","balanceToCNY":"0.00"}]
         * sumBalanceToCny : 3391.15
         */

        private String symbol;
        private double sumBalance;
        private String logo;
        private double sumBalanceToCny;
        private List<SymbolInfosBean> symbolInfos;

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public double getSumBalance() {
            return sumBalance;
        }

        public void setSumBalance(double sumBalance) {
            this.sumBalance = sumBalance;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public double getSumBalanceToCny() {
            return sumBalanceToCny;
        }

        public void setSumBalanceToCny(double sumBalanceToCny) {
            this.sumBalanceToCny = sumBalanceToCny;
        }

        public List<SymbolInfosBean> getSymbolInfos() {
            return symbolInfos;
        }

        public void setSymbolInfos(List<SymbolInfosBean> symbolInfos) {
            this.symbolInfos = symbolInfos;
        }

        public static class SymbolInfosBean implements Parcelable {

            /**
             * symbol : ETH
             * balance : 0
             * importMethod : 1
             * contractAddress :
             * logo : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D
             * walletAddress : 0xcba4c84f375d564a6d1c991683e3361fb83f1c1b
             * balanceToCNY : 0.00
             */

            private String symbol;
            private String balance;
            private String importMethod;
            private String contractAddress;
            private String logo;
            private String walletAddress;
            private String balanceToCNY;

            public String getSymbol() {
                return symbol;
            }

            public void setSymbol(String symbol) {
                this.symbol = symbol;
            }

            public String getBalance() {
                return balance;
            }

            public void setBalance(String balance) {
                this.balance = balance;
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

            public String getBalanceToCNY() {
                return balanceToCNY;
            }

            public void setBalanceToCNY(String balanceToCNY) {
                this.balanceToCNY = balanceToCNY;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.symbol);
                dest.writeString(this.balance);
                dest.writeString(this.importMethod);
                dest.writeString(this.contractAddress);
                dest.writeString(this.logo);
                dest.writeString(this.walletAddress);
                dest.writeString(this.balanceToCNY);
            }

            public SymbolInfosBean() {
            }

            protected SymbolInfosBean(Parcel in) {
                this.symbol = in.readString();
                this.balance = in.readString();
                this.importMethod = in.readString();
                this.contractAddress = in.readString();
                this.logo = in.readString();
                this.walletAddress = in.readString();
                this.balanceToCNY = in.readString();
            }

            public static final Creator<SymbolInfosBean> CREATOR = new Creator<SymbolInfosBean>() {
                @Override
                public SymbolInfosBean createFromParcel(Parcel source) {
                    return new SymbolInfosBean(source);
                }

                @Override
                public SymbolInfosBean[] newArray(int size) {
                    return new SymbolInfosBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.symbol);
            dest.writeDouble(this.sumBalance);
            dest.writeString(this.logo);
            dest.writeDouble(this.sumBalanceToCny);
            dest.writeList(this.symbolInfos);
        }

        public SymbolListBean() {
        }

        protected SymbolListBean(Parcel in) {
            this.symbol = in.readString();
            this.sumBalance = in.readDouble();
            this.logo = in.readString();
            this.sumBalanceToCny = in.readDouble();
            this.symbolInfos = new ArrayList<SymbolInfosBean>();
            in.readList(this.symbolInfos, SymbolInfosBean.class.getClassLoader());
        }

        public static final Creator<SymbolListBean> CREATOR = new Creator<SymbolListBean>() {
            @Override
            public SymbolListBean createFromParcel(Parcel source) {
                return new SymbolListBean(source);
            }

            @Override
            public SymbolListBean[] newArray(int size) {
                return new SymbolListBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.balanceTotal);
        dest.writeList(this.symbolList);
    }

    public CurrencyInfosByCustomerResponse() {
    }

    protected CurrencyInfosByCustomerResponse(Parcel in) {
        this.balanceTotal = in.readString();
        this.symbolList = new ArrayList<SymbolListBean>();
        in.readList(this.symbolList, SymbolListBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<CurrencyInfosByCustomerResponse> CREATOR = new Parcelable.Creator<CurrencyInfosByCustomerResponse>() {
        @Override
        public CurrencyInfosByCustomerResponse createFromParcel(Parcel source) {
            return new CurrencyInfosByCustomerResponse(source);
        }

        @Override
        public CurrencyInfosByCustomerResponse[] newArray(int size) {
            return new CurrencyInfosByCustomerResponse[size];
        }
    };
}
