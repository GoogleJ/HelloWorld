package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.request.OperateAssetsRequest;
import com.zxjk.moneyspace.bean.response.AssetManageBean;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.GlideUtil;

public class AddBlockShowItemActivity extends BaseActivity {
    private RecyclerView recycler;
    private BaseQuickAdapter<AssetManageBean, BaseViewHolder> adapter;
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
        adapter = new BaseQuickAdapter<AssetManageBean, BaseViewHolder>(R.layout.item_add_block_showitem) {
            @Override
            protected void convert(BaseViewHolder helper, AssetManageBean item) {
                ImageView ivLogo = helper.getView(R.id.ivLogo);
                GlideUtil.loadNormalImg(ivLogo, item.getLogo());
                helper.setText(R.id.tvCoin, item.getSymbol());
                Switch sw = helper.getView(R.id.sw);
                if (item.getIsDelete().equals("1")) {
                    sw.setChecked(false);
                } else {
                    sw.setChecked(true);
                }
                sw.setOnClickListener(v -> {
                    OperateAssetsRequest request = new OperateAssetsRequest();
                    request.setCustomerId(Constant.userId);
                    request.setContractAddress(item.getContractAddress());
                    request.setParentSymbol(item.getParentSymbol());
                    request.setSymbol(item.getSymbol());
                    if (sw.isChecked()) {
                        request.setIsDelete("0");
                    } else {
                        request.setIsDelete("1");
                    }
                    ServiceFactory.getInstance().getBaseService(Api.class)
                            .operateAssets(GsonUtils.toJson(request))
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.normalTrans())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(AddBlockShowItemActivity.this)))
                            .subscribe(s -> {
                                hasChanged = true;
                                ToastUtils.showShort(R.string.successfully_modified);
                            }, t -> {
                                AddBlockShowItemActivity.this.handleApiError(t);
                                sw.setChecked(!sw.isChecked());
                            });
                });
            }
        };
        recycler.setAdapter(adapter);

        ServiceFactory.getInstance().getBaseService(Api.class)
                .assetManage()
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
