package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.bean.response.GetRecommendCommunity;
import com.zxjk.duoduo.bean.response.SearchCommunityResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.socialspace.SocialHomeActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

public class AddressListSearchActivity extends BaseActivity {
    private RecyclerView recyclerViewFriend;
    private RecyclerView recyclerViewSocial;
    private EditText searchEdit;
    private LinearLayout llTop2;
    private ImageView imgSearchDelete;

    private BaseQuickAdapter friendAdapter;
    private BaseQuickAdapter socialAdapter;

    private int currentPage = 0;
    private String pageSize = "3";
    private String keyWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list_search);

        initView();

        initData();

        searchEdit.requestFocus();
        keyboardShow();

    }

    private void keyboardShow() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchEdit, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    private void initView() {
        recyclerViewFriend = findViewById(R.id.recycler_friend);
        recyclerViewSocial = findViewById(R.id.recycler_social);
        searchEdit = findViewById(R.id.m_search_edit);
        llTop2 = findViewById(R.id.llTop2);
        imgSearchDelete = findViewById(R.id.img_search_delete);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.contact_search);

        findViewById(R.id.tv_more_friend).setOnClickListener(v -> {
            Intent intent = new Intent(this, GlobalSearchActivity.class);
            intent.putExtra("searchText",searchEdit.getText().toString());
            startActivity(intent);
        });

        findViewById(R.id.tv_more_social).setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchGroupActivity.class);

            intent.putExtra("searchText",searchEdit.getText().toString());
            startActivity(intent);
        });


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

    private void initData() {

        searchEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (TextUtils.isEmpty(searchEdit.getText().toString().trim())) {
                    ToastUtils.showShort(R.string.input_empty);
                    return false;
                }
                findViewById(R.id.ll1).setVisibility(View.VISIBLE);
                keyWord = searchEdit.getText().toString();
                searchCustomerInfo(keyWord, true);
                searchCommunity(keyWord);
                return true;
            }
            return false;
        });
        friendAdapter = new BaseQuickAdapter<FriendInfoResponse, BaseViewHolder>(R.layout.item_search) {
            @Override
            protected void convert(BaseViewHolder helper, FriendInfoResponse item) {
                helper.setText(R.id.m_item_search_text, item.getNick())
                        .setText(R.id.m_item_search_dudu_id, getString(R.string.duoduo_id) + " " + item.getDuoduoId())
                        .addOnClickListener(R.id.m_item_search_layout);
                ImageView heardImage = helper.getView(R.id.m_item_search_icon);
                GlideUtil.loadCircleImg(heardImage, item.getHeadPortrait());
            }
        };
        friendAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            FriendInfoResponse user = (FriendInfoResponse) friendAdapter.getData().get(position);
            CommonUtils.resolveFriendList(this, user.getId());
        });

        recyclerViewFriend.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFriend.setAdapter(friendAdapter);

        socialAdapter = new BaseQuickAdapter<SearchCommunityResponse.ListBean, BaseViewHolder>(R.layout.item_publicgroup) {
            @Override
            protected void convert(BaseViewHolder helper, SearchCommunityResponse.ListBean item) {
                helper.setText(R.id.group_nike_name, item.getCommunityName())
                        .setText(R.id.group_sign, item.getIntroduction())
                        .setText(R.id.group_owner_name, getString(R.string.creater, item.getOwnerNick()))
                        .setText(R.id.tv_number_of_people, item.getMembers());
                GlideUtil.loadCornerImg(helper.getView(R.id.group_head_portrait), item.getCommunityLogo(), 6);
            }
        };

        socialAdapter.setOnItemClickListener((adapter, view, position) -> {
            GetRecommendCommunity listBean = (GetRecommendCommunity) adapter.getData().get(position);
            Intent intent = new Intent(this, SocialHomeActivity.class);
            intent.putExtra("id", listBean.getGroupId());
            startActivity(intent);
        });


        recyclerViewSocial.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSocial.setAdapter(socialAdapter);

    }

    @SuppressLint("CheckResult")
    private void searchCommunity(String data) {

        ServiceFactory.getInstance().getBaseService(Api.class)
                .searchCommunity(data, currentPage, Integer.valueOf(pageSize))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this, 0)))
                .subscribe(b -> {
                    if (b.getList().size() <= 0) {
                        View emptyView2 = LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, llTop2, false);
                        ImageView iv2 = emptyView2.findViewById(R.id.iv);
                        TextView tv2 = emptyView2.findViewById(R.id.tv);
                        iv2.setImageResource(R.drawable.ic_empty_nosearch);
                        tv2.setText(R.string.empty_nosearch);
                        socialAdapter.setEmptyView(emptyView2);
                    } else {
                        socialAdapter.setNewData(b.getList());
                    }

                }, this::handleApiError);
    }

    @SuppressLint("CheckResult")
    private void searchCustomerInfo(String data, boolean isSearch) {

        KeyboardUtils.hideSoftInput(this);
        ServiceFactory.getInstance().getBaseService(Api.class)
                .searchCustomerInfo(data, currentPage + "", pageSize)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(isSearch ? CommonUtils.initDialog(this, getString(R.string.searching)) : null))
                .compose(RxSchedulers.normalTrans())
                .subscribe(list -> {
                    if (list.size() <= 0) {
                        View emptyView = getLayoutInflater().inflate(R.layout.view_app_null_type, null);
                        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                        ImageView app_type = emptyView.findViewById(R.id.app_type);
                        TextView app_prompt_text = emptyView.findViewById(R.id.app_prompt_text);
                        app_type.setImageResource(R.drawable.ic_empty_nosearch);
                        app_prompt_text.setText(getString(R.string.no_search));
                        friendAdapter.setEmptyView(emptyView);
                    }
                    friendAdapter.setNewData(list);
                });
    }

    public void cancel(View view) {
        searchEdit.setText("");
        keyboardShow();
    }
}
