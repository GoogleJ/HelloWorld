package com.zxjk.duoduo.ui.socialspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.EditListCommunityCultureResponse;
import com.zxjk.duoduo.bean.response.SocialCaltureListBean;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class SocialVideoEditActivity extends BaseActivity {

    private static final int REQUEST_ADD = 1;

    private LinearLayout llTopTips;
    private RecyclerView recycler;
    private BaseQuickAdapter<EditListCommunityCultureResponse.VideoBean, BaseViewHolder> adapter;

    private SocialCaltureListBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_video_edit);

        bean = getIntent().getParcelableExtra("bean");

        llTopTips = findViewById(R.id.llTopTips);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.video_manage1);

        recycler = findViewById(R.id.recycler);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BaseQuickAdapter<EditListCommunityCultureResponse.VideoBean, BaseViewHolder>(R.layout.item_social_video_list_edit, null) {
            @Override
            protected void convert(BaseViewHolder helper, EditListCommunityCultureResponse.VideoBean item) {

            }
        };

        View emptyview = LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, null, false);
        TextView tv = emptyview.findViewById(R.id.tv);
        ImageView iv = emptyview.findViewById(R.id.iv);
        tv.setText(R.string.emptylist4);
        iv.setImageResource(R.drawable.ic_empty_videos);
        adapter.setEmptyView(emptyview);

        recycler.setAdapter(adapter);
    }

    public void closeTip(View view) {
        llTopTips.setVisibility(View.GONE);
    }

    public void createVideo(View view) {
        if (bean.getVideo().getVideoList().size() != 3) {
            ToastUtils.showShort(R.string.upload_video_max);
            return;
        }
        Intent intent = new Intent(this, SocialVideoAddActivity.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        intent.putExtra("maxCount", 3 - bean.getVideo().getVideoList().size());
        startActivityForResult(intent, REQUEST_ADD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;
        if (resultCode != 1) return;

        switch (requestCode) {
            case REQUEST_ADD:

                break;
        }
    }
}
