package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetSerialBean;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.DataUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailInfoActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ivType)
    ImageView ivType;

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

        GetSerialBean data = getIntent().getParcelableExtra("data");

        GlideUtil.loadNormalImg(ivType, data.getLogo());

        SpannableString string = new SpannableString((data.getSerialType().equals("1") ? "-" : "+") + data.getAmount() + " " + data.getSymbol());
        string.setSpan(new RelativeSizeSpan(0.56f), string.length() - data.getSymbol().length(), string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvMoney.setText(string);

        tvTradeType.setText(data.getSerialTitle());
        tvTradeTime.setText(DataUtils.timeStamp2Date(Long.parseLong(data.getCreateTime()), "yyyy-MM-dd HH:mm:ss"));
        tvTradeNumber.setText(data.getSerialNumber());
    }

    @OnClick(R.id.rl_back)
    public void onClick() {
        finish();
    }
}
