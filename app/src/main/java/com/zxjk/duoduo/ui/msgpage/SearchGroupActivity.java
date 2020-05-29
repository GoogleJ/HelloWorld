package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetRecommendCommunity;
import com.zxjk.duoduo.bean.response.SearchCommunityResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.socialspace.SocialHomeActivity;
import com.zxjk.duoduo.utils.ClickUtils;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

public class SearchGroupActivity extends BaseActivity {
    private boolean hasSearch = false;
    private LinearLayout llTop;
    private RecyclerView recycler;
    private EditText etSearch;
    private BaseQuickAdapter adapter;
    private Api api;
    private LinearLayout ll1;

    private String searchWord;
    private int currentPage = 1;
    private int pageOffset = 10;

    private BaseQuickAdapter adapter2;
    private RecyclerView recycler2;
    private int currPage = 1;
    private boolean isSet = false;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);

        api = ServiceFactory.getInstance().getBaseService(Api.class);

        llTop = findViewById(R.id.llTop);
        etSearch = findViewById(R.id.etSearch);
        recycler = findViewById(R.id.recycler);
        recycler2 = findViewById(R.id.recycler2);
        ll1 = findViewById(R.id.ll1);
        findViewById(R.id.tv_refresh_recommend).setOnClickListener(v -> {
            if (!ClickUtils.isFastDoubleClick(R.id.tv_refresh_recommend)) {
                recommendCommunity();
            }
        });

        findViewById(R.id.rl_back).setOnClickListener(v -> {
            finish();
        });

        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("找社群");

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler2.setLayoutManager(new LinearLayoutManager(this));

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchWord = etSearch.getText().toString().trim();
                if (TextUtils.isEmpty(searchWord)) {
                    ToastUtils.showShort(R.string.input_empty);
                    return false;
                }
                currentPage = 1;
                KeyboardUtils.hideSoftInput(SearchGroupActivity.this);
                api.searchCommunity(searchWord, currentPage, pageOffset)
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(b -> {
                            if (!hasSearch) recycler.setAdapter(adapter);
                            ll1.setVisibility(View.GONE);
                            recycler2.setVisibility(View.GONE);
                            recycler.setVisibility(View.VISIBLE);
                            adapter.setNewData(b.getList());
                            adapter.disableLoadMoreIfNotFullPage();
                        }, this::handleApiError);
                return true;
            }
            return false;
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    findViewById(R.id.img_search_delete).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.img_search_delete).setVisibility(View.VISIBLE);
                }
            }
        });

        recommendCommunity();

        searchCommunity();

//        etSearch.requestFocus();
    }

    private void searchCommunity() {
        adapter = new BaseQuickAdapter<SearchCommunityResponse.ListBean, BaseViewHolder>(R.layout.item_publicgroup) {
            @Override
            protected void convert(BaseViewHolder helper, SearchCommunityResponse.ListBean item) {
                helper.setText(R.id.group_nike_name, item.getCommunityName())
                        .setText(R.id.group_sign, item.getIntroduction())
                        .setText(R.id.group_owner_name, "创建人: " + item.getOwnerNick())
                        .setText(R.id.tv_number_of_people, item.getMembers());
                GlideUtil.loadCornerImg(helper.getView(R.id.group_head_portrait), item.getCommunityLogo(), 6);
            }
        };

        adapter.setOnItemClickListener((adapter, view, position) -> {
            SearchCommunityResponse.ListBean listBean = (SearchCommunityResponse.ListBean) adapter.getData().get(position);
            Intent intent = new Intent(SearchGroupActivity.this, SocialHomeActivity.class);
            intent.putExtra("id", listBean.getGroupId());
            startActivity(intent);
        });

        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, llTop, false);
        ImageView iv = emptyView.findViewById(R.id.iv);
        TextView tv = emptyView.findViewById(R.id.tv);
        iv.setImageResource(R.drawable.ic_empty_nosearch);
        tv.setText(R.string.empty_nosearch);
        adapter.setEmptyView(emptyView);

        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(
                () -> api.searchCommunity(searchWord, currentPage + 1, pageOffset)
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver())
                        .subscribe(b -> {
                            currentPage += 1;
                            adapter.addData(b.getList());
                            if (!b.isHasNextPage()) {
                                adapter.loadMoreEnd();
                            } else {
                                adapter.loadMoreComplete();
                            }
                        }, t -> {
                            handleApiError(t);
                            adapter.loadMoreFail();
                        })
                , recycler);
    }

    public void cancel(View view) {
        recycler.setVisibility(View.GONE);
        recycler2.setVisibility(View.VISIBLE);
        ll1.setVisibility(View.VISIBLE);
        currPage = 1;
        recommendCommunity();
        etSearch.setText("");
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @SuppressLint("CheckResult")
    private void recommendCommunity() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .recommendCommunity(String.valueOf(currPage))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    currPage += 1;
                    if (currPage == 4) {
                        currPage = 1;
                    }

                    if (isSet) {
                        adapter2.addData(s);
                    } else {
                        isSet = true;
                        adapter2.setNewData(s);
                    }

                }, this::handleApiError);

        adapter2 = new BaseQuickAdapter<GetRecommendCommunity, BaseViewHolder>(R.layout.item_publicgroup) {
            @Override
            protected void convert(BaseViewHolder helper, GetRecommendCommunity item) {
                helper.setText(R.id.group_nike_name, item.getGroupNickName())
                        .setText(R.id.group_sign, item.getIntroduction())
                        .setText(R.id.group_owner_name, "创建人: " + item.getNick())
                        .setText(R.id.tv_number_of_people, item.getMembersSize());
                GlideUtil.loadNormalImg(helper.getView(R.id.group_head_portrait), item.getLogo());
            }
        };

        adapter2.setOnItemClickListener((adapter, view, position) -> {
            GetRecommendCommunity listBean = (GetRecommendCommunity) adapter.getData().get(position);
            Intent intent = new Intent(SearchGroupActivity.this, SocialHomeActivity.class);
            intent.putExtra("id", listBean.getGroupId());
            startActivity(intent);
        });

        recycler2.setAdapter(adapter2);
    }

}