package com.zxjk.duoduo.rongIM.plugin;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
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
    private List<EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean> listBeans;
    private boolean isLongPress = true;

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
        ServiceFactory.getInstance().getBaseService(Api.class)
                .communityApplicationList(rongExtension.getTargetId())
                .compose(bindToLifecycle())
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
                        isLongPress = true;
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

                    popView.findViewById(R.id.ll_customer_service).setOnClickListener(v -> {

                    });

                    adapter.setOnItemClickListener((adapter1, view, position) -> {
                        if (adapter.getData().get(position).getApplicationName().equals("编辑")) {
                            ToastUtils.showShort("0000000..0000000");
                        }
                        if(isLongPress){
                            Intent intent = new Intent(fragment.getActivity(), WebActivity.class);
                            intent.putExtra("url", adapter.getData().get(position).getApplicationAddress());
                            intent.putExtra("appName", adapter.getData().get(position).getApplicationName());
                            intent.putExtra("fromSocialApp", true);
                            fragment.startActivity(intent);
                        }
                    });

                    adapter.setOnItemLongClickListener((adapter12, view, position) -> {
                        if(isLongPress){
                            Resources resources = fragment.getContext().getResources();
                            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                                    + resources.getResourcePackageName(R.drawable.ic_compile) + "/"
                                    + resources.getResourceTypeName(R.drawable.ic_compile) + "/"
                                    + resources.getResourceEntryName(R.drawable.ic_compile));
                            EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean applicationListBean = new EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean();
                            applicationListBean.setApplicationLogo(uri.toString());
                            applicationListBean.setApplicationName("编辑");
//                        listBeans.add(applicationListBean);
                            adapter.addData(applicationListBean);
                            isLongPress = false;
                        }
                        return false;
                    });

                    recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
                    recyclerView.setAdapter(adapter);

                    adapter.setNewData(listBeans);

                    popView.showPopupWindow();
                }, this::handleApiError);
    }

}