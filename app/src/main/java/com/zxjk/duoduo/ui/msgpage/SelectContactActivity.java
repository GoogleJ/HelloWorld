package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.adapter.SelectContactAdapter;
import com.zxjk.duoduo.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressLint("CheckResult")
public class SelectContactActivity extends BaseActivity implements TextWatcher {

    private RecyclerView recyclerView;
    private SelectContactAdapter mAdapter;
    private TextView tvTitle;
    private List<FriendInfoResponse> data = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        initView();

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.select_contact));
        recyclerView = findViewById(R.id.all_members_recycler_view);
        EditText searchEdit = findViewById(R.id.search_select_contact);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        mAdapter = new SelectContactAdapter();
        recyclerView.setAdapter(mAdapter);

        getFriendListById();

        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            Intent intent = new Intent();
            intent.putExtra("walletAddress", data.get(position).getWalletAddress());
            setResult(3, intent);
            finish();
        });
        searchEdit.addTextChangedListener(this);
    }

    /**
     * 获取好友列表
     */
    private void getFriendListById() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getFriendListById()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(friendInfoResponses -> {
                    data = friendInfoResponses;
                    mAdapter.setNewData(friendInfoResponses);
                }, this::handleApiError);
    }

    private List<FriendInfoResponse> search(String str) {
        List<FriendInfoResponse> filterList = new ArrayList<>();// 过滤后的list
        if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
            String simpleStr = str.replaceAll("\\-|\\s", "");
            for (FriendInfoResponse f : data) {
                if (f.getId().contains(simpleStr) || f.getRemark().contains(simpleStr) || f.getMobile().contains(simpleStr)
                        || f.getNick().contains(simpleStr) || f.getDuoduoId().contains(simpleStr)) {
                    if (!filterList.contains(f)) {
                        filterList.add(f);
                    }
                }
            }
        } else {
            for (FriendInfoResponse f : data) {
                String s = str.toLowerCase(Locale.CHINESE);
                boolean isNameContains = f.getId().toLowerCase(Locale.CHINESE)
                        .contains(s) || f.getRemark().toLowerCase(Locale.CHINESE)
                        .contains(s) || f.getMobile().toLowerCase(Locale.CHINESE)
                        .contains(s) || f.getNick().toLowerCase(Locale.CHINESE)
                        .contains(s) || f.getDuoduoId().toLowerCase(Locale.CHINESE)
                        .contains(s);
                if (isNameContains) {
                    if (!filterList.contains(f)) {
                        filterList.add(f);
                    }
                }
            }
        }
        return filterList;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() != 0) {
            mAdapter.setNewData(search(s.toString()));
        } else {
            mAdapter.setNewData(data);
        }
    }
}
