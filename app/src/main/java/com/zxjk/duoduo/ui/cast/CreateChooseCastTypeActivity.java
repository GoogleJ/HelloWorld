package com.zxjk.duoduo.ui.cast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.OnlineServiceActivity;
import com.zxjk.duoduo.utils.CommonUtils;

public class CreateChooseCastTypeActivity extends BaseActivity {
    private int chooseFlag = -1;

    private ImageView ivCheck1;
    private ImageView ivCheck2;
    private ImageView ivCheck3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_choose_cast_type);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.create_cast);

        ivCheck1 = findViewById(R.id.ivCheck1);
        ivCheck2 = findViewById(R.id.ivCheck2);
        ivCheck3 = findViewById(R.id.ivCheck3);

    }

    public void wechat(View view) {
        chooseFlag = 0;
        ivCheck1.setVisibility(View.VISIBLE);
        ivCheck2.setVisibility(View.INVISIBLE);
        ivCheck3.setVisibility(View.INVISIBLE);
    }


    @SuppressLint("CheckResult")
    public void video(View view) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .enableOpenVideoLive()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    if (s.equals("1")) {
                        chooseFlag = 1;
                        ivCheck1.setVisibility(View.INVISIBLE);
                        ivCheck2.setVisibility(View.VISIBLE);
                        ivCheck3.setVisibility(View.INVISIBLE);
                    } else {
                        ToastUtils.showShort(R.string.developing);
                    }
                }, this::handleApiError);
    }

    public void audio(View view) {
        ToastUtils.showShort(R.string.developing);
//        chooseFlag = 3;
//        ivCheck1.setVisibility(View.INVISIBLE);
//        ivCheck2.setVisibility(View.INVISIBLE);
//        ivCheck3.setVisibility(View.VISIBLE);
    }

    @SuppressLint("CheckResult")
    public void next(View view) {
        if (chooseFlag == -1) {
            ToastUtils.showShort(R.string.choose_cast_type);
            return;
        }

        ServiceFactory.getInstance().getBaseService(Api.class)
                .enableOpenLive(getIntent().getStringExtra("groupId"))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    Intent intent = new Intent(this, CreateWechatCastActivity.class);
                    intent.putExtra("groupId", getIntent().getStringExtra("groupId"));
                    intent.putExtra("chooseFlag", String.valueOf(chooseFlag));
                    startActivity(intent);
                    finish();
                }, this::handleApiError);
    }

    public void service(View view) {
        startActivity(new Intent(this, OnlineServiceActivity.class));
    }
}
