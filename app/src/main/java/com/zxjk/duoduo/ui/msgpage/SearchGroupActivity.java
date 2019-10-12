package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetPublicGroupResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.InformationNotificationMessage;

public class SearchGroupActivity extends BaseActivity {

    private boolean hasSearch = false;
    private LinearLayout llTop;
    private RecyclerView recycler;
    private EditText etSearch;
    private BaseQuickAdapter adapter;
    private Api api;

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
                String str = etSearch.getText().toString().trim();
                if (TextUtils.isEmpty(str)) {
                    ToastUtils.showShort(R.string.input_empty);
                    return false;
                }
                api.getPublicGroupList(str)
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(data -> {
                            if (!hasSearch) recycler.setAdapter(adapter);
                            adapter.setNewData(data);
                        }, this::handleApiError);
                return true;
            }
            return false;
        });

        float itemHeight = (ScreenUtils.getScreenWidth() - CommonUtils.dip2px(this, 36)) / 2f;

        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new BaseQuickAdapter<GetPublicGroupResponse, BaseViewHolder>(R.layout.item_publicgroup) {
            @Override
            protected void convert(BaseViewHolder helper, GetPublicGroupResponse item) {
                helper.setText(R.id.tvGroupName, item.getGroupNikeName())
                        .setText(R.id.tvGroupOnwerName, item.getGroupOwnerNike())
                        .setText(R.id.tvCount, " (" + item.getGroupMemberCount() + "人) ");

                FrameLayout fl = helper.getView(R.id.fl);
                ViewGroup.LayoutParams layoutParams = fl.getLayoutParams();
                layoutParams.height = (int) itemHeight;
                fl.setLayoutParams(layoutParams);

                GlideUtil.loadNormalImg(helper.getView(R.id.ivHead), item.getHeadPortrait().split(",")[0]);

                helper.setVisible(R.id.ivPay, item.getIsOpen().equals("1"));

                Button btnJoin = helper.getView(R.id.btnJoin);
                if (item.getIsNotInGroup().equals("1")) {
                    btnJoin.setEnabled(true);
                    btnJoin.setText(R.string.join);
                    btnJoin.setBackground(ContextCompat.getDrawable(SearchGroupActivity.this, R.drawable.shape_theme2));
                    btnJoin.setOnClickListener(v -> {
                        if (item.getIsOpen().equals("1")) {
                            Intent intent = new Intent(SearchGroupActivity.this, PayEnterGroupPayActivity.class);
                            intent.putExtra("groupId", item.getId());
                            startActivity(intent);
                            finish();
                        } else {
                            ServiceFactory.getInstance().getBaseService(Api.class)
                                    .enterGroup(item.getId(), item.getGroupOwnerId(), Constant.userId)
                                    .compose(bindToLifecycle())
                                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(SearchGroupActivity.this)))
                                    .compose(RxSchedulers.normalTrans())
                                    .subscribe(s -> {
                                        //发送进群灰条
                                        InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain("\"" +
                                                Constant.currentUser.getNick() + "\"加入了群组");
                                        Message message = Message.obtain(item.getId(), Conversation.ConversationType.GROUP, notificationMessage);
                                        RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
                                        finish();
                                        RongIM.getInstance().startGroupChat(SearchGroupActivity.this, item.getId(), item.getGroupNikeName());
                                    }, SearchGroupActivity.this::handleApiError);
                        }
                    });
                } else {
                    btnJoin.setEnabled(false);
                    btnJoin.setText(R.string.joined);
                    btnJoin.setBackground(ContextCompat.getDrawable(SearchGroupActivity.this, R.drawable.shape_ceced0_cor40));
                }
            }
        };

        adapter.setEmptyView(LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, llTop, false));

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

}
