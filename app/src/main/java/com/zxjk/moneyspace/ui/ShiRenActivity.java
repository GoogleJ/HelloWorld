package com.zxjk.moneyspace.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aliyun.aliyunface.api.ZIMFacade;
import com.aliyun.aliyunface.api.ZIMFacadeBuilder;
import com.blankj.utilcode.util.ToastUtils;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxException;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ShiRenActivity extends BaseActivity {

    private EditText etName;
    private EditText etCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shi_ren);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.real_name_authentication);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        etName = findViewById(R.id.etName);
        etCard = findViewById(R.id.etCard);
    }

    @SuppressLint("CheckResult")
    public void commit(View view) {
        String name = etName.getText().toString().trim();
        String card = etCard.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(card)) {
            ToastUtils.showShort(R.string.input_empty);
            return;
        }

        Api api = ServiceFactory.getInstance().getBaseService(Api.class);
        api.getAuthToken(ZIMFacade.getMetaInfos(this), name, card)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .compose(RxSchedulers.normalTrans())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(certifyId -> Observable.create(emitter -> {
                    ZIMFacade zimFacade = ZIMFacadeBuilder.create(this);
                    zimFacade.verify(certifyId, true, response -> {
                                if (null != response && 1000 == response.code) {
                                    // 认证成功
                                    emitter.onNext(true);
                                } else {
                                    // 认证失败
                                    emitter.onError(new RxException.ParamsException("认证失败,请稍后尝试", 100));
                                }
                                return true;
                            }
                    );
                }))
                .observeOn(Schedulers.io())
                .flatMap(b -> api.initAuthData())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    ToastUtils.showShort("认证成功");
                    setResult(1);
                    finish();
                }, t -> {
                    handleApiError(t);
                    finish();
                });
    }
}
