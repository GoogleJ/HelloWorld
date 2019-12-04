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
import com.zxjk.duoduo.bean.response.SearchCommunityBean;
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
    private String str;
    private SpannableString builder;

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
                str = etSearch.getText().toString().trim();
                if (TextUtils.isEmpty(str)) {
                    ToastUtils.showShort(R.string.input_empty);
                    return false;
                }
                KeyboardUtils.hideSoftInput(SearchGroupActivity.this);
                api.searchCommunity(str)
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(b -> {
                            if (!hasSearch) recycler.setAdapter(adapter);
//                            adapter.setNewData(Collections.singletonList(b));
                            adapter.setNewData(b);
                        }, this::handleApiError);
                return true;
            }
            return false;
        });

        float itemHeight = (ScreenUtils.getScreenWidth() - CommonUtils.dip2px(this, 36)) / 2f;

        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new BaseQuickAdapter<SearchCommunityBean, BaseViewHolder>(R.layout.item_publicgroup) {
            @Override
            protected void convert(BaseViewHolder helper, SearchCommunityBean item) {
                if (item.getCommunityName().contains(str)){
                    helper.setText(R.id.tvGroupName, matcherSearchText(Color.parseColor("#4486ff"), item.getCommunityName(), str))
                            .setText(R.id.tvGroupOnwerName, item.getOwnerNick())
                            .setText(R.id.tvCount, " (" + item.getMembers() + "人) ");
                } else if (item.getCode().contains(str)){
                    helper.setText(R.id.tvGroupName, item.getCommunityName())
                            .setText(R.id.tvGroupOnwerName, "社群号:" + item.getCode())
                            .setTextColor(R.id.tvGroupOnwerName, Color.parseColor("#4486ff"))
                            .setText(R.id.tvCount, " (" + item.getMembers() + "人) ");
                } else if (item.getOwnerNick().contains(str)){
                    helper.setText(R.id.tvGroupName, item.getCommunityName())
                            .setText(R.id.tvGroupOnwerName, matcherSearchText(Color.parseColor("#4486ff"), item.getOwnerNick(), str))
                            .setText(R.id.tvCount, " (" + item.getMembers() + "人) ");
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
                });
            }
        };

        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, llTop, false);
        ImageView iv = emptyView.findViewById(R.id.iv);
        TextView tv = emptyView.findViewById(R.id.tv);
        iv.setImageResource(R.drawable.ic_empty_nosearch);
        tv.setText(R.string.no_search);
        adapter.setEmptyView(emptyView);

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
