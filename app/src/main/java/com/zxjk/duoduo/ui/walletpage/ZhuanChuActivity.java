package com.zxjk.duoduo.ui.walletpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.SignTransactionRequest;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.QrCodeActivity;
import com.zxjk.duoduo.ui.msgpage.SelectContactActivity;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MD5Utils;
import com.zxjk.duoduo.utils.MoneyValueFilter;

import java.text.DecimalFormat;

@SuppressLint("CheckResult")
public class ZhuanChuActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    private SeekBar seekZhuanchu;
    private TextView tvKuanggongPrice;
    private TextView tvGwei;
    private TextView tv_currency;
    private EditText etWalletAddress;
    private EditText etCount;
    private TextView mTvZhuanChuAll;
    private MoneyValueFilter moneyValueFilter;
    private String symbol;
    private String formAddress;
    private String coinType;
    private String parentSymbol;
    private String balance;
    private String tokenDecimal;
    private String contractAddress;
    private String sum;
    private float gasMax;
    private float gasMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuan_chu);

        symbol = getIntent().getStringExtra("symbol");
        formAddress = getIntent().getStringExtra("address");
        coinType = getIntent().getStringExtra("coinType");
        parentSymbol = getIntent().getStringExtra("parentSymbol");
        balance = getIntent().getStringExtra("balance");
        tokenDecimal = getIntent().getStringExtra("tokenDecimal");
        contractAddress = getIntent().getStringExtra("contractAddress");
        sum = getIntent().getStringExtra("sum");

        initView();

        if (parentSymbol.equals("ETH")) {
            //ETH
            if (coinType.equals("0")) {
                gasMin = (float) (5 * 6000 * 10E-9);
                gasMax = 0.006f - gasMin;
            } else {
                gasMin = (float) (5 * 9000 * 10E-9);
                gasMax = 0.009f - gasMin;
            }
        } else {

        }

        moneyValueFilter = new MoneyValueFilter();
        seekZhuanchu = findViewById(R.id.seekZhuanchu);
        etCount = findViewById(R.id.etCount);
        moneyValueFilter.setDigits(5);
        etCount.setHint(getString(R.string.num_can_be_trans, (sum + symbol)));
        etCount.setFilters(new InputFilter[]{moneyValueFilter});
        etWalletAddress = findViewById(R.id.etWalletAddress);
        tv_currency = findViewById(R.id.tv_currency);
        tvKuanggongPrice = findViewById(R.id.tvKuanggongPrice);
        mTvZhuanChuAll = findViewById(R.id.tv_zhuanchu_all);
        mTvZhuanChuAll.setOnClickListener(v -> {
            etCount.setText(sum);
        });
        tvGwei = findViewById(R.id.tvGwei);
        seekZhuanchu.setMax(1000);
        seekZhuanchu.setOnSeekBarChangeListener(this);
        seekZhuanchu.setProgress(1);
        seekZhuanchu.setProgress(0);
        initData();
    }

    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.zhuanchu));

        ImageView ivend = findViewById(R.id.img_code);

        ivend.setImageResource(R.drawable.ic_import_wallet_scan);
        ivend.setOnClickListener(v -> {
            Intent intent = new Intent(this, QrCodeActivity.class);
            intent.putExtra("actionType", QrCodeActivity.ACTION_IMPORT_WALLET);
            startActivityForResult(intent, 1);
        });
    }

    private void initData() {
        //返回
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        tv_currency.setText(symbol);
    }


    private String walletAddress;
    private String count;
    private String gasPrice;

    //提交
    public void submit(View view) {
        walletAddress = etWalletAddress.getText().toString().trim();
        if (TextUtils.isEmpty(walletAddress)) {
            ToastUtils.showShort(R.string.inputwalletaddress);
            return;
        }
        count = etCount.getText().toString().trim();
        if (TextUtils.isEmpty(count)) {
            ToastUtils.showShort(R.string.inputZhuanChuCount);
            return;
        }

        gasPrice = tvKuanggongPrice.getText().toString().replace("≈", "").replace("ether", "").trim();

        if (parentSymbol.equals("ETH")) {
            //ETH
            if (coinType.equals("0")) {
                if (!((Double.valueOf(etCount.getText().toString().trim()) + Double.valueOf(gasPrice)) <= Double.valueOf(balance))) {
                    ToastUtils.showShort(R.string.wronginput1);
                    return;
                }
            } else {
                if (!(Double.valueOf(etCount.getText().toString().trim()) <= Double.valueOf(balance))) {
                    ToastUtils.showShort(R.string.wronginput1);
                    return;
                }
            }
        }

        new NewPayBoard(this).show(result -> {
            if (parentSymbol.equals("ETH")) {
                SignTransactionRequest request = new SignTransactionRequest();
                request.setBalance(count);
                request.setGasPrice(tvGwei.getText().toString().split(" ")[0]);
                request.setFromAddress(formAddress);
                request.setToAddress(walletAddress);
                request.setContractAddress(contractAddress);
                request.setTokenDecimal(tokenDecimal);
                request.setSerialType("0");
                request.setTokenName(symbol);

                ServiceFactory.getInstance().getBaseService(Api.class)
                        .signTransaction(GsonUtils.toJson(request), MD5Utils.getMD5(result))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ZhuanChuActivity.this)))
                        .subscribe(s -> {
                            ToastUtils.showShort(R.string.zhuanchusuccess1);
                            finish();
                        }, ZhuanChuActivity.this::handleApiError);
            } else {

            }
        });
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tvKuanggongPrice.setText("≈" + new DecimalFormat("#0.0000").format((progress / 1000f * gasMax) + gasMin) + " ether");
        tvGwei.setText(new DecimalFormat("#0.00").format((progress / 1000f * 95) + 5.00) + " gwei");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            if (data != null) {
                String result = data.getStringExtra("result");
                etWalletAddress.setText("");
                etWalletAddress.setText(result);
            }
        }
    }
}
