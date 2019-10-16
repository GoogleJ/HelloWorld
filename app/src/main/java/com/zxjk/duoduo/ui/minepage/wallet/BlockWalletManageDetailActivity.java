package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetMainSymbolByCustomerIdBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

public class BlockWalletManageDetailActivity extends BaseActivity {
    private GetMainSymbolByCustomerIdBean data;

    private ImageView ivLogo;
    private TextView tvMoney;
    private TextView tvAddress;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_wallet_manage_detail);
        data = getIntent().getParcelableExtra("data");

        TextView title = findViewById(R.id.tv_title);
        title.setText(data.getWalletName());
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        ivLogo = findViewById(R.id.ivLogo);
        tvMoney = findViewById(R.id.tvMoney);
        tvAddress = findViewById(R.id.tvAddress);

        GlideUtil.loadNormalImg(ivLogo, data.getLogo());
        tvAddress.setText(data.getWalletAddress());

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getBalanceByAddress(data.getSymbol(), data.getWalletAddress())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> tvMoney.setText(s), this::handleApiError);
    }

    //修改钱包名称
    public void changeName(View view) {

    }

    //导出助记词
    public void exportWords(View view) {

    }

    //导出keystore
    public void exportKeystore(View view) {

    }

    //导出私钥
    public void exportKey(View view) {

    }

    //删除钱包
    public void deleteWallet(View view) {

    }
}
