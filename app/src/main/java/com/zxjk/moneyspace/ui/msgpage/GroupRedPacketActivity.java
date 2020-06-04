package com.zxjk.moneyspace.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.request.SendGroupRedPackageRequest;
import com.zxjk.moneyspace.bean.response.BaseResponse;
import com.zxjk.moneyspace.bean.response.GetPaymentListBean;
import com.zxjk.moneyspace.bean.response.GroupResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.minepage.DetailListActivity;
import com.zxjk.moneyspace.ui.minepage.wallet.ChooseCoinActivity;
import com.zxjk.moneyspace.ui.msgpage.rongIM.message.RedPacketMessage;
import com.zxjk.moneyspace.ui.widget.NewPayBoard;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.GlideUtil;
import com.zxjk.moneyspace.utils.MD5Utils;
import com.zxjk.moneyspace.utils.MoneyValueFilter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.widget.WheelView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

@SuppressLint("CheckResult")
public class GroupRedPacketActivity extends BaseActivity {
    // 红包类型：1.拼手气红包  2.普通红包
    private String redType = "1";

    private GetPaymentListBean result;
    private ArrayList<GetPaymentListBean> list = new ArrayList<>();
    private GroupResponse group;
    private ImageView ivCoinIcon;
    private ImageView ivCoinIcon2;
    private TextView tvCoin;
    private TextView tvCoin2;
    private EditText etMoney;
    private EditText etMoney2;
    private EditText etCount;
    private EditText etBless;
    private ImageView pin;
    private TextView tvamount;
    private LinearLayout llTop;
    private TabLayout tabLayout;
    private LinearLayout llControl;
    private LinearLayout llControl1;
    private TextView tvControl1;
    private TextView tvControl2;
    private Switch sw;
    private LinearLayout llRoot;
    private QuickPopup timePicker;

    private NumberFormat nf;

    //保留位数
    private int moneyNums = 4;

    private boolean controlFlag = false;

    private ArrayList<String> list1 = new ArrayList<>(2);
    private ArrayList<String> list2 = new ArrayList<>(10);
    private int selectType;

    private String lastNumIs;
    private String lastNumNot;
    private String penultimateIs;
    private String penultimateNot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_red_packet);
        BarUtils.setStatusBarColor(this, Color.parseColor("#FF665B"));

        init();
    }

    private void init() {
        llRoot = findViewById(R.id.llRoot);
        ivCoinIcon = findViewById(R.id.ivCoinIcon);
        ivCoinIcon2 = findViewById(R.id.ivCoinIcon2);
        tvCoin = findViewById(R.id.tvCoin);
        tvCoin2 = findViewById(R.id.tvCoin2);
        etMoney = findViewById(R.id.etMoney);
        etMoney2 = findViewById(R.id.etMoney2);
        etCount = findViewById(R.id.etCount);
        etBless = findViewById(R.id.etBless);
        pin = findViewById(R.id.img_pin);
        tvamount = findViewById(R.id.tv_amount);
        llTop = findViewById(R.id.llTop);
        tabLayout = findViewById(R.id.tabLayout);
        llControl = findViewById(R.id.llControl);
        llControl1 = findViewById(R.id.llControl1);
        tvControl1 = findViewById(R.id.tvControl1);
        tvControl2 = findViewById(R.id.tvControl2);
        sw = findViewById(R.id.sw);

        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.excludeTarget(etMoney, true);
            changeBounds.setInterpolator(new OvershootInterpolator());
            TransitionManager.beginDelayedTransition(llRoot, changeBounds);

            if (isChecked) {
//                llControl1.setVisibility(View.VISIBLE);
            } else {
                llControl1.setVisibility(View.GONE);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    top1(null);
                } else {
                    top2(null);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        BarUtils.addMarginTopEqualStatusBarHeight(llTop);

        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 0 && count == 0) {
                    etMoney2.setText(getString(R.string.zero_value));
                } else {
                    etMoney2.setTextColor(Color.parseColor("#000000"));
                    etMoney2.setText(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        nf = NumberFormat.getNumberInstance();
        if (getIntent().getBooleanExtra("fromWechatCast", false)) {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getPaymentList()
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(l -> {
                        list.addAll(l);
                        result = list.get(0);
                        GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
                        GlideUtil.loadCircleImg(ivCoinIcon2, result.getLogo());
                        tvCoin.setText(result.getSymbol());
                        tvCoin2.setText(result.getSymbol());
                        etMoney.setHint(getString(R.string.can_user_value_symbol, result.getBalance(), result.getSymbol()));

                        if (result.getSymbol().equals("CNY")) {
                            moneyNums = 2;
                        } else {
                            moneyNums = 4;
                        }

                        nf.setMaximumFractionDigits(moneyNums);
                        etMoney.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(moneyNums), new InputFilter.LengthFilter(10)});
                    }, this::handleApiError);
        } else {
            getGroupInfo(getIntent().getStringExtra("groupId"));
        }
    }

    public void top1(View view) {
        if (controlFlag) {
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.excludeTarget(etMoney, true);
            changeBounds.setInterpolator(new OvershootInterpolator());
            TransitionManager.beginDelayedTransition(llRoot, changeBounds);
            llControl.setVisibility(View.GONE);
            llControl1.setVisibility(View.GONE);
            sw.setChecked(false);
        }

        pin.setVisibility(View.GONE);
        redType = "2";
        tvamount.setText(R.string.money1);
        pin.setScaleX(0);
        pin.setScaleY(0);
    }

    public void top2(View view) {
        if (controlFlag) {
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.excludeTarget(etMoney, true);
            changeBounds.setInterpolator(new OvershootInterpolator());

            TransitionManager.beginDelayedTransition(llRoot, changeBounds);
//            llControl.setVisibility(View.VISIBLE);
        }

        pin.setVisibility(View.VISIBLE);
        tvamount.setText(R.string.money2);
        redType = "1";
        pin.animate().scaleXBy(1 - pin.getScaleX()).scaleYBy(1 - pin.getScaleY())
                .setDuration(150).start();
    }

    private void getGroupInfo(String groupId) {
        Api api = ServiceFactory.getInstance().getBaseService(Api.class);
        api.getGroupByGroupId(groupId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .flatMap((Function<GroupResponse, Observable<List<GetPaymentListBean>>>) r -> {
                    runOnUiThread(() -> {
                        group = r;
                        etCount.setHint(getString(R.string.group_total_member_count, group.getGroupInfo().getCustomerNumber()));
                        etCount.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (!s.toString().equals("")) {
                                    if (Integer.parseInt(s.toString()) > Integer.parseInt(group.getGroupInfo().getCustomerNumber())) {
                                        ToastUtils.showShort(R.string.red_tips1);
                                        etCount.setText(group.getGroupInfo().getCustomerNumber());
                                    }
                                }
                            }
                        });
                    });
                    return api.getPaymentList().compose(RxSchedulers.normalTrans());
                })
                .flatMap((Function<List<GetPaymentListBean>, ObservableSource<BaseResponse<String>>>) l -> {
                    runOnUiThread(() -> {
                        list.addAll(l);
                        result = list.get(0);
                        GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
                        GlideUtil.loadCircleImg(ivCoinIcon2, result.getLogo());
                        tvCoin.setText(result.getSymbol());
                        tvCoin2.setText(result.getSymbol());
                        etMoney.setHint(getString(R.string.can_user_value_symbol, result.getBalance(), result.getSymbol()));

                        if (result.getSymbol().equals("CNY")) {
                            moneyNums = 2;
                        } else {
                            moneyNums = 4;
                        }

                        nf.setMaximumFractionDigits(moneyNums);
                        etMoney.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(moneyNums), new InputFilter.LengthFilter(10)});
                    });
                    return api.getRedOperatorIdentity();
                })
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this, 0)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    if (!TextUtils.isEmpty(s) && "1".equals(s)) {
                        controlFlag = true;
                    }
                    top2(null);
                }, this::handleApiError);
    }

    public void control1(View view) {
        selectType = 1;
        select();
    }

    public void control2(View view) {
        selectType = 2;
        select();
    }

    private void select() {
        if (timePicker == null) {
            initSelectList();

            TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
            showAnimation.setDuration(250);
            TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
            dismissAnimation.setDuration(500);

            timePicker = QuickPopupBuilder.with(this)

                    .contentView(R.layout.popup_choosetime_cast)
                    .config(new QuickPopupConfig()
                            .withShowAnimation(showAnimation)
                            .withDismissAnimation(dismissAnimation)
                            .withClick(R.id.ivClose, null, true))
                    .build();

            WheelView wheel1 = timePicker.findViewById(R.id.wheel1);
            WheelView wheel2 = timePicker.findViewById(R.id.wheel2);
            TextView tvBottom = timePicker.findViewById(R.id.tvBottom);

            tvBottom.setOnClickListener(v -> {
                String result = list1.get(wheel1.getSelectedIndex()) + list2.get(wheel2.getSelectedIndex());

                if (selectType == 1) {
                    tvControl1.setText(result);
                    tvControl1.setTextColor(Color.parseColor("#272E3F"));

                    lastNumIs = "";
                    lastNumNot = "";
                    if (wheel1.getSelectedIndex() == 0) {
                        lastNumNot = list2.get(wheel2.getSelectedIndex());
                    } else {
                        lastNumIs = list2.get(wheel2.getSelectedIndex());
                    }
                } else {
                    tvControl2.setText(result);
                    tvControl2.setTextColor(Color.parseColor("#272E3F"));

                    penultimateIs = "";
                    penultimateNot = "";
                    if (wheel1.getSelectedIndex() == 0) {
                        penultimateNot = list2.get(wheel2.getSelectedIndex());
                    } else {
                        penultimateIs = list2.get(wheel2.getSelectedIndex());
                    }
                }

                timePicker.dismiss();
            });

            initWheelView(wheel1);
            initWheelView(wheel2);

            wheel1.setItems(list1);
            wheel2.setItems(list2);

        }

        TextView title = timePicker.findViewById(R.id.tvTitle);
        title.setText(selectType == 1 ? R.string.control_red1 : R.string.control_red2);

        timePicker.showPopupWindow();
    }

    private void initSelectList() {
        list1.add(getString(R.string.control_not_to));
        list1.add(getString(R.string.control_to));

        for (int i = 0; i < 10; i++) {
            list2.add("" + i);
        }
    }

    private void initWheelView(WheelView wheel) {
        WheelView.DividerConfig dividerConfig = new WheelView.DividerConfig();
        dividerConfig.setColor(Color.parseColor("#DDDDDD"));
        dividerConfig.setThick(CommonUtils.dip2px(this, 1));

        wheel.setLineSpaceMultiplier(WheelView.LINE_SPACE_MULTIPLIER);
        wheel.setTextPadding(WheelView.TEXT_PADDING);
        wheel.setTextSize(17);
        wheel.setTypeface(Typeface.DEFAULT);
        wheel.setTextColor(Color.parseColor("#D9D9D9"), Color.parseColor("#272E3F"));
        wheel.setOffset(WheelView.ITEM_OFF_SET);
        wheel.setCycleDisable(true);
        wheel.setUseWeight(true);
        wheel.setDividerConfig(dividerConfig);
    }

    //发送红包
    public void sendRed(View view) {
        if (result == null) {
            ToastUtils.showShort(R.string.select_cointype);
            return;
        }
        String price = etMoney.getText().toString().trim();
        String num = etCount.getText().toString().trim();

        if (TextUtils.isEmpty(price)) {
            ToastUtils.showShort(R.string.input_money);
            return;
        } else {
            etMoney2.setText(etMoney.getText().toString().trim());
        }

        if (TextUtils.isEmpty(num)) {
            ToastUtils.showShort(R.string.input_num);
            return;
        }

        if (Double.parseDouble(price) == 0) {
            ToastUtils.showShort(R.string.input_money1);
            return;
        }

        if (Double.parseDouble(num) == 0) {
            ToastUtils.showShort(R.string.input_num1);
            return;
        }

        KeyboardUtils.hideSoftInput(this);
        new NewPayBoard(this).show(pwd -> {
            String message = etBless.getText().toString().trim();

            boolean fromWechatCast = getIntent().getBooleanExtra("fromWechatCast", false);

            SendGroupRedPackageRequest request = new SendGroupRedPackageRequest();
            request.setPayPwd(MD5Utils.getMD5(pwd));
            request.setGroupId(getIntent().getStringExtra("groupId"));
            request.setType(redType);
            request.setMessage(TextUtils.isEmpty(message) ? getString(R.string.m_red_envelopes_label) : message);
            request.setIsGame("1");
            request.setNumber(num);
            request.setSymbol(result.getSymbol());
            request.setSendRedPacketType(fromWechatCast ? "1" : "0");

            if (controlFlag) {
                if (sw.isChecked()) {
                    request.setRedSwitch("1");

                    if (!TextUtils.isEmpty(lastNumIs)) {
                        request.setLastNumIs(lastNumIs);
                    }
                    if (!TextUtils.isEmpty(lastNumNot)) {
                        request.setLastNumNot(lastNumNot);
                    }
                    if (!TextUtils.isEmpty(penultimateIs)) {
                        request.setPenultimateIs(penultimateIs);
                    }
                    if (!TextUtils.isEmpty(penultimateNot)) {
                        request.setPenultimateNot(penultimateNot);
                    }
                }
            }

            if (redType.equals("2")) {
                request.setTotalAmount(nf.format(Integer.parseInt(num) * Double.parseDouble(price)));
                request.setMoney(price);
            } else {
                request.setTotalAmount(price);
            }

            ServiceFactory.getInstance().getBaseService(Api.class)
                    .sendGroupRedPackage(GsonUtils.toJson(request))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(GroupRedPacketActivity.this)))
                    .subscribe(s -> {
                        RedPacketMessage redPacketMessage = new RedPacketMessage();
                        redPacketMessage.setIsGame("1");
                        redPacketMessage.setFromCustomer(Constant.userId);
                        redPacketMessage.setRemark(TextUtils.isEmpty(message) ? getString(R.string.m_red_envelopes_label) : message);
                        redPacketMessage.setRedId(s.getId());
                        Message groupRedMessage = Message.obtain(getIntent().getStringExtra("groupId"),
                                fromWechatCast ? Conversation.ConversationType.CHATROOM : Conversation.ConversationType.GROUP,
                                redPacketMessage);
                        RongIM.getInstance().sendMessage(groupRedMessage, null, null,
                                new IRongCallback.ISendMessageCallback() {
                                    @Override
                                    public void onAttached(Message message) {
                                    }

                                    @Override
                                    public void onSuccess(Message message) {
                                        finish();
                                    }

                                    @Override
                                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                                    }
                                });
                    }, this::handleApiError);
        });
    }

    public void chooseCoin(View view) {
        Intent intent = new Intent(this, ChooseCoinActivity.class);
        intent.putExtra("data", list);
        startActivityForResult(intent, 1);
    }

    public void back(View view) {
        finish();
    }

    public void redList(View view) {
        startActivity(new Intent(this, DetailListActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == 1 && resultCode == 1) {
            result = data.getParcelableExtra("result");
            GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
            GlideUtil.loadCircleImg(ivCoinIcon2, result.getLogo());
            tvCoin.setText(result.getSymbol());
            etMoney.setHint(getString(R.string.can_user_value_symbol, result.getBalance(), result.getSymbol()));
            tvCoin2.setText(result.getSymbol());

            if (result.getSymbol().equals("CNY")) {
                moneyNums = 2;
            } else {
                moneyNums = 4;
            }
            nf.setMaximumFractionDigits(moneyNums);
            etMoney.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(moneyNums), new InputFilter.LengthFilter(10)});
        }
    }

}
