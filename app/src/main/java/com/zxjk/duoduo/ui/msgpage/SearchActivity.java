package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.adapter.SearchAdapter;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("CheckResult")
public class SearchActivity extends BaseActivity {
    @BindView(R.id.m_search_edit)
    EditText searchEdit;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    SearchAdapter mAdapter;
    Intent intent;
    View emptyView;
    private boolean isFirst = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.search));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        emptyView = getLayoutInflater().inflate(R.layout.view_app_null_type, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ImageView app_type = emptyView.findViewById(R.id.app_type);
        TextView app_prompt_text = emptyView.findViewById(R.id.app_prompt_text);
        app_type.setImageResource(R.drawable.ic_empty_nosearch);
        app_prompt_text.setText(getString(R.string.no_search));
        initData();
        initUI();
    }

    private void initData() {
        searchEdit.setOnEditorActionListener((v, actionId, event) -> {
            //搜索按键action
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!TextUtils.isEmpty(searchEdit.getText().toString())) {
                    getFriendListInfoById(searchEdit.getText().toString());
                } else {
                    ToastUtils.showShort(getString(R.string.input_search_edit));
                }
                return true;
            }
            return false;
        });
    }

    private void initUI() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new SearchAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            FriendInfoResponse friendInfoResponse = mAdapter.getData().get(position);
            Intent intent = new Intent(SearchActivity.this, FriendDetailsActivity.class);
            intent.putExtra("friendResponse", friendInfoResponse);
            startActivity(intent);
        });
        mAdapter.setNewData(new ArrayList<>());
    }

    public void getFriendListInfoById(String f) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getFriendListById()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .subscribe(friendInfoResponses -> {
                    if (isFirst) {
                        mAdapter.setEmptyView(emptyView);
                    }
                    mAdapter.setNewData(new ArrayList<>());
                    for (int i = 0; i < friendInfoResponses.size(); i++) {
                        if (friendInfoResponses.get(i).getId().contains(f)
                                || friendInfoResponses.get(i).getRemark().contains(f)
                                || friendInfoResponses.get(i).getMobile().contains(f)
                                || friendInfoResponses.get(i).getNick().contains(f)
                                || friendInfoResponses.get(i).getRealname().contains(f)) {
                            mAdapter.addData(friendInfoResponses.get(i));
                        }
                    }
                    if (mAdapter.getData().size() == 0) {
                        ToastUtils.showShort("查无此人");
                    }
                }, this::handleApiError);
    }
}
