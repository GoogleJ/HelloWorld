package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.adapter.GlobalSearchAdapter;
import com.zxjk.duoduo.ui.widget.NewsLoadMoreView;
import com.zxjk.duoduo.utils.CommonUtils;

@SuppressLint("CheckResult")
public class GlobalSearchActivity extends BaseActivity {
    GlobalSearchAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private EditText searchEdit;
    private ImageView imgSearchDelete;

    private int currentPage = -1;
    private String pageSize = "12";
    private String keyWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_search);
        keyWord = getIntent().getStringExtra("searchText");
        searchEdit = findViewById(R.id.m_search_edit);
        mRecyclerView = findViewById(R.id.recycler_view);
        imgSearchDelete = findViewById(R.id.img_search_delete);

        initData();
        initUI();
        if (!TextUtils.isEmpty(keyWord)) {
            searchEdit.setText(keyWord);
            searchCustomerInfo(keyWord, true);
        } else {
            searchEdit.requestFocus();
        }
    }

    private void initData() {
        searchEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                keyWord = searchEdit.getText().toString();
                searchCustomerInfo(keyWord, true);
                return true;
            }
            return false;
        });
    }

    private void initUI() {
        View emptyView = getLayoutInflater().inflate(R.layout.view_app_null_type, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ImageView app_type = emptyView.findViewById(R.id.app_type);
        TextView app_prompt_text = emptyView.findViewById(R.id.app_prompt_text);
        app_type.setImageResource(R.drawable.ic_empty_nosearch);
        app_prompt_text.setText(getString(R.string.no_search));
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new GlobalSearchAdapter(this);
        mAdapter.setEmptyView(emptyView);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            FriendInfoResponse user = mAdapter.getData().get(position);
            CommonUtils.resolveFriendList(GlobalSearchActivity.this, user.getId());
        });
        mAdapter.setEnableLoadMore(true);
        mAdapter.setLoadMoreView(new NewsLoadMoreView());
        mAdapter.setOnLoadMoreListener(() -> searchCustomerInfo(keyWord, false), mRecyclerView);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.contact_search);

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    imgSearchDelete.setVisibility(View.GONE);
                } else {
                    imgSearchDelete.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //模糊搜索好友
    public void searchCustomerInfo(String data, boolean isSearch) {
        if (TextUtils.isEmpty(data) ) {
            ToastUtils.showShort(R.string.input_empty);
            return;
        }
        if (isSearch) currentPage = -1;
        KeyboardUtils.hideSoftInput(GlobalSearchActivity.this);
        ServiceFactory.getInstance().getBaseService(Api.class)
                .searchCustomerInfo(data, currentPage + 1 + "", pageSize)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(isSearch ? CommonUtils.initDialog(GlobalSearchActivity.this, getString(R.string.searching)) : null))
                .compose(RxSchedulers.normalTrans())
                .subscribe(list -> {
                    currentPage += 1;
                    if (isSearch) {
                        mAdapter.setNewData(list);
                        mAdapter.disableLoadMoreIfNotFullPage();
                    } else {
                        mAdapter.addData(list);
                        if (list.size() < Integer.parseInt(pageSize)) {
                            mAdapter.loadMoreEnd();
                        } else {
                            mAdapter.loadMoreComplete();
                        }
                    }
                }, t -> {
                    handleApiError(t);
                    mAdapter.loadMoreFail();
                });
    }

    public void cancel(View view) {
        searchEdit.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchEdit, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
