package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.zxjk.duoduo.bean.response.SocialCaltureListBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.ZoomActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.CreateGroupActivity;
import com.zxjk.duoduo.ui.widget.ImagePagerIndicator;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
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

public class SocialHomeActivity extends BaseActivity {

    private int[] detailTitles = {R.string.social_calture, R.string.social_act};
    private static final int REQUEST_SLOGAN = 1;
    private static final int REQUEST_NOTICE = 2;
    private static final int REQUEST_SOCIALNAME = 3;
    private static final int REQUEST_LOGO = 4;
    private static final int REQUEST_BG = 5;
    private int minimumHeightForVisibleOverlappingContent = 0;
    private int totalScrollRange = 0;
    private int statusbarHeight = 0;

    private AppBarLayout app_bar;
    private CollapsingToolbarLayout collapsingLayout;
    private LinearLayout llTop;
    private LinearLayout llSecond;
    private ImageView ivBg;
    private Toolbar toolbar;
    private ImageView ivToolBarStart;
    private TextView tvTitle;
    private TextView tvSocialCode;
    private ImageView ivToolBarEnd;
    private ViewPager pagerOut;
    private MagicIndicator indicatorTop;

    private RecyclerView recyclerGroupMember;
    private BaseQuickAdapter<CommunityInfoResponse.MembersBean, BaseViewHolder> socialMemAdapter;
    private int maxMemVisiableItem;
    private int colorOwner;
    private TextView tvSlogan;
    private TextView tvSocialName;
    private TextView tvSocialId;
    private TextView tvNotice;
    private ImageView ivHead;
    private ImageView ivOpenConversation;
    private LinearLayout llSocialNotice;
    private LinearLayout llRemoveMem;
    private LinearLayout llInviteOrRemove;
    private View bgMask;

    private ViewStub viewStubPay;
    private ViewStub viewStubFree;
    private boolean contentEnable;
    private boolean hasInitTop;
    private boolean isInEditStatus;

    private CommunityInfoResponse response;

    private SocialCalturePage socialCalturePage;
    private DynamicsPage dynamicsPage;

    private String groupId;

    private boolean fromConversatin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTrasnferStatusBar(true);
        BarUtils.setStatusBarLightMode(this, true);
        setContentView(R.layout.activity_social_home);

        initView();

        fromConversatin = getIntent().getBooleanExtra("fromConversatin", false);
        if (fromConversatin) {
            ivOpenConversation.setVisibility(View.GONE);
        }

        groupId = getIntent().getStringExtra("id");

        initFragment();

        maxMemVisiableItem = (ScreenUtils.getScreenWidth() - CommonUtils.dip2px(this, 84)) / CommonUtils.dip2px(this, 25) + 1;

        ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
        layoutParams.height = (int) (ScreenUtils.getScreenWidth() * 0.75);
        ivBg.setLayoutParams(layoutParams);
        ivBg.setImageResource(R.drawable.bg_default_social);

        ViewGroup.LayoutParams layoutParams1 = bgMask.getLayoutParams();
        layoutParams1.height = (int) (ScreenUtils.getScreenWidth() * 0.75 * 0.5);
        bgMask.setLayoutParams(layoutParams1);

        onAppBarScroll();

        initData();

        setToolBarMarginTop();

        setSocialBackgroundHeight();

        setSupportActionBar(toolbar);

        initIndicator();

        initPager();
    }

    private void initFragment() {
        socialCalturePage = new SocialCalturePage();
        dynamicsPage = new DynamicsPage();

        Bundle args = new Bundle();
        args.putString("groupId", groupId);
        socialCalturePage.setArguments(args);

        socialCalturePage.setDoneAction(l -> {
            isInEditStatus = false;
            tvSocialId.setVisibility(View.VISIBLE);
            app_bar.setExpanded(true, true);
            tvTitle.setText(response.getName());
            ivToolBarStart.setVisibility(View.VISIBLE);
            tvSocialCode.setVisibility(View.VISIBLE);
            ivOpenConversation.animate().translationXBy(-ivOpenConversation.getWidth())
                    .setInterpolator(new OvershootInterpolator()).start();
        });
    }

    @SuppressLint("CheckResult")
    private void initData() {
        Api api = ServiceFactory.getInstance().getBaseService(Api.class);

        api.communityCulture(groupId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .flatMap((Function<CommunityCultureResponse, ObservableSource<BaseResponse<CommunityInfoResponse>>>) r -> {
                    runOnUiThread(() -> {
                        if (!r.getType().equals("culture")) {
                            banAppBarScroll(true);

                            indicatorTop.setVisibility(View.GONE);
                            contentEnable = false;
                            llSocialNotice.setVisibility(View.GONE);
                            pagerOut.setVisibility(View.GONE);
                            llInviteOrRemove.setVisibility(View.GONE);
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

                                                    banAppBarScroll(true);

                                                    indicatorTop.setVisibility(View.VISIBLE);
                                                    llSocialNotice.setVisibility(View.VISIBLE);
                                                    pagerOut.setVisibility(View.VISIBLE);
                                                    llInviteOrRemove.setVisibility(View.VISIBLE);
                                                    ivOpenConversation.setVisibility(View.VISIBLE);

                                                    InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain("\"" +
                                                            Constant.currentUser.getNick() + "\"加入了社群");
                                                    Message message = Message.obtain(groupId, Conversation.ConversationType.GROUP, notificationMessage);
                                                    RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);

                                                    contentEnable = true;

                                                    parseCaltureResult(s);
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
                                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                                                .subscribe(s -> {
                                                    inflate.setVisibility(View.GONE);

                                                    banAppBarScroll(true);

                                                    indicatorTop.setVisibility(View.VISIBLE);
                                                    llSocialNotice.setVisibility(View.VISIBLE);
                                                    pagerOut.setVisibility(View.VISIBLE);
                                                    llInviteOrRemove.setVisibility(View.VISIBLE);
                                                    ivOpenConversation.setVisibility(View.VISIBLE);

                                                    InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain("\"" +
                                                            Constant.currentUser.getNick() + "\"加入了社群");
                                                    Message message = Message.obtain(groupId, Conversation.ConversationType.GROUP, notificationMessage);
                                                    RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);

                                                    contentEnable = true;
                                                    parseCaltureResult(s);
                                                }, this::handleApiError)));
                            }
                        } else {
                            contentEnable = true;
                            parseCaltureResult(r);
                        }
                    });
                    return api.communityInfo(groupId);
                })
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(r -> {
                    response = r;
                    tvSlogan.setText(r.getIntroduction());
                    tvSocialId.setText("社群号:" + r.getCode());
                    tvSocialName.setText(r.getName());
                    GlideUtil.loadNormalImg(ivBg, r.getBgi());
                    GlideUtil.loadNormalImg(ivHead, r.getLogo());
                    tvTitle.setText(r.getName());
                    tvSocialCode.setText("社群号:" + r.getCode());
                    tvNotice.setText(r.getAnnouncement());

                    if (!TextUtils.isEmpty(r.getIdentity()) && !r.getIdentity().equals("0")) {
                        llRemoveMem.setVisibility(View.VISIBLE);
                    }

                    ivHead.setOnClickListener(v -> {
                        Intent intent = new Intent(this, ZoomActivity.class);
                        intent.putExtra("image", response.getLogo());
                        intent.putExtra("fromSocialHomePage", true);
                        if (!response.getIdentity().equals("0")) {
                            intent.putExtra("id", response.getGroupId());
                            intent.putExtra("type", 1);
                        }
                        startActivityForResult(intent, REQUEST_LOGO);
                    });

                    ivBg.setOnClickListener(v -> {
                        Intent intent = new Intent(this, ZoomActivity.class);
                        intent.putExtra("image", response.getBgi());
                        intent.putExtra("fromSocialHomePage", true);
                        if (!response.getIdentity().equals("0")) {
                            intent.putExtra("id", response.getGroupId());
                            intent.putExtra("type", 2);
                        }
                        startActivityForResult(intent, REQUEST_BG);
                    });

                    String memCount = getIntent().getStringExtra("memCount");
                    int realCount = -1;
                    if (!TextUtils.isEmpty(memCount)) {
                        try {
                            realCount = Integer.parseInt(memCount);
                        } catch (Exception e) {
                        }
                    }
                    if (realCount != -1 && realCount >= maxMemVisiableItem) {
                        maxMemVisiableItem -= 1;
                        List<CommunityInfoResponse.MembersBean> result = r.getMembers().subList(0, maxMemVisiableItem - 1);
                        int numLeft = realCount - result.size();
                        result.add(new CommunityInfoResponse.MembersBean());
                        ViewGroup.MarginLayoutParams layoutParams2 = (ViewGroup.MarginLayoutParams) recyclerGroupMember.getLayoutParams();
                        layoutParams2.width = CommonUtils.dip2px(this, 25) * (result.size() + 1) + CommonUtils.dip2px(this, 20);
                        layoutParams2.setMarginEnd(CommonUtils.dip2px(this, 20));
                        recyclerGroupMember.setLayoutParams(layoutParams2);
                        initAdapterForSocialMem(result, numLeft);
                    } else {
                        ViewGroup.MarginLayoutParams layoutParams2 = (ViewGroup.MarginLayoutParams) recyclerGroupMember.getLayoutParams();
                        layoutParams2.width = CommonUtils.dip2px(this, 25) * (r.getMembers().size()) + CommonUtils.dip2px(this, 20);
                        recyclerGroupMember.setLayoutParams(layoutParams2);
                        initAdapterForSocialMem(r.getMembers(), 0);
                    }
                }, t -> {
                    handleApiError(t);
                    finish();
                });
    }

    //解析社群文化为多类型data
    private void parseCaltureResult(CommunityCultureResponse r) {

        ArrayList<SocialCaltureListBean> caltures = new ArrayList<>();

        if (r.getOfficialWebsite() != null) {
            SocialCaltureListBean webBean = new SocialCaltureListBean(SocialCaltureListBean.TYPE_WEB);
            webBean.setOfficialWebsite(r.getOfficialWebsite());
            caltures.add(webBean);
        }
        if (r.getFiles() != null) {
            SocialCaltureListBean fileBean = new SocialCaltureListBean(SocialCaltureListBean.TYPE_FILE);
            fileBean.setFiles(r.getFiles());
            caltures.add(fileBean);
        }
        if (r.getVideo() != null) {
            SocialCaltureListBean videoBean = new SocialCaltureListBean(SocialCaltureListBean.TYPE_VIDEO);
            videoBean.setVideo(r.getVideo());
            caltures.add(videoBean);
        }
        if (r.getApplication() != null) {
            SocialCaltureListBean appBean = new SocialCaltureListBean(SocialCaltureListBean.TYPE_APP);
            appBean.setApplication(r.getApplication());
            caltures.add(appBean);
        }
        if (r.getActivities() != null) {
            SocialCaltureListBean actBean = new SocialCaltureListBean(SocialCaltureListBean.TYPE_ACTIVITY);
            actBean.setActivities(r.getActivities());
            caltures.add(actBean);
        }

        socialCalturePage.bindCaltureData(caltures);
    }

    private void banAppBarScroll(boolean isScroll) {
        if (app_bar == null) return;
        View mAppBarChildAt = app_bar.getChildAt(0);
        AppBarLayout.LayoutParams mAppBarParams = (AppBarLayout.LayoutParams) mAppBarChildAt.getLayoutParams();
        if (isScroll) {
            mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
            mAppBarChildAt.setLayoutParams(mAppBarParams);
        } else {
            mAppBarParams.setScrollFlags(0);
        }
    }

    public void addSocialMem(View view) {
        if (!contentEnable || isInEditStatus) {
            ToastUtils.showShort(R.string.cantdone);
            return;
        }
        Intent intent = new Intent(SocialHomeActivity.this, CreateGroupActivity.class);
        intent.putExtra("eventType", 2);
        intent.putExtra("groupId", response.getGroupId());
        intent.putExtra("fromSocial", true);
        intent.putExtra("socialLogo", response.getLogo());
        startActivity(intent);
    }

    public void removeSocialMem(View view) {
        if (!contentEnable || isInEditStatus) {
            ToastUtils.showShort(R.string.cantdone);
            return;
        }
        Intent intent = new Intent(SocialHomeActivity.this, CreateGroupActivity.class);
        intent.putExtra("eventType", 3);
        intent.putExtra("groupId", response.getGroupId());
        intent.putExtra("fromSocial", true);
        startActivity(intent);
    }

    private void initAdapterForSocialMem(List<CommunityInfoResponse.MembersBean> data, int numLeft) {
        Collections.reverse(data);
        recyclerGroupMember.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, true));
        colorOwner = Color.parseColor("#ffc000");

        socialMemAdapter = new BaseQuickAdapter<CommunityInfoResponse.MembersBean, BaseViewHolder>(R.layout.item_social_membs) {
            @Override
            protected void convert(BaseViewHolder helper, CommunityInfoResponse.MembersBean item) {
                CircleImageView ivMemberHead = helper.getView(R.id.ivMemberHead);
                ImageView ivOwner = helper.getView(R.id.ivOwner);
                TextView tvNumLeft = helper.getView(R.id.tvNumLeft);

                if (helper.getAdapterPosition() == data.size() - 1) {
                    ivOwner.setVisibility(View.VISIBLE);
                    ivMemberHead.setBorderColor(colorOwner);
                    ivMemberHead.setBorderWidth(CommonUtils.dip2px(ivMemberHead.getContext(), 1));
                } else {
                    ivOwner.setVisibility(View.GONE);
                    ivMemberHead.setBorderColor(Color.WHITE);
                    ivMemberHead.setBorderWidth(1);
                }

                if (helper.getAdapterPosition() == 0 && data.size() == maxMemVisiableItem) {
                    tvNumLeft.setVisibility(View.VISIBLE);
                    ivMemberHead.setVisibility(View.GONE);
                    tvNumLeft.setText("+" + (numLeft > 999 ? 999 : numLeft));
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) helper.itemView.getLayoutParams();
                    params.setMarginStart(-CommonUtils.dip2px(ivMemberHead.getContext(), 5));
                    helper.itemView.setLayoutParams(params);
                } else {
                    Glide.with(ivMemberHead.getContext()).load(item.getHeadPortrait()).error(R.drawable.errorimg_head)
                            .into(ivMemberHead);
                }
            }
        };
        socialMemAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (!contentEnable || isInEditStatus) {
                ToastUtils.showShort(R.string.cantdone);
                return;
            }
            Intent intent = new Intent(SocialHomeActivity.this, SocialAllMemberActivity.class);
            intent.putExtra("socialName", response.getName());
            intent.putExtra("groupId", groupId);
            intent.putExtra("ownerId", response.getOwnerId());
            startActivity(intent);
        });
        recyclerGroupMember.setAdapter(socialMemAdapter);
        socialMemAdapter.setNewData(data);
    }

    private void onAppBarScroll() {
        app_bar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (isInEditStatus) {
                return;
            }

            int absOffset = Math.abs(verticalOffset);

            if (absOffset <= minimumHeightForVisibleOverlappingContent) {
                if (ivToolBarEnd.getVisibility() == View.GONE && response != null && !response.getIdentity().equals("0")) {
                    ivToolBarEnd.setVisibility(View.VISIBLE);
                    ivToolBarEnd.setImageResource(R.drawable.ic_socialhome_end_white);
                }
                if (ivToolBarStart.getVisibility() == View.GONE) {
                    ivToolBarStart.setImageResource(R.drawable.ic_social_back);
                    ivToolBarStart.setVisibility(View.VISIBLE);
                }
                if (tvTitle.getVisibility() == View.VISIBLE) {
                    tvTitle.setVisibility(View.INVISIBLE);
                    tvSocialCode.setVisibility(View.INVISIBLE);
                }
            } else if (absOffset < totalScrollRange) {
                if (ivToolBarEnd.getVisibility() == View.VISIBLE && response != null && !response.getIdentity().equals("0")) {
                    ivToolBarEnd.setVisibility(View.GONE);
                }
                if (ivToolBarStart.getVisibility() == View.VISIBLE) {
                    ivToolBarStart.setVisibility(View.GONE);
                }
                if (tvTitle.getVisibility() == View.VISIBLE) {
                    tvTitle.setVisibility(View.INVISIBLE);
                    tvSocialCode.setVisibility(View.INVISIBLE);
                }
            } else if (absOffset == totalScrollRange) {
                if (tvTitle.getVisibility() != View.VISIBLE) {
                    tvTitle.setVisibility(View.VISIBLE);
                    tvSocialCode.setVisibility(View.VISIBLE);
                }
                if (ivToolBarEnd.getVisibility() == View.GONE && response != null && !response.getIdentity().equals("0")) {
                    ivToolBarEnd.setVisibility(View.VISIBLE);
                    ivToolBarEnd.setImageResource(R.drawable.ic_socialhome_end_black);
                }
                if (ivToolBarStart.getVisibility() == View.GONE) {
                    ivToolBarStart.setVisibility(View.VISIBLE);
                    ivToolBarStart.setImageResource(R.drawable.ico_back);
                }
            }
        });
    }

    private void setToolBarMarginTop() {
        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
        statusbarHeight = BarUtils.getStatusBarHeight();
        layoutParams1.topMargin = statusbarHeight;
        toolbar.setLayoutParams(layoutParams1);
    }

    private int appbarHeight;

    private void setSocialBackgroundHeight() {
        app_bar.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (!hasInitTop) {
                hasInitTop = true;
                llTop.setPadding(CommonUtils.dip2px(SocialHomeActivity.this, 16), (int) (ScreenUtils.getScreenWidth() * 0.75) - llSecond.getHeight(), CommonUtils.dip2px(SocialHomeActivity.this, 16), 0);
            }
            if (app_bar.getHeight() > appbarHeight) {
                appbarHeight = app_bar.getHeight();
                totalScrollRange = app_bar.getHeight() - BarUtils.getActionBarHeight() - statusbarHeight - CommonUtils.dip2px(SocialHomeActivity.this, 48);
                minimumHeightForVisibleOverlappingContent = app_bar.getHeight() - CommonUtils.dip2px(SocialHomeActivity.this, 48) - CommonUtils.dip2px(SocialHomeActivity.this, 160);
            }
        });
    }

    private void initView() {
        app_bar = findViewById(R.id.app_bar);
        collapsingLayout = findViewById(R.id.collapsingLayout);
        ivBg = findViewById(R.id.ivBg);
        toolbar = findViewById(R.id.toolbar);
        pagerOut = findViewById(R.id.pagerOut);
        indicatorTop = findViewById(R.id.indicatorTop);
        ivToolBarStart = findViewById(R.id.ivToolBarStart);
        tvTitle = findViewById(R.id.tvTitle);
        tvSocialCode = findViewById(R.id.tvSocialCode);
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
        llTop = findViewById(R.id.llTop);
        llSecond = findViewById(R.id.llSecond);
        bgMask = findViewById(R.id.bgMask);
        llRemoveMem = findViewById(R.id.llRemoveMem);
        llInviteOrRemove = findViewById(R.id.llInviteOrRemove);
    }

    private void initPager() {
        pagerOut.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), R.string.appbar_scrolling_view_behavior) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                if (position == 0) return socialCalturePage;
                else return dynamicsPage;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        ViewPagerHelper.bind(indicatorTop, pagerOut);
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
                SimplePagerTitleView pagerTitleView = new SimplePagerTitleView(context) {
                    float mMinScale = 0.85f;

                    @Override
                    public void onSelected(int index, int totalCount) {
                        super.onSelected(index, totalCount);
                        setTypeface(Typeface.DEFAULT_BOLD);
                    }

                    @Override
                    public void onDeselected(int index, int totalCount) {
                        super.onDeselected(index, totalCount);
                        setTypeface(Typeface.DEFAULT);
                    }

                    @Override
                    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
                        super.onLeave(index, totalCount, leavePercent, leftToRight);
                        setScaleX(1.0f + (mMinScale - 1.0f) * leavePercent);
                        setScaleY(1.0f + (mMinScale - 1.0f) * leavePercent);
                    }

                    @Override
                    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
                        super.onEnter(index, totalCount, enterPercent, leftToRight);
                        setScaleX(mMinScale + (1.0f - mMinScale) * enterPercent);
                        setScaleY(mMinScale + (1.0f - mMinScale) * enterPercent);
                    }
                };
                pagerTitleView.setTextSize(16f);
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
    }

    public void socialSlogan(View view) {
        if (!contentEnable || isInEditStatus) {
            ToastUtils.showShort(R.string.cantdone);
            return;
        }
        Intent intent = new Intent(this, EditSocialBasicActivity.class);
        intent.putExtra("type", "1");
        intent.putExtra("data", response);
        startActivityForResult(intent, REQUEST_SLOGAN);
    }

    public void socialNotice(View view) {
        if (!contentEnable || isInEditStatus) {
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

    public void QRCode(View View) {
        if (!contentEnable || isInEditStatus) {
            ToastUtils.showShort(R.string.cantdone);
            return;
        }
        Intent intent = new Intent(this, SocialQRCodeActivity.class);
        intent.putExtra("type", "3");
        intent.putExtra("data", response);
        startActivityForResult(intent, REQUEST_NOTICE);
    }

    public void openConversation(View view) {
        RongIM.getInstance().startGroupChat(this, response.getGroupId(), response.getName());
    }

    public void back(View view) {
        if (ivToolBarStart.getVisibility() != View.VISIBLE) {
            return;
        }
        finish();
    }

    @SuppressLint("CheckResult")
    public void menu(View view) {
        if (ivToolBarEnd.getVisibility() != View.VISIBLE) {
            return;
        }

        if (!contentEnable) {
            ToastUtils.showShort(R.string.cantdone);
            return;
        }

        ServiceFactory.getInstance().getBaseService(Api.class)
                .editListCommunityCulture(groupId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(SocialHomeActivity.this)))
                .subscribe(r -> {
                    isInEditStatus = true;
                    ivToolBarEnd.setVisibility(View.GONE);
                    ivToolBarStart.setVisibility(View.GONE);
                    tvSocialCode.setVisibility(View.GONE);
                    tvTitle.setText(R.string.edit_social_calture);
                    tvTitle.setVisibility(View.VISIBLE);
                    socialCalturePage.change2Edit(r);
                    app_bar.setExpanded(false, true);
                    ivOpenConversation.animate().translationXBy(ivOpenConversation.getWidth()).start();
                }, this::handleApiError);
    }

    public void fakeClick(View view) {
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
                case REQUEST_SOCIALNAME:
                    response.setName(data.getStringExtra("name"));
                    tvSocialName.setText(response.getName());
                case REQUEST_LOGO:
                    String logo = data.getStringExtra("url");
                    response.setLogo(logo);
                    GlideUtil.loadNormalImg(ivHead, logo);
                    break;
                case REQUEST_BG:
                    String bg = data.getStringExtra("url");
                    response.setBgi(bg);
                    GlideUtil.loadNormalImg(ivBg, bg);
                    break;
            }
        }
    }
}
