package com.zxjk.duoduo.ui.msgpage;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
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
import com.zxjk.duoduo.utils.GlideUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchGroupActivity extends BaseActivity {
    private Api api;

    private String searchWord;
    private int currentPage = 1;
    private int pageOffset = 10;
    private int currPage = 1;

    private LinearLayout llTop;
    private EditText etSearch;
    private ImageView ivRefresh;
    private ImageView imgDelete;
    private LinearLayout refreshRecommandLL;
    private RecyclerView recycler;

    private BaseQuickAdapter searchAdapter;
    private BaseQuickAdapter recommandAdapter;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);

        api = ServiceFactory.getInstance().getBaseService(Api.class);
        searchWord = getIntent().getStringExtra("searchText");

        initView();

        searchLogic();

        initSearchAdapter();

        initRecommandAdapter();

        recommandSocial();

        if (!TextUtils.isEmpty(searchWord)) {
            etSearch.setText(searchWord);
            currentPage = 1;
            KeyboardUtils.hideSoftInput(SearchGroupActivity.this);
            api.searchCommunity(searchWord, currentPage, pageOffset)
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver())
                    .subscribe(b -> {
                        refreshRecommandLL.setVisibility(View.GONE);
                        recycler.setAdapter(searchAdapter);
                        searchAdapter.replaceData(b.getList());
                        searchAdapter.disableLoadMoreIfNotFullPage();
                    }, this::handleApiError);
        }
//        etSearch.requestFocus();
    }

    private void initRecommandAdapter() {
        recommandAdapter = new BaseQuickAdapter<GetRecommendCommunity, BaseViewHolder>(R.layout.item_publicgroup) {
            @Override
            protected void convert(BaseViewHolder helper, GetRecommendCommunity item) {
                helper.setText(R.id.group_nike_name, item.getGroupNickName())
                        .setText(R.id.group_sign, item.getIntroduction())
                        .setText(R.id.group_owner_name, getString(R.string.creater, item.getNick()))
                        .setText(R.id.tv_number_of_people, item.getMembersSize());
                GlideUtil.loadCornerImg(helper.getView(R.id.group_head_portrait), item.getLogo(), 6);
            }
        };

        recommandAdapter.setOnItemClickListener((adapter, view, position) -> {
            GetRecommendCommunity listBean = (GetRecommendCommunity) adapter.getData().get(position);
            Intent intent = new Intent(SearchGroupActivity.this, SocialHomeActivity.class);
            intent.putExtra("id", listBean.getGroupId());
            startActivity(intent);
        });

        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, llTop, false);
        ImageView iv = emptyView.findViewById(R.id.iv);
        TextView tv = emptyView.findViewById(R.id.tv);
        iv.setImageResource(R.drawable.ic_empty_nosearch);
        tv.setText(R.string.empty_nosearch6);
        recommandAdapter.setEmptyView(emptyView);

        recycler.setAdapter(recommandAdapter);
    }

    @SuppressLint("CheckResult")
    private void searchLogic() {
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
                        .compose(RxSchedulers.ioObserver())
                        .subscribe(b -> {
                            refreshRecommandLL.setVisibility(View.GONE);
                            recycler.setAdapter(searchAdapter);
                            searchAdapter.replaceData(b.getList());
                            searchAdapter.disableLoadMoreIfNotFullPage();
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
                    imgDelete.setVisibility(View.GONE);
                } else {
                    imgDelete.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initView() {
        llTop = findViewById(R.id.llTop);
        ivRefresh = findViewById(R.id.ivRefresh);
        imgDelete = findViewById(R.id.img_search_delete);
        etSearch = findViewById(R.id.etSearch);
        recycler = findViewById(R.id.recycler);
        refreshRecommandLL = findViewById(R.id.ll1);

        findViewById(R.id.tv_refresh_recommend).setOnClickListener(v -> {
            if (!ClickUtils.isFastDoubleClick(R.id.tv_refresh_recommend)) {
                recommandSocial();
                ObjectAnimator.ofFloat(ivRefresh, "rotation", 0, 365).start();
            }
        });

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.find_social);

        recycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initSearchAdapter() {
        searchAdapter = new BaseQuickAdapter<SearchCommunityResponse.ListBean, BaseViewHolder>(R.layout.item_publicgroup) {
            @Override
            protected void convert(BaseViewHolder helper, SearchCommunityResponse.ListBean item) {
                helper.setText(R.id.group_nike_name, item.getCommunityName())
                        .setText(R.id.group_sign, item.getIntroduction())
                        .setText(R.id.group_owner_name, getString(R.string.creater, item.getOwnerNick()))
                        .setText(R.id.tv_number_of_people, item.getMembers());

                if (item.getCommunityName().contains(searchWord)) {
                    helper.setText(R.id.group_nike_name, matcherSearchText(ContextCompat.getColor(SearchGroupActivity.this, R.color.colorTheme),
                            item.getCommunityName(), searchWord));
                }

                GlideUtil.loadCornerImg(helper.getView(R.id.group_head_portrait), item.getCommunityLogo(), 6);
            }
        };

        searchAdapter.setOnItemClickListener((adapter, view, position) -> {
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
        searchAdapter.setEmptyView(emptyView);

        searchAdapter.setEnableLoadMore(true);
        searchAdapter.setOnLoadMoreListener(
                () -> api.searchCommunity(searchWord, currentPage + 1, pageOffset)
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver())
                        .subscribe(b -> {
                            currentPage += 1;
                            searchAdapter.addData(b.getList());
                            if (!b.isHasNextPage()) {
                                searchAdapter.loadMoreEnd();
                            } else {
                                searchAdapter.loadMoreComplete();
                            }
                        }, t -> {
                            handleApiError(t);
                            searchAdapter.loadMoreFail();
                        })
                , recycler);
    }

    public void cleanSearch(View view) {
        recycler.setAdapter(recommandAdapter);
        refreshRecommandLL.setVisibility(View.VISIBLE);
        currPage = 1;
        recommandSocial();
        etSearch.setText("");
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @SuppressLint("CheckResult")
    private void recommandSocial() {
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
                    recommandAdapter.replaceData(s);
                }, this::handleApiError);
    }

    private SpannableString matcherSearchText(int color, String text, String keyword) {
        SpannableString ss = new SpannableString(text);
        Pattern pattern = Pattern.compile(keyword);
        Matcher matcher = pattern.matcher(ss);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            ss.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }

}