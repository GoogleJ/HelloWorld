package com.zxjk.moneyspace.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.ZoomActivity;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.GlideUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

@SuppressLint("CheckResult")
public class AddFriendDetailsActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.iv_gender)
    ImageView ivGender;
    @BindView(R.id.iv_headPortrait)
    ImageView ivHeadPortrait;
    @BindView(R.id.tv_DuoDuoNumber)
    TextView tvDuoDuoNumber;
    @BindView(R.id.tv_district)
    TextView tvDistrict;
    @BindView(R.id.tv_signature)
    TextView tvSignature;
    @BindView(R.id.tv_addAddressBook)
    TextView tvAddAddressBook;

    private String imageUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        ButterKnife.bind(this);

        tvTitle.setText(R.string.personal_details);

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getCustomerInfoById(getIntent().getStringExtra("friendId"))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(r -> {
                    tvNickname.setText(r.getNick());
                    tvDuoDuoNumber.setText(getString(R.string.duoduo_acount) + " " + r.getDuoduoId());
                    tvDistrict.setText(getString(R.string.district) + " " + r.getAddress());
                    tvSignature.setText(TextUtils.isEmpty(r.getSignature()) ? getString(R.string.none) : r.getSignature());
                    imageUrl = r.getHeadPortrait();
                    GlideUtil.loadCircleImg(ivHeadPortrait, r.getHeadPortrait());

                    if ("0".equals(r.getSex())) {
                        ivGender.setImageDrawable(getDrawable(R.drawable.icon_gender_man));
                    } else {
                        ivGender.setImageDrawable(getDrawable(R.drawable.icon_gender_woman));
                    }

                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(r.getId(), r.getNick(), Uri.parse(r.getHeadPortrait())));
                }, this::handleApiError);
    }

    @OnClick({R.id.rl_back, R.id.tv_addAddressBook, R.id.iv_headPortrait})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_addAddressBook:
                Intent intent = new Intent(this, VerificationActivity.class);
                intent.putExtra("friendId", getIntent().getStringExtra("friendId"));
                intent.putExtra("groupId", getIntent().getStringExtra("groupId"));
                startActivity(intent);
                break;
            case R.id.iv_headPortrait:
                Intent intent5 = new Intent(this, ZoomActivity.class);
                intent5.putExtra("image", imageUrl);
                startActivity(intent5,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                                ivHeadPortrait, "img").toBundle());
                break;
        }
    }
}
