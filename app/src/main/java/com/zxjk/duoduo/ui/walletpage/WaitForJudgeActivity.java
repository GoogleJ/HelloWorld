package com.zxjk.moneyspace.ui.walletpage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.bean.response.ReleaseSaleResponse;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.ZoomActivity;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.msgpage.VerificationActivity;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.rong.imkit.RongIM;

@SuppressLint({"CheckResult", "SimpleDateFormat"})
public class WaitForJudgeActivity extends BaseActivity {

    @BindView(R.id.tv_chat)
    TextView tvChat;
    @BindView(R.id.rl_contact_him)
    RelativeLayout rlContactHim;
    private TextView tvWaitForJudgeCountDown;
    private ReleaseSaleResponse response;
    private boolean isFriend;
    private String friendId;
    private String friendNickName;
    private ImageView iv_qrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_judge);
        ButterKnife.bind(this);

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.waiting_audit));

        tvWaitForJudgeCountDown = findViewById(R.id.tvWaitForJudgeCountDown);
        iv_qrCode = findViewById(R.id.iv_qrCode);
        TextView tvWaitForJudgeOrderId = findViewById(R.id.tvWaitForJudgeOrderId);
        TextView tvWaitForJudgeMoney = findViewById(R.id.tvWaitForJudgeMoney);
        TextView tvWaitForJudgePrice = findViewById(R.id.tvWaitForJudgePrice);
        TextView tvWaitForJudgeCount = findViewById(R.id.tvWaitForJudgeCount);
        TextView tvWaitForJudgeTime = findViewById(R.id.tvWaitForJudgeTime);
        TextView tvWaitForJudgeReceiver = findViewById(R.id.tvWaitForJudgeReceiver);
        TextView tvWaitForJudgeReceiverAccount = findViewById(R.id.tvWaitForJudgeReceiverAccount);
        ImageView iv_wechat = findViewById(R.id.iv_wechat);
        ImageView iv_alipay = findViewById(R.id.iv_alipay);
        ImageView iv_bank = findViewById(R.id.iv_bank);

        String buytype = getIntent().getStringExtra("buytype");
        switch (buytype) {
            case "1":
                iv_wechat.setVisibility(View.VISIBLE);
                break;
            case "2":
                iv_alipay.setVisibility(View.VISIBLE);
                break;
            case "3":
                iv_bank.setVisibility(View.VISIBLE);
                break;
            default:
        }

        response = (ReleaseSaleResponse) getIntent().getSerializableExtra("data");

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getFriendListById()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .subscribe(listBaseResponse -> {
                    if (listBaseResponse != null) {
                        for (int i = 0; i < listBaseResponse.size(); i++) {
                            if (String.valueOf(response.getCustomerId()).equals(listBaseResponse.get(i).getId())) {
                                tvChat.setText(getString(R.string.send_message));
                                friendId = listBaseResponse.get(i).getId();
                                friendNickName = listBaseResponse.get(i).getNick();
                                isFriend = true;
                                break;
                            }
                        }
                    }
                }, this::handleApiError);

        tvWaitForJudgeOrderId.setText(response.getBothOrderId());
        tvWaitForJudgeMoney.setText(response.getMoney());
        tvWaitForJudgePrice.setText(getIntent().getStringExtra("rate"));
        tvWaitForJudgeCount.setText(response.getNumber());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        tvWaitForJudgeTime.setText(format.format(Long.parseLong(response.getCreateTime())));
        tvWaitForJudgeReceiver.setText(response.getNick());
        tvWaitForJudgeReceiverAccount.setText(response.getReceiptNumber());

        long l1 = (System.currentTimeMillis() - Long.parseLong(response.getPayTime())) / 1000;
        long total = (900 - l1) <= 0 ? 0 : (900 - l1);
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(total)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(l -> {
                    long minute = (total - l) / 60;
                    long second = (total - l) % 60;
                    tvWaitForJudgeCountDown.setText(minute + ":" + (second == 60 ? "00" : ((second < 10 ? ("0" + second) : second))));
                    if (total == 0 || l == total - 1) {
                        ToastUtils.showShort(R.string.timeup);
                        finish();
                    }
                }, t -> {
                });
        //联系他 拨打电话
        getPermisson(rlContactHim, result -> {
            if (result) {
                Intent intent3 = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + response.getMobile());
                intent3.setData(data);
                startActivity(intent3);
            }
        }, Manifest.permission.CALL_PHONE);
    }

    @OnClick({R.id.rl_back, R.id.rl_payment_code, R.id.rl_add_buddy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //返回
            case R.id.rl_back:
                finish();
                break;
            //收款码
            case R.id.rl_payment_code:
                ReleaseSaleResponse data1 = (ReleaseSaleResponse) getIntent().getSerializableExtra("data");
                Intent intent5 = new Intent(this, ZoomActivity.class);
                intent5.putExtra("image", data1.getReceiptPicture());
                startActivity(intent5,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                                iv_qrCode, "12").toBundle());
                break;
            //加好友
            case R.id.rl_add_buddy:
                if (isFriend) {
                    RongIM.getInstance().startPrivateChat(this, friendId, friendNickName);
                } else {
                    Intent intent1 = new Intent(this, VerificationActivity.class);
                    intent1.putExtra("friendId", String.valueOf(response.getCustomerId()));
                    startActivity(intent1);
                }
                break;
        }
    }
}
