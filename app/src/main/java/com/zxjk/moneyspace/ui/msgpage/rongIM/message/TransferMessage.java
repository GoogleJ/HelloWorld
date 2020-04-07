package com.zxjk.moneyspace.ui.msgpage.rongIM.message;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

@SuppressLint("ParcelCreator")
@MessageTag(value = "app:transfer", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class TransferMessage extends MessageContent {

    /**
     * 里头的所有字段实体可根据需求自己定义
     * toCustomerId 接受者用户Id
     * money 转账金额
     * payPwd 支付密码
     * remark 转账说明
     */
    private String money;
    private String remark;
    private String type;
    private String transferId;
    private String name;
    private String extra;
    private String fromCustomer;
    private String symbol;

    public TransferMessage() {
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("money", getMoney());
            jsonObj.put("remark", getRemark());
            jsonObj.put("type", getType());
            jsonObj.put("transferId", getTransferId());
            jsonObj.put("name", getName());
            jsonObj.put("extra", getExtra());
            jsonObj.put("fromCustomer", getFromCustomerId());
            jsonObj.put("symbol", getSymbol());
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public TransferMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            if (jsonObj.has("money")) {
                setMoney(jsonObj.optString("money"));
            }

            if (jsonObj.has("remark")) {
                setRemark(jsonObj.optString("remark"));
            }

            if (jsonObj.has("type")) {
                setType(jsonObj.optString("type"));
            }

            if (jsonObj.has("transferId")) {
                setTransferId(jsonObj.optString("transferId"));
            }

            if (jsonObj.has("name")) {
                setName(jsonObj.optString("name"));
            }

            if (jsonObj.has("extra")) {
                setExtra(jsonObj.optString("extra"));
            }

            if (jsonObj.has("fromCustomer")) {
                setFromCustomerId(jsonObj.optString("fromCustomer"));
            }

            if (jsonObj.has("symbol")) {
                setSymbol(jsonObj.optString("symbol"));
            }

        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, money);
        ParcelUtils.writeToParcel(dest, remark);
        ParcelUtils.writeToParcel(dest, type);
        ParcelUtils.writeToParcel(dest, transferId);
        ParcelUtils.writeToParcel(dest, name);
        ParcelUtils.writeToParcel(dest, extra);
        ParcelUtils.writeToParcel(dest, fromCustomer);
        ParcelUtils.writeToParcel(dest, symbol);
    }

    //给消息赋值。
    public TransferMessage(Parcel in) {
        //这里可继续增加你消息的属性
        setMoney(ParcelUtils.readFromParcel(in));
        setRemark(ParcelUtils.readFromParcel(in));
        setType(ParcelUtils.readFromParcel(in));
        setTransferId(ParcelUtils.readFromParcel(in));
        setName(ParcelUtils.readFromParcel(in));
        setExtra(ParcelUtils.readFromParcel(in));
        setFromCustomerId(ParcelUtils.readFromParcel(in));
        setSymbol(ParcelUtils.readFromParcel(in));
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<TransferMessage> CREATOR = new Creator<TransferMessage>() {

        @Override
        public TransferMessage createFromParcel(Parcel source) {
            return new TransferMessage(source);
        }

        @Override
        public TransferMessage[] newArray(int size) {
            return new TransferMessage[size];
        }
    };

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getFromCustomerId() {
        return fromCustomer;
    }

    public void setFromCustomerId(String fromCustomerId) {
        this.fromCustomer = fromCustomerId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
