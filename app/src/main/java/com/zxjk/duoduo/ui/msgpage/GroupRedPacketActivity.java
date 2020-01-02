package com.zxjk.duoduo.ui.msgpage;

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
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.SendGroupRedPackageRequest;
import com.zxjk.duoduo.bean.response.BaseResponse;
import com.zxjk.duoduo.bean.response.GetPaymentListBean;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.DetailListActivity;
import com.zxjk.duoduo.ui.minepage.wallet.ChooseCoinActivity;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.RedPacketMessage;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MD5Utils;
import com.zxjk.duoduo.utils.MoneyValueFilter;

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
        color2 = Color.parseColor("#4585F5");

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
                if(start ==0 && count ==0){
                    etMoney2.setText("0.0000");
                } else {
                        etMoney2.setTextColor(Color.parseColor("#000000"));
                        etMoney2.setText(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etMoney.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(2)});

        nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        String groupId = getIntent().getStringExtra("groupId");
        getGroupInfo(groupId);

    }

    public void top1(View view) {
        redType = "1";
        tvtop1.setTextColor(color2);

        tvtop2.setTextColor(color1);

        fm1.setBackground(getResources().getDrawable(R.drawable.shape_gray2));
        fm2.setBackground(getResources().getDrawable(R.drawable.shape_gray1));
        redpaytobar.setText("拼手气红包");
        tvamount.setText("总金额");
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
        redpaytobar.setText("普通红包");
        tvamount.setText("单个金额");
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
                        etCount.setHint("本群共" + group.getCustomers().size() + "人");
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
                    etMoney.setHint("可用" + result.getBalance() + result.getSymbol());
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
        }else {
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

            SendGroupRedPackageRequest request = new SendGroupRedPackageRequest();
            request.setPayPwd(MD5Utils.getMD5(pwd));
            request.setGroupId(group.getGroupInfo().getId());
            request.setType(redType);
            request.setMessage(TextUtils.isEmpty(message) ? getString(R.string.m_red_envelopes_label) : message);
            request.setIsGame("1");
            request.setNumber(num);
            request.setSymbol(result.getSymbol());
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
                        Message groupRedMessage = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP, redPacketMessage);
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
                    }, GroupRedPacketActivity.this::handleApiError);
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
            etMoney.setHint("可用" + result.getBalance() + result.getSymbol());
        }
    }

}
