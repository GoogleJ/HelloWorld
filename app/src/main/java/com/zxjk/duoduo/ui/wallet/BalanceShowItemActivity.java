package com.zxjk.duoduo.ui.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.BalanceAssetManageBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

public class BalanceShowItemActivity extends BaseActivity {
    private RecyclerView recycler;
    private BaseQuickAdapter<BalanceAssetManageBean, BaseViewHolder> adapter;
    private boolean hasChanged;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_block_show_item);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.add_new_blockitem);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickAdapter<BalanceAssetManageBean, BaseViewHolder>(R.layout.item_add_block_showitem) {
            @Override
            protected void convert(BaseViewHolder helper, BalanceAssetManageBean item) {
                ImageView ivLogo = helper.getView(R.id.ivLogo);
                GlideUtil.loadNormalImg(ivLogo, item.getLogo());
                helper.setText(R.id.tvCoin, item.getSymbol());
                Switch sw = helper.getView(R.id.sw);
                if (item.getIsClose().equals("1")) {
                    sw.setChecked(false);
                } else {
                    sw.setChecked(true);
                }
                sw.setOnClickListener(v -> {
                    BalanceAssetManageBean request = new BalanceAssetManageBean();
                    request.setSymbol(item.getSymbol());
                    if (sw.isChecked()) {
                        request.setIsClose("0");
                    } else {
                        request.setIsClose("1");
                    }
                    ServiceFactory.getInstance().getBaseService(Api.class)
                            .balanceManage(request.getSymbol(),request.getIsClose())
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.normalTrans())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(BalanceShowItemActivity.this)))
                            .subscribe(s -> {
                                hasChanged = true;
                                ToastUtils.showShort(R.string.successfully_modified);
                            }, t -> {
                                BalanceShowItemActivity.this.handleApiError(t);
                                sw.setChecked(!sw.isChecked());
                            });
                });
            }
        };
        recycler.setAdapter(adapter);

        ServiceFactory.getInstance().getBaseService(Api.class)
                .balanceAssetManage()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(list -> adapter.setNewData(list), this::handleApiError);
    }

    @Override
    public void finish() {
        if (hasChanged) {
            setResult(1);
        }
        super.finish();
    }
}
