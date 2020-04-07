package com.zxjk.moneyspace.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.ArrayList;
import java.util.List;

public class WalletChainInfosResponse implements Parcelable {

    /**
     * balanceTotal : 0.00
     * isExist : 1
     * symbolList : [{"symbol":"ETH","sumBalance":"0.00","sumBalanceToCny":"0.00","logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D","symbolInfos":[{"customerId":"","symbol":"ETH","walletAddress":"0xfb0226b560cf4cff7231d8b1a21381ae77a0c4cb","walletMnemonic":"","walletPrivateKey":"","walletKeystore":"","createTime":"","updateTime":"","isDelete":"","walletName":"","importMethod":"3","balance":"0.00","balanceToCNY":"0.00","contractAddress":"","coinType":"0","decimals":18,"tokenDecimal":0,"logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D","parentSymbol":"ETH"}],"walletAddress":"0xfb0226b560cf4cff7231d8b1a21381ae77a0c4cb","coinType":"0","parentSymbol":"ETH","tokenDecimal":18,"contractAddress":""}]
     */

    private String balanceTotal;
    private int isExist;
    private List<SymbolListBean> symbolList;

    public String getBalanceTotal() {
        return balanceTotal;
    }

    public void setBalanceTotal(String balanceTotal) {
        this.balanceTotal = balanceTotal;
    }

    public int getIsExist() {
        return isExist;
    }

    public void setIsExist(int isExist) {
        this.isExist = isExist;
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
         * sumBalance : 0.00
         * sumBalanceToCny : 0.00
         * logo : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D
         * symbolInfos : [{"customerId":"","symbol":"ETH","walletAddress":"0xfb0226b560cf4cff7231d8b1a21381ae77a0c4cb","walletMnemonic":"","walletPrivateKey":"","walletKeystore":"","createTime":"","updateTime":"","isDelete":"","walletName":"","importMethod":"3","balance":"0.00","balanceToCNY":"0.00","contractAddress":"","coinType":"0","decimals":18,"tokenDecimal":0,"logo":"http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D","parentSymbol":"ETH"}]
         * walletAddress : 0xfb0226b560cf4cff7231d8b1a21381ae77a0c4cb
         * coinType : 0
         * parentSymbol : ETH
         * tokenDecimal : 18
         * contractAddress :
         */

        private String symbol;
        private String sumBalance;
        private String sumBalanceToCny;
        private String logo;
        private String walletAddress;
        private String coinType;
        private String parentSymbol;
        private int tokenDecimal;
        private String contractAddress;
        private List<SymbolInfosBean> symbolInfos;

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getSumBalance() {
            return sumBalance;
        }

        public void setSumBalance(String sumBalance) {
            this.sumBalance = sumBalance;
        }

        public String getSumBalanceToCny() {
            return sumBalanceToCny;
        }

        public void setSumBalanceToCny(String sumBalanceToCny) {
            this.sumBalanceToCny = sumBalanceToCny;
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

        public String getCoinType() {
            return coinType;
        }

        public void setCoinType(String coinType) {
            this.coinType = coinType;
        }

        public String getParentSymbol() {
            return parentSymbol;
        }

        public void setParentSymbol(String parentSymbol) {
            this.parentSymbol = parentSymbol;
        }

        public int getTokenDecimal() {
            return tokenDecimal;
        }

        public void setTokenDecimal(int tokenDecimal) {
            this.tokenDecimal = tokenDecimal;
        }

        public String getContractAddress() {
            return contractAddress;
        }

        public void setContractAddress(String contractAddress) {
            this.contractAddress = contractAddress;
        }

        public List<SymbolInfosBean> getSymbolInfos() {
            return symbolInfos;
        }

        public void setSymbolInfos(List<SymbolInfosBean> symbolInfos) {
            this.symbolInfos = symbolInfos;
        }

        public static class SymbolInfosBean implements Parcelable, MultiItemEntity {
            /**
             * customerId :
             * symbol : ETH
             * walletAddress : 0xfb0226b560cf4cff7231d8b1a21381ae77a0c4cb
             * walletMnemonic :
             * walletPrivateKey :
             * walletKeystore :
             * createTime :
             * updateTime :
             * isDelete :
             * walletName :
             * importMethod : 3
             * balance : 0.00
             * balanceToCNY : 0.00
             * contractAddress :
             * coinType : 0
             * decimals : 18
             * tokenDecimal : 0
             * logo : http://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1571042699243.png?Expires=1886402690&OSSAccessKeyId=LTAI3V54BzteDdTi&Signature=a619rkpjn7POYrmLP1vZ3KpOm5k%3D
             * parentSymbol : ETH
             */

            private String customerId;
            private String symbol;
            private String walletAddress;
            private String walletMnemonic;
            private String walletPrivateKey;
            private String walletKeystore;
            private String createTime;
            private String updateTime;
            private String isDelete;
            private String walletName;
            private String importMethod;
            private String balance;
            private String balanceToCNY;
            private String contractAddress;
            private String coinType;
            private int decimals;
            private int tokenDecimal;
            private String logo;
            private String parentSymbol;
            private boolean isLast;

            public boolean isLast() {
                return isLast;
            }

            public void setLast(boolean last) {
                isLast = last;
            }

            public String getCustomerId() {
                return customerId;
            }

            public void setCustomerId(String customerId) {
                this.customerId = customerId;
            }

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

            public String getWalletMnemonic() {
                return walletMnemonic;
            }

            public void setWalletMnemonic(String walletMnemonic) {
                this.walletMnemonic = walletMnemonic;
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

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(String updateTime) {
                this.updateTime = updateTime;
            }

            public String getIsDelete() {
                return isDelete;
            }

            public void setIsDelete(String isDelete) {
                this.isDelete = isDelete;
            }

            public String getWalletName() {
                return walletName;
            }

            public void setWalletName(String walletName) {
                this.walletName = walletName;
            }

            public String getImportMethod() {
                return importMethod;
            }

            public void setImportMethod(String importMethod) {
                this.importMethod = importMethod;
            }

            public String getBalance() {
                return balance;
            }

            public void setBalance(String balance) {
                this.balance = balance;
            }

            public String getBalanceToCNY() {
                return balanceToCNY;
            }

            public void setBalanceToCNY(String balanceToCNY) {
                this.balanceToCNY = balanceToCNY;
            }

            public String getContractAddress() {
                return contractAddress;
            }

            public void setContractAddress(String contractAddress) {
                this.contractAddress = contractAddress;
            }

            public String getCoinType() {
                return coinType;
            }

            public void setCoinType(String coinType) {
                this.coinType = coinType;
            }

            public int getDecimals() {
                return decimals;
            }

            public void setDecimals(int decimals) {
                this.decimals = decimals;
            }

            public int getTokenDecimal() {
                return tokenDecimal;
            }

            public void setTokenDecimal(int tokenDecimal) {
                this.tokenDecimal = tokenDecimal;
            }

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public String getParentSymbol() {
                return parentSymbol;
            }

            public void setParentSymbol(String parentSymbol) {
                this.parentSymbol = parentSymbol;
            }

            public SymbolInfosBean() {
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.customerId);
                dest.writeString(this.symbol);
                dest.writeString(this.walletAddress);
                dest.writeString(this.walletMnemonic);
                dest.writeString(this.walletPrivateKey);
                dest.writeString(this.walletKeystore);
                dest.writeString(this.createTime);
                dest.writeString(this.updateTime);
                dest.writeString(this.isDelete);
                dest.writeString(this.walletName);
                dest.writeString(this.importMethod);
                dest.writeString(this.balance);
                dest.writeString(this.balanceToCNY);
                dest.writeString(this.contractAddress);
                dest.writeString(this.coinType);
                dest.writeInt(this.decimals);
                dest.writeInt(this.tokenDecimal);
                dest.writeString(this.logo);
                dest.writeString(this.parentSymbol);
                dest.writeByte(this.isLast ? (byte) 1 : (byte) 0);
            }

            protected SymbolInfosBean(Parcel in) {
                this.customerId = in.readString();
                this.symbol = in.readString();
                this.walletAddress = in.readString();
                this.walletMnemonic = in.readString();
                this.walletPrivateKey = in.readString();
                this.walletKeystore = in.readString();
                this.createTime = in.readString();
                this.updateTime = in.readString();
                this.isDelete = in.readString();
                this.walletName = in.readString();
                this.importMethod = in.readString();
                this.balance = in.readString();
                this.balanceToCNY = in.readString();
                this.contractAddress = in.readString();
                this.coinType = in.readString();
                this.decimals = in.readInt();
                this.tokenDecimal = in.readInt();
                this.logo = in.readString();
                this.parentSymbol = in.readString();
                this.isLast = in.readByte() != 0;
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

            @Override
            public int getItemType() {
                return 2;
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.symbol);
            dest.writeString(this.sumBalance);
            dest.writeString(this.sumBalanceToCny);
            dest.writeString(this.logo);
            dest.writeString(this.walletAddress);
            dest.writeString(this.coinType);
            dest.writeString(this.parentSymbol);
            dest.writeInt(this.tokenDecimal);
            dest.writeString(this.contractAddress);
            dest.writeList(this.symbolInfos);
        }

        public SymbolListBean() {
        }

        protected SymbolListBean(Parcel in) {
            this.symbol = in.readString();
            this.sumBalance = in.readString();
            this.sumBalanceToCny = in.readString();
            this.logo = in.readString();
            this.walletAddress = in.readString();
            this.coinType = in.readString();
            this.parentSymbol = in.readString();
            this.tokenDecimal = in.readInt();
            this.contractAddress = in.readString();
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
        dest.writeInt(this.isExist);
        dest.writeList(this.symbolList);
    }

    public WalletChainInfosResponse() {
    }

    protected WalletChainInfosResponse(Parcel in) {
        this.balanceTotal = in.readString();
        this.isExist = in.readInt();
        this.symbolList = new ArrayList<SymbolListBean>();
        in.readList(this.symbolList, SymbolListBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<WalletChainInfosResponse> CREATOR = new Parcelable.Creator<WalletChainInfosResponse>() {
        @Override
        public WalletChainInfosResponse createFromParcel(Parcel source) {
            return new WalletChainInfosResponse(source);
        }

        @Override
        public WalletChainInfosResponse[] newArray(int size) {
            return new WalletChainInfosResponse[size];
        }
    };
}
