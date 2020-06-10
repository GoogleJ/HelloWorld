package com.zxjk.duoduo.ui.msgpage;

import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.reflect.TypeToken;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.CommunityListBean;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.socialspace.SocialHomeActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MMKVUtils;

import java.util.ArrayList;
import java.util.List;

public class MoreSearchActivity extends BaseActivity {
    private String searchText;
    private String type;
    private RecyclerView recyclerFriend;
    private RecyclerView recyclerSocial;
    private BaseQuickAdapter<CommunityListBean, BaseViewHolder> socialListAdapter;
    private BaseQuickAdapter<FriendInfoResponse, BaseViewHolder> friendListAdapter;
    private EditText searchEdit;
    private ImageView imgSearchDelete;
    private LinearLayout llFriend;
    private LinearLayout llSocial;


    private List<CommunityListBean> communityList = new ArrayList<>();
    private List<CommunityListBean> communityLists = new ArrayList<>();
    private List<FriendInfoResponse> friendInfoList = new ArrayList<>();
    private List<FriendInfoResponse> friendInfoLists = new ArrayList<>();

    private String keyWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_search);
        searchText = getIntent().getStringExtra("searchText");
        type = getIntent().getStringExtra("type");


        String CommunityListBean = MMKVUtils.getInstance().decodeString("CommunityListBean");
        if (!TextUtils.isEmpty(CommunityListBean)) {
            communityList = GsonUtils.fromJson(CommunityListBean, new TypeToken<List<CommunityListBean>>() {
            }.getType());
        }
        String FriendInfoResponse = MMKVUtils.getInstance().decodeString("FriendInfoResponse");
        if (!TextUtils.isEmpty(FriendInfoResponse)) {
            friendInfoList = GsonUtils.fromJson(FriendInfoResponse, new TypeToken<List<FriendInfoResponse>>() {
            }.getType());
        }

        initView();

        initData();

    }

    private void initView() {
        recyclerFriend = findViewById(R.id.recycler_friend);
        recyclerSocial = findViewById(R.id.recycler_social);
        searchEdit = findViewById(R.id.m_search_edit);
        imgSearchDelete = findViewById(R.id.img_search_delete);
        llFriend = findViewById(R.id.ll_friend);
        llSocial = findViewById(R.id.ll_social);

        searchEdit.requestFocus();

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


        searchEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (TextUtils.isEmpty(searchEdit.getText().toString().trim())) {
                    ToastUtils.showShort(R.string.input_empty);
                    return false;
                }
                keyWord = searchEdit.getText().toString();
                if (type.equals("0")) {
                    searchfriendList(keyWord);
                } else {
                    searchSocialList(keyWord);
                }

                return true;
            }
            return false;
        });

        if (type.equals("0")) {
            llFriend.setVisibility(View.VISIBLE);
        } else {
            llSocial.setVisibility(View.VISIBLE);
        }

    }

    private void searchfriendList(String keyWord) {
        friendInfoLists.clear();
        String FriendInfoResponse = MMKVUtils.getInstance().decodeString("FriendInfoResponse");
        if (!TextUtils.isEmpty(FriendInfoResponse)) {
            friendInfoList = GsonUtils.fromJson(FriendInfoResponse, new TypeToken<List<FriendInfoResponse>>() {
            }.getType());
        }
        for (int i = 0; i < friendInfoList.size(); i++) {
            if (!TextUtils.isEmpty(friendInfoList.get(i).getRemark())) {
                if (friendInfoList.get(i).getRemark().contains(keyWord)) {
                    friendInfoLists.add(friendInfoList.get(i));
                }
            } else {
                if (friendInfoList.get(i).getNick().contains(keyWord)) {
                    friendInfoLists.add(friendInfoList.get(i));
                }
            }
        }

        View emptyView = getLayoutInflater().inflate(R.layout.view_app_null_type, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ImageView app_type = emptyView.findViewById(R.id.app_type);
        TextView app_prompt_text = emptyView.findViewById(R.id.app_prompt_text);
        app_type.setImageResource(R.drawable.ic_empty_nosearch);
        app_prompt_text.setText(getString(R.string.no_search));
        friendListAdapter.setEmptyView(emptyView);
        friendListAdapter.setNewData(friendInfoLists);
    }

    private void searchSocialList(String keyWord) {
        communityLists.clear();
        String CommunityListBean = MMKVUtils.getInstance().decodeString("CommunityListBean");
        if (!TextUtils.isEmpty(CommunityListBean)) {
            communityList = GsonUtils.fromJson(CommunityListBean, new TypeToken<List<CommunityListBean>>() {
            }.getType());
        }
        for (int i = 0; i < communityList.size(); i++) {
            if (communityList.get(i).getCommunityName().contains(keyWord)) {
                communityLists.add(communityList.get(i));
            }
            if (communityLists.size() == 3) {
                break;
            }
        }
        View emptyView = getLayoutInflater().inflate(R.layout.view_app_null_type, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ImageView app_type = emptyView.findViewById(R.id.app_type);
        TextView app_prompt_text = emptyView.findViewById(R.id.app_prompt_text);
        app_type.setImageResource(R.drawable.ic_empty_nosearch);
        app_prompt_text.setText(R.string.empty_nosearch);
        socialListAdapter.setEmptyView(emptyView);
        socialListAdapter.setNewData(communityLists);
    }

    private void initData() {
        socialListAdapter = new BaseQuickAdapter<CommunityListBean, BaseViewHolder>(R.layout.item_publicgroup) {
            @Override
            protected void convert(BaseViewHolder helper, CommunityListBean item) {
                helper.setText(R.id.group_nike_name, item.getCommunityName())
                        .setText(R.id.group_sign, item.getIntroduction())
                        .setText(R.id.group_owner_name, getString(R.string.creater, item.getOwnerNick()))
                        .setText(R.id.tv_number_of_people, item.getMembers());
                GlideUtil.loadCornerImg(helper.getView(R.id.group_head_portrait), item.getCommunityLogo(), 6);
            }
        };

        socialListAdapter.setOnItemClickListener((adapter, view, position) -> {
            CommunityListBean listBean = (CommunityListBean) adapter.getData().get(position);
            Intent intent = new Intent(this, SocialHomeActivity.class);
            intent.putExtra("id", listBean.getGroupId());

            startActivity(intent);
        });

        recyclerSocial.setLayoutManager(new LinearLayoutManager(this));
        recyclerSocial.setAdapter(socialListAdapter);


        friendListAdapter = new BaseQuickAdapter<FriendInfoResponse, BaseViewHolder>(R.layout.item_search) {
            @Override
            protected void convert(BaseViewHolder helper, FriendInfoResponse item) {
                helper.setText(R.id.m_item_search_text, item.getNick())
                        .setText(R.id.m_item_search_dudu_id, getString(R.string.duoduo_id) + " " + item.getDuoduoId())
                        .addOnClickListener(R.id.m_item_search_layout);
                ImageView heardImage = helper.getView(R.id.m_item_search_icon);
                GlideUtil.loadCircleImg(heardImage, item.getHeadPortrait());
            }
        };

        friendListAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            FriendInfoResponse user = friendListAdapter.getData().get(position);
            CommonUtils.resolveFriendList(this, user.getId());
        });

        recyclerFriend.setLayoutManager(new LinearLayoutManager(this));
        recyclerFriend.setAdapter(friendListAdapter);

        if (!TextUtils.isEmpty(searchText)) {
            searchEdit.setText(searchText);
            if (type.equals("0")) {
                searchfriendList(searchText);
            } else {
                searchSocialList(searchText);
            }
        } else {
            keyboardShow();
        }
    }

    private void keyboardShow() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchEdit, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void cancel(View view) {
        searchEdit.setText("");
        keyboardShow();
    }
}
