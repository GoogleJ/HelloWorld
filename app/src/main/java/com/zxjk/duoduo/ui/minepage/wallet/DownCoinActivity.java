package com.zxjk.duoduo.ui.minepage.wallet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.SignTransactionRequest;
import com.zxjk.duoduo.bean.response.GetBalanceInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.QrCodeActivity;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MD5Utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DownCoinActivity extends BaseActivity {

    private GetBalanceInfoResponse.BalanceListBean data;

    private LinearLayout llBalanceWallet;
    private LinearLayout llBlockWallet;
    private boolean balance2block = true;
    private boolean isAniming;

    private TextView tvBlanceAddress;
    private EditText etBlockAddress;
    private EditText etCount;
    private TextView tvSymbol;
    private View divider;
    private TextView tvAllIn;
    private TextView tvBalance;
    private TextView tvTips;
    private LinearLayout llBlock2Balance;
    private TextView tvHuaZhuanGasPrice1;
    private SeekBar seekHuaZhuan;
    private TextView tvHuaZhuanGasPrice2;
    private TextView tvGasPrice;

    private float gasMax;
    private float gasMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_coin);

        data = getIntent().getParcelableExtra("data");
        initView();

    }

    private void initView() {
        TextView title = findViewById(R.id.tv_title);
        title.setText(data.getCurrencyName() + "交易");
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        llBalanceWallet = findViewById(R.id.llBalanceWallet);
        llBlockWallet = findViewById(R.id.llBlockWallet);
        tvBlanceAddress = findViewById(R.id.tvBlanceAddress);
        etBlockAddress = findViewById(R.id.etBlockAddress);
        etCount = findViewById(R.id.etCount);
        tvSymbol = findViewById(R.id.tvSymbol);
        divider = findViewById(R.id.divider);
        tvAllIn = findViewById(R.id.tvAllIn);
        tvBalance = findViewById(R.id.tvBalance);
        tvTips = findViewById(R.id.tvTips);
        llBlock2Balance = findViewById(R.id.llBlock2Balance);
        tvHuaZhuanGasPrice1 = findViewById(R.id.tvHuaZhuanGasPrice1);
        seekHuaZhuan = findViewById(R.id.seekHuaZhuan);
        tvHuaZhuanGasPrice2 = findViewById(R.id.tvHuaZhuanGasPrice2);
        tvGasPrice = findViewById(R.id.tvGasPrice);

        tvBlanceAddress.setText(data.getBalanceAddress());
        tvTips.setText("提币数量");
        etCount.setHint("请输入提币数量");
        tvGasPrice.setVisibility(View.VISIBLE);
        llBlock2Balance.setVisibility(View.GONE);
        divider.setVisibility(View.VISIBLE);
        tvAllIn.setVisibility(View.VISIBLE);
        tvBalance.setVisibility(View.VISIBLE);
        tvBalance.setText("余额钱包可用数量" + data.getBalance() + data.getCurrencyName());
        String str = "交易手续费为：" + data.getRate() + data.getCurrencyName();
        SpannableString string = new SpannableString(str);
        string.setSpan(new ForegroundColorSpan(Color.parseColor("#FC6660")), 7, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new RelativeSizeSpan(0.8f), str.length() - data.getCurrencyName().length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvGasPrice.setText(string);
        tvSymbol.setText(data.getCurrencyName());

        tvAllIn.setOnClickListener(v -> {
            if (balance2block) {
                double result = subtract(Double.parseDouble(data.getBalance()), Double.parseDouble(data.getRate()));
                if (result < 0) {
                    ToastUtils.showShort(R.string.balance_not_enough);
                    return;
                }
                etCount.setText(String.valueOf(result));
            } else {

            }
        });


        if (data.getParentSymbol().equals("ETH")) {
            //ETH
            if (data.getCoinType().equals("0")) {
                gasMin = (float) (5 * 6000 * 10E-9);
                gasMax = 0.006f - gasMin;
            } else {
                gasMin = (float) (5 * 9000 * 10E-9);
                gasMax = 0.009f - gasMin;
            }
        } else {

        }

        seekHuaZhuan.setMax(1000);
        seekHuaZhuan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvHuaZhuanGasPrice1.setText("≈" + new DecimalFormat("#0.0000").format((progress / 1000f * gasMax) + gasMin) + " ether");
                tvHuaZhuanGasPrice2.setText(new DecimalFormat("#0.00").format((progress / 1000f * 95) + 5.00) + " gwei");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekHuaZhuan.setProgress(1);
        seekHuaZhuan.setProgress(0);
    }

    public void changeDirection(View view) {
        if (isAniming) {
            return;
        }
        isAniming = true;
        balance2block = !balance2block;
        if (balance2block) {
            swapViewUpDown(llBlockWallet, llBalanceWallet);
        } else {
            swapViewUpDown(llBalanceWallet, llBlockWallet);
        }
    }

    public void scan(View view) {
        Intent intent = new Intent(this, QrCodeActivity.class);
        intent.putExtra("actionType", QrCodeActivity.ACTION_IMPORT_WALLET);
        startActivityForResult(intent, 1);
    }

    public void chooseBlockAddress(View view) {

    }

    @SuppressLint("CheckResult")
    public void confirm(View view) {
        String blockAddress = etBlockAddress.getText().toString().trim();
        if (TextUtils.isEmpty(blockAddress)) {
            ToastUtils.showShort(R.string.input_wallet_address);
            return;
        }

        String count = etCount.getText().toString().trim();
        if (TextUtils.isEmpty(count)) {
            if (balance2block) {
                ToastUtils.showShort(R.string.input_up_count);
            } else {
                ToastUtils.showShort(R.string.input_down_count);
            }
            return;
        }

        if (Double.parseDouble(count) <= 0) {
            if (balance2block) {
                ToastUtils.showShort(R.string.up_count_less_zero);
            } else {
                ToastUtils.showShort(R.string.down_count_less_zero);
            }
            return;
        }

        if (balance2block) {
            new NewPayBoard(this).show(result -> {
                if (data.getParentSymbol().equals("ETH")) {
                    SignTransactionRequest request = new SignTransactionRequest();
                    request.setBalance(count);
                    request.setTokenName(data.getCurrencyName());
                    request.setToAddress(blockAddress);

                    ServiceFactory.getInstance().getBaseService(Api.class)
                            .signTransaction(GsonUtils.toJson(request), MD5Utils.getMD5(result))
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.normalTrans())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                            .subscribe(s -> {
//                                ToastUtils.showShort(R.string.down_coin_success);
//                                finish();
                            }, this::handleApiError);
                } else {

                }
            });
        } else {

        }
    }

    private void swapViewUpDown(View upView, View downView) {
        upView.animate().translationYBy(upView.getHeight()).setDuration(500)
                .setInterpolator(new OvershootInterpolator());
        downView.animate().translationYBy(-downView.getHeight()).setDuration(500)
                .setInterpolator(new OvershootInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isAniming = false;

                        seekHuaZhuan.setProgress(0);
                        etBlockAddress.setText("");
                        etCount.setText("");
                        if (balance2block) {
                            tvTips.setText("提币数量");
                            etCount.setHint("请输入提币数量");
                            tvGasPrice.setVisibility(View.VISIBLE);
                            llBlock2Balance.setVisibility(View.GONE);
                            divider.setVisibility(View.VISIBLE);
                            tvAllIn.setVisibility(View.VISIBLE);
                            tvBalance.setVisibility(View.VISIBLE);
                            tvBalance.setText("余额钱包可用数量" + data.getBalance() + data.getCurrencyName());
                            String str = "交易手续费为：" + data.getRate() + data.getCurrencyName();
                            SpannableString string = new SpannableString(str);
                            string.setSpan(new ForegroundColorSpan(Color.parseColor("#FC6660")), 7, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            string.setSpan(new RelativeSizeSpan(0.8f), str.length() - data.getCurrencyName().length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tvGasPrice.setText(string);
                        } else {
                            tvTips.setText("充币数量");
                            etCount.setHint("请输入充币数量");
                            tvGasPrice.setVisibility(View.GONE);
                            llBlock2Balance.setVisibility(View.VISIBLE);
                            divider.setVisibility(View.GONE);
                            tvAllIn.setVisibility(View.GONE);
                            tvBalance.setVisibility(View.GONE);
                        }
                    }
                });
    }


    private double subtract(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1 && data != null) {
            etBlockAddress.setText(data.getStringExtra("result"));
        }
    }
}
