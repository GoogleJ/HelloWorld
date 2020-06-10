package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.EditListCommunityCultureResponse;
import com.zxjk.duoduo.bean.response.SocialCaltureListBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.WebActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.OnlineServiceActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

public class SocialAppActivity extends BaseActivity {

    private final int REQUEST_ADD = 1;
    private final int REQUEST_MODIFY = 2;

    private RecyclerView recyclerApp1;
    private RecyclerView recyclerApp2;
    private TextView mCustomerService;

    private BaseQuickAdapter<EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean, BaseViewHolder> adapter1;
    private BaseQuickAdapter<EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean, BaseViewHolder> adapter2;

    private boolean isEdit = true;

    private SocialCaltureListBean data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_app);

        isEdit = getIntent().getBooleanExtra("isEdit", true);

        data = getIntent().getParcelableExtra("data");

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.social_app);

        recyclerApp1 = findViewById(R.id.recyclerApp1);
        recyclerApp2 = findViewById(R.id.recyclerApp2);
        mCustomerService = findViewById(R.id.tv_customer_service);
        mCustomerService.setOnClickListener(v -> {
            startActivity(new Intent(this, OnlineServiceActivity.class));
        });

        adapter1 = new BaseQuickAdapter<EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean, BaseViewHolder>(R.layout.item_social_app_list, null) {
            @Override
            protected void convert(BaseViewHolder helper, EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean item) {
                TextView textView = helper.getView(R.id.tvUnable);
                if (item.getIsOpen().equals("0")) {
                    textView.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.GONE);
                }

                ImageView ivAppIcon = helper.getView(R.id.ivAppIcon);
                GlideUtil.loadNormalImg(ivAppIcon, item.getApplicationLogo());

                helper.setText(R.id.tvTitle, item.getApplicationName());
            }
        };

        adapter2 = new BaseQuickAdapter<EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean, BaseViewHolder>(R.layout.item_social_app_list, null) {
            @Override
            protected void convert(BaseViewHolder helper, EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean item) {
                ImageView ivAppIcon = helper.getView(R.id.ivAppIcon);
                GlideUtil.loadNormalImg(ivAppIcon, item.getApplicationLogo());

                helper.setText(R.id.tvTitle, item.getApplicationName());
            }
        };

        adapter1.setOnItemClickListener((adapter, view, position) -> {
            if (adapter1.getData().get(position).getIsOpen().equals("0")) return;
            if (isEdit) {

                return;
            }
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("url", adapter1.getData().get(position).getApplicationAddress());
            intent.putExtra("title", adapter1.getData().get(position).getApplicationName());
            startActivity(intent);
        });

        adapter2.setOnItemClickListener((adapter, view, position) -> {
            if (isEdit) {
                Intent intent = new Intent(this, SocialAppEditActivity.class);
                intent.putExtra("groupId", getIntent().getStringExtra("groupId"));
                intent.putExtra("applicationId", adapter2.getData().get(position).getApplicationId());
                intent.putExtra("applicationAddress", adapter2.getData().get(position).getApplicationAddress());
                intent.putExtra("applicationLogo", adapter2.getData().get(position).getApplicationLogo());
                intent.putExtra("applicationName", adapter2.getData().get(position).getApplicationName());
                startActivity(intent);
                finish();
                return;
            }
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("url", adapter1.getData().get(position).getApplicationAddress());
            intent.putExtra("title", adapter1.getData().get(position).getApplicationName());
            startActivity(intent);
        });

        TextView emptyView1 = new TextView(this);
        emptyView1.setGravity(Gravity.CENTER);
        emptyView1.setText(R.string.no_thirdpart_application);
        emptyView1.setTextSize(15);
        emptyView1.setTextColor(ContextCompat.getColor(this, R.color.textcolor2));
        adapter1.setEmptyView(emptyView1);

        TextView emptyView2 = new TextView(this);
        emptyView2.setGravity(Gravity.CENTER);
        emptyView2.setText(R.string.no_cust_application);
        emptyView2.setTextSize(15);
        emptyView2.setTextColor(ContextCompat.getColor(this, R.color.textcolor2));
        adapter2.setEmptyView(emptyView2);

        recyclerApp1.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerApp2.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerApp1.setAdapter(adapter1);
        recyclerApp2.setAdapter(adapter2);

        initData(false);
    }

    @SuppressLint("CheckResult")
    private void initData(boolean refreshOrigin) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .communityApplicationList(getIntent().getStringExtra("groupId"))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(r -> {
//                    if (refreshOrigin) data.getApplication().setApplicationList(r.getApplication());
                    adapter1.setNewData(r.getOfficialApplication());
                    adapter2.setNewData(r.getApplication());
                }, this::handleApiError);
    }

    public void addApp(View view) {
        Intent intent = new Intent(this, SocialAppEditActivity.class);
        intent.putExtra("groupId", getIntent().getStringExtra("groupId"));
        intent.putExtra("isAdd", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        if (data != null) {
            Intent intent = new Intent();
            intent.putExtra("data", data);
            setResult(1, intent);
        }
        super.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;

        if (resultCode != 1) return;

        if (requestCode == REQUEST_ADD) {
            initData(true);
        } else if (requestCode == REQUEST_MODIFY) {
            initData(true);
        }
    }
}
