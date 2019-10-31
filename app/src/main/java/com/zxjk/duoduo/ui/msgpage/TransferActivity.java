package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.BaseResponse;
import com.zxjk.duoduo.bean.response.GetPaymentListBean;
import com.zxjk.duoduo.bean.response.LoginResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.wallet.ChooseCoinActivity;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.TransferMessage;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MD5Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

public class TransferActivity extends BaseActivity {

    private GetPaymentListBean result;
    private ArrayList<GetPaymentListBean> list = new ArrayList<>();
    private UserInfo targetUser;
//    private boolean fromScan;

    private CircleImageView ivHead;
    private TextView tvName;
    private ImageView ivCoinIcon;
    private TextView tvCoin;
    private EditText etMoney;
    private EditText etNote;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        setTrasnferStatusBar(true);

        ivHead = findViewById(R.id.ivHead);
        tvName = findViewById(R.id.tvName);
        ivCoinIcon = findViewById(R.id.ivCoinIcon);
        tvCoin = findViewById(R.id.tvCoin);
        etMoney = findViewById(R.id.etMoney);
        etNote = findViewById(R.id.etNote);

//        fromScan = getIntent().getBooleanExtra("fromScan", false);
//        if (fromScan) {
//            if (TextUtils.isEmpty(getIntent().getStringExtra("betMoney"))) {
//                etMoney.setHint("0.00");
//                etMoney.setEnabled(true);
//                etMoney.setSelection(m_transfer_money_text.getText().length());
//            } else {
//                etMoney.setText(getIntent().getStringExtra("betMoney"));
//                etMoney.setEnabled(false);
//            }
//        }

        targetUser = getIntent().getParcelableExtra("user");
        Api api = ServiceFactory.getInstance().getBaseService(Api.class);
        if (null == targetUser) {
            api.getCustomerInfoById(getIntent().getStringExtra("userId"))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .compose(RxSchedulers.normalTrans())
                    .flatMap((Function<LoginResponse, Observable<BaseResponse<List<GetPaymentListBean>>>>) userInfo -> {
                        runOnUiThread(() -> {
                            targetUser = new UserInfo(userInfo.getId(), userInfo.getNick(), Uri.parse(userInfo.getHeadPortrait()));
                            Glide.with(TransferActivity.this).load(targetUser.getPortraitUri().toString()).into(ivHead);
                            tvName.setText("转账给" + targetUser.getName());
                        });
                        return api.getPaymentList();
                    })
                    .compose(RxSchedulers.normalTrans())
                    .subscribe(l -> {
                        list.addAll(l);
                        result = list.get(0);
                        GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
                        tvCoin.setText(result.getSymbol());
                    }, t -> {
                        handleApiError(t);
                        finish();
                    });
        } else {
            api.getPaymentList()
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .compose(bindToLifecycle())
                    .subscribe(l -> {
                        list.addAll(l);
                        result = list.get(0);
                        GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
                        tvCoin.setText(result.getSymbol());
                    }, t -> {
                        handleApiError(t);
                        finish();
                    });

            Glide.with(this).load(targetUser.getPortraitUri().toString()).into(ivHead);
            tvName.setText("转账给" + targetUser.getName());
        }
    }

    @SuppressLint("CheckResult")
    public void transfer(View view) {
        if (result == null) {
            ToastUtils.showShort(R.string.select_cointype);
            return;
        }
        if (TextUtils.isEmpty(etMoney.getText().toString().trim())) {
            ToastUtils.showShort(R.string.inputzhuanzhangmoney);
            return;
        }
        if (Double.valueOf(etMoney.getText().toString().trim()) == 0) {
            ToastUtils.showShort(R.string.inputzhuanzhangmoney1);
            return;
        }
        KeyboardUtils.hideSoftInput(this);
        new NewPayBoard(this).show(psw -> {
            String zhuanzhangInfo = etNote.getText().toString().trim();
            String remarks = TextUtils.isEmpty(zhuanzhangInfo) ? ("转账给" + targetUser.getName()) : zhuanzhangInfo;
            String payPsd = MD5Utils.getMD5(psw);
            String money = etMoney.getText().toString().trim();
            String toId = targetUser.getUserId();

//            if (fromScan) {
//                String money = getIntent().getStringExtra("betMoney");
//                if (TextUtils.isEmpty(money)) {
//                    money = money;
//                }
//                ServiceFactory.getInstance().getBaseService(Api.class)
//                        .transfer(getIntent().getStringExtra("userId"), money
//                                , MD5Utils.getMD5(psw), remarks)
//                        .compose(bindToLifecycle())
//                        .flatMap((Function<BaseResponse<TransferResponse>, ObservableSource<BaseResponse<TransferResponse>>>) response -> ServiceFactory.getInstance().getBaseService(Api.class)
//                                .collect(response.data.getId()))
//                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(TransferActivity.this)))
//                        .compose(RxSchedulers.normalTrans())
//                        .subscribe(response -> {
//                            ToastUtils.showShort(R.string.zhuanchusuccess);
//                            Intent intent = new Intent(this, HomeActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
//                        }, this::handleApiError);
//                return;
//            }

            ServiceFactory.getInstance().getBaseService(Api.class)
                    .transfer(toId, money, payPsd, remarks, result.getSymbol())
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(TransferActivity.this)))
                    .compose(RxSchedulers.normalTrans())
                    .subscribe(s -> {
                        TransferMessage message = new TransferMessage();
                        message.setRemark(remarks);
                        message.setMoney(money);
                        message.setSymbol(result.getSymbol());
                        message.setTransferId(s.getId());
                        message.setName(targetUser.getName());
                        message.setFromCustomerId(Constant.userId);
                        Message message1 = Message.obtain(targetUser.getUserId(), Conversation.ConversationType.PRIVATE, message);
                        RongIM.getInstance().sendMessage(message1, null, null, new IRongCallback.ISendMessageCallback() {
                            @Override
                            public void onAttached(Message message) {
                            }

                            @Override
                            public void onSuccess(Message message) {
                                Intent intent = new Intent(TransferActivity.this, TransferSuccessActivity.class);
                                intent.putExtra("betMoney", money);
                                intent.putExtra("name", targetUser.getName());
                                intent.putExtra("symbol", result.getSymbol());
                                startActivity(intent);
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
//        if (fromScan) {
//            return;
//        }
        Intent intent = new Intent(this, ChooseCoinActivity.class);
        intent.putExtra("data", list);
        startActivityForResult(intent, 1);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == 1 && resultCode == 1) {
            result = data.getParcelableExtra("result");
            GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
            tvCoin.setText(result.getSymbol());
        }
    }

}
