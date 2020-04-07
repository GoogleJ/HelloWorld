package com.zxjk.moneyspace.ui.minepage.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetPaymentListBean;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.GlideUtil;

import java.util.ArrayList;

public class ChooseCoinActivity extends BaseActivity {

    private RecyclerView recycler;
    private BaseQuickAdapter adapter;
    private ArrayList<GetPaymentListBean> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_coin);

        data = getIntent().getParcelableArrayListExtra("data");
        if (data == null) {
            return;
        }

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.currency_option);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickAdapter<GetPaymentListBean, BaseViewHolder>(R.layout.item_choose_cointype, data) {
            @Override
            protected void convert(BaseViewHolder helper, GetPaymentListBean item) {
                ImageView ivIcon = helper.getView(R.id.ivIcon);
                GlideUtil.loadCircleImg(ivIcon,item.getLogo());
                helper.setText(R.id.tvCoin, item.getSymbol());
            }
        };
        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent();
            intent.putExtra("result", (GetPaymentListBean) adapter.getData().get(position));
            setResult(1, intent);
            finish();
        });
    }
}
