package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.artifex.mupdf.viewer.DocumentActivity;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mabeijianxi.smallvideorecord2.JianXiCamera;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsVideo;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.EditListCommunityCultureResponse;
import com.zxjk.duoduo.bean.response.SocialCaltureListBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.ui.WebActivity;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.ui.widget.CircleNavigator;
import com.zxjk.duoduo.ui.widget.dialog.LoadingDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocialCalturePage extends BaseFragment {
    private final int REQUEST_SETTINGWEB = 1;
    private final int REQUEST_SETTINGFILE = 2;
    private final int REQUEST_SETTINGVIDEO = 3;
    private final int REQUEST_SETTINGAPP = 4;

    private RecyclerView recycler;
    private BaseMultiItemQuickAdapter<SocialCaltureListBean, BaseViewHolder> adapter;
    private String groupId;
    private boolean canModify;

    private java.util.Formatter timeFormatter;
    private StringBuilder mFormatBuilder;
    private File futureStudioIconFile;

    @SuppressLint("CheckResult")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        groupId = getArguments().getString("groupId");
        canModify = getArguments().getBoolean("canModify", false);

        mFormatBuilder = new StringBuilder();
        timeFormatter = new java.util.Formatter(mFormatBuilder, Locale.ENGLISH);

        rootView = inflater.inflate(R.layout.calturepager, container, false);

        recycler = rootView.findViewById(R.id.recycler);
        recycler.setItemAnimator(null);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseMultiItemQuickAdapter<SocialCaltureListBean, BaseViewHolder>(null) {

            {
                addItemType(SocialCaltureListBean.TYPE_WEB, R.layout.item_socialcalture_web);
                addItemType(SocialCaltureListBean.TYPE_FILE, R.layout.item_socialcalture_file);
                addItemType(SocialCaltureListBean.TYPE_VIDEO, R.layout.item_socialcalture_video);
                addItemType(SocialCaltureListBean.TYPE_APP, R.layout.item_socialcalture_app);
            }

            @Override
            protected void convert(BaseViewHolder helper, SocialCaltureListBean item) {
                helper.addOnClickListener(R.id.tvModify);

                switch (helper.getItemViewType()) {
                    case SocialCaltureListBean.TYPE_WEB:
                        initViewForWebPage(helper, item);
                        break;
                    case SocialCaltureListBean.TYPE_FILE:
                        initViewForFilePage(helper, item);
                        break;
                    case SocialCaltureListBean.TYPE_VIDEO:
                        initViewForVideoPage(helper, item);
                        break;
                    case SocialCaltureListBean.TYPE_APP:
                        initViewForAppPage(helper, item);
                        break;
                }
            }
        };

        adapter.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.empty_recycler_social_calture, container, false));

        adapter.setOnItemClickListener((adapter, view, position) -> {
            SocialCaltureListBean bean = (SocialCaltureListBean) adapter.getData().get(position);
            switch (adapter.getItemViewType(position)) {
                case SocialCaltureListBean.TYPE_WEB:
                    if (canModify) {
                        Intent intent = new Intent(getContext(), SocialWebEditActivity.class);
                        intent.putExtra("id", groupId);
                        intent.putExtra("bean", bean);
                        startActivityForResult(intent, REQUEST_SETTINGWEB);
                    } else {
                        TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
                        showAnimation.setDuration(250);
                        TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
                        dismissAnimation.setDuration(500);
                        QuickPopup show = QuickPopupBuilder.with(getActivity())
                                .contentView(R.layout.popup_dangerweb)
                                .config(new QuickPopupConfig()
                                        .withShowAnimation(showAnimation)
                                        .withDismissAnimation(dismissAnimation)
                                        .withClick(R.id.llBack, null, true)
                                        .withClick(R.id.tv, v -> {
                                            Intent intent = new Intent(getContext(), WebActivity.class);
                                            intent.putExtra("url", bean.getOfficialWebsite().getOfficialWebsiteList().get(0).getWebsiteUrl());
                                            intent.putExtra("title", bean.getOfficialWebsite().getOfficialWebsiteList().get(0).getWebsiteTitle());
                                            startActivity(intent);
                                        }, true))
                                .show();
                        TextView tv = show.findViewById(R.id.tvTips);
                        tv.setText(Html.fromHtml(getString(R.string.danger_tips,bean.getOfficialWebsite().getOfficialWebsiteList().get(0).getWebsiteTitle())));
                    }
                    break;
                case SocialCaltureListBean.TYPE_FILE:
                    if (canModify) {
                        Intent intent = new Intent(getContext(), SocialFileActivity.class);
                        intent.putExtra("id", groupId);
                        intent.putExtra("bean", bean);
                        startActivityForResult(intent, REQUEST_SETTINGFILE);
                    }
                    break;
                case SocialCaltureListBean.TYPE_VIDEO:
                    if (canModify) {
                        Intent intent = new Intent(getContext(), SocialVideoEditActivity.class);
                        intent.putExtra("id", groupId);
                        intent.putExtra("bean", bean);
                        startActivityForResult(intent, REQUEST_SETTINGVIDEO);
                    }
                    break;
                case SocialCaltureListBean.TYPE_APP:
                    if (canModify) {
                        Intent intent = new Intent(getContext(), SocialAppActivity.class);
                        intent.putExtra("groupId", groupId);
                        intent.putExtra("data", bean);
                        startActivityForResult(intent, REQUEST_SETTINGAPP);
                    }
                    break;
            }
        });

        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            SocialCaltureListBean bean = (SocialCaltureListBean) adapter.getData().get(position);
            Intent intent;
            switch (adapter.getItemViewType(position)) {
                case SocialCaltureListBean.TYPE_WEB:
                    intent = new Intent(getContext(), SocialWebEditActivity.class);
                    intent.putExtra("id", groupId);
                    intent.putExtra("bean", bean);
                    startActivityForResult(intent, REQUEST_SETTINGWEB);
                    break;
                case SocialCaltureListBean.TYPE_FILE:
                    intent = new Intent(getContext(), SocialFileActivity.class);
                    intent.putExtra("id", groupId);
                    intent.putExtra("bean", bean);
                    startActivityForResult(intent, REQUEST_SETTINGFILE);
                    break;
                case SocialCaltureListBean.TYPE_VIDEO:
                    intent = new Intent(getContext(), SocialVideoEditActivity.class);
                    intent.putExtra("id", groupId);
                    intent.putExtra("bean", bean);
                    startActivityForResult(intent, REQUEST_SETTINGVIDEO);
                    break;
                case SocialCaltureListBean.TYPE_APP:
                    intent = new Intent(getContext(), SocialAppActivity.class);
                    intent.putExtra("groupId", groupId);
                    intent.putExtra("data", bean);
                    startActivityForResult(intent, REQUEST_SETTINGAPP);
                    break;
            }
        });

        recycler.setAdapter(adapter);

        return rootView;
    }

    private void initViewForWebPage(BaseViewHolder helper, SocialCaltureListBean item) {
        EditListCommunityCultureResponse.OfficialWebsiteBean web = item.getOfficialWebsite();

        TextView tvModify = helper.getView(R.id.tvModify);
        if (canModify && web.getOfficialWebsiteList().size() != 0) {
            tvModify.setVisibility(View.VISIBLE);
        } else {
            tvModify.setVisibility(View.GONE);
        }

        helper.setText(R.id.tv, (web.getOfficialWebsiteList().size() == 0 || TextUtils.isEmpty(web.getOfficialWebsiteList().get(0).getWebsiteContent())) ?
                getContext().getString(R.string.empty_socialweb) : web.getOfficialWebsiteList().get(0).getWebsiteContent())
                .setText(R.id.tvTitle, item.getOfficialWebsite().getTitle());
    }

    private void initViewForFilePage(BaseViewHolder helper, SocialCaltureListBean item) {
        TextView tvModify = helper.getView(R.id.tvModify);
        if (canModify && item.getFiles().getFilesList().size() != 0) {
            tvModify.setVisibility(View.VISIBLE);
        } else {
            tvModify.setVisibility(View.GONE);
        }

        TextView tvNumLeft = helper.getView(R.id.tvNumLeft);
        if (canModify) {
            tvNumLeft.setText(getString(R.string.nums_left_upload_social_files, (Integer.parseInt(item.getFiles().getFileCreate()) - item.getFiles().getFilesList().size())));
        } else {
            tvNumLeft.setText("");
        }

        RecyclerView fileRecycler = helper.getView(R.id.recycler);
        fileRecycler.setLayoutManager(new GridLayoutManager(getContext(), 4));

        int dp56 = CommonUtils.dip2px(getContext(), 56);
        int dp48 = CommonUtils.dip2px(getContext(), 48);
        int dp8 = CommonUtils.dip2px(getContext(), 8);
        int recyclerWidth = ScreenUtils.getScreenWidth() - dp48;
        boolean isNormalSize = true;
        if (((recyclerWidth - dp56 * 4) / 4) < dp8) {
            isNormalSize = false;
        }

        boolean finalIsNormalSize = isNormalSize;

        BaseQuickAdapter<EditListCommunityCultureResponse.FilesBean.FilesListBean, BaseViewHolder> appAdapter;
        appAdapter = new BaseQuickAdapter<EditListCommunityCultureResponse.FilesBean.FilesListBean, BaseViewHolder>(R.layout.item_social_app, item.getFiles().getFilesList()) {
            @Override
            protected void convert(BaseViewHolder helper, EditListCommunityCultureResponse.FilesBean.FilesListBean item) {
                ImageView ivAppIcon = helper.getView(R.id.ivAppIcon);
                if (!finalIsNormalSize) {
                    LinearLayout llContent = helper.getView(R.id.llContent);
                    ViewGroup.LayoutParams layoutParams = llContent.getLayoutParams();
                    if (layoutParams.width != dp48) {
                        layoutParams.width = dp48;
                        llContent.setLayoutParams(layoutParams);
                        ViewGroup.LayoutParams layoutParams1 = ivAppIcon.getLayoutParams();
                        layoutParams1.width = dp48;
                        layoutParams1.height = dp48;
                        ivAppIcon.setLayoutParams(layoutParams1);
                    }
                }

                if (item.getFileFormat().contains("doc") || item.getFileFormat().contains("docx")) {
                    Glide.with(getContext()).load(R.drawable.ic_social_file_word).into(ivAppIcon);
                } else if (item.getFileFormat().contains("xls") || item.getFileFormat().contains("xlsx")) {
                    Glide.with(getContext()).load(R.drawable.ic_social_file_excel).into(ivAppIcon);
                } else if (item.getFileFormat().contains("ppt") || item.getFileFormat().contains("pptx")) {
                    Glide.with(getContext()).load(R.drawable.ic_social_file_ppt).into(ivAppIcon);
                } else {
                    Glide.with(getContext()).load(R.drawable.ic_social_file_pdf).into(ivAppIcon);
                }

                helper.setText(R.id.tvTitle, item.getFileName());
            }
        };

        appAdapter.setOnItemClickListener((adapter, view, position) -> downloadAndShowFile(appAdapter, position));

        View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.empty_recycler_social_app, (ViewGroup) rootView, false);
        appAdapter.setEmptyView(emptyView);
        emptyView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SocialFileActivity.class);
            intent.putExtra("id", groupId);
            intent.putExtra("bean", item);
            startActivityForResult(intent, REQUEST_SETTINGFILE);
        });
        tvModify.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SocialFileActivity.class);
            intent.putExtra("id", groupId);
            intent.putExtra("bean", item);
            startActivityForResult(intent, REQUEST_SETTINGFILE);
        });

        fileRecycler.setAdapter(appAdapter);

        appAdapter.setNewData(item.getFiles().getFilesList());
    }

    private void downloadAndShowFile(BaseQuickAdapter<EditListCommunityCultureResponse.FilesBean.FilesListBean, BaseViewHolder> appAdapter, int position) {
        EditListCommunityCultureResponse.FilesBean.FilesListBean filesListBean = appAdapter.getData().get(position);
        String url = filesListBean.getFileAddress().replace(Constant.OSS_URL, "");

        File dcim = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (dcim.exists()) {
            JianXiCamera.setVideoCachePath(dcim + "/Hilamg/");
        } else {
            JianXiCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
                    "/sdcard-ext/")
                    + "/Hilamg/");
        }
        if (filesListBean.getFileFormat().contains(".")) {
            futureStudioIconFile = new File(dcim + "/Hilamg/SocialFiles/", url + filesListBean.getFileFormat());
        } else {
            futureStudioIconFile = new File(dcim + "/Hilamg/SocialFiles/", url);
        }
        if (futureStudioIconFile.exists()) {
            if (futureStudioIconFile != null && futureStudioIconFile.exists()) {
                if (!filesListBean.getFileFormat().contains("pdf")) {
                    QbSdk.openFileReader(getContext(), futureStudioIconFile.getPath(), null, null);
                } else {
                    Intent intent = new Intent(getContext(), DocumentActivity.class);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.fromFile(new File(futureStudioIconFile.getPath())));
                    startActivity(intent);
                }
            } else {
                ToastUtils.showShort(R.string.cantopenfile);
            }
        } else {
            LoadingDialog loadingDialog = new LoadingDialog(getActivity(), getString(R.string.downloading));
            loadingDialog.show();
            ServiceFactory.getInstance().getNormalService(Constant.OSS_URL, Api.class)
                    .downloadFile(url)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            loadingDialog.dismissReally();
                            if (response != null && response.isSuccessful()) {
                                boolean toDisk;
                                if (filesListBean.getFileFormat().contains(".")) {
                                    toDisk = writeResponseBodyToDisk(response.body());
                                } else {
                                    toDisk = writeResponseBodyToDisk(response.body());
                                }
                                if (toDisk && futureStudioIconFile != null && futureStudioIconFile.exists()) {
                                    if (!filesListBean.getFileFormat().contains("pdf")) {
                                        QbSdk.openFileReader(getContext(), futureStudioIconFile.getPath(), null, null);
                                    } else {
                                        Intent intent = new Intent(getContext(), DocumentActivity.class);
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.setData(Uri.fromFile(new File(futureStudioIconFile.getPath())));
                                        startActivity(intent);
                                    }
                                } else {
                                    ToastUtils.showShort(R.string.cantopenfile);
                                }
                            } else {
                                ToastUtils.showShort(R.string.cantopenfile);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            loadingDialog.dismissReally();
                            ToastUtils.showShort(R.string.cantopenfile);
                        }
                    });
        }
    }

    private void initViewForAppPage(BaseViewHolder helper, SocialCaltureListBean item) {
        int dp56 = CommonUtils.dip2px(getContext(), 56);
        int dp48 = CommonUtils.dip2px(getContext(), 48);
        int dp8 = CommonUtils.dip2px(getContext(), 8);

        EditListCommunityCultureResponse.ApplicationBean app = item.getApplication();

        TextView tvModify = helper.getView(R.id.tvModify);
        if (canModify && app.getApplicationList().size() != 0) {
            tvModify.setVisibility(View.VISIBLE);
        } else {
            tvModify.setVisibility(View.GONE);
        }

        RecyclerView appRecycler = helper.getView(R.id.recycler);
        appRecycler.setLayoutManager(new GridLayoutManager(getContext(), 4));

        TextView tvTips = helper.getView(R.id.tvTips);
        if (canModify) {
            tvTips.setVisibility(View.VISIBLE);
        } else {
            tvTips.setVisibility(View.INVISIBLE);
        }

        int recyclerWidth = ScreenUtils.getScreenWidth() - dp48;
        boolean isNormalSize = true;
        if (((recyclerWidth - dp56 * 4) / 4) < dp8) {
            isNormalSize = false;
        }

        boolean finalIsNormalSize = isNormalSize;

        BaseQuickAdapter<EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean, BaseViewHolder> appAdapter;
        appAdapter = new BaseQuickAdapter<EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean, BaseViewHolder>(R.layout.item_social_app, item.getApplication().getApplicationList()) {
            @Override
            protected void convert(BaseViewHolder helper, EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean item) {
                ImageView ivAppIcon = helper.getView(R.id.ivAppIcon);
                if (!finalIsNormalSize) {
                    LinearLayout llContent = helper.getView(R.id.llContent);
                    ViewGroup.LayoutParams layoutParams = llContent.getLayoutParams();
                    if (layoutParams.width != dp48) {
                        layoutParams.width = dp48;
                        llContent.setLayoutParams(layoutParams);
                        ViewGroup.LayoutParams layoutParams1 = ivAppIcon.getLayoutParams();
                        layoutParams1.width = dp48;
                        layoutParams1.height = dp48;
                        ivAppIcon.setLayoutParams(layoutParams1);
                    }
                }

                GlideUtil.loadCornerImg(ivAppIcon, item.getApplicationLogo(), 10);
                helper.setText(R.id.tvTitle, item.getApplicationName());
            }
        };

        appAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (appAdapter.getData().get(position).getIsOffical().equals("0")) {
                TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
                showAnimation.setDuration(250);
                TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
                dismissAnimation.setDuration(500);
                QuickPopup show = QuickPopupBuilder.with(getActivity())
                        .contentView(R.layout.popup_dangerweb)
                        .config(new QuickPopupConfig()
                                .withShowAnimation(showAnimation)
                                .withDismissAnimation(dismissAnimation)
                                .withClick(R.id.llBack, null, true)
                                .withClick(R.id.tv, v -> {
                                    EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean applicationListBean = appAdapter.getData().get(position);
                                    Intent intent = new Intent(getContext(), WebActivity.class);
                                    intent.putExtra("url", applicationListBean.getApplicationAddress());
                                    intent.putExtra("title", applicationListBean.getApplicationName());
                                    startActivity(intent);
                                }, true))
                        .show();
                TextView tips = show.findViewById(R.id.tvTips);
                tips.setText(Html.fromHtml(getString(R.string.danger_tips, appAdapter.getData().get(position).getApplicationName())));
            } else {
                EditListCommunityCultureResponse.ApplicationBean.ApplicationListBean applicationListBean = appAdapter.getData().get(position);
                Intent intent = new Intent(getContext(), WebActivity.class);
                intent.putExtra("url", applicationListBean.getApplicationAddress());
                intent.putExtra("title", applicationListBean.getApplicationName());
                startActivity(intent);
            }
        });

        View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.empty_recycler_social_app, (ViewGroup) rootView, false);
        appAdapter.setEmptyView(emptyView);
        emptyView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SocialAppActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("data", item);
            startActivityForResult(intent, REQUEST_SETTINGAPP);
        });
        tvModify.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SocialAppActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("data", item);
            startActivityForResult(intent, REQUEST_SETTINGAPP);
        });

        appRecycler.setAdapter(appAdapter);

        appAdapter.setNewData(item.getApplication().getApplicationList());
    }

    private void initViewForVideoPage(BaseViewHolder helper, SocialCaltureListBean item) {
        EditListCommunityCultureResponse.VideoBean video = item.getVideo();

        TextView tvModify = helper.getView(R.id.tvModify);
        if (canModify && video.getVideoList().size() != 0) {
            tvModify.setVisibility(View.VISIBLE);
        } else {
            tvModify.setVisibility(View.GONE);
        }

        ViewPager pagerVideo = helper.getView(R.id.pagerVideo);
        LinearLayout llVideoEmpty = helper.getView(R.id.llVideoEmpty);
        MagicIndicator indicatorVideo = helper.getView(R.id.indicatorVideo);
        TextView tvNumLeft = helper.getView(R.id.tvNumLeft);

        ViewGroup.LayoutParams layoutParams = pagerVideo.getLayoutParams();
        int width = (int) ((ScreenUtils.getScreenWidth() - CommonUtils.dip2px(getContext(), 64)) / 1.15f);
        int height = (int) (width * 0.55f);
        layoutParams.width = width;
        layoutParams.height = height;
        pagerVideo.setLayoutParams(layoutParams);

        if (canModify) {
            tvNumLeft.setText(getString(R.string.upload_video_max1, (Integer.parseInt(item.getVideo().getVideoCreate()) - item.getVideo().getVideoList().size())));
        } else {
            tvNumLeft.setText("");
        }

        if (item.getVideo().getVideoList().size() != 0) {
            CircleNavigator navigator = new CircleNavigator(getContext());
            navigator.setFollowTouch(true);
            navigator.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorTheme));
            navigator.setCircleCount(video.getVideoList().size());
            indicatorVideo.setNavigator(navigator);

            pagerVideo.setOffscreenPageLimit(3);
            pagerVideo.setPageTransformer(false, new ViewPager.PageTransformer() {
                private static final float MAX_ALPHA = 0.5f;
                private static final float MAX_SCALE = 0.9f;

                @Override
                public void transformPage(View page, float position) {
                    if (position < -1 || position > 1) {
                        //不可见区域
                        page.setAlpha(MAX_ALPHA);
                        page.setScaleX(MAX_SCALE);
                        page.setScaleY(MAX_SCALE);
                    } else {
                        //可见区域，透明度效果
                        if (position <= 0) {
                            //pos区域[-1,0)
                            page.setAlpha(MAX_ALPHA + MAX_ALPHA * (1 + position));
                        } else {
                            //pos区域[0,1]
                            page.setAlpha(MAX_ALPHA + MAX_ALPHA * (1 - position));
                        }
                        //可见区域，缩放效果
                        float scale = Math.max(MAX_SCALE, 1 - Math.abs(position));
                        page.setScaleX(scale);
                        page.setScaleY(scale);
                    }
                }
            });

            pagerVideo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    indicatorVideo.onPageScrolled(position % video.getVideoList().size(), positionOffset, positionOffsetPixels);
                }

                public void onPageSelected(int position) {
                    indicatorVideo.onPageSelected(position % video.getVideoList().size());
                }

                public void onPageScrollStateChanged(int state) {
                    indicatorVideo.onPageScrollStateChanged(state);
                }
            });

            pagerVideo.setVisibility(View.VISIBLE);
            indicatorVideo.setVisibility(View.VISIBLE);
            llVideoEmpty.setVisibility(View.GONE);
            pagerVideo.setAdapter(new PagerAdapter() {
                @Override
                public int getCount() {
                    return item.getVideo().getVideoList().size();
                }

                @Override
                public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                    return view == object;
                }

                @NonNull
                @Override
                public Object instantiateItem(@NonNull ViewGroup container, int position) {
                    View inflate = LayoutInflater.from(getContext()).inflate(R.layout.item_social_video, container, false);

                    ImageView imageView = inflate.findViewById(R.id.ivVideoPic);
                    GlideUtil.loadNormalImg(imageView, item.getVideo().getVideoList().get(position).getVideoPic());
                    imageView.setOnClickListener(v -> {
                        if (video.getVideoList().size() == 0) {
                            Intent intent = new Intent(getContext(), SocialVideoEditActivity.class);
                            intent.putExtra("id", groupId);
                            intent.putExtra("bean", item);
                            startActivityForResult(intent, REQUEST_SETTINGVIDEO);
                        } else {
                            String url = item.getVideo().getVideoList().get(position).getVideoAddress();
                            if (TbsVideo.canUseTbsPlayer(getContext())) {
                                TbsVideo.openVideo(getContext(), url);
                            } else {
                                ToastUtils.showShort(R.string.cantopenvideo);
                            }
                        }
                    });

                    TextView tvVideoTitle = inflate.findViewById(R.id.tvVideoTitle);
                    tvVideoTitle.setText(item.getVideo().getVideoList().get(position).getVideoName());
                    TextView tvDuration = inflate.findViewById(R.id.tvDuration);
                    tvDuration.setText(stringForTime(Long.parseLong(item.getVideo().getVideoList().get(position).getVideoDuration())));

                    container.addView(inflate);

                    return inflate;
                }

                @Override
                public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                }
            });
        } else {
            pagerVideo.setVisibility(View.GONE);
            indicatorVideo.setVisibility(View.GONE);
            llVideoEmpty.setVisibility(View.VISIBLE);
        }
    }

    private String stringForTime(long timeMs) {
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;

        long minutes = (totalSeconds / 60) % 60;

        long hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return timeFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else {
            return timeFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public void bindCaltureData(List<SocialCaltureListBean> data) {
        if (data != null) {
            adapter.setNewData(data);
        }
    }

    //解析社群文化为多类型data
    private void parseCaltureResult(EditListCommunityCultureResponse r) {

        ArrayList<SocialCaltureListBean> caltures = new ArrayList<>();

        if (r.getOfficialWebsite() != null) {
            SocialCaltureListBean webBean = new SocialCaltureListBean(SocialCaltureListBean.TYPE_WEB);
            webBean.setOfficialWebsite(r.getOfficialWebsite());
            caltures.add(webBean);
        }
        if (r.getFiles() != null) {
            SocialCaltureListBean fileBean = new SocialCaltureListBean(SocialCaltureListBean.TYPE_FILE);
            fileBean.setFiles(r.getFiles());
            caltures.add(fileBean);
        }
        if (r.getVideo() != null) {
            SocialCaltureListBean videoBean = new SocialCaltureListBean(SocialCaltureListBean.TYPE_VIDEO);
            videoBean.setVideo(r.getVideo());
            caltures.add(videoBean);
        }
        if (r.getApplication() != null) {
            SocialCaltureListBean appBean = new SocialCaltureListBean(SocialCaltureListBean.TYPE_APP);
            appBean.setApplication(r.getApplication());
            caltures.add(appBean);
        }

        bindCaltureData(caltures);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        if (requestCode == REQUEST_SETTINGWEB && resultCode == 1) {
            SocialCaltureListBean bean = data.getParcelableExtra("data");
            adapter.getData().add(0, bean);
            adapter.getData().remove(1);
            adapter.notifyItemChanged(0);
        }

        if (requestCode == REQUEST_SETTINGFILE && resultCode == 1) {
            SocialCaltureListBean bean = data.getParcelableExtra("data");
            if (bean.getFiles().getFilesList().size() == 0)
                bean.getFiles().setFilesOpen("0");
            adapter.getData().add(1, bean);
            adapter.getData().remove(2);
            adapter.notifyItemChanged(1);
        }

        if (requestCode == REQUEST_SETTINGVIDEO && resultCode == 1) {
            SocialCaltureListBean bean = data.getParcelableExtra("data");
            if (bean.getVideo().getVideoList().size() == 0)
                bean.getVideo().setVideoOpen("0");
            adapter.getData().add(2, bean);
            adapter.getData().remove(3);
            adapter.notifyItemChanged(2);
        }

        if (requestCode == REQUEST_SETTINGAPP && resultCode == 1) {
            SocialCaltureListBean bean = data.getParcelableExtra("data");
            if (bean.getApplication().getApplicationList().size() == 0)
                bean.getApplication().setApplicationOpen("0");
            adapter.getData().add(3, bean);
            adapter.getData().remove(4);
            adapter.notifyItemChanged(3);
        }

    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            FileUtils.createOrExistsFile(futureStudioIconFile);
            //初始化输入流
            InputStream inputStream = null;
            //初始化输出流
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }

                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
