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

import com.blankj.utilcode.util.ToastUtils;
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

import java.util.ArrayList;
import java.util.List;

@SuppressLint("CheckResult")
public class NewBlockWalletActivity extends BaseActivity {
    private final int REQUEST_MANAGE = 1;

    private WalletChainInfosResponse response;

    private boolean isShow = true;
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

        response = getIntent().getParcelableExtra("response");

        initView();

        initRecycler();

        initData();

        showOrHideMoney();
    }

    private void initView() {
        ivShowOrHide = findViewById(R.id.ivShowOrHide);
        tvNewBlockWalletSum = findViewById(R.id.tvNewBlockWalletSum);
        tvSign = findViewById(R.id.tvSign);

        ivShowOrHide.setOnClickListener(v -> showOrHideMoney());
    }

    private void showOrHideMoney() {
        isShow = !isShow;
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
        if (response == null) {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getWalletChainInfos()
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(response -> {
                        this.response = response;
                        tvNewBlockWalletSum.setText(isShow ? response.getBalanceTotal() : hideStr);
                        adapter.setNewData(parseData(response.getSymbolList()));
                    }, this::handleApiError);
        } else {
            tvNewBlockWalletSum.setText(isShow ? response.getBalanceTotal() : hideStr);
            adapter.setNewData(parseData(response.getSymbolList()));
        }
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
                        if (level0Bean.getSubItems().size() != 0) {
                            ivArrow.setVisibility(View.VISIBLE);
                        } else {
                            ivArrow.setVisibility(View.INVISIBLE);
                        }
                        ImageView ivlogo = helper.getView(R.id.ivLogo);
                        GlideUtil.loadNormalImg(ivlogo, level0Bean.getBean().getLogo());
                        helper.setText(R.id.tvCoin, level0Bean.getBean().getSymbol())
                                .setText(R.id.tvMoney1, isShow ? (level0Bean.getBean().getSumBalance() + "") : hideStr)
                                .setText(R.id.tvMoney2, isShow ? ("≈¥" + level0Bean.getBean().getSumBalanceToCny()) : hideStr);

                        if (level0Bean.isExpanded()) {
                            helper.itemView.setBackgroundResource(R.drawable.shape_white_top8);
                        } else {
                            helper.itemView.setBackgroundResource(R.drawable.shape_white_8);
                        }

                        helper.itemView.setOnClickListener(v -> {
                            if (level0Bean.getSubItems() == null || level0Bean.getSubItems().size() == 0) {
                                // TODO: 2019/10/17 跳转交易详情页
                                Intent intent = new Intent(NewBlockWalletActivity.this, WalletTradeActivity.class);
                                intent.putExtra("symbol", ((WalletChainInfoLevel0) item).getBean().getSymbol());
                                intent.putExtra("address", ((WalletChainInfoLevel0) item).getBean().getWalletAddress());
                                intent.putExtra("money", ((WalletChainInfoLevel0) item).getBean().getSumBalanceToCny());
                                intent.putExtra("sum", ((WalletChainInfoLevel0) item).getBean().getSumBalance());
                                intent.putExtra("logo", ((WalletChainInfoLevel0) item).getBean().getLogo());
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
                        helper.setText(R.id.tvCoin, (bean.getImportMethod().equals("3") ? "创建的" : "导入的") + bean.getSymbol() + "钱包地址")
                                .setText(R.id.tvMoney1, isShow ? (bean.getBalance() + "") : hideStr)
                                .setText(R.id.tvMoney2, isShow ? ("≈¥" + bean.getBalanceToCNY()) : hideStr)
                                .setText(R.id.tvAddress, isShow ? bean.getWalletAddress() : hideStr);
                        if (positions.contains(helper.getAdapterPosition())) {
                            helper.itemView.setBackgroundResource(R.drawable.shape_white_bottom8);
                        } else {
                            helper.itemView.setBackgroundColor(Color.WHITE);
                        }
                        helper.itemView.setOnClickListener(v -> {
                            // TODO: 2019/10/17 跳转交易详情页
                            Intent intent = new Intent(NewBlockWalletActivity.this, WalletTradeActivity.class);
                            intent.putExtra("symbol", bean.getSymbol());
                            intent.putExtra("address", bean.getWalletAddress());
                            intent.putExtra("money", bean.getBalanceToCNY());
                            intent.putExtra("sum", bean.getBalance());
                            intent.putExtra("logo", bean.getLogo());
                            startActivity(intent);
                        });
                        break;
                }
            }
        };

        recycler.setAdapter(adapter);
    }

    private ArrayList<Integer> positions = new ArrayList<>();

    private ArrayList<MultiItemEntity> parseData(List<WalletChainInfosResponse.SymbolListBean> origin) {
        ArrayList<MultiItemEntity> result = new ArrayList<>();
        positions.clear();

        int position = -1;
        boolean flag;
        for (int i = 0; i < origin.size(); i++) {
            flag = false;
            position += 1;

            WalletChainInfosResponse.SymbolListBean bean = origin.get(i);
            WalletChainInfoLevel0 level0 = new WalletChainInfoLevel0(bean);

            for (int j = 0; j < bean.getSymbolInfos().size(); j++) {
                flag = true;
                level0.addSubItem(bean.getSymbolInfos().get(j));
                position += 1;
            }

            if (flag) {
                positions.add(position);
            }
            result.add(level0);
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
        ToastUtils.showShort(R.string.developing);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MANAGE) {
            if (resultCode == 1) {
                response = null;
                initData();
            }
        }
    }
}
