package com.zxjk.moneyspace.ui.minepage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetVicinityResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.GlideUtil;

import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;

public class NearByActivity extends BaseActivity {

    private RecyclerView recycler;
    private BaseQuickAdapter<GetVicinityResponse, BaseViewHolder> adapter;
    private List<GetVicinityResponse> nearList;
    private AMapLocation l;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by);

        l = getIntent().getParcelableExtra("location");

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getVicinity(String.valueOf(l.getLongitude()), String.valueOf(l.getLatitude()))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(this::initView, this::handleApiError);
    }

    private void initView(List<GetVicinityResponse> nearList) {
        this.nearList = nearList;
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BaseQuickAdapter<GetVicinityResponse, BaseViewHolder>(R.layout.item_nearby, nearList) {
            @Override
            protected void convert(BaseViewHolder helper, GetVicinityResponse item) {
                float distance = AMapUtils.calculateLineDistance(
                        new LatLng(Double.parseDouble(item.getLatitude()), Double.parseDouble(item.getLongitude())),
                        new LatLng(l.getLatitude(), l.getLongitude()));

                helper.setText(R.id.tvName, item.getNick().length() >= 15 ? item.getNick().substring(0, 12) + "..." : item.getNick())
                        .setText(R.id.tvSign, "个性签名：" + (TextUtils.isEmpty(item.getSignature()) ? "暂无" : item.getSignature()))
                        .setText(R.id.tvDistance, parseDistance(distance));

                GlideUtil.loadCircleImg(helper.getView(R.id.ivHead), item.getHeadPortrait());

                helper.setImageResource(R.id.ivSex, item.getSex().equals("0") ? R.drawable.icon_gender_man : R.drawable.icon_gender_woman);
            }
        };

        adapter.setOnItemClickListener((adapter1, view, position) ->
                CommonUtils.resolveFriendList(this, ((GetVicinityResponse) adapter1.getData().get(position)).getId()));

        recycler.setAdapter(adapter);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.collection_nearby);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        RelativeLayout rl_end = findViewById(R.id.rl_end);
        rl_end.setVisibility(View.VISIBLE);
        rl_end.setOnClickListener(v -> detail());
    }

    private void detail() {
        TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
        showAnimation.setDuration(250);
        TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
        dismissAnimation.setDuration(500);
        QuickPopupBuilder.with(NearByActivity.this)
                .contentView(R.layout.popup_nearby)
                .config(new QuickPopupConfig()
                        .withShowAnimation(showAnimation)
                        .withDismissAnimation(dismissAnimation)
                        .withClick(R.id.tv1, v -> handlePopwindow(1), true)
                        .withClick(R.id.tv2, v -> handlePopwindow(2), true)
                        .withClick(R.id.tv3, v -> handlePopwindow(3), true)
                        .withClick(R.id.tv4, v -> handlePopwindow(4), true))
                .show();
    }

    //1.女生 2.男生 3.全部 4.清除
    private void handlePopwindow(int action) {
        if (adapter == null) {
            return;
        }

        switch (action) {
            case 1:
            case 2:
                adapter.setNewData(handleSex(action));
                break;
            case 3:
                adapter.setNewData(nearList);
                break;
            case 4:
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .getVicinity("", "")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(NearByActivity.this)))
                        .subscribe(s -> finish(), this::handleApiError);
                break;
            default:
        }
    }

    private List<GetVicinityResponse> handleSex(int action) {
        List<GetVicinityResponse> result = new ArrayList<>(nearList.size());
        for (GetVicinityResponse r : nearList) {
            if (action == 1) {
                if (!r.getSex().equals("0")) {
                    result.add(r);
                }
            } else {
                if (r.getSex().equals("0")) {
                    result.add(r);
                }
            }
        }
        return result;
    }

    private String parseDistance(float distance) {
        if (distance <= 1) {
            return "< 1m";
        } else if (distance <= 100) {
            return "< " + ((int) (distance)) + "m";
        } else if (distance > 100 && distance < 1000) {
            return ("< " + (int) (distance / 100)) + "00m";
        } else {
            return ("< " + ((int) (distance - distance % 1000)) / 1000) + "km";
        }
    }

}
