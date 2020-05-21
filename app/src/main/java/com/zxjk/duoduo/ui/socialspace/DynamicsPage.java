package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetLiveInfoByGroupIdResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.ui.cast.WechatCastDetailActivity;
import com.zxjk.duoduo.utils.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DynamicsPage extends BaseFragment {

    private RecyclerView recycler;
    private String groupId;
    private boolean canModify;

    private BaseQuickAdapter<GetLiveInfoByGroupIdResponse, BaseViewHolder> adapter;

    @SuppressLint("CheckResult")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dynamicspager, container, false);

        groupId = getArguments().getString("groupId");
        canModify = getArguments().getBoolean("canModify", false);
        recycler = rootView.findViewById(R.id.recycler);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BaseQuickAdapter<GetLiveInfoByGroupIdResponse, BaseViewHolder>(R.layout.item_dynamics_page) {
            @Override
            protected void convert(BaseViewHolder helper, GetLiveInfoByGroupIdResponse item) {
                helper.setText(R.id.tv_topic, item.getTopic());
                TextView tvStartTime = helper.getView(R.id.tv_start_time);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String sd = sdf.format(new Date(Long.parseLong(item.getStartTime())));
                tvStartTime.setText(sd);

                ImageView tvLivePoster = helper.getView(R.id.tv_live_poster);
                Glide.with(getContext()).load(item.getLivePoster()).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        tvLivePoster.setBackground(resource);
                    }
                });
            }
        };

        View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.empty_recycler_social_calture, container, false);
        ImageView iv = emptyView.findViewById(R.id.iv);
        TextView tv = emptyView.findViewById(R.id.tv);
        iv.setImageResource(R.drawable.ic_emptyview_nofriend);
        tv.setText(R.string.noSocialDynamics);

        adapter.setEmptyView(emptyView);
        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener((adapter, view, position) -> {
            GetLiveInfoByGroupIdResponse getLiveInfoByGroupIdResponse = (GetLiveInfoByGroupIdResponse) adapter.getData().get(position);
            Intent intent = new Intent(getContext(), WechatCastDetailActivity.class);
            intent.putExtra("roomId", getLiveInfoByGroupIdResponse.getRoomId());
            intent.putExtra("chooseFlag", "1");
            intent.putExtra("livePlayBack", "1");
            startActivity(intent);
        });

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getLiveInfoByGroupId(groupId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getContext())))
                .subscribe(s -> adapter.setNewData(s), this::handleApiError);

        return rootView;
    }
}