package com.zxjk.moneyspace.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
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

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

@SuppressLint("CheckResult")
public class GroupRedPacketActivity extends BaseActivity {
    // 红包类型：1.拼手气红包  2.普通红包
    private String redType = "1";

    private GetPaymentListBean result;
    private ArrayList<GetPaymentListBean> list = new ArrayList<>();
    private GroupResponse group;
    private TextView redpaytobar;

    private ImageView ivCoinIcon;
    private ImageView ivCoinIcon2;
    private TextView tvCoin;
    private TextView tvCoin2;
    private EditText etMoney;
    private EditText etMoney2;
    private EditText etCount;
    private EditText etBless;
    private TextView tvtop1;
    private TextView tvtop2;
    private ImageView ivtop1;
    private ImageView ivtop2;
    private ImageView pin;
    private TextView tvamount;
    private FrameLayout fm1;
    private FrameLayout fm2;

    private NumberFormat nf;
    private int color1;
    private int color2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_red_packet);
        setTrasnferStatusBar(true);

        color1 = Color.parseColor("#FFFFFF");
        color2 = Color.parseColor("#272E3F");

        ivCoinIcon = findViewById(R.id.ivCoinIcon);
        ivCoinIcon2 = findViewById(R.id.ivCoinIcon2);
        tvCoin = findViewById(R.id.tvCoin);
        tvCoin2 = findViewById(R.id.tvCoin2);
        etMoney = findViewById(R.id.etMoney);
        etMoney2 = findViewById(R.id.etMoney2);
        etCount = findViewById(R.id.etCount);
        etBless = findViewById(R.id.etBless);
        tvtop1 = findViewById(R.id.tvtop1);
        tvtop2 = findViewById(R.id.tvtop2);
        ivtop1 = findViewById(R.id.ivtop1);
        ivtop2 = findViewById(R.id.ivtop2);
        redpaytobar = findViewById(R.id.redpaytobar);
        pin = findViewById(R.id.img_pin);
        tvamount = findViewById(R.id.tv_amount);
        fm1 = findViewById(R.id.fm1);
        fm2 = findViewById(R.id.fm2);

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

        etMoney.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(5), new InputFilter.LengthFilter(10)});

        nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
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
                    }, this::handleApiError);
        } else {
            getGroupInfo(getIntent().getStringExtra("groupId"));
        }
    }

    public void top1(View view) {
        redType = "1";
        tvtop1.setTextColor(color2);

        tvtop2.setTextColor(color1);

        fm1.setBackground(getResources().getDrawable(R.drawable.shape_gray2));
        fm2.setBackground(getResources().getDrawable(R.drawable.shape_gray1));
        redpaytobar.setText(R.string.pinshouqi);
        tvamount.setText(R.string.money2);
        pin.setVisibility(View.VISIBLE);
    }

    public void top2(View view) {
        redType = "2";
        tvtop2.setTextColor(color2);
        ivtop2.setWillNotDraw(true);
        tvtop1.setTextColor(color1);
        ivtop1.setWillNotDraw(false);
        fm1.setBackground(getResources().getDrawable(R.drawable.shape_gray1));
        fm2.setBackground(getResources().getDrawable(R.drawable.shape_gray2));
        redpaytobar.setText(R.string.red_packet1);
        tvamount.setText(R.string.money1);
        pin.setVisibility(View.GONE);
    }

    private void getGroupInfo(String groupId) {
        Api api = ServiceFactory.getInstance().getBaseService(Api.class);
        api.getGroupByGroupId(groupId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .flatMap((Function<GroupResponse, Observable<BaseResponse<List<GetPaymentListBean>>>>) r -> {
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
                                        ToastUtils.showShort("红包个数不能大于群人数");
                                        etCount.setText(group.getGroupInfo().getCustomerNumber());
                                    }
                                }
                            }
                        });
                    });
                    return api.getPaymentList();
                })
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(l -> {
                    list.addAll(l);
                    result = list.get(0);
                    GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
                    GlideUtil.loadCircleImg(ivCoinIcon2, result.getLogo());
                    tvCoin.setText(result.getSymbol());
                    tvCoin2.setText(result.getSymbol());
                    etMoney.setHint(getString(R.string.can_user_value_symbol, result.getBalance(), result.getSymbol()));
                }, this::handleApiError);
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
        }
    }

}
