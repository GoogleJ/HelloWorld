package com.zxjk.duoduo.ui.msgpage;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddContactActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_phoneFriend)
    RelativeLayout rlPhoneFriend;
    @BindView(R.id.rl_scan)
    RelativeLayout rlScan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitle.setText(getString(R.string.add_contacts));
        //通讯录好友
        getPermisson(rlPhoneFriend, result -> {
            if (result) startActivity(new Intent(this, PhoneContactActivity.class));
        }, Manifest.permission.READ_CONTACTS);
        //扫一扫
        getPermisson(rlScan, result -> {
            if (result) startActivity(new Intent(this, QrCodeActivity.class));
        }, Manifest.permission.CAMERA);
    }


    @OnClick({R.id.rl_back, R.id.tv_qr_code, R.id.tv_search, R.id.rl_weChatFriend})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //返回
            case R.id.rl_back:
                finish();
                break;
            //我的二维码
            case R.id.tv_qr_code:
                startActivity(new Intent(this, MyQrCodeActivity.class));
                break;
            //搜索
            case R.id.tv_search:
                startActivity(new Intent(this, GlobalSearchActivity.class));
                break;
            //微信好友
            case R.id.rl_weChatFriend:
                NiceDialog.init().setLayoutId(R.layout.layout_general_dialog7).setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        holder.setOnClickListener(R.id.rl_weChat, v -> {
                            dialog.dismiss();
//                            WeChatShareUtil.shareImg(AddContactActivity.this, 0);
                        });
                        holder.setOnClickListener(R.id.rl_circleFriends, v -> {
                            dialog.dismiss();
//                            WeChatShareUtil.shareImg(AddContactActivity.this, 1);
                        });
                    }
                }).setShowBottom(true)
                        .setDimAmount(0.5f)
                        .show(getSupportFragmentManager());
                break;
        }
    }
}
