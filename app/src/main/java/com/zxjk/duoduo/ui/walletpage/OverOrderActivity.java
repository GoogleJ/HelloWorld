package com.zxjk.duoduo.ui.walletpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetOverOrderResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.ZoomActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MD5Utils;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("CheckResult")
public class OverOrderActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView tv7;
    private ImageView iv;
    private ImageView iv_wechat, iv_alipay, iv_bank;
    private GetOverOrderResponse data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over_order);
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.order));
        data = (GetOverOrderResponse) getIntent().getSerializableExtra("data");

        iv_wechat = findViewById(R.id.iv_wechat);
        iv_alipay = findViewById(R.id.iv_alipay);
        iv_bank = findViewById(R.id.iv_bank);

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);

        tv7 = findViewById(R.id.tv7);
        iv = findViewById(R.id.iv);

        tv1.setText(data.getBuyNick());
        tv2.setText(data.getMoney());
        tv3.setText(getIntent().getStringExtra("rate"));
        tv4.setText(data.getNumber());
        tv5.setText(data.getBothOrderId());

        if (data.getPayType().equals("1")) {

            iv_wechat.setVisibility(View.VISIBLE);
        } else if (data.getPayType().equals("2")) {
            iv_alipay.setVisibility(View.VISIBLE);
        } else {
            iv_bank.setVisibility(View.VISIBLE);
        }

        tv7.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(data.getCreateTime())));
    }

    public void showQR(View view) {
        Intent intent5 = new Intent(this, ZoomActivity.class);
        intent5.putExtra("image", data.getPicture());
        startActivity(intent5,
                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        iv, "12").toBundle());
    }

    //拒绝审核
    @SuppressLint("CheckResult")
    public void rejectOrder(View view) {
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setText(R.id.tv_title, "拒绝审核");
                holder.setText(R.id.tv_content, "拒绝审核会导致订单失败，恶意点击，将直接冻结账户。");
                holder.setText(R.id.tv_cancel, "返回");
                holder.setText(R.id.tv_notarize, "确认拒绝");
                holder.setOnClickListener(R.id.tv_cancel, v1 -> dialog.dismiss());
                holder.setOnClickListener(R.id.tv_notarize, v -> {
                    dialog.dismiss();
                    ServiceFactory.getInstance().getBaseService(Api.class)
                            .rejectAudit(data.getBuyOrderId()
                                    , data.getBothOrderId(), data.getSellOrderId())
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.normalTrans())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(OverOrderActivity.this)))
                            .subscribe(s -> {
                                ToastUtils.showShort(R.string.rejectfinish);
                                finish();
                            }, OverOrderActivity.this::handleApiError);
                });

            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
    }

    //通过审核
    @SuppressLint("CheckResult")
    public void overOrder(View view) {
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setText(R.id.tv_title, "通过审核");
                holder.setText(R.id.tv_content, "请务必确认收到款项，并核对收款无误。");
                holder.setText(R.id.tv_cancel, "取消");
                holder.setText(R.id.tv_notarize, "确认");
                holder.setOnClickListener(R.id.tv_cancel, v1 -> dialog.dismiss());
                holder.setOnClickListener(R.id.tv_notarize, v -> {
                    dialog.dismiss();
                    KeyboardUtils.hideSoftInput(OverOrderActivity.this);
                    new NewPayBoard(OverOrderActivity.this).show(psw -> ServiceFactory.getInstance().getBaseService(Api.class)
                            .overOrder(data.getBuyId(), data.getBuyOrderId()
                                    , data.getSellOrderId(), data.getBothOrderId(), MD5Utils.getMD5(psw))
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.normalTrans())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(OverOrderActivity.this)))
                            .subscribe(s -> {
                                ToastUtils.showShort(R.string.judgefinish);
                                finish();
                            }, OverOrderActivity.this::handleApiError));
                });
            }
        }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
    }

    @OnClick(R.id.rl_back)
    public void onClick() {
        finish();
    }
}
