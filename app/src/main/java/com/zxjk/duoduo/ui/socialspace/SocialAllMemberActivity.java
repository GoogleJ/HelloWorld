package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.AllGroupMembersResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.InviteForSocialActivity;
import com.zxjk.duoduo.ui.msgpage.widget.IndexView;
import com.zxjk.duoduo.utils.AesUtil;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.PinYinUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SocialAllMemberActivity extends BaseActivity {

    private RecyclerView recycler;
    private BaseQuickAdapter<AllGroupMembersResponse, BaseViewHolder> adapter;
    private ArrayList<AllGroupMembersResponse> data = new ArrayList<>();
    private EditText etSearch;

    private IndexView index;
    private TextView tvLetter;

    private String ownerId;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_all_member);

        ownerId = getIntent().getStringExtra("ownerId");

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        TextView title = findViewById(R.id.tv_title);
        recycler = findViewById(R.id.recycler);
        etSearch = findViewById(R.id.etSearch);
        index = findViewById(R.id.index);
        tvLetter = findViewById(R.id.tvLetter);

        index.initSocial();
        index.setOnTouchingLetterChangedListener(l -> {
            if (l.equals("*")) {
                recycler.scrollToPosition(1);
            }
            for (int i = 0; i < data.size(); i++) {
                String letters = data.get(i).getFirstLetter();
                if (letters.equals(l)) {
                    recycler.scrollToPosition(i + 1);
                    break;
                }
            }
        });
        index.setShowTextDialog(tvLetter);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BaseQuickAdapter<AllGroupMembersResponse, BaseViewHolder>(R.layout.item_social_allmembs) {
            @Override
            protected void convert(BaseViewHolder helper, AllGroupMembersResponse item) {
                helper.setText(R.id.tvName, item.getNick());

                TextView tvFirstLetter = helper.getView(R.id.tvFirstLetter);
                if (item.getFirstLetter().contains("!")) {
                    tvFirstLetter.setText(R.string.groupowner_or_manager);
                } else {
                    tvFirstLetter.setText(item.getFirstLetter());
                }

                if (helper.getAdapterPosition() == 0) {
                    tvFirstLetter.setVisibility(View.GONE);
                } else if (helper.getAdapterPosition() == 1) {
                    tvFirstLetter.setVisibility(View.VISIBLE);
                } else if (item.getFirstLetter().equals(getData().get(helper.getAdapterPosition() - 2).getFirstLetter())) {
                    tvFirstLetter.setVisibility(View.GONE);
                } else {
                    tvFirstLetter.setVisibility(View.VISIBLE);
                }

                ImageView iv = helper.getView(R.id.ivHead);
                GlideUtil.loadCircleImg(iv, item.getHeadPortrait());
                TextView tvSign = helper.getView(R.id.tvSign);
                if (item.getId().equals(ownerId)) {
                    tvSign.setVisibility(View.VISIBLE);
                    tvSign.setText(R.string.group_owner);
                    tvSign.setBackgroundResource(R.drawable.shapef7b230_3);
                } else if (item.getIsAdmin().equals("1")) {
                    tvFirstLetter.setVisibility(View.GONE);
                    tvSign.setVisibility(View.VISIBLE);
                    tvSign.setText(R.string.manager);
                    tvSign.setBackgroundResource(R.drawable.shape_theme3);
                } else {
                    tvSign.setVisibility(View.GONE);
                }
            }
        };

        View inflate = View.inflate(this, R.layout.invitewechatfriend, null);
        inflate.setOnClickListener(v -> {
            String uri2Code = Constant.APP_SHARE_URL + AesUtil.getInstance().encrypt("id=" + Constant.userId + "&groupId=" + getIntent().getStringExtra("groupId") + "&type=" + "1");

            UMWeb link = new UMWeb(uri2Code);
            link.setTitle("邀请你加入群聊");
            link.setDescription("我在使用Hilamg，邀请你加\n" +
                    "入“BTC官方交流社群”，快\n" +
                    "来加入我们吧～");
            link.setThumb(new UMImage(this, R.drawable.ic_hilamglogo3));
            new ShareAction(this).withMedia(link).setPlatform(SHARE_MEDIA.WEIXIN).share();
            finish();
        });
        adapter.addHeaderView(inflate);

        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener((adapter, view, position) -> CommonUtils.resolveFriendList(this, ((AllGroupMembersResponse) adapter.getData().get(position)).getId()));

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getGroupMemByGroupId(getIntent().getStringExtra("groupId"))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .map(list -> {
                    for (AllGroupMembersResponse f : list) {
                        f.setFirstLetter(PinYinUtils.converterToFirstSpellForSocial(f.getNick(), f.getId().equals(ownerId) ? "1" : (f.getIsAdmin().equals("1") ? "2" : "3")));
                    }
                    Comparator<AllGroupMembersResponse> comparator = (o1, o2) -> o1.getFirstLetter().compareTo(o2.getFirstLetter());
                    Collections.sort(list, comparator);
                    return list;
                })
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(list -> {
                    title.setText(getString(R.string.group_member) + "(" + list.size() + ")");
                    data.addAll(list);
                    adapter.setNewData(data);
                }, this::handleApiError);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                List<AllGroupMembersResponse> filterList = new ArrayList<>();

                for (AllGroupMembersResponse contact : data) {
                    boolean isNameContains = contact.getNick()
                            .contains(str);
                    if (isNameContains) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }

                adapter.setNewData(filterList);
            }
        });
    }

}
