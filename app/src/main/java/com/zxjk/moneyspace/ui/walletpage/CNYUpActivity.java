package com.zxjk.moneyspace.ui.walletpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.CnyRechargeResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;

import java.util.Arrays;

//人民币充值
public class CNYUpActivity extends BaseActivity {
    private RecyclerView recycler;

    private BaseQuickAdapter<String, BaseViewHolder> adapter;

    private String money;
    private int currentSelect = 0;

    private String[] values;

    private CnyRechargeResponse r;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnyup);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.chongzhi);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new GridLayoutManager(this, 3));

        adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_cny1) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {

                boolean selected = helper.getAdapterPosition() == currentSelect;

                TextView tv = helper.getView(R.id.tv);

                if (selected) {
                    tv.setBackgroundResource(R.drawable.shape_theme);
                    tv.setTextColor(Color.parseColor("#E7C39B"));
                } else {
                    tv.setBackgroundResource(R.drawable.shape_d9d9d9_3);
                    tv.setTextColor(Color.parseColor("#6D7278"));
                }

                tv.setText(values[helper.getAdapterPosition()]);

            }
        };

        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (currentSelect == position) return;

            currentSelect = position;
            money = values[position];

            adapter.notifyDataSetChanged();
        });

        ServiceFactory.getInstance().getBaseService(Api.class)
                .cnyRecharge()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(response -> {
                    r = response;

                    values = response.getCnyRechargeMoney().split(",");
                    if (values.length == 0) {
                        ToastUtils.showShort(R.string.function_fail);
                        finish();
                    }
                    money = values[0];

                    recycler.setAdapter(adapter);
                    adapter.setNewData(Arrays.asList(values));

                }, throwable -> {
                    handleApiError(throwable);
                    finish();
                });
    }

    public void next(View view) {
        Intent intent = new Intent(this, CNYUpConfirm1Activity.class);
        intent.putExtra("money", money);
        intent.putExtra("bank", r);
        startActivity(intent);
    }

}
