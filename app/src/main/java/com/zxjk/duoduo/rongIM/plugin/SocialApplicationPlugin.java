package com.zxjk.duoduo.rongIM.plugin;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.CommunityApplicationListResponse;
import com.zxjk.duoduo.bean.response.EditListCommunityCultureResponse;
import com.zxjk.duoduo.bean.response.SocialCaltureListBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.WebActivity;
import com.zxjk.duoduo.ui.minepage.OnlineServiceActivity;
import com.zxjk.duoduo.ui.socialspace.SocialAppActivity;
import com.zxjk.duoduo.ui.socialspace.SocialCalturePage;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class SocialApplicationPlugin implements IPluginModule {
    public boolean isAdministrator;
    private List<EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean> listBeans;

    public SocialApplicationPlugin(boolean isAdministrator) {
        this.isAdministrator = isAdministrator;
    }

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
        LifecycleProvider<Lifecycle.Event> provider = AndroidLifecycle.createLifecycleProvider(fragment.getActivity());
        ServiceFactory.getInstance().getBaseService(Api.class)
                .communityApplicationList(rongExtension.getTargetId())
                .compose(provider.bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(fragment.getContext())))
                .compose(RxSchedulers.normalTrans())
                .subscribe(r -> {
                    listBeans = r.getOfficialApplication();
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


                    if (isAdministrator) {
                        Resources resources = fragment.getContext().getResources();
                        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                                + resources.getResourcePackageName(R.drawable.ic_compile) + "/"
                                + resources.getResourceTypeName(R.drawable.ic_compile) + "/"
                                + resources.getResourceEntryName(R.drawable.ic_compile));
                        EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean applicationListBean = new EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean();
                        applicationListBean.setApplicationLogo(uri.toString());
                        applicationListBean.setApplicationName("编辑");
                        listBeans.add(applicationListBean);
                    }

                    TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
                    showAnimation.setDuration(200);
                    showAnimation.setInterpolator(new AccelerateInterpolator());
                    TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
                    dismissAnimation.setDuration(400);
                    QuickPopup popView = QuickPopupBuilder.with(fragment.getActivity())
                            .contentView(R.layout.popup_social_application)
                            .config(new QuickPopupConfig()
                                    .withShowAnimation(showAnimation)
                                    .withDismissAnimation(dismissAnimation)
                                    .gravity(Gravity.BOTTOM)
                            ).build();

                    ImageView img_pull_down = popView.findViewById(R.id.img_pull_down);
                    img_pull_down.setOnClickListener(v -> {
                        popView.dismiss();
                    });
                    RecyclerView recyclerView = popView.findViewById(R.id.recycler_view);
                    BaseQuickAdapter<EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean, BaseViewHolder> adapter
                            = new BaseQuickAdapter<EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean, BaseViewHolder>(R.layout.item_socialapplication) {
                        @Override
                        protected void convert(BaseViewHolder helper, EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean item) {
                            ImageView ivAppIcon = helper.getView(R.id.ivAppIcon);
                            GlideUtil.loadNormalImg(ivAppIcon, item.getApplicationLogo());
                            helper.setText(R.id.tvTitle, item.getApplicationName());
                        }
                    };


                    if(isAdministrator){
                        popView.findViewById(R.id.ll_customer_service).setVisibility(View.VISIBLE);
                    }else {
                        popView.findViewById(R.id.ll_customer_service).setVisibility(View.GONE);
                    }
                    popView.findViewById(R.id.ll_customer_service).setOnClickListener(v -> {
                        fragment.startActivity(new Intent(fragment.getActivity(), OnlineServiceActivity.class));
                    });

                    adapter.setOnItemClickListener((adapter1, view, position) -> {
                        if (adapter.getData().get(position).getApplicationName().equals("编辑")) {
                            Intent intent = new Intent(fragment.getActivity(), SocialAppActivity.class);
                            intent.putExtra("groupId", rongExtension.getTargetId());
                            fragment.startActivity(intent);
                            popView.dismiss();
                        } else {
                            Intent intent = new Intent(fragment.getActivity(), WebActivity.class);
                            intent.putExtra("url", adapter.getData().get(position).getApplicationAddress());
                            intent.putExtra("appName", adapter.getData().get(position).getApplicationName());
                            intent.putExtra("fromSocialApp", true);

                            fragment.startActivity(intent);
                        }
                    });

                    recyclerView.setLayoutManager(new GridLayoutManager(fragment.getContext(), 4));
                    recyclerView.setAdapter(adapter);

                    adapter.setNewData(listBeans);
                    popView.showPopupWindow();
                }, t -> {
                });
    }



    @Override
    public void onActivityResult(int var1, int var2, Intent var3) {

    }

}