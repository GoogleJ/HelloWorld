package com.zxjk.duoduo.ui.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.BarUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetBalanceInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseLazyFragment;
import com.zxjk.duoduo.ui.minepage.DetailListActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MMKVUtils;

public class WalletFragment extends BaseLazyFragment {
    private final int REQUEST_ADD = 2;

    private ImageView ivShowOrHide;
    private TextView tvList;
    private TextView tvManage;
    private TextView tvMoney2BTC;
    private TextView tvMoney2CNY;
    private RecyclerView recycler;
    private BaseQuickAdapter<GetBalanceInfoResponse.BalanceListBean, BaseViewHolder> adapter;

    private GetBalanceInfoResponse response;
    private boolean isShow;
    private String hideStr = "******";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.walletpage, container, false);

        View appbar = rootView.findViewById(R.id.appbar);
        appbar.setPadding(appbar.getPaddingStart(), appbar.getPaddingTop() + BarUtils.getStatusBarHeight(),
                appbar.getPaddingEnd(), appbar.getPaddingBottom());

        ivShowOrHide = rootView.findViewById(R.id.ivShowOrHide);
        tvList = rootView.findViewById(R.id.tvList);
        tvManage = rootView.findViewById(R.id.tvManage);
        tvMoney2BTC = rootView.findViewById(R.id.tvMoney2BTC);
        tvMoney2CNY = rootView.findViewById(R.id.tvMoney2CNY);
        recycler = rootView.findViewById(R.id.recycler);

        adapter = new BaseQuickAdapter<GetBalanceInfoResponse.BalanceListBean, BaseViewHolder>(R.layout.item_balancelist) {
            @Override
            protected void convert(BaseViewHolder helper, GetBalanceInfoResponse.BalanceListBean item) {
                ImageView ivIcon = helper.getView(R.id.ivIcon);
                GlideUtil.loadNormalImg(ivIcon, item.getLogo());

                helper.setText(R.id.tvCoin, item.getCurrencyName())
                        .setText(R.id.tvMoney1, isShow ? item.getBalanceSum() : hideStr)
                        .setText(R.id.tvMoney2, isShow ? ("≈ ¥ " + item.getPriceToCny()) : hideStr);
            }
        };

        adapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(getContext(), BalanceDetailActivity.class);
            intent.putExtra("data", (Parcelable) adapter.getData().get(position));
            startActivity(intent);
        });

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);
        isShow = MMKVUtils.getInstance().decodeBool("bahaviour2_showWalletBalance", true);

        ivShowOrHide.setOnClickListener(v -> {
            isShow = !isShow;
            MMKVUtils.getInstance().enCode("bahaviour2_showWalletBalance", isShow);
            showOrHide();
        });

        tvList.setOnClickListener(v -> startActivity(new Intent(getContext(), DetailListActivity.class)));

        tvManage.setOnClickListener(v -> startActivityForResult(new Intent(getContext(), BalanceShowItemActivity.class), REQUEST_ADD));

        rootView.findViewById(R.id.llBalanceWalletTop1).setOnClickListener(v -> startActivity(new Intent(getContext(), RecipetQRActivity.class)));

        rootView.findViewById(R.id.llBalanceWalletTop2).setOnClickListener(v ->
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .isExistWalletInfo()
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getContext())))
                        .compose(RxSchedulers.normalTrans())
                        .subscribe(s -> {
                            if (s.equals("0")) {
                                startActivity(new Intent(getContext(), BlockWalletEmptyActivity.class));
                            } else {
                                Intent intent = new Intent(getContext(), NewBlockWalletActivity.class);
                                startActivity(intent);
                            }
                        }, this::handleApiError));

        return rootView;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onResume() {
        super.onResume();
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getBalanceInfo()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getContext())))
                .subscribe(response -> {
                    this.response = response;
                    tvMoney2BTC.setText(response.getTotalToBtc());
                    tvMoney2CNY.setText("≈  " + response.getTotalToCny() + " CNY");
                    adapter.setNewData(response.getBalanceList());
                    showOrHide();
                }, this::handleApiError);
    }

    private void showOrHide() {
        if (isShow) {
            ivShowOrHide.setImageResource(R.drawable.ic_blockwallet_hide);
            tvMoney2BTC.setText(response.getTotalToBtc());
            tvMoney2CNY.setText("≈  " + response.getTotalToCny() + " CNY");
            adapter.notifyDataSetChanged();
        } else {
            ivShowOrHide.setImageResource(R.drawable.ic_blockwallet_show);
            tvMoney2BTC.setText(hideStr);
            tvMoney2CNY.setText(hideStr);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD) {
            if (resultCode == 1) {
                response = null;
                loadData();
            }
        }
    }

}
