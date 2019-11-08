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
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.SignTransactionRequest;
import com.zxjk.duoduo.bean.response.GetBalanceInfoResponse;
import com.zxjk.duoduo.bean.response.GetParentSymbolBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.QrCodeActivity;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.ui.widget.dialog.MuteRemoveDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MD5Utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

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
    private ImageView ivScan;

    private float gasMax;
    private float gasMin;

    private QuickPopup chooseAddressPop;
    private RecyclerView recyclerChooseAddress;
    private BaseQuickAdapter<GetParentSymbolBean, BaseViewHolder> addressAdapter;
    private ArrayList<GetParentSymbolBean> parentSymbolBeans = new ArrayList<>();
    private int checkedIndex = -1;
    //从本平台选择到的地址对应余额
    private String blockMoney;

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
        ivScan = findViewById(R.id.ivScan);

        tvBlanceAddress.setText(data.getBalanceAddress());
        tvTips.setText("提币数量");
        etCount.setHint("请输入提币数量");
        tvGasPrice.setVisibility(View.VISIBLE);
        llBlock2Balance.setVisibility(View.GONE);
        divider.setVisibility(View.VISIBLE);
        tvAllIn.setVisibility(View.VISIBLE);
        tvBalance.setVisibility(View.VISIBLE);
        tvBalance.setText("余额钱包可用数量" + data.getBalance() + data.getCoin());
        String str = "交易手续费为：" + data.getRate() + data.getCoin();
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
                if (!TextUtils.isEmpty(blockMoney)) {
                    etCount.setText(blockMoney);
                }
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

    @SuppressLint("CheckResult")
    public void chooseBlockAddress(View view) {
        if (chooseAddressPop == null) {
            TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
            showAnimation.setDuration(250);
            TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
            dismissAnimation.setDuration(500);
            chooseAddressPop = QuickPopupBuilder.with(this)
                    .contentView(R.layout.pop_choose_blockaddress)
                    .config(new QuickPopupConfig()
                            .withShowAnimation(showAnimation)
                            .withDismissAnimation(dismissAnimation)
                            .dismissOnOutSideTouch(false)
                            .withClick(R.id.ivClose, null, true)
                            .withClick(R.id.tvConfirm, v -> {
                                if (checkedIndex == -1) {
                                    ToastUtils.showShort(R.string.select_walletaddress);
                                    return;
                                }

                                GetParentSymbolBean bean = parentSymbolBeans.get(checkedIndex);
                                if (!balance2block) {
                                    ServiceFactory.getInstance().getBaseService(Api.class)
                                            .getBalanceInfoByAddress(bean.getWalletAddress(), bean.getCoinType(), bean.getParentSymbol(), bean.getContractAddress(), bean.getTokenDecimal())
                                            .compose(bindToLifecycle())
                                            .compose(RxSchedulers.normalTrans())
                                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(DownCoinActivity.this)))
                                            .subscribe(s -> {
                                                etBlockAddress.setText(bean.getWalletAddress());
                                                blockMoney = s;
                                                divider.setVisibility(View.VISIBLE);
                                                tvAllIn.setVisibility(View.VISIBLE);
                                                tvBalance.setVisibility(View.VISIBLE);
                                                tvBalance.setText("数字钱包可用数量" + blockMoney + data.getCoin());
                                            }, DownCoinActivity.this::handleApiError);
                                } else {
                                    etBlockAddress.setText(bean.getWalletAddress());
                                }
                            }, true))
                    .build();

            recyclerChooseAddress = chooseAddressPop.findViewById(R.id.recyclerChooseAddress);
            recyclerChooseAddress.setLayoutManager(new LinearLayoutManager(this));
            addressAdapter = new BaseQuickAdapter<GetParentSymbolBean, BaseViewHolder>(R.layout.item_choose_block_walletaddress) {
                @Override
                protected void convert(BaseViewHolder helper, GetParentSymbolBean item) {
                    ImageView ivlogo = helper.getView(R.id.ivLogo);
                    GlideUtil.loadNormalImg(ivlogo, item.getLogo());

                    helper.setText(R.id.tvSymbol, item.getWalletName())
                            .setText(R.id.tvAddress, item.getWalletAddress());
                    CheckBox cb = helper.getView(R.id.cb);
                    if (helper.getAdapterPosition() == checkedIndex) {
                        cb.setChecked(true);
                    } else {
                        cb.setChecked(false);
                    }
                }
            };
            addressAdapter.setOnItemClickListener((adapter, view1, p) -> {
                int position = -1;
                if (checkedIndex != -1) {
                    position = checkedIndex;
                }
                checkedIndex = p;
                if (position != -1) {
                    adapter.notifyItemChanged(position);
                }
                adapter.notifyItemChanged(checkedIndex);
            });

            recyclerChooseAddress.setAdapter(addressAdapter);
        }

        if (parentSymbolBeans.size() == 0) {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getParentSymbol(data.getCoin())
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(list -> {
                        if (list.size() != 0) {
                            this.parentSymbolBeans.addAll(list);
                            addressAdapter.setNewData(list);
                            chooseAddressPop.showPopupWindow();
                        } else {
                            MuteRemoveDialog dialog = new MuteRemoveDialog(this, "取消", "去创建", "提示", "您还未创建数字钱包，请创建后再进行划转。");
                            dialog.setOnCommitListener(() -> startActivity(new Intent(this, BlockWalletEmptyActivity.class)));
                            dialog.show();
                        }
                    }, this::handleApiError);
        } else {
            checkedIndex = -1;
            blockMoney = "";
            addressAdapter.notifyDataSetChanged();
            chooseAddressPop.showPopupWindow();
        }
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
            if (!balance2block) {
                ToastUtils.showShort(R.string.input_up_count);
            } else {
                ToastUtils.showShort(R.string.input_down_count);
            }
            return;
        }

        if (Double.parseDouble(count) <= 0) {
            if (!balance2block) {
                ToastUtils.showShort(R.string.up_count_less_zero);
            } else {
                ToastUtils.showShort(R.string.down_count_less_zero);
            }
            return;
        }

        if (blockAddress.equals(data.getBalanceAddress())) {
            ToastUtils.showShort(R.string.address_same);
            return;
        }

        String limit = data.getCurrencyLimit();
        if (!TextUtils.isEmpty(limit)) {
            double limitNum = Double.parseDouble(limit);
            if (balance2block) {
                if (Double.parseDouble(count) <= limitNum) {
                    ToastUtils.showShort("当前提币数量不得小于" + limitNum);
                    return;
                }
            }
        }

        new NewPayBoard(this)
                .show(pwd -> {
                    if (balance2block) {
                        if (data.getParentSymbol().equals("ETH")) {
                            SignTransactionRequest request = new SignTransactionRequest();
                            request.setBalance(count);
                            request.setTokenName(data.getCoin());
                            request.setToAddress(blockAddress);
                            request.setFromAddress(data.getBalanceAddress());
                            request.setSerialType("1");
                            request.setTransType("1");
                            request.setRate(data.getRate());

                            ServiceFactory.getInstance().getBaseService(Api.class)
                                    .signTransaction(GsonUtils.toJson(request), MD5Utils.getMD5(pwd))
                                    .compose(bindToLifecycle())
                                    .compose(RxSchedulers.normalTrans())
                                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                                    .subscribe(s -> {
                                        Intent intent = new Intent(this, UpDownCoinResultActivity.class);
                                        intent.putExtra("type", balance2block ? "提币" : "充币");
                                        intent.putExtra("logo", data.getCoin());
                                        startActivity(intent);
                                        finish();
                                    }, this::handleApiError);
                        } else {

                        }
                    } else {
                        SignTransactionRequest request = new SignTransactionRequest();
                        request.setFromAddress(blockAddress);
                        request.setToAddress(data.getBalanceAddress());
                        request.setGasPrice(tvHuaZhuanGasPrice2.getText().toString().split(" ")[0]);
                        request.setBalance(count);
                        request.setSerialType("1");
                        request.setTransType("0");
                        request.setTokenName(data.getCoin());

                        ServiceFactory.getInstance().getBaseService(Api.class)
                                .signTransaction(GsonUtils.toJson(request), MD5Utils.getMD5(pwd))
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.normalTrans())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                                .subscribe(s -> {
                                    Intent intent = new Intent(this, UpDownCoinResultActivity.class);
                                    intent.putExtra("type", balance2block ? "提币" : "充币");
                                    intent.putExtra("logo", data.getLogo());
                                    startActivity(intent);
                                    finish();
                                }, this::handleApiError);
                    }
                });
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
                            tvBalance.setText("余额钱包可用数量" + data.getBalance() + data.getCoin());
                            String str = "交易手续费为：" + data.getRate() + data.getCoin();
                            SpannableString string = new SpannableString(str);
                            string.setSpan(new ForegroundColorSpan(Color.parseColor("#FC6660")), 7, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            string.setSpan(new RelativeSizeSpan(0.8f), str.length() - data.getCurrencyName().length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tvGasPrice.setText(string);
                            ivScan.setVisibility(View.VISIBLE);
                            etBlockAddress.setEnabled(true);
                            etBlockAddress.setHint(R.string.tips_downcoin);
                        } else {
                            tvTips.setText("充币数量");
                            etCount.setHint("请输入充币数量");
                            tvGasPrice.setVisibility(View.GONE);
                            llBlock2Balance.setVisibility(View.VISIBLE);
                            divider.setVisibility(View.GONE);
                            tvAllIn.setVisibility(View.GONE);
                            tvBalance.setVisibility(View.GONE);
                            ivScan.setVisibility(View.GONE);
                            etBlockAddress.setEnabled(false);
                            etBlockAddress.setHint(R.string.tips_downcoin2);
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
            if (!balance2block) {
                blockMoney = "";
                divider.setVisibility(View.GONE);
                tvAllIn.setVisibility(View.GONE);
                tvBalance.setVisibility(View.GONE);
            }
        }
    }
}
