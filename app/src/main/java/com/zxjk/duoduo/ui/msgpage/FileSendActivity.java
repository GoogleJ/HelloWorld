package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;

import io.rong.imlib.model.UserInfo;

@SuppressLint("CheckResult")
public class FileSendActivity extends BaseActivity implements PhoneFileFragment.BackHandledInterface {

    private PhoneFileFragment phoneFileFragment;

    private TextView ivBack;
    private TextView tvWeChatFile;
    private TextView tvPhoneFile;
    private ViewPager viewPager;

    private UserInfo userInfo;

    private String groupId;
    private String uId;
    private int pageItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_send);

        initView();

        initInfo();

        initData();
    }

    private void initView() {
        ivBack = findViewById(R.id.ivBack);
        tvWeChatFile = findViewById(R.id.tv_we_chat_file);
        tvPhoneFile = findViewById(R.id.tv_phone_file);
        viewPager = findViewById(R.id.pager);
    }

    private void initData() {

        ivBack.setOnClickListener(v -> finish());
        tvWeChatFile.setTextColor(getResources().getColor(R.color.black, getTheme()));
        tvPhoneFile.setTextColor(getResources().getColor(R.color.m_add_friend_wechat_label_2, getTheme()));
        tvWeChatFile.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tvPhoneFile.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return position == 0 ? new WeChatFileFragment() : new PhoneFileFragment();
            }

            @Override
            public int getCount() {
                return 2;
            }

        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        pageItem = 0;
                        weChatFileButton();
                        break;
                    case 1:
                        pageItem = 1;
                        phoneFileButton();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initInfo() {
        groupId = getIntent().getStringExtra("groupId");
        userInfo = getIntent().getParcelableExtra("user");

        SharedPreferences mSharedPreferences = getSharedPreferences("sUId", FileSendActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        Api api = ServiceFactory.getInstance().getBaseService(Api.class);
        if (groupId == null) {
            uId = userInfo.getUserId();
            if (uId == null) {
                api.getCustomerInfoById(getIntent().getStringExtra("userId"))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(l ->
                                        uId = userInfo.getUserId()
                                , this::handleApiError);
            }
            editor.putString("ConversationType", "private");
        } else {
            uId = groupId;
            editor.putString("ConversationType", "group");
        }
        editor.putString("uId", uId);
        editor.apply();
    }

    public void weChatFile(View view) {
        weChatFileButton();
        viewPager.setCurrentItem(0);
    }

    public void phoneFile(View view) {
        phoneFileButton();
        viewPager.setCurrentItem(1);
    }

    private void weChatFileButton() {
        tvWeChatFile.setBackgroundResource(0);
        tvWeChatFile.setTextColor(getResources().getColor(R.color.black, getTheme()));
        tvWeChatFile.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tvPhoneFile.setBackground(getResources().getDrawable(R.drawable.shape_file_send2, getTheme()));
        tvPhoneFile.setTextColor(getResources().getColor(R.color.m_add_friend_wechat_label_2, getTheme()));
        tvPhoneFile.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
    }

    private void phoneFileButton() {
        tvPhoneFile.setBackgroundResource(0);
        tvPhoneFile.setTextColor(getResources().getColor(R.color.black, getTheme()));
        tvPhoneFile.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tvWeChatFile.setBackground(getResources().getDrawable(R.drawable.shape_file_send1, getTheme()));
        tvWeChatFile.setTextColor(getResources().getColor(R.color.m_add_friend_wechat_label_2, getTheme()));
        tvWeChatFile.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

    }


    @Override
    public void setSelectedFragment(PhoneFileFragment phoneFileFragment) {
        this.phoneFileFragment = phoneFileFragment;
    }

    @Override
    public void onBackPressed() {

        if (phoneFileFragment == null || !phoneFileFragment.onBackPressed()) {
            super.onBackPressed();
            finish();
        }else {
            if(pageItem == 0){
                finish();
            }
        }
    }
}
