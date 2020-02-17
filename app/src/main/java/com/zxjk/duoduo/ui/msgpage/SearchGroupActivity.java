package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.SearchCommunityResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.socialspace.SocialHomeActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchGroupActivity extends BaseActivity {

    private boolean hasSearch = false;
    private LinearLayout llTop;
    private RecyclerView recycler;
    private EditText etSearch;
    private BaseQuickAdapter adapter;
    private Api api;
    private SpannableString builder;

    private String searchWord;
    private int currentPage = 1;
    private int pageOffset = 10;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);

        api = ServiceFactory.getInstance().getBaseService(Api.class);

        llTop = findViewById(R.id.llTop);
        etSearch = findViewById(R.id.etSearch);
        recycler = findViewById(R.id.recycler);

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
                            adapter.setNewData(b.getList());
                            adapter.disableLoadMoreIfNotFullPage();
                        }, this::handleApiError);
                return true;
            }
            return false;
        });

        float itemHeight = (ScreenUtils.getScreenWidth() - CommonUtils.dip2px(this, 36)) / 2f;

        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new BaseQuickAdapter<SearchCommunityResponse.ListBean, BaseViewHolder>(R.layout.item_publicgroup) {
            @Override
            protected void convert(BaseViewHolder helper, SearchCommunityResponse.ListBean item) {
                if (item.getCommunityName().contains(searchWord)) {
                    helper.setText(R.id.tvGroupName, matcherSearchText(Color.parseColor("#4486ff"), item.getCommunityName(), searchWord))
                            .setText(R.id.tvGroupOwnerName, item.getOwnerNick())
                            .setText(R.id.tvCount, item.getMembers());
                } else if (item.getCode().contains(searchWord)) {
                    helper.setText(R.id.tvGroupName, item.getCommunityName())
                            .setText(R.id.tvGroupOwnerName,  matcherSearchText(Color.parseColor("#4486ff"), getString(R.string.social_code) + item.getCode(), searchWord))
                            .setText(R.id.tvCount, item.getMembers());
                } else if (item.getOwnerNick().contains(searchWord)) {
                    helper.setText(R.id.tvGroupName, item.getCommunityName())
                            .setText(R.id.tvGroupOwnerName, matcherSearchText(Color.parseColor("#4486ff"), item.getOwnerNick(), searchWord))
                            .setText(R.id.tvCount, item.getMembers());
                }

                FrameLayout fl = helper.getView(R.id.fl);
                ViewGroup.LayoutParams layoutParams = fl.getLayoutParams();
                layoutParams.height = (int) itemHeight;
                fl.setLayoutParams(layoutParams);

                GlideUtil.loadNormalImg(helper.getView(R.id.ivHead), item.getCommunityLogo());

                helper.setVisible(R.id.ivPay, item.getIsPay().equals("1"));

                Button btnJoin = helper.getView(R.id.btnJoin);
                if (item.getInGroup().equals("0")) {
                    btnJoin.setText(R.string.join);
                } else {
                    btnJoin.setText(R.string.check);
                }

                btnJoin.setOnClickListener(v -> {
                    Intent intent = new Intent(SearchGroupActivity.this, SocialHomeActivity.class);
                    intent.putExtra("id", item.getGroupId());
                    startActivity(intent);
                    finish();
                });
            }
        };

        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, llTop, false);
        ImageView iv = emptyView.findViewById(R.id.iv);
        TextView tv = emptyView.findViewById(R.id.tv);
        iv.setImageResource(R.drawable.ic_empty_nosearch);
        tv.setText(R.string.no_search);
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

        etSearch.requestFocus();
    }

    public void cancel(View view) {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private SpannableString matcherSearchText(int color, String text, String keyword) {
        builder = new SpannableString(text);
        Pattern pattern = Pattern.compile(keyword);
        Matcher matcher = pattern.matcher(builder);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            builder.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }
}
