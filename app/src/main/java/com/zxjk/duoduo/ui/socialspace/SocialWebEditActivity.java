package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.EditCommunityWebSiteRequest;
import com.zxjk.duoduo.bean.response.BaseResponse;
import com.zxjk.duoduo.bean.response.EditListCommunityCultureResponse;
import com.zxjk.duoduo.bean.response.SocialCaltureListBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;

import java.util.ArrayList;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class SocialWebEditActivity extends BaseActivity {

    private EditText etName;
    private EditText etSlogan;
    private EditText etAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_web_edit);

        etName = findViewById(R.id.etName);
        etSlogan = findViewById(R.id.etSlogan);
        etAddress = findViewById(R.id.etAddress);

        SocialCaltureListBean origin = getIntent().getParcelableExtra("bean");
        if (origin.getOfficialWebsite().getOfficialWebsiteList().size() != 0) {
            EditListCommunityCultureResponse.OfficialWebsiteBean.OfficialWebsiteListBean bean = origin.getOfficialWebsite().getOfficialWebsiteList().get(0);
            if (!TextUtils.isEmpty(bean.getWebsiteTitle())) {
                etName.setText(bean.getWebsiteTitle());
            }
            if (!TextUtils.isEmpty(bean.getWebsiteContent())) {
                etSlogan.setText(bean.getWebsiteContent());
            }
            if (!TextUtils.isEmpty(bean.getWebsiteUrl())) {
                etAddress.setText(bean.getWebsiteUrl());
            }
        }
    }

    public void back(View view) {
        finish();
    }

    @SuppressLint("CheckResult")
    public void save(View view) {
        String name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShort(R.string.input_webname);
            return;
        }

        String slogan = etSlogan.getText().toString().trim();
        if (TextUtils.isEmpty(slogan)) {
            ToastUtils.showShort(R.string.input_webslogan);
            return;
        }

        String address = etAddress.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            ToastUtils.showShort(R.string.input_social_web_address);
            return;
        }

        String id = getIntent().getStringExtra("id");
        EditCommunityWebSiteRequest request = new EditCommunityWebSiteRequest();

        SocialCaltureListBean origin = getIntent().getParcelableExtra("bean");

        if (origin.getOfficialWebsite().getOfficialWebsiteList().size() == 0) {
            ArrayList<EditListCommunityCultureResponse.OfficialWebsiteBean.OfficialWebsiteListBean> list = new ArrayList<>();
            list.add(new EditListCommunityCultureResponse.OfficialWebsiteBean.OfficialWebsiteListBean());
            origin.getOfficialWebsite().setOfficialWebsiteList(list);
            request.setType("add");
        } else {
            request.setType("update");
            request.setWebsiteId(origin.getOfficialWebsite().getOfficialWebsiteList().get(0).getWebsiteId());
        }

        request.setGroupId(id);
        request.setWebsiteTitle(name);
        request.setWebsiteContent(slogan);
        request.setWebsiteUrl(address);
        ServiceFactory.getInstance().getBaseService(Api.class)
                .editCommunityWebSite(GsonUtils.toJson(request))
                .flatMap((Function<BaseResponse<String>, ObservableSource<BaseResponse<String>>>) stringBaseResponse -> {
                    request.setType("openOrClose");
                    request.setOfficialWebsiteOpen("1");
                    return ServiceFactory.getInstance().getBaseService(Api.class)
                            .editCommunityWebSite(GsonUtils.toJson(request));
                })
                .compose(RxSchedulers.normalTrans())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    origin.getOfficialWebsite().setTitle(name);
                    origin.getOfficialWebsite().getOfficialWebsiteList().get(0).setWebsiteTitle(name);
                    origin.getOfficialWebsite().getOfficialWebsiteList().get(0).setWebsiteId(s);
                    origin.getOfficialWebsite().getOfficialWebsiteList().get(0).setWebsiteUrl(address);
                    origin.getOfficialWebsite().getOfficialWebsiteList().get(0).setWebsiteContent(slogan);

                    Intent intent = new Intent();
                    intent.putExtra("data", origin);
                    setResult(1, intent);
                    finish();
                }, this::handleApiError);
    }
}
