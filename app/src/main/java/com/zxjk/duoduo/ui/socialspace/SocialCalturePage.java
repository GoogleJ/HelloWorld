package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.EditListCommunityCultureResponse;
import com.zxjk.duoduo.bean.response.SocialCaltureListBean;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SocialCalturePage extends BaseFragment implements View.OnClickListener {
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
                helper.addOnClickListener(R.id.sw);
                switch (helper.getItemViewType()) {
                    case SocialCaltureListBean.TYPE_WEB:
                        EditListCommunityCultureResponse.OfficialWebsiteBean web = item.getOfficialWebsite();
                        if (!TextUtils.isEmpty(web.getOfficialWebsiteOpen())
                                && web.getOfficialWebsiteOpen().equals("1")) {
                            helper.setChecked(R.id.sw, true);
                        } else {
                            helper.setChecked(R.id.sw, false);
                        }
                        helper.setText(R.id.tv, (web.getOfficialWebsiteList().size() == 0 || TextUtils.isEmpty(web.getOfficialWebsiteList().get(0).getWebsiteTitle())) ?
                                getContext().getString(R.string.empty_socialweb) : web.getOfficialWebsiteList().get(0).getWebsiteTitle())
                                .setText(R.id.tvTitle, item.getOfficialWebsite().getTitle());
                        break;
                    case SocialCaltureListBean.TYPE_FILE:

                        break;
                    case SocialCaltureListBean.TYPE_VIDEO:

                        break;
                    case SocialCaltureListBean.TYPE_APP:

                        break;
                    case SocialCaltureListBean.TYPE_ACTIVITY:
                        Switch sw = helper.getView(R.id.sw);
                        if (!sw.isChecked()) {
                            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) helper.itemView.getLayoutParams();
                            layoutParams.bottomMargin = dp56;
                            helper.itemView.setLayoutParams(layoutParams);
                        } else {
                            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) helper.itemView.getLayoutParams();
                            layoutParams.bottomMargin = 0;
                            helper.itemView.setLayoutParams(layoutParams);
                        }
                        break;
                }
            }
        };

        adapter.setOnItemClickListener((adapter, view, position) -> {
            switch (adapter.getItemViewType(position)) {
                case SocialCaltureListBean.TYPE_WEB:
                    startActivity(new Intent(getContext(), SocialWebEditActivity.class));

                    break;
                case SocialCaltureListBean.TYPE_FILE:
                    ToastUtils.showShort(R.string.developing);

                    break;
                case SocialCaltureListBean.TYPE_VIDEO:
                    ToastUtils.showShort(R.string.developing);
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
            switch (adapter.getItemViewType(position)) {
                case SocialCaltureListBean.TYPE_WEB:

                    break;
                case SocialCaltureListBean.TYPE_FILE:
                    ((Switch) view).setChecked(false);
                    ToastUtils.showShort(R.string.developing);
                    break;
                case SocialCaltureListBean.TYPE_VIDEO:
                    ((Switch) view).setChecked(false);
                    ToastUtils.showShort(R.string.developing);
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

}
