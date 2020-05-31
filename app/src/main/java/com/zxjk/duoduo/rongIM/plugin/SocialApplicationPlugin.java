package com.zxjk.duoduo.rongIM.plugin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter.OnItemClickListener;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.EditListCommunityCultureResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.WebActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import java.util.List;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class SocialApplicationPlugin extends BaseActivity implements IPluginModule {
    private BaseQuickAdapter<EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean,BaseViewHolder> adapter;
    private RecyclerView recyclerView;
    @Override
    public Drawable obtainDrawable(Context context) {
        return context.getDrawable(R.drawable.ic_application);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.socialApplication);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {


        TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
        showAnimation.setDuration(350);
        TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
        dismissAnimation.setDuration(500);
        QuickPopup quickPopupBuilder = QuickPopupBuilder.with(fragment.getContext())
                .contentView(R.layout.popup_social_application)
                .config(new QuickPopupConfig()
                        .withShowAnimation(showAnimation)
                        .withDismissAnimation(dismissAnimation)
                ).show();

        ImageView img_pull_down = quickPopupBuilder.findViewById(R.id.img_pull_down);
        img_pull_down.setOnClickListener(v -> quickPopupBuilder.dismiss());
        recyclerView = quickPopupBuilder.findViewById(R.id.recycler_view);

        adapter = new BaseQuickAdapter<EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean, BaseViewHolder>(R.layout.item_socialapplication) {
            @Override
            protected void convert(BaseViewHolder helper, EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean item) {

                ImageView ivAppIcon = helper.getView(R.id.ivAppIcon);
                GlideUtil.loadNormalImg(ivAppIcon, item.getApplicationLogo());

                helper.setText(R.id.tvTitle, item.getApplicationName());
            }
        };

        adapter.setOnItemClickListener((adapter1, view, position) -> {
            Intent intent = new Intent(fragment.getActivity(), WebActivity.class);
            intent.putExtra("url",adapter.getData().get(position).getApplicationAddress());
            intent.putExtra("title", adapter.getData().get(position).getApplicationName());
            fragment.startActivity(intent);
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(adapter);

        ServiceFactory.getInstance().getBaseService(Api.class)
                .communityApplicationList(rongExtension.getTargetId())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(fragment.getContext())))
                .compose(RxSchedulers.normalTrans())
                .subscribe(r -> {
                    List<EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean> listBeans = r.getOfficialApplication();
                    for (int i = 0; i < r.getApplication().size(); i++) {
                        EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean applicationListBean = new EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean();
                        applicationListBean.setApplicationAddress(r.getApplication().get(i).getApplicationAddress());
                        applicationListBean.setApplicationId(r.getApplication().get(i).getApplicationId());
                        applicationListBean.setApplicationLogo(r.getApplication().get(i).getApplicationLogo());
                        applicationListBean.setApplicationName(r.getApplication().get(i).getApplicationName());
                        applicationListBean.setIsOffical(r.getApplication().get(i).getIsOffical());
                        applicationListBean.setIsOpen(r.getApplication().get(i).getIsOpen());
                        listBeans.add(applicationListBean);
                    }
                    adapter.setNewData(listBeans);
                }, this::handleApiError);



    }

}