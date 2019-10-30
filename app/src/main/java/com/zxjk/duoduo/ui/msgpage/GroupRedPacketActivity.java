package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.SendGroupRedPackageRequest;
import com.zxjk.duoduo.bean.response.GetPaymentListBean;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.wallet.ChooseCoinActivity;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.RedPacketMessage;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MD5Utils;

import java.util.ArrayList;

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

    private ImageView ivCoinIcon;
    private TextView tvCoin;
    private EditText etMoney;
    private EditText etCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_red_packet);

        String groupId = getIntent().getStringExtra("groupId");

//        getGroupInfo(groupId);

    }

//    private void getGroupInfo(String groupId) {
//        ServiceFactory.getInstance().getBaseService(Api.class)
//                .getGroupByGroupId(groupId)
//                .compose(bindToLifecycle())
//                .compose(RxSchedulers.normalTrans())
//                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
//                .subscribe(response -> {
//                    group = response;
//                    tvGroupRedGroupnums.setText("本群共" + group.getCustomers().size() + "人");
//                }, this::handleApiError);
//    }
//
//    //发送红包
//    public void sendRed(View view) {
//        String price = etGroupRed1.getText().toString().trim();
//        String num = etGroupRed2.getText().toString().trim();
//
//        if (TextUtils.isEmpty(price)) {
//            ToastUtils.showShort(R.string.input_money);
//            return;
//        }
//
//        if (TextUtils.isEmpty(num)) {
//            ToastUtils.showShort(R.string.input_num);
//            return;
//        }
//
//        if (Double.parseDouble(price) == 0) {
//            ToastUtils.showShort(R.string.input_money1);
//            return;
//        }
//
//        if (Double.parseDouble(num) == 0) {
//            ToastUtils.showShort(R.string.input_num1);
//            return;
//        }
//
//        if (Double.parseDouble(price) / Double.parseDouble(num) < 0.01) {
//            ToastUtils.showShort(R.string.less_min);
//            return;
//        }
//
//        KeyboardUtils.hideSoftInput(this);
//        Rect rect = new Rect();
//        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//        new NewPayBoard(this).show(result -> {
//            String price1 = etGroupRed1.getText().toString().trim();
//            String num1 = etGroupRed2.getText().toString().trim();
//            String message = etGroupRed3.getText().toString().trim();
//
//            SendGroupRedPackageRequest request = new SendGroupRedPackageRequest();
//            request.setPayPwd(MD5Utils.getMD5(result));
//            request.setGroupId(group.getGroupInfo().getId());
//            request.setType(redType);
//            request.setMessage(TextUtils.isEmpty(message) ? getString(R.string.m_red_envelopes_label) : message);
//            request.setTotalAmount(tvGroupRedMoney.getText().toString());
//            request.setIsGame("1");
//            request.setNumber(num1);
//            if (redType.equals("2")) {
//                request.setMoney(price1);
//            }
//
//            ServiceFactory.getInstance().getBaseService(Api.class)
//                    .sendGroupRedPackage(GsonUtils.toJson(request))
//                    .compose(bindToLifecycle())
//                    .compose(RxSchedulers.normalTrans())
//                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(GroupRedPacketActivity.this)))
//                    .subscribe(s -> {
//                        RedPacketMessage redPacketMessage = new RedPacketMessage();
//                        redPacketMessage.setIsGame("1");
//                        redPacketMessage.setFromCustomer(Constant.userId);
//                        redPacketMessage.setRemark(TextUtils.isEmpty(message) ? getString(R.string.m_red_envelopes_label) : message);
//                        redPacketMessage.setRedId(s.getId());
//                        Message groupRedMessage = Message.obtain(group.getGroupInfo().getId(), Conversation.ConversationType.GROUP, redPacketMessage);
//                        RongIM.getInstance().sendMessage(groupRedMessage, null, null,
//                                new IRongCallback.ISendMessageCallback() {
//                                    @Override
//                                    public void onAttached(Message message) {
//                                    }
//
//                                    @Override
//                                    public void onSuccess(Message message) {
//                                        finish();
//                                    }
//
//                                    @Override
//                                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                                    }
//                                });
//                    }, GroupRedPacketActivity.this::handleApiError);
//        });
//    }
//
//    public void chooseCoin(View view) {
//        Intent intent = new Intent(this, ChooseCoinActivity.class);
//        intent.putExtra("data", list);
//        startActivityForResult(intent, 1);
//    }
//
//    public void back(View view) {
//        finish();
//    }
//
//    public void redList(View view) {
//        startActivity(new Intent(this, RedPackageListActivity.class));
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (data == null) return;
//        if (requestCode == 1 && resultCode == 1) {
//            result = data.getParcelableExtra("result");
//            GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
//            tvCoin.setText(result.getSymbol());
//        }
//    }

}
