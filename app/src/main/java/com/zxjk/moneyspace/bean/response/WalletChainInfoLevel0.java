package com.zxjk.moneyspace.bean.response;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

public class WalletChainInfoLevel0 extends AbstractExpandableItem<WalletChainInfosResponse.SymbolListBean.SymbolInfosBean> implements MultiItemEntity {

    private WalletChainInfosResponse.SymbolListBean bean;

    public WalletChainInfoLevel0(WalletChainInfosResponse.SymbolListBean bean) {
        this.bean = bean;
    }

    public WalletChainInfosResponse.SymbolListBean getBean() {
        return bean;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return 1;
    }
}
