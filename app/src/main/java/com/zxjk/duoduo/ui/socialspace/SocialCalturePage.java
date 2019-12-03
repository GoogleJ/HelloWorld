package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.EditCommunityVideoRequest;
import com.zxjk.duoduo.bean.request.EditCommunityWebSiteRequest;
import com.zxjk.duoduo.bean.response.EditListCommunityCultureResponse;
import com.zxjk.duoduo.bean.response.SocialCaltureListBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.WebActivity;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SocialCalturePage extends BaseFragment implements View.OnClickListener {
    private final int REQUEST_SETTINGWEB = 1;
    private final int REQUEST_SETTINGFILE = 2;
    private final int REQUEST_SETTINGVIDEO = 3;
    private final int REQUEST_SETTINGAPP = 4;
    private final int REQUEST_SETTINGACT = 5;

    private LinearLayout llBottom;
    private RecyclerView recycler;
    private BaseMultiItemQuickAdapter<SocialCaltureListBean, BaseViewHolder> adapter;

    private String groupId;

    private OnDoneAction doneAction;

    public interface OnDoneAction {
        void done(List<SocialCaltureListBean> result);
    }

    public void setDoneAction(OnDoneAction doneAction) {
        this.doneAction = doneAction;
    }

    public SocialCalturePage(String groupId) {
        this.groupId = groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    private int dp56;

    @SuppressLint("CheckResult")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.calturepager, container, false);

        dp56 = CommonUtils.dip2px(getContext(), 56);

        llBottom = rootView.findViewById(R.id.llBottom);
        llBottom.setOnClickListener(this);

        recycler = rootView.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseMultiItemQuickAdapter<SocialCaltureListBean, BaseViewHolder>(null) {

            {
                addItemType(SocialCaltureListBean.TYPE_WEB, R.layout.item_socialcalture_web);
                addItemType(SocialCaltureListBean.TYPE_FILE, R.layout.item_socialcalture_file);
                addItemType(SocialCaltureListBean.TYPE_VIDEO, R.layout.item_socialcalture_video);
                addItemType(SocialCaltureListBean.TYPE_APP, R.layout.item_socialcalture_app);
                addItemType(SocialCaltureListBean.TYPE_ACTIVITY, R.layout.item_socialcalture_activity);
            }

            @Override
            protected void convert(BaseViewHolder helper, SocialCaltureListBean item) {
                boolean isEdit = llBottom.getVisibility() == View.VISIBLE;
                Switch sw = helper.getView(R.id.sw);
                if (!isEdit) {
                    sw.setVisibility(View.INVISIBLE);
                } else {
                    sw.setVisibility(View.VISIBLE);
                }

                helper.addOnClickListener(R.id.sw);
                switch (helper.getItemViewType()) {
                    case SocialCaltureListBean.TYPE_WEB:
                        initViewForWebPage(helper, item);
                        break;
                    case SocialCaltureListBean.TYPE_FILE:

                        break;
                    case SocialCaltureListBean.TYPE_VIDEO:
                        initViewForVideoPage(helper, item);
                        break;
                    case SocialCaltureListBean.TYPE_APP:

                        break;
                    case SocialCaltureListBean.TYPE_ACTIVITY:
                        initViewForActivityPage(helper);
                        break;
                }
            }
        };

        adapter.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.empty_recycler_social_calture, container, false));

        adapter.setOnItemClickListener((adapter, view, position) -> {
            SocialCaltureListBean bean = (SocialCaltureListBean) adapter.getData().get(position);
            switch (adapter.getItemViewType(position)) {
                case SocialCaltureListBean.TYPE_WEB:
                    if (llBottom.getVisibility() == View.VISIBLE) {
                        Intent intent = new Intent(getContext(), SocialWebEditActivity.class);
                        intent.putExtra("id", groupId);
                        intent.putExtra("bean", bean);
                        startActivityForResult(intent, REQUEST_SETTINGWEB);
                    } else {
                        Intent intent = new Intent(getContext(), WebActivity.class);
                        intent.putExtra("url", bean.getOfficialWebsite().getOfficialWebsiteList().get(0).getWebsiteUrl());
                        intent.putExtra("title", bean.getOfficialWebsite().getOfficialWebsiteList().get(0).getWebsiteTitle());
                        startActivity(intent);
                    }
                    break;
                case SocialCaltureListBean.TYPE_FILE:
                    ToastUtils.showShort(R.string.developing);

                    break;
                case SocialCaltureListBean.TYPE_VIDEO:
                    if (llBottom.getVisibility() == View.VISIBLE) {
                        Intent intent = new Intent(getContext(), SocialVideoEditActivity.class);
                        intent.putExtra("id", groupId);
                        intent.putExtra("bean", bean);
                        startActivityForResult(intent, REQUEST_SETTINGWEB);
                    } else {
                        ToastUtils.showShort("暂时无法播放本视频");
                    }
                    break;
                case SocialCaltureListBean.TYPE_APP:
                    ToastUtils.showShort(R.string.developing);
                    break;
                case SocialCaltureListBean.TYPE_ACTIVITY:
                    ToastUtils.showShort(R.string.developing);
                    break;
            }
        });

        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            SocialCaltureListBean bean = (SocialCaltureListBean) adapter.getData().get(position);
            Switch sw = (Switch) view;
            switch (adapter.getItemViewType(position)) {
                case SocialCaltureListBean.TYPE_WEB:
                    if (bean.getOfficialWebsite().getOfficialWebsiteList().size() == 0) {
                        sw.setChecked(false);
                        Intent intent = new Intent(getContext(), SocialWebEditActivity.class);
                        intent.putExtra("id", groupId);
                        intent.putExtra("bean", bean);
                        startActivityForResult(intent, REQUEST_SETTINGWEB);
                    } else {
                        //call api 2 update status
                        EditCommunityWebSiteRequest request = new EditCommunityWebSiteRequest();
                        request.setGroupId(groupId);
                        request.setType("openOrClose");
                        request.setOfficialWebsiteOpen(sw.isChecked() ? "1" : "0");
                        ServiceFactory.getInstance().getBaseService(Api.class)
                                .editCommunityWebSite(GsonUtils.toJson(request))
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.normalTrans())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getActivity())))
                                .subscribe(s -> {
                                    bean.getOfficialWebsite().setOfficialWebsiteOpen(sw.isChecked() ? "1" : "0");
                                    adapter.notifyItemChanged(position);
                                }, t -> {
                                    sw.setChecked(!sw.isChecked());
                                    handleApiError(t);
                                });
                    }
                    break;
                case SocialCaltureListBean.TYPE_FILE:
                    ((Switch) view).setChecked(false);
                    ToastUtils.showShort(R.string.developing);
                    break;
                case SocialCaltureListBean.TYPE_VIDEO:
                    if (bean.getVideo().getVideoList().size() == 0) {
                        sw.setChecked(false);
                        Intent intent = new Intent(getContext(), SocialVideoEditActivity.class);
                        intent.putExtra("id", groupId);
                        intent.putExtra("bean", bean);
                        startActivityForResult(intent, REQUEST_SETTINGWEB);
                    } else {
                        //call api 2 update status
                        EditCommunityVideoRequest request = new EditCommunityVideoRequest();
                        request.setGroupId(groupId);
                        request.setType("openOrClose");
                        request.setVideoOpen(sw.isChecked() ? "1" : "0");
                        ServiceFactory.getInstance().getBaseService(Api.class)
                                .editCommunityVideo(GsonUtils.toJson(request))
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.normalTrans())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getActivity())))
                                .subscribe(s -> {
                                    bean.getVideo().setVideoOpen(sw.isChecked() ? "1" : "0");
                                    adapter.notifyItemChanged(position);
                                }, t -> {
                                    sw.setChecked(!sw.isChecked());
                                    handleApiError(t);
                                });
                    }
                    break;
                case SocialCaltureListBean.TYPE_APP:
                    ((Switch) view).setChecked(false);
                    ToastUtils.showShort(R.string.developing);
                    break;
                case SocialCaltureListBean.TYPE_ACTIVITY:
                    ((Switch) view).setChecked(false);
                    ToastUtils.showShort(R.string.developing);
                    break;
            }
        });

        recycler.setAdapter(adapter);

        return rootView;
    }

    private void initViewForActivityPage(BaseViewHolder helper) {
        if (llBottom.getVisibility() == View.VISIBLE) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) helper.itemView.getLayoutParams();
            layoutParams.bottomMargin = dp56;
            helper.itemView.setLayoutParams(layoutParams);
        } else {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) helper.itemView.getLayoutParams();
            layoutParams.bottomMargin = 0;
            helper.itemView.setLayoutParams(layoutParams);
        }
    }

    private void initViewForWebPage(BaseViewHolder helper, SocialCaltureListBean item) {
        EditListCommunityCultureResponse.OfficialWebsiteBean web = item.getOfficialWebsite();
        if (!TextUtils.isEmpty(web.getOfficialWebsiteOpen())
                && web.getOfficialWebsiteOpen().equals("1")) {
            helper.setChecked(R.id.sw, true);
        } else {
            helper.setChecked(R.id.sw, false);
        }
        helper.setText(R.id.tv, (web.getOfficialWebsiteList().size() == 0 || TextUtils.isEmpty(web.getOfficialWebsiteList().get(0).getWebsiteContent())) ?
                getContext().getString(R.string.empty_socialweb) : web.getOfficialWebsiteList().get(0).getWebsiteContent())
                .setText(R.id.tvTitle, item.getOfficialWebsite().getTitle());
    }

    private void initViewForVideoPage(BaseViewHolder helper, SocialCaltureListBean item) {
        ViewPager pagerVideo = helper.getView(R.id.pagerVideo);
        LinearLayout llVideoEmpty = helper.getView(R.id.llVideoEmpty);

        if (item.getVideo().getVideoList().size() != 0) {
            pagerVideo.setVisibility(View.VISIBLE);
            llVideoEmpty.setVisibility(View.GONE);
        } else {
            pagerVideo.setVisibility(View.GONE);
            llVideoEmpty.setVisibility(View.VISIBLE);
        }

        if (item.getVideo().getVideoList().size() != 0 && item.getVideo().getVideoOpen().equals("1")) {
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
                    ImageView imageView = new ImageView(getContext());
                    GlideUtil.loadNormalImg(imageView, item.getVideo().getVideoList().get(position).getVideoPic());
                    return imageView;
                }

                @Override
                public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                }
            });
        }
    }

    public void bindCaltureData(List<SocialCaltureListBean> data) {
        if (data != null) {
            adapter.setNewData(data);
        }
    }

    @SuppressLint("CheckResult")
    public void change2Edit(EditListCommunityCultureResponse r) {
        parseCaltureResult(r);
        llBottom.setVisibility(View.VISIBLE);
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
        if (r.getActivities() != null) {
            SocialCaltureListBean actBean = new SocialCaltureListBean(SocialCaltureListBean.TYPE_ACTIVITY);
            actBean.setActivities(r.getActivities());
            caltures.add(actBean);
        }

        bindCaltureData(caltures);
    }

    @Override
    public void onClick(View v) {
        if (doneAction != null) {
            if (adapter != null) {
                Iterator<SocialCaltureListBean> iterator = adapter.getData().iterator();
                while (iterator.hasNext()) {
                    SocialCaltureListBean bean = iterator.next();
                    switch (bean.getItemType()) {
                        case SocialCaltureListBean.TYPE_WEB:
                            if (bean.getOfficialWebsite().getOfficialWebsiteOpen().equals("0"))
                                iterator.remove();
                            break;
                        case SocialCaltureListBean.TYPE_FILE:
                            if (bean.getFiles().getFilesOpen().equals("0"))
                                iterator.remove();
                            break;
                        case SocialCaltureListBean.TYPE_VIDEO:
                            if (bean.getVideo().getVideoOpen().equals("0"))
                                iterator.remove();
                            break;
                        case SocialCaltureListBean.TYPE_APP:
                            if (bean.getApplication().getApplicationOpen().equals("0"))
                                iterator.remove();
                            break;
                        case SocialCaltureListBean.TYPE_ACTIVITY:
                            if (bean.getActivities().getActivityOpen().equals("0"))
                                iterator.remove();
                            break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
            doneAction.done(adapter.getData());
            llBottom.setVisibility(View.GONE);
        }
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
            adapter.getData().add(1, bean);
            adapter.getData().remove(2);
            adapter.notifyItemChanged(1);
        }

        if (requestCode == REQUEST_SETTINGVIDEO && resultCode == 1) {
            SocialCaltureListBean bean = data.getParcelableExtra("data");
            adapter.getData().add(2, bean);
            adapter.getData().remove(3);
            adapter.notifyItemChanged(2);
        }

        if (requestCode == REQUEST_SETTINGAPP && resultCode == 1) {
            SocialCaltureListBean bean = data.getParcelableExtra("data");
            adapter.getData().add(3, bean);
            adapter.getData().remove(4);
            adapter.notifyItemChanged(3);
        }

        if (requestCode == REQUEST_SETTINGACT && resultCode == 1) {
            SocialCaltureListBean bean = data.getParcelableExtra("data");
            adapter.getData().add(4, bean);
            adapter.getData().remove(5);
            adapter.notifyItemChanged(4);
        }
    }
}
