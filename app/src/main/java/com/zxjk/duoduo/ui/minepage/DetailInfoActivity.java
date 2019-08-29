package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.DetailListResposne;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.DataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailInfoActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.shouruxiangqing));

        TextView tvMoney = findViewById(R.id.tvMoney);
        TextView tvTradeType = findViewById(R.id.tvTradeType);
        TextView tvTradeTime = findViewById(R.id.tvTradeTime);
        TextView tvTradeNumber = findViewById(R.id.tvTradeNumber);
        TextView tvTradeNote = findViewById(R.id.tvTradeNote);

        DetailListResposne data = (DetailListResposne) getIntent().getSerializableExtra("data");
        tvMoney.setText(DataUtils.getTwoDecimals(data.getMot()) + " MoT");
        tvTradeType.setText(getIntent().getStringExtra("type"));
        tvTradeTime.setText(DataUtils.timeStamp2Date(Long.parseLong(data.getCreateTime()), "yyyy-MM-dd HH:mm:ss"));
        tvTradeNumber.setText(data.getSerialNumber());
        if (!TextUtils.isEmpty(getIntent().getStringExtra("remarks"))) {
            tvTradeNote.setText(getIntent().getStringExtra("remarks"));
        } else {
            tvTradeNote.setText(getIntent().getStringExtra("type"));
        }
    }


    @OnClick(R.id.rl_back)
    public void onClick() {
        finish();
    }
}
