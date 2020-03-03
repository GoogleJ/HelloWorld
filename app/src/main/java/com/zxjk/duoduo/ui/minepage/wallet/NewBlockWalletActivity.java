package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.WalletChainInfoLevel0;
import com.zxjk.duoduo.bean.response.WalletChainInfosResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MMKVUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("CheckResult")
public class NewBlockWalletActivity extends BaseActivity {
    private final int REQUEST_MANAGE = 1;
    private final int REQUEST_ADD = 2;

    private WalletChainInfosResponse response;

    private boolean isShow;
    private String hideStr = "****  ****";

    private ImageView ivShowOrHide;
    private TextView tvNewBlockWalletSum;
    private TextView tvSign;

    private RecyclerView recycler;
    private BaseMultiItemQuickAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_block_wallet);
        setTrasnferStatusBar(true);

        initView();

        initRecycler();

        initData();

    }

    private void initView() {
        ivShowOrHide = findViewById(R.id.ivShowOrHide);
        tvNewBlockWalletSum = findViewById(R.id.tvNewBlockWalletSum);
        tvSign = findViewById(R.id.tvSign);

        ivShowOrHide.setOnClickListener(v -> showOrHideMoney());
    }

    private void showOrHideMoney() {
        isShow = !isShow;
        MMKVUtils.getInstance().enCode("bahaviour1_showWalletBalance", isShow);
        adapter.notifyDataSetChanged();
        if (isShow) {
            ivShowOrHide.setImageResource(R.drawable.ic_blockwallet_hide);
            tvSign.setVisibility(View.VISIBLE);
            tvNewBlockWalletSum.setText(response.getBalanceTotal());
        } else {
            ivShowOrHide.setImageResource(R.drawable.ic_blockwallet_show);
            tvSign.setVisibility(View.GONE);
            tvNewBlockWalletSum.setText(hideStr);
        }
    }

    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getWalletChainInfos()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .doOnNext(response -> {
                    NewBlockWalletActivity.this.response = response;
                    tvNewBlockWalletSum.setText(isShow ? response.getBalanceTotal() : hideStr);
                    isShow = MMKVUtils.getInstance().decodeBool("bahaviour1_showWalletBalance");
                    showOrHideMoney();
                })
                .observeOn(Schedulers.io())
                .map(response -> parseData(response.getSymbolList()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::setNewData, t -> {
                    handleApiError(t);
                    finish();
                });
    }

    private void initRecycler() {
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(null) {
            {
                addItemType(1, R.layout.item_new_blockwallet1);
                addItemType(2, R.layout.item_new_blockwallet2);
            }

            @Override
            protected void convert(BaseViewHolder helper, MultiItemEntity item) {
                int type = helper.getItemViewType();
                switch (type) {
                    case 1:
                        WalletChainInfoLevel0 level0Bean = (WalletChainInfoLevel0) item;
                        ImageView ivArrow = helper.getView(R.id.ivArrow);
                        if (level0Bean.getSubItems() == null || level0Bean.getSubItems().size() == 0) {
                            ivArrow.setVisibility(View.INVISIBLE);
                        } else {
                            ivArrow.setVisibility(View.VISIBLE);
                        }
                        ImageView ivlogo = helper.getView(R.id.ivLogo);
                        GlideUtil.loadNormalImg(ivlogo, level0Bean.getBean().getLogo());
                        helper.setText(R.id.tvCoin, level0Bean.getBean().getSymbol())
                                .setText(R.id.tvMoney1, isShow ? level0Bean.getBean().getSumBalance() : hideStr)
                                .setText(R.id.tvMoney2, isShow ? (level0Bean.getBean().getSumBalanceToCny().equals("-") ?
                                        "-" : ("≈¥" + level0Bean.getBean().getSumBalanceToCny())) : hideStr);

                        if (level0Bean.isExpanded()) {
                            helper.itemView.setBackgroundResource(R.drawable.shape_white_top8);
                        } else {
                            helper.itemView.setBackgroundResource(R.drawable.shape_white_8);
                        }

                        helper.itemView.setOnClickListener(v -> {
                            if (level0Bean.getSubItems() == null || level0Bean.getSubItems().size() == 0) {
                                Intent intent = new Intent(NewBlockWalletActivity.this, WalletTradeActivity.class);
                                intent.putExtra("symbol", ((WalletChainInfoLevel0) item).getBean().getSymbol());
                                intent.putExtra("address", ((WalletChainInfoLevel0) item).getBean().getWalletAddress());
                                intent.putExtra("money", ((WalletChainInfoLevel0) item).getBean().getSumBalanceToCny());
                                intent.putExtra("sum", ((WalletChainInfoLevel0) item).getBean().getSumBalance());
                                intent.putExtra("logo", ((WalletChainInfoLevel0) item).getBean().getLogo());
                                intent.putExtra("coinType", ((WalletChainInfoLevel0) item).getBean().getCoinType());
                                intent.putExtra("parentSymbol", ((WalletChainInfoLevel0) item).getBean().getParentSymbol());
                                intent.putExtra("tokenDecimal", ((WalletChainInfoLevel0) item).getBean().getTokenDecimal());
                                intent.putExtra("contractAddress", ((WalletChainInfoLevel0) item).getBean().getContractAddress());
                                startActivity(intent);
                                return;
                            }
                            if (level0Bean.isExpanded()) {
                                collapse(helper.getAdapterPosition());
                            } else {
                                expand(helper.getAdapterPosition());
                            }
                        });
                        break;
                    case 2:
                        WalletChainInfosResponse.SymbolListBean.SymbolInfosBean bean = (WalletChainInfosResponse.SymbolListBean.SymbolInfosBean) item;
                        helper.setText(R.id.tvCoin, bean.getWalletName())
                                .setText(R.id.tvMoney1, isShow ? bean.getBalance() : hideStr)
                                .setText(R.id.tvMoney2, isShow ? (bean.getBalanceToCNY().equals("-") ? "-" : "≈¥" + bean.getBalanceToCNY()) : hideStr)
                                .setText(R.id.tvAddress, isShow ? bean.getWalletAddress() : hideStr);
                        if (bean.isLast()) {
                            helper.itemView.setBackgroundResource(R.drawable.shape_white_bottom8);
                        } else {
                            helper.itemView.setBackgroundColor(Color.WHITE);
                        }
                        helper.itemView.setOnClickListener(v -> {
                            Intent intent = new Intent(NewBlockWalletActivity.this, WalletTradeActivity.class);
                            intent.putExtra("symbol", bean.getSymbol());
                            intent.putExtra("address", bean.getWalletAddress());
                            intent.putExtra("money", bean.getBalanceToCNY());
                            intent.putExtra("sum", bean.getBalance());
                            intent.putExtra("logo", bean.getLogo());
                            intent.putExtra("coinType", bean.getCoinType());
                            intent.putExtra("parentSymbol", bean.getParentSymbol());
                            intent.putExtra("tokenDecimal", bean.getTokenDecimal());
                            intent.putExtra("contractAddress", bean.getContractAddress());
                            startActivity(intent);
                        });
                        break;
                }
            }
        };

        recycler.setAdapter(adapter);
    }

    private ArrayList<MultiItemEntity> parseData(List<WalletChainInfosResponse.SymbolListBean> origin) {
        ArrayList<MultiItemEntity> result = new ArrayList<>();

        for (int i = 0; i < origin.size(); i++) {

            WalletChainInfosResponse.SymbolListBean bean = origin.get(i);
            WalletChainInfoLevel0 level0 = new WalletChainInfoLevel0(bean);

            result.add(level0);

            if (bean.getSymbolInfos().size() == 1) {
                continue;
            }

            for (int j = 0; j < bean.getSymbolInfos().size(); j++) {
                if (j == bean.getSymbolInfos().size() - 1) {
                    bean.getSymbolInfos().get(j).setLast(true);
                }
                level0.addSubItem(bean.getSymbolInfos().get(j));
            }

        }

        return result;
    }

    public void back(View view) {
        finish();
    }

    public void manage(View view) {
        startActivityForResult(new Intent(this, BlockWalletManageActivity.class), REQUEST_MANAGE);
    }

    public void add(View view) {
        startActivityForResult(new Intent(this, AddBlockShowItemActivity.class), REQUEST_ADD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MANAGE) {
            if (resultCode == 1) {
                response = null;
                initData();
            }
        } else if (requestCode == REQUEST_ADD) {
            if (resultCode == 1) {
                response = null;
                initData();
            }
        }
    }
}
