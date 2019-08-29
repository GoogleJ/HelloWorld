package com.zxjk.duoduo.ui.walletpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetOverOrderResponse;
import com.zxjk.duoduo.ui.ZoomActivity;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExchangeOrderSuccessActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView tv7;
    private TextView tv8;
    private ImageView iv_wechat, iv_alipay, iv_bank;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_order_detail);
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.order));
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);
        tv7 = findViewById(R.id.tv7);
        tv8 = findViewById(R.id.tv8);
        iv_wechat = findViewById(R.id.iv_wechat);
        iv_alipay = findViewById(R.id.iv_alipay);
        iv_bank = findViewById(R.id.iv_bank);
        iv = findViewById(R.id.iv);
        GetOverOrderResponse data = (GetOverOrderResponse) getIntent().getSerializableExtra("data");

        tv2.setText(data.getMoney());
        tv3.setText(getIntent().getStringExtra("rate"));
        tv4.setText(data.getNumber());
        tv5.setText(data.getBothOrderId());

        if (data.getPayType().equals("1")) {
            iv_wechat.setVisibility(View.VISIBLE);
        } else if (data.getPayType().equals("2")) {
            iv_alipay.setVisibility(View.VISIBLE);
        } else {
            iv_bank.setVisibility(View.VISIBLE);
        }

        tv7.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(data.getCreateTime())));
        tv8.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(data.getCloseTime())));
    }

    public void showQR(View view) {
        GetOverOrderResponse data = (GetOverOrderResponse) getIntent().getSerializableExtra("data");
        Intent intent5 = new Intent(this, ZoomActivity.class);
        intent5.putExtra("image", data.getPicture());
        startActivity(intent5,
                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        iv, "12").toBundle());
    }

    @OnClick(R.id.rl_back)
    public void onClick() {
        finish();
    }
}
