package com.zxjk.duoduo.bean.response;

import java.util.List;

public class ReceivePointResponse {
    private String money;
    private String receivePoint;
    private String symbol;
    private List<GetSignListResponse.PointsListBean> pointsList;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getReceivePoint() {
        return receivePoint;
    }

    public void setReceivePoint(String receivePoint) {
        this.receivePoint = receivePoint;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public List<GetSignListResponse.PointsListBean> getPointsList() {
        return pointsList;
    }

    public void setPointsList(List<GetSignListResponse.PointsListBean> pointsList) {
        this.pointsList = pointsList;
    }
}
