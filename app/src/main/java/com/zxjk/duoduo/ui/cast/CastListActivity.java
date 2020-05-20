package com.zxjk.duoduo.ui.cast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.CastListBean;
import com.zxjk.duoduo.db.Cast;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import java.util.ArrayList;
import java.util.List;

public class CastListActivity extends BaseActivity {

    private RecyclerView recycler;
    private BaseQuickAdapter<CastListBean, BaseViewHolder> adapter;
    private String chooseFlag;

    @SuppressLint("CheckResult")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast_list);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        chooseFlag = getIntent().getStringExtra("chooseFlag");
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.castlist);

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BaseQuickAdapter<CastListBean, BaseViewHolder>(R.layout.item_castlist) {
            @Override
            protected void convert(BaseViewHolder helper, CastListBean item) {
                GlideUtil.loadNormalImg(helper.getView(R.id.iv), item.getLivePoster());
                helper.setText(R.id.tvTitle, item.getTopic())
                        .setText(R.id.tvContent, getString(R.string.introduce) + "：" + item.getLiveDetails())
                        .setText(R.id.tvTime, getString(R.string.time) + "：" + TimeUtils.millis2String(Long.parseLong(item.getStartTime()), "yyyy-MM-dd HH:mm:ss"));
            }
        };

        recycler.setAdapter(adapter);

        View emptyView = View.inflate(this, R.layout.empty_publicgroup, null);
        TextView tv = emptyView.findViewById(R.id.tv);
        ImageView iv = emptyView.findViewById(R.id.iv);
        tv.setText(R.string.nocast);
        iv.setImageResource(R.drawable.bg_cast_empty);

        adapter.setEmptyView(emptyView);

        adapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(this, WechatCastDetailActivity.class);
            intent.putExtra("roomId", ((CastListBean) adapter.getData().get(position)).getRoomId());
            intent.putExtra("chooseFlag",chooseFlag);
            startActivity(intent);
        });

        ServiceFactory.getInstance().getBaseService(Api.class)
                .toLiveList()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .doOnNext(origin -> {
                    if (origin.size() == 0) {
                        Application.daoSession.getCastDao().deleteAll();
                    } else {
                        List<Cast> result = new ArrayList<>(origin.size());
                        for (CastListBean castListBean : origin) {
                            result.add(castListBean.convert2TableBean());
                        }
                        Application.daoSession.getCastDao().insertOrReplaceInTx(result);
                    }
                })
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(list -> {
                    adapter.setNewData(list);
                }, this::handleApiError);
    }
}
