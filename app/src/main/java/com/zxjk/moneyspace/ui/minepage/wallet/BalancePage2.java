package com.zxjk.moneyspace.ui.minepage.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetBalanceInfoResponse;
import com.zxjk.moneyspace.ui.base.BaseFragment;
import com.zxjk.moneyspace.utils.GlideUtil;

import java.util.List;

public class BalancePage2 extends BaseFragment {
    private RecyclerView recycler;
    private BaseQuickAdapter<GetBalanceInfoResponse.BalanceListBean, BaseViewHolder> adapter;

    private boolean isShow;
    private String hideStr = "********";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.balancepage2, container, false);
        recycler = rootView.findViewById(R.id.recycler);
        initList();

        return rootView;
    }

    private void initList() {
        adapter = new BaseQuickAdapter<GetBalanceInfoResponse.BalanceListBean, BaseViewHolder>(R.layout.item_balancelist) {
            @Override
            protected void convert(BaseViewHolder helper, GetBalanceInfoResponse.BalanceListBean item) {
                ImageView ivIcon = helper.getView(R.id.ivIcon);
                GlideUtil.loadNormalImg(ivIcon, item.getLogo());

                helper.setText(R.id.tvCoin, item.getCurrencyName())
                        .setText(R.id.tvMoney1, isShow ? item.getBalanceSum() : hideStr)
                        .setText(R.id.tvMoney2, isShow ? ("≈" + item.getPriceToCny() + "CNY") : hideStr);
            }
        };
        adapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(getActivity(), BalanceDetailActivity.class);
            intent.putExtra("data", (Parcelable) adapter.getData().get(position));
            startActivity(intent);
        });
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);
    }

    public void showOrHide(boolean isShow) {
        if (adapter != null) {
            this.isShow = isShow;
            adapter.notifyDataSetChanged();
        }
    }

    public void showData(List<GetBalanceInfoResponse.BalanceListBean> balanceList) {
        if (adapter != null) {
            adapter.setNewData(balanceList);
        }
    }
}
