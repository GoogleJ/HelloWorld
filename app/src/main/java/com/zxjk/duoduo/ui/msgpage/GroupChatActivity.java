package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GroupChatResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.adapter.GroupChatAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;

public class GroupChatActivity extends BaseActivity implements TextWatcher {
    @BindView(R.id.m_group_chat_edit_1)
    EditText mGroupChatEdit1;
    @BindView(R.id.m_group_chat_edit)
    LinearLayout mGroupChatEdit;
    @BindView(R.id.m_group_chat_recycler_view)
    RecyclerView mGroupChatRecyclerView;
    @BindView(R.id.tv_commit)
    TextView tv_commit;

    GroupChatAdapter groupChatAdapter;

    View emptyView;

    List<GroupChatResponse> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        ButterKnife.bind(this);

        tv_commit.setVisibility(View.VISIBLE);
        tv_commit.setTextColor(Color.parseColor("#333333"));
        tv_commit.setBackground(null);
        tv_commit.setText(R.string.createGroup);
        tv_commit.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateGroupActivity.class);
            intent.putExtra("eventType", 1);
            startActivity(intent);
        });

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.m_group_chat));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        emptyView = getLayoutInflater().inflate(R.layout.view_app_null_type, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        TextView app_prompt_text = emptyView.findViewById(R.id.app_prompt_text);
        app_prompt_text.setText(getString(R.string.no_qunzu));

        LinearLayoutManager manage = new LinearLayoutManager(this);
        mGroupChatRecyclerView.setLayoutManager(manage);
        groupChatAdapter = new GroupChatAdapter();
        mGroupChatEdit1.addTextChangedListener(GroupChatActivity.this);
        //从网络获取用户所有群组信息
        getMyGroupChat(Constant.userId);
        groupChatAdapter.setEmptyView(emptyView);
        mGroupChatRecyclerView.setAdapter(groupChatAdapter);

        groupChatAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            RongIM.getInstance().startGroupChat(this, groupChatAdapter.getData().get(position).getId(), groupChatAdapter.getData().get(position).getGroupNikeName());
            finish();
        });
    }

    @SuppressLint("CheckResult")
    private void getMyGroupChat(String userId) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getMygroupinformation(userId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    list = s;
                    groupChatAdapter.setNewData(list);
                }, this::handleApiError);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {

        String groupname = s.toString();
        if (groupname.length() > 0) {
            List<GroupChatResponse> groupnamelist = search(groupname); //查找对应的群组数据
            groupChatAdapter.setNewData(groupnamelist);
        } else {
            groupChatAdapter.setNewData(list);
        }
    }

    /**
     * 模糊查询
     *
     * @param str
     * @return
     */
    private List<GroupChatResponse> search(String str) {
        List<GroupChatResponse> filterList = new ArrayList<GroupChatResponse>();// 过滤后的list
        if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
            String simpleStr = str.replaceAll("\\-|\\s", "");
            for (GroupChatResponse contact : list) {
                if (contact.getGroupNikeName() != null) {
                    if (contact.getGroupNikeName().contains(simpleStr) || contact.getGroupNikeName().contains(str)) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        } else {
            for (GroupChatResponse contact : list) {
                if (contact.getGroupNikeName() != null) {
                    //姓名全匹配,姓名首字母简拼匹配,姓名全字母匹配
                    boolean isNameContains = contact.getGroupNikeName().toLowerCase(Locale.CHINESE)
                            .contains(str.toLowerCase(Locale.CHINESE));

                    if (isNameContains) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        }
        return filterList;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getMyGroupChat(Constant.userId);
    }
}
