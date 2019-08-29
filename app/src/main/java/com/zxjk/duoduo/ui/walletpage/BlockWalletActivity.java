package com.zxjk.duoduo.ui.walletpage;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.CreateWalletResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("CheckResult")
public class BlockWalletActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_end)
    ImageView ivEnd;
    @BindView(R.id.rl_end)
    RelativeLayout rlEnd;

    private ImageView ivBlockWalletHeadImg;
    private TextView tvBlockWalletAddress;
    private TextView tvBlockWalletNick;
    private TextView tvBlockWalletBalance;
    private TextView tvBlockWalletETH1;
    private TextView tvBlockWalletETH2;

    private String ETHmoney1;
    private String ETHmoney2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_wallet);
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.blockWallet));
        rlEnd.setVisibility(View.VISIBLE);
        ivEnd.setImageResource(R.drawable.ic_exchange_list);


        ivBlockWalletHeadImg = findViewById(R.id.ivBlockWalletHeadImg);
        tvBlockWalletAddress = findViewById(R.id.tvBlockWalletAddress);
        tvBlockWalletNick = findViewById(R.id.tvBlockWalletNick);
        tvBlockWalletBalance = findViewById(R.id.tvBlockWalletBalance);
        tvBlockWalletETH1 = findViewById(R.id.tvBlockWalletETH1);
        tvBlockWalletETH2 = findViewById(R.id.tvBlockWalletETH2);

        GlideUtil.loadCircleImg(ivBlockWalletHeadImg, Constant.currentUser.getHeadPortrait());
        tvBlockWalletAddress.setText(Constant.currentUser.getWalletAddress());
        tvBlockWalletAddress.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(tvBlockWalletAddress.getText().toString());
            ToastUtils.showShort(getString(R.string.duplicated_to_clipboard));
        });
        tvBlockWalletNick.setText(Constant.currentUser.getNick());

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getWallet(Constant.currentUser.getDuoduoId())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(response -> {
                    ETHmoney1 = response.getBalanceEth() + "ETH";
                    ETHmoney2 = "≈￥ " + new DecimalFormat("#0.00").format(response.getExchange());
                    tvBlockWalletETH1.setText(ETHmoney1);
                    tvBlockWalletETH2.setText(ETHmoney2);
                    tvBlockWalletBalance.setText(response.getBalanceMoToken());
                    Constant.walletResponse = response;
                }, throwable -> {
                    handleApiError(throwable);
                    finish();
                });
    }

    public void enterETHOrders(View view) {
        Intent intent = new Intent(this, BlockETHOrders.class);
        intent.putExtra("money1", ETHmoney1);
        intent.putExtra("money2", ETHmoney2);
        startActivity(intent);
    }

    public void zhuanchu(View view) {
        startActivity(new Intent(this, ZhuanChuActivity.class));
    }

    public void huazhuan(View view) {
        startActivity(new Intent(this, HuaZhuanActivity.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.walletResponse = new CreateWalletResponse();
    }

    @OnClick({R.id.rl_back, R.id.rl_end})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //返回
            case R.id.rl_back:
                finish();
                break;
            //记录
            case R.id.rl_end:
                if (Constant.walletResponse != null) {
                    startActivity(new Intent(this, BlockWalletOrdersActivity.class));
                }
                break;
        }
    }
}
