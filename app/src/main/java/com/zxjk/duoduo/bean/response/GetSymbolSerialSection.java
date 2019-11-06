package com.zxjk.duoduo.bean.response;

import com.chad.library.adapter.base.entity.SectionEntity;

public class GetSymbolSerialSection extends SectionEntity<GetSymbolSerialResponse.SymbolSerialDTOSBean.SerialListBean> {

    private String month;
    private String income;
    private String expenditure;

    public GetSymbolSerialSection(GetSymbolSerialResponse.SymbolSerialDTOSBean.SerialListBean bean, String month, String income, String expenditure) {
        super(bean);
        this.month = month;
        this.income = income;
        this.expenditure = expenditure;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
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
}
