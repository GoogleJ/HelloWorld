package com.zxjk.duoduo.bean.response;

import java.util.List;

public class ReceivePointResponse {
    private String money;
    private List<GetSignListResponse.PointsListBean> pointsList;

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
