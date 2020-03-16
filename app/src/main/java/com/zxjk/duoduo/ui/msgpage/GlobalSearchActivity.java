package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("CheckResult")
public class GlobalSearchActivity extends BaseActivity {
    @BindView(R.id.m_search_edit)
    EditText searchEdit;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    GlobalSearchAdapter mAdapter;

    private int currentPage = -1;
    private String pageSize = "10";
    private String keyWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_search);
        ButterKnife.bind(this);
        initData();
        initUI();
        searchEdit.requestFocus();
    }

    private void initData() {
        searchEdit.setOnEditorActionListener((v, actionId, event) -> {
            //搜索按键action
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
    }

    //模糊搜索好友
    public void searchCustomerInfo(String data, boolean isSearch) {
        if (TextUtils.isEmpty(data)) {
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
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
