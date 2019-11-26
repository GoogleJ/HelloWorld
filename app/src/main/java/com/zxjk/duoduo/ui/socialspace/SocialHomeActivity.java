package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.BaseResponse;
import com.zxjk.duoduo.bean.response.CommunityCultureResponse;
import com.zxjk.duoduo.bean.response.CommunityInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.CreateGroupActivity;
import com.zxjk.duoduo.ui.msgpage.GroupChatInformationActivity;
import com.zxjk.duoduo.ui.widget.ImagePagerIndicator;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.ui.widget.SlopScrollView;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.MD5Utils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.InformationNotificationMessage;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class SocialHomeActivity extends BaseActivity {

    private int[] detailTitles = {R.string.social_calture, R.string.social_act};
    //    private static final float SCROLL_SLOP = ;
    private static final int REQUEST_SLOGAN = 1;
    private static final int REQUEST_NOTICE = 2;
    private int minimumHeightForVisibleOverlappingContent = 0;
    private int totalScrollRange = 0;
    private int toolbarHeight = 0;
    private int statusbarHeight = 0;

    private AppBarLayout app_bar;
    private CollapsingToolbarLayout collapsingLayout;
    private ImageView ivBg;
    private Toolbar toolbar;
    private ImageView ivToolBarStart;
    private TextView tvTitle;
    private ImageView ivToolBarEnd;
    private SlopScrollView slopScroll;
    private ViewPager pagerOut;
    private MagicIndicator indicatorOut;
    private MagicIndicator indicatorTop;

    private RecyclerView recyclerGroupMember;
    private BaseQuickAdapter<CommunityInfoResponse.MembersBean, BaseViewHolder> socialMemAdapter;
    int maxMemVisiableItem;
    private int colorOwner;
    private TextView tvSlogan;
    private TextView tvSocialName;
    private TextView tvSocialId;
    private TextView tvNotice;
    private ImageView ivHead;
    private ImageView ivOpenConversation;
    private LinearLayout llSocialNotice;

    private ViewStub viewStubPay;
    private ViewStub viewStubFree;
    private boolean contentEnable = false;

    private QuickPopup menuPop;

    private CommunityInfoResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTrasnferStatusBar(true);
        BarUtils.setStatusBarLightMode(this, true);
        setContentView(R.layout.activity_social_home);

        initView();

        maxMemVisiableItem = (ScreenUtils.getScreenWidth() - CommonUtils.dip2px(this, 64)) % CommonUtils.dip2px(this, 48);

        initData();

        setSocialBackgroundHeight();

        setToolBarMarginTop();

        setSupportActionBar(toolbar);

        onAppBarScroll();

        initSlopScrollView();

        setPagerHeight();

        initIndicator();

        initPager();
    }

    @SuppressLint("CheckResult")
    private void initData() {
        String groupId = getIntent().getStringExtra("id");
        Api api = ServiceFactory.getInstance().getBaseService(Api.class);

        api.communityCulture(groupId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .flatMap((Function<CommunityCultureResponse, ObservableSource<BaseResponse<CommunityInfoResponse>>>) r -> {
                    runOnUiThread(() -> {
                        if (!r.getType().equals("culture")) {
                            contentEnable = false;
                            llSocialNotice.setVisibility(View.GONE);
                            indicatorOut.setVisibility(View.GONE);
                            pagerOut.setVisibility(View.GONE);
                            ivOpenConversation.setVisibility(View.GONE);
                            if (r.getType().equals("free")) {
                                View inflate = viewStubFree.inflate();
                                inflate.findViewById(R.id.tvFunc).setOnClickListener(v ->
                                        api.enterGroup(groupId, "", Constant.userId)
                                                .compose(bindToLifecycle())
                                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                                                .compose(RxSchedulers.normalTrans())
                                                .subscribe(s -> {
                                                    inflate.setVisibility(View.GONE);
                                                    llSocialNotice.setVisibility(View.VISIBLE);
                                                    indicatorOut.setVisibility(View.VISIBLE);
                                                    pagerOut.setVisibility(View.VISIBLE);
                                                    ivOpenConversation.setVisibility(View.VISIBLE);

                                                    InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain("\"" +
                                                            Constant.currentUser.getNick() + "\"加入了群组");
                                                    Message message = Message.obtain(groupId, Conversation.ConversationType.GROUP, notificationMessage);
                                                    RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
                                                }, this::handleApiError));
                            } else if (r.getType().equals("pay")) {
                                View inflate = viewStubPay.inflate();
                                TextView tvPayMoney = inflate.findViewById(R.id.tvPayMoney);
                                TextView tvSymbol = inflate.findViewById(R.id.tvSymbol);
                                ImageView ivIcon = inflate.findViewById(R.id.ivIcon);
                                tvPayMoney.setText(r.getPay().getPayFee());
                                tvSymbol.setText(r.getPay().getPaySymbol());
                                GlideUtil.loadNormalImg(ivIcon, r.getPay().getPayLogo());
                                findViewById(R.id.tvFunc).setOnClickListener(v -> new NewPayBoard(this).show(pwd ->
                                        api.payToGroup(groupId, "", MD5Utils.getMD5(pwd), r.getPay().getPayFee(), r.getPay().getPaySymbol())
                                                .compose(bindToLifecycle())
                                                .compose(RxSchedulers.normalTrans())
                                                .compose(RxSchedulers.ioObserver())
                                                .subscribe(s -> {
                                                    inflate.setVisibility(View.GONE);
                                                    llSocialNotice.setVisibility(View.VISIBLE);
                                                    indicatorOut.setVisibility(View.VISIBLE);
                                                    pagerOut.setVisibility(View.VISIBLE);
                                                    ivOpenConversation.setVisibility(View.VISIBLE);

                                                    InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain("\"" +
                                                            Constant.currentUser.getNick() + "\"加入了群组");
                                                    Message message = Message.obtain(groupId, Conversation.ConversationType.GROUP, notificationMessage);
                                                    RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
                                                }, this::handleApiError)));
                            }
                        } else {
                            contentEnable = true;
                        }
                    });
                    return api.communityInfo(groupId);
                })
                .compose(RxSchedulers.normalTrans())
                .map(r -> {
                    runOnUiThread(() -> {
                        response = r;
                        tvSlogan.setText("社群简介:" + r.getIntroduction());
                        tvSocialId.setText("社群号:" + r.getCode());
                        tvSocialName.setText(r.getName());
                        GlideUtil.loadNormalImg(ivBg, r.getBgi());
                        GlideUtil.loadNormalImg(ivHead, r.getLogo());
                        tvTitle.setText(r.getName());
                        tvNotice.setText(r.getAnnouncement());
                    });
                    if (r.getIdentity().equals("0")) {
                        ArrayList<CommunityInfoResponse.MembersBean> list = new ArrayList<>(r.getMembers());
                        if (r.getMembers().size() >= maxMemVisiableItem) {
                            List<CommunityInfoResponse.MembersBean> membersBeans = list.subList(0, maxMemVisiableItem - 1);
                            membersBeans.add(new CommunityInfoResponse.MembersBean());
                            return membersBeans;
                        } else {
                            list.add(new CommunityInfoResponse.MembersBean());
                            return list;
                        }
                    } else {
                        ArrayList<CommunityInfoResponse.MembersBean> list = new ArrayList<>(r.getMembers());
                        if (r.getMembers().size() >= maxMemVisiableItem - 1) {
                            List<CommunityInfoResponse.MembersBean> membersBeans = list.subList(0, maxMemVisiableItem - 2);
                            membersBeans.add(new CommunityInfoResponse.MembersBean());
                            membersBeans.add(new CommunityInfoResponse.MembersBean());
                            return membersBeans;
                        } else {
                            list.add(new CommunityInfoResponse.MembersBean());
                            list.add(new CommunityInfoResponse.MembersBean());
                            return list;
                        }
                    }
                })
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(this::initAdapterForSocialMem, t -> {
                    handleApiError(t);
                    finish();
                });
    }

    private void initAdapterForSocialMem(List<CommunityInfoResponse.MembersBean> data) {
        recyclerGroupMember.setLayoutManager(new GridLayoutManager(this, data.size()));
        colorOwner = Color.parseColor("#ffc000");
        socialMemAdapter = new BaseQuickAdapter<CommunityInfoResponse.MembersBean, BaseViewHolder>(R.layout.item_social_membs) {
            @Override
            protected void convert(BaseViewHolder helper, CommunityInfoResponse.MembersBean item) {
                CircleImageView ivMemberHead = helper.getView(R.id.ivMemberHead);
                ImageView ivOwner = helper.getView(R.id.ivOwner);

                if (helper.getAdapterPosition() == 0) {
                    ivOwner.setVisibility(View.VISIBLE);
                    ivMemberHead.setBorderColor(colorOwner);
                } else {
                    ivOwner.setVisibility(View.GONE);
                    ivMemberHead.setBorderColor(Color.WHITE);
                }

                if (response.getIdentity().equals("0")) {
                    if (helper.getAdapterPosition() == data.size() - 1) {
                        ivMemberHead.setImageResource(R.drawable.ic_social_member_add);
                        ivMemberHead.setOnClickListener(v -> {
                            Intent intent = new Intent(SocialHomeActivity.this, CreateGroupActivity.class);
                            intent.putExtra("eventType", 2);
                            intent.putExtra("groupId", response.getGroupId());
                            intent.putExtra("fromSocial", true);
                            startActivity(intent);
                        });
                    } else {
                        Glide.with(SocialHomeActivity.this).load(item.getHeadPortrait()).into(ivMemberHead);
                        ivMemberHead.setOnClickListener(v -> {

                        });
                    }
                } else {
                    if (helper.getAdapterPosition() == data.size() - 1) {
                        ivMemberHead.setImageResource(R.drawable.ic_social_member_remove);
                        ivMemberHead.setOnClickListener(v -> {
                            Intent intent = new Intent(SocialHomeActivity.this, CreateGroupActivity.class);
                            intent.putExtra("eventType", 3);
                            intent.putExtra("groupId", response.getGroupId());
                            intent.putExtra("fromSocial", true);
                            startActivity(intent);
                        });
                    } else if (helper.getAdapterPosition() == data.size() - 2) {
                        ivMemberHead.setImageResource(R.drawable.ic_social_member_add);
                        ivMemberHead.setOnClickListener(v -> {
                            Intent intent = new Intent(SocialHomeActivity.this, CreateGroupActivity.class);
                            intent.putExtra("eventType", 2);
                            intent.putExtra("groupId", response.getGroupId());
                            intent.putExtra("fromSocial", true);
                            startActivity(intent);
                        });
                    } else {
                        Glide.with(SocialHomeActivity.this).load(item.getHeadPortrait()).into(ivMemberHead);
                        ivMemberHead.setOnClickListener(v -> {

                        });
                    }
                }
            }
        };
        recyclerGroupMember.setAdapter(socialMemAdapter);
        socialMemAdapter.setNewData(data);
    }

    private void initSlopScrollView() {
        int slop = CommonUtils.dip2px(this, 240);
        slopScroll.setScrollSlop(slop);

        slopScroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY >= slop && indicatorTop.getVisibility() == View.INVISIBLE) {
                indicatorTop.setVisibility(View.VISIBLE);
                indicatorOut.setVisibility(View.INVISIBLE);
            } else if (scrollY < slop && indicatorTop.getVisibility() == View.VISIBLE) {
                indicatorTop.setVisibility(View.INVISIBLE);
                indicatorOut.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onAppBarScroll() {
        app_bar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int absOffset = Math.abs(verticalOffset);
            if (minimumHeightForVisibleOverlappingContent <= 0) {
                minimumHeightForVisibleOverlappingContent = app_bar.getMinimumHeightForVisibleOverlappingContent();
            }
            if (totalScrollRange <= 0) {
                totalScrollRange = app_bar.getTotalScrollRange();
                collapsingLayout.setScrimVisibleHeightTrigger((int) (totalScrollRange * 0.5));
            }

            if (absOffset <= minimumHeightForVisibleOverlappingContent) {
                if (ivToolBarEnd.getVisibility() == View.GONE) {
                    ivToolBarEnd.setVisibility(View.VISIBLE);
                    ivToolBarStart.setImageResource(R.drawable.ic_social_back);
                }
                if (ivToolBarStart.getVisibility() == View.GONE) {
                    ivToolBarStart.setVisibility(View.VISIBLE);
                }
            } else if (absOffset < totalScrollRange) {
                if (ivToolBarEnd.getVisibility() == View.VISIBLE) {
                    ivToolBarEnd.setVisibility(View.GONE);
                    ivToolBarStart.setVisibility(View.GONE);
                }
                if (tvTitle.getVisibility() == View.VISIBLE) {
                    tvTitle.setVisibility(View.INVISIBLE);
                }
            } else if (absOffset == totalScrollRange) {
                if (tvTitle.getVisibility() == View.INVISIBLE) {
                    tvTitle.setVisibility(View.VISIBLE);
                }
                if (ivToolBarEnd.getVisibility() == View.VISIBLE) {
                    ivToolBarEnd.setVisibility(View.GONE);
                }
                ivToolBarStart.setVisibility(View.VISIBLE);
                ivToolBarStart.setImageResource(R.drawable.ico_back);
            }
        });
    }

    private void setToolBarMarginTop() {
        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
        toolbarHeight = layoutParams1.height;
        statusbarHeight = BarUtils.getStatusBarHeight();
        layoutParams1.topMargin = statusbarHeight;
        toolbar.setLayoutParams(layoutParams1);
    }

    private void setSocialBackgroundHeight() {
        ViewGroup.LayoutParams layoutParams = app_bar.getLayoutParams();
        layoutParams.height = (int) (ScreenUtils.getScreenWidth() * 0.75);
        app_bar.setLayoutParams(layoutParams);
        ivBg.setImageResource(R.drawable.bg_default_social);
    }

    private void initView() {
        app_bar = findViewById(R.id.app_bar);
        collapsingLayout = findViewById(R.id.collapsingLayout);
        ivBg = findViewById(R.id.ivBg);
        toolbar = findViewById(R.id.toolbar);
        slopScroll = findViewById(R.id.slopScroll);
        pagerOut = findViewById(R.id.pagerOut);
        indicatorOut = findViewById(R.id.indicatorOut);
        indicatorTop = findViewById(R.id.indicatorTop);
        ivToolBarStart = findViewById(R.id.ivToolBarStart);
        tvTitle = findViewById(R.id.tvTitle);
        tvNotice = findViewById(R.id.tvNotice);
        ivToolBarEnd = findViewById(R.id.ivToolBarEnd);
        recyclerGroupMember = findViewById(R.id.recyclerGroupMember);
        tvSlogan = findViewById(R.id.tvSlogan);
        tvSocialName = findViewById(R.id.tvSocialName);
        tvSocialId = findViewById(R.id.tvSocialId);
        ivHead = findViewById(R.id.ivHead);
        ivOpenConversation = findViewById(R.id.ivOpenConversation);
        llSocialNotice = findViewById(R.id.llSocialNotice);
        viewStubPay = findViewById(R.id.viewStubPay);
        viewStubFree = findViewById(R.id.viewStubFree);
    }

    private void initPager() {
        pagerOut.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), R.string.appbar_scrolling_view_behavior) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                CalturePage calturePage = new CalturePage();
                if (position == 1) calturePage.social2();
                return calturePage;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        ViewPagerHelper.bind(indicatorOut, pagerOut);
        ViewPagerHelper.bind(indicatorTop, pagerOut);
    }

    private void setPagerHeight() {
        ViewGroup.LayoutParams layoutParams = pagerOut.getLayoutParams();
        layoutParams.height = ScreenUtils.getScreenHeight() - (toolbarHeight + statusbarHeight + CommonUtils.dip2px(SocialHomeActivity.this, 48) + BarUtils.getNavBarHeight());
        pagerOut.setLayoutParams(layoutParams);
    }

    private void initIndicator() {
        CommonNavigator navigator1 = new CommonNavigator(this);
        navigator1.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return detailTitles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SimplePagerTitleView pagerTitleView = new SimplePagerTitleView(context);
                pagerTitleView.setTextSize(15.5f);
                pagerTitleView.setNormalColor(ContextCompat.getColor(SocialHomeActivity.this, R.color.textColor9));
                pagerTitleView.setSelectedColor(ContextCompat.getColor(SocialHomeActivity.this, R.color.black));
                pagerTitleView.setText(detailTitles[index]);
                pagerTitleView.setOnClickListener(v -> pagerOut.setCurrentItem(index));
                return pagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return new ImagePagerIndicator(context, -3);
            }
        });

        CommonNavigator navigator2 = new CommonNavigator(SocialHomeActivity.this);
        navigator2.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return detailTitles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SimplePagerTitleView pagerTitleView = new SimplePagerTitleView(context);
                pagerTitleView.setTextSize(15.5f);
                pagerTitleView.setNormalColor(ContextCompat.getColor(SocialHomeActivity.this, R.color.textColor9));
                pagerTitleView.setSelectedColor(ContextCompat.getColor(SocialHomeActivity.this, R.color.black));
                pagerTitleView.setText(detailTitles[index]);
                pagerTitleView.setOnClickListener(v -> pagerOut.setCurrentItem(index));
                return pagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return new ImagePagerIndicator(context, -3);
            }
        });

        indicatorTop.setNavigator(navigator1);
        indicatorOut.setNavigator(navigator2);
    }

    public void socialSlogan(View view) {
        if (!contentEnable) {
            ToastUtils.showShort(R.string.cantdone);
            return;
        }
        Intent intent = new Intent(this, EditSocialBasicActivity.class);
        intent.putExtra("type", "1");
        intent.putExtra("data", response);
        startActivityForResult(intent, REQUEST_SLOGAN);
    }

    public void socialNotice(View view) {
        if (!contentEnable) {
            ToastUtils.showShort(R.string.cantdone);
            return;
        }
        Intent intent = new Intent(this, EditSocialBasicActivity.class);
        intent.putExtra("type", "2");
        intent.putExtra("data", response);
        startActivityForResult(intent, REQUEST_NOTICE);
    }

    public void copySocialId(View view) {
        ToastUtils.showShort(R.string.duplicated_to_clipboard);
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("text", response.getCode()));
        }
    }

    public void openConversation(View view) {
        RongIM.getInstance().startGroupChat(this, response.getGroupId(), response.getName());
    }

    public void back(View view) {
        finish();
    }

    public void menu(View view) {
        if (!contentEnable) {
            ToastUtils.showShort(R.string.cantdone);
            return;
        }
        if (menuPop == null) {
            View.OnClickListener onClickListener = child -> ToastUtils.showShort(R.string.developing);
            menuPop = QuickPopupBuilder.with(this)
                    .contentView(R.layout.pop_social_top)
                    .config(new QuickPopupConfig()
                            .backgroundColor(android.R.color.transparent)
                            .gravity(Gravity.BOTTOM | Gravity.END)
                            .withShowAnimation(AnimationUtils.loadAnimation(this, R.anim.push_scale_in))
                            .withDismissAnimation(AnimationUtils.loadAnimation(this, R.anim.push_scale_out))
                            .withClick(R.id.ic_social_end_pop1, onClickListener, true)
                            .withClick(R.id.ic_social_end_pop2, child -> {
                                if (response == null) return;
                                Intent intent = new Intent(this, SocialManageActivity.class);
                                intent.putExtra("data", response);
                                startActivity(intent);
                            }, true)
                            .withClick(R.id.ic_social_end_pop3, onClickListener, true))
                    .build();
        }

        menuPop.showPopupWindow(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == 1) {
            switch (requestCode) {
                case REQUEST_NOTICE:
                    response.setAnnouncementEditDate(data.getStringExtra("result"));
                    response.setAnnouncement(data.getStringExtra("notice"));
                    tvNotice.setText(response.getAnnouncement());
                    break;
                case REQUEST_SLOGAN:
                    response.setIntroductionEditDate(data.getStringExtra("result"));
                    response.setName(data.getStringExtra("name"));
                    response.setIntroduction(data.getStringExtra("slogan"));
                    tvSocialName.setText(response.getName());
                    tvSlogan.setText(response.getIntroduction());
                    break;
            }
        }
    }
}
