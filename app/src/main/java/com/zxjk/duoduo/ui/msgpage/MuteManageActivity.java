package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GroupManagementInfoBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.NewsLoadMoreView;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("CheckResult")
public class MuteManageActivity extends BaseActivity {

    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    private SwipeRefreshLayout refreshLayout;

    private int page = 0;
    private int numsPerPage = 10;
    private String searchKey = "";
    private BaseQuickAdapter<GroupManagementInfoBean, BaseViewHolder> adapter1;
    private BaseQuickAdapter<GroupManagementInfoBean, BaseViewHolder> adapter2;
    private List<GroupManagementInfoBean> data1 = new ArrayList<>();
    private List<GroupManagementInfoBean> data2 = new ArrayList<>();

    private TextView tvListMute;
    private TextView tv_title;
    private RelativeLayout rl_back;
    private EditText etSearch;

    private Api api;

    private String groupId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mute_manage);

        api = ServiceFactory.getInstance().getBaseService(Api.class);
        groupId = getIntent().getStringExtra("groupId");

        initView();

        initData();
    }

    private void initView() {

        recyclerView2 = findViewById(R.id.rc2);
        refreshLayout = findViewById(R.id.refresh_layout);

        tv_title = findViewById(R.id.tv_title);
        rl_back = findViewById(R.id.rl_back);
        etSearch = findViewById(R.id.etSearch);
    }

    private void initData() {
        tv_title.setText(R.string.mute_manage);
        rl_back.setOnClickListener(v -> finish());
        refreshLayout.setRefreshing(true);
        refreshLayout.setColorSchemeColors(Color.parseColor("#4585F5"));
        refreshLayout.setOnRefreshListener(() -> {
            page = 0;
            if (searchKey.equals("")) {
                getDumbManagers("");
            } else {
                getDumbManagers(searchKey);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                page = 0;
                searchKey = editable.toString();
                if (TextUtils.isEmpty(searchKey)) {
                    getDumbManagers("");
                }
            }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                page = 0;

                if (!TextUtils.isEmpty(searchKey)) {
                    getDumbManagers(searchKey);
                    return true;
                }
            }
            return false;
        });


        adapter2 = new BaseQuickAdapter<GroupManagementInfoBean, BaseViewHolder>(R.layout.item_members_of_the_banned) {
            @Override
            protected void convert(BaseViewHolder helper, GroupManagementInfoBean item) {
                GroupManagementInfoBean groupManagementInfo = item;
                helper.setText(R.id.tv_name, item.getNick());
                GlideUtil.loadCircleImg(helper.getView(R.id.iv_head), item.getHeadPortrait());

                TextView mute = helper.getView(R.id.tv_mute);
                TextView blacklist = helper.getView(R.id.tv_blacklist);

                blacklist.setText("加入黑名单");
                blacklist.setTextColor(getResources().getColor(R.color.color5c, null));
                blacklist.setBackground(getResources().getDrawable(R.drawable.shape_kick_nor, null));

                mute.setText(R.string.mute);
                mute.setBackground(getResources().getDrawable(R.drawable.shape_mute_nor, null));
                mute.setTextColor(getResources().getColor(R.color.colorTheme, null));

                mute.setOnClickListener(v -> api.muteMembers(groupId, item.getCustomerId(), "add")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(MuteManageActivity.this)))
                        .compose(RxSchedulers.normalTrans())
                        .subscribe(c -> {
                            tvListMute.setVisibility(View.VISIBLE);
                            groupManagementInfo.setIsBanned("1");
                            data1.add(0, groupManagementInfo);
                            adapter1.notifyItemInserted(0);
                            data2.remove(item);
                            notifyItemRemoved(helper.getAdapterPosition());
                        }, MuteManageActivity.this::handleApiError));

                blacklist.setOnClickListener(v -> api.blacklistOperation(groupId, item.getCustomerId(), "kickOut")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(MuteManageActivity.this)))
                        .compose(RxSchedulers.normalTrans())
                        .subscribe(c -> {
                            tvListMute.setVisibility(View.VISIBLE);
                            groupManagementInfo.setIsKickOut("1");
                            data1.add(0, groupManagementInfo);
                            adapter1.notifyItemInserted(0);
                            data2.remove(item);
                            notifyItemRemoved(helper.getAdapterPosition());
                        }, MuteManageActivity.this::handleApiError));
            }
        };


        View headerView = getLayoutInflater().inflate(R.layout.rc_mutemanage_header, null);
        adapter2.addHeaderView(headerView);
        recyclerView2.setAdapter(adapter2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2.setNestedScrollingEnabled(false);

        adapter2.setLoadMoreView(new NewsLoadMoreView());
        adapter2.setEnableLoadMore(true);
        adapter2.setOnLoadMoreListener(() -> {
            if (searchKey.equals("")) {
                getDumbManagers("");
            } else {
                getDumbManagers(searchKey);
            }
        }, recyclerView2);


        recyclerView1 = headerView.findViewById(R.id.rc1);
        tvListMute = headerView.findViewById(R.id.tv_list_mute);
        adapter1 = new BaseQuickAdapter<GroupManagementInfoBean, BaseViewHolder>(R.layout.item_members_of_the_banned) {
            @Override
            protected void convert(BaseViewHolder helper, GroupManagementInfoBean item) {
                GroupManagementInfoBean groupManagementInfo = item;
                helper.setText(R.id.tv_name, item.getNick());
                GlideUtil.loadCircleImg(helper.getView(R.id.iv_head), item.getHeadPortrait());

                TextView mute = helper.getView(R.id.tv_mute);
                TextView blacklist = helper.getView(R.id.tv_blacklist);

                if (item.getIsKickOut().equals("1")) {
                    blacklist.setText("移出黑名单");
                    blacklist.setBackground(getResources().getDrawable(R.drawable.shape_kick_che, null));
                    blacklist.setTextColor(getResources().getColor(R.color.white, null));
                    mute.setVisibility(View.INVISIBLE);
                } else {
                    blacklist.setText("加入黑名单");
                    blacklist.setTextColor(getResources().getColor(R.color.color5c, null));
                    blacklist.setBackground(getResources().getDrawable(R.drawable.shape_kick_nor, null));
                }

                if (item.getIsBanned().equals("1")) {
                    if (item.getIsKickOut().equals("1")) {
                        mute.setVisibility(View.INVISIBLE);
                    } else {
                        mute.setVisibility(View.VISIBLE);
                        mute.setText("解除禁言");
                        mute.setBackground(getResources().getDrawable(R.drawable.shape_mute_che, null));
                        mute.setTextColor(getResources().getColor(R.color.white, null));
                    }
                } else {
                    mute.setVisibility(View.INVISIBLE);
                }

                mute.setOnClickListener(v -> api.muteMembers(groupId, item.getCustomerId(), "remove")
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(MuteManageActivity.this)))
                        .compose(RxSchedulers.normalTrans())
                        .subscribe(c -> {

                            groupManagementInfo.setIsBanned("0");
                            groupManagementInfo.setIsKickOut("0");
                            data2.add(0, groupManagementInfo);
                            adapter2.notifyItemInserted(0);
                            data1.remove(item);
                            notifyItemRemoved(helper.getAdapterPosition());
                            if (data1.size() == 0) {
                                tvListMute.setVisibility(View.GONE);
                            }
                        }, MuteManageActivity.this::handleApiError));

                blacklist.setOnClickListener(v -> {
                    if (item.getIsKickOut().equals("0")) {
                        api.blacklistOperation(groupId, item.getCustomerId(), "kickOut")
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(MuteManageActivity.this)))
                                .compose(RxSchedulers.normalTrans())
                                .subscribe(c -> {
                                    blacklist.setText("移出黑名单");
                                    blacklist.setBackground(getResources().getDrawable(R.drawable.shape_kick_che, null));
                                    blacklist.setTextColor(getResources().getColor(R.color.white, null));
                                    mute.setVisibility(View.INVISIBLE);
                                    item.setIsKickOut("1");
                                }, MuteManageActivity.this::handleApiError);
                    } else {
                        if (item.getIsBanned().equals("1")) {
                            api.blacklistOperation(groupId, item.getCustomerId(), "remove")
                                    .compose(bindToLifecycle())
                                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(MuteManageActivity.this)))
                                    .compose(RxSchedulers.normalTrans())
                                    .subscribe(c -> {
                                        groupManagementInfo.setIsKickOut("0");
                                        blacklist.setText("加入黑名单");
                                        blacklist.setTextColor(getResources().getColor(R.color.color5c, null));
                                        blacklist.setBackground(getResources().getDrawable(R.drawable.shape_kick_nor, null));
                                        mute.setVisibility(View.VISIBLE);
                                        mute.setText("解除禁言");
                                        mute.setBackground(getResources().getDrawable(R.drawable.shape_mute_che, null));
                                        mute.setTextColor(getResources().getColor(R.color.white, null));
                                    }, MuteManageActivity.this::handleApiError);

                        } else {
                            api.blacklistOperation(groupId, item.getCustomerId(), "remove")
                                    .compose(bindToLifecycle())
                                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(MuteManageActivity.this)))
                                    .compose(RxSchedulers.normalTrans())
                                    .subscribe(c -> {
                                        groupManagementInfo.setIsBanned("0");
                                        groupManagementInfo.setIsKickOut("0");
                                        data2.add(0, groupManagementInfo);
                                        adapter2.notifyItemInserted(0);
                                        data1.remove(item);
                                        notifyItemRemoved(helper.getAdapterPosition());
                                        if (data1.size() == 0) {
                                            tvListMute.setVisibility(View.GONE);
                                        }
                                    }, MuteManageActivity.this::handleApiError);

                        }
                    }
                });
            }
        };

        recyclerView1.setAdapter(adapter1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout.setRefreshing(true);
        getDumbManagers("");
    }


    private void getDumbManagers(String searchKey) {
        api.getDumbManagers(groupId, String.valueOf(page), String.valueOf(numsPerPage), searchKey)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .doOnTerminate(() -> (refreshLayout).setRefreshing(false))
                .subscribe(list -> {

                    page += 1;

                    if (page == 1) {
                        data1.clear();
                        data2.clear();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getIsBanned().equals("1") || list.get(i).getIsKickOut().equals("1")) {
                                data1.add(list.get(i));
                            } else {
                                data2.add(list.get(i));
                            }
                        }
                        adapter1.setNewData(data1);
                        adapter2.setNewData(data2);
                        adapter2.disableLoadMoreIfNotFullPage();
                    } else {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getIsBanned().equals("1") || list.get(i).getIsKickOut().equals("1")) {
                                data1.add(list.get(i));
                            } else {
                                data2.add(list.get(i));
                            }
                        }

                        if (list.size() >= numsPerPage) {
                            adapter1.loadMoreComplete();
                            adapter2.loadMoreComplete();
                        } else {
                            adapter1.loadMoreEnd(false);
                            adapter2.loadMoreEnd(false);
                        }
                    }


                    if (data1.size() != 0) {
                        tvListMute.setVisibility(View.VISIBLE);
                    }
                });
    }
}