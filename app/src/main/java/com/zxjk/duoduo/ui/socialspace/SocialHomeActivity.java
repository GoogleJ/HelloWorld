package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AnimationUtils;
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

    private ViewStub viewStubPay;
    private ViewStub viewStubFree;
    private boolean contentEnable;
    private boolean hasInitTop;
    private boolean isInEditStatus;

    private QuickPopup menuPop;

    private CommunityInfoResponse response;

    private SocialCalturePage socialCalturePage;
    private DynamicsPage dynamicsPage;

    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTrasnferStatusBar(true);
        BarUtils.setStatusBarLightMode(this, true);
        setContentView(R.layout.activity_social_home);

        initView();

        groupId = getIntent().getStringExtra("id");

        initFragment();

        maxMemVisiableItem = (ScreenUtils.getScreenWidth() - CommonUtils.dip2px(this, 64)) / CommonUtils.dip2px(this, 48);

        ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
        layoutParams.height = (int) (ScreenUtils.getScreenWidth() * 0.75);
        ivBg.setLayoutParams(layoutParams);
        ivBg.setImageResource(R.drawable.bg_default_social);

        initData();

        setToolBarMarginTop();

        setSocialBackgroundHeight();

        setSupportActionBar(toolbar);

        onAppBarScroll();

        initIndicator();

        initPager();
    }

    private void initFragment() {
        socialCalturePage = new SocialCalturePage(groupId);
        dynamicsPage = new DynamicsPage(groupId);

        socialCalturePage.setDoneAction(l -> {
            isInEditStatus = false;
            tvSocialId.setVisibility(View.VISIBLE);
            app_bar.setExpanded(true, true);
            tvTitle.setText(response.getName());
            ivToolBarStart.setVisibility(View.VISIBLE);
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
                            banAppBarScroll(false);

                            indicatorTop.setVisibility(View.GONE);
                            contentEnable = false;
                            llSocialNotice.setVisibility(View.GONE);
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
                                                    flushAfterEnter();
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
                                                    flushAfterEnter();
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
                .map(r -> {
                    runOnUiThread(() -> {
                        response = r;
                        tvSlogan.setText("社群简介:" + r.getIntroduction());
                        tvSocialId.setText("社群号:" + r.getCode());
                        tvSocialName.setText(r.getName());
                        GlideUtil.loadNormalImg(ivBg, r.getBgi());
                        GlideUtil.loadNormalImg(ivHead, r.getLogo());
                        tvTitle.setText(r.getName());
                        tvSocialCode.setText("社群号:" + r.getCode());
                        tvNotice.setText(r.getAnnouncement());

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
                    } else if (r.getIdentity().equals("1") || r.getIdentity().equals("2")) {
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
                    } else {
                        ArrayList<CommunityInfoResponse.MembersBean> list = new ArrayList<>(r.getMembers());
                        if (r.getMembers().size() > maxMemVisiableItem) {
                            List<CommunityInfoResponse.MembersBean> membersBeans = list.subList(0, maxMemVisiableItem);
                            return membersBeans;
                        } else {
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

    private void flushAfterEnter() {
        appbarHeight = 0;
        banAppBarScroll(true);
        indicatorTop.setVisibility(View.VISIBLE);
        llSocialNotice.setVisibility(View.VISIBLE);
        pagerOut.setVisibility(View.VISIBLE);
        ivOpenConversation.setVisibility(View.VISIBLE);
        CommunityInfoResponse.MembersBean membersBean = new CommunityInfoResponse.MembersBean();
        membersBean.setHeadPortrait(Constant.currentUser.getHeadPortrait());
        ArrayList<CommunityInfoResponse.MembersBean> list = new ArrayList<>(response.getMembers());
        list.add(membersBean);
        response.setMembers(list);
        response.setIdentity("0");
        if (response.getMembers().size() >= maxMemVisiableItem) {
            List<CommunityInfoResponse.MembersBean> membersBeans = list.subList(0, maxMemVisiableItem - 1);
            membersBeans.add(new CommunityInfoResponse.MembersBean());
            initAdapterForSocialMem(membersBeans);
        } else {
            list.add(new CommunityInfoResponse.MembersBean());
            initAdapterForSocialMem(list);
        }
        contentEnable = true;
        InformationNotificationMessage notificationMessage = InformationNotificationMessage.obtain("\"" +
                Constant.currentUser.getNick() + "\"加入了群组");
        Message message = Message.obtain(groupId, Conversation.ConversationType.GROUP, notificationMessage);
        RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
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
                        });
                    } else {
                        Glide.with(SocialHomeActivity.this).load(item.getHeadPortrait()).into(ivMemberHead);
                        ivMemberHead.setOnClickListener(v -> {
                            if (!contentEnable || isInEditStatus) {
                                ToastUtils.showShort(R.string.cantdone);
                                return;
                            }
                        });
                    }
                } else if (response.getIdentity().equals("1") || response.getIdentity().equals("2")) {
                    if (helper.getAdapterPosition() == data.size() - 1) {
                        ivMemberHead.setImageResource(R.drawable.ic_social_member_remove);
                        ivMemberHead.setOnClickListener(v -> {
                            if (!contentEnable || isInEditStatus) {
                                ToastUtils.showShort(R.string.cantdone);
                                return;
                            }
                            Intent intent = new Intent(SocialHomeActivity.this, CreateGroupActivity.class);
                            intent.putExtra("eventType", 3);
                            intent.putExtra("groupId", response.getGroupId());
                            intent.putExtra("fromSocial", true);
                            startActivity(intent);
                        });
                    } else if (helper.getAdapterPosition() == data.size() - 2) {
                        ivMemberHead.setImageResource(R.drawable.ic_social_member_add);
                        ivMemberHead.setOnClickListener(v -> {
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
                        });
                    } else {
                        Glide.with(SocialHomeActivity.this).load(item.getHeadPortrait()).into(ivMemberHead);
                        ivMemberHead.setOnClickListener(v -> {
                            if (!contentEnable || isInEditStatus) {
                                ToastUtils.showShort(R.string.cantdone);
                                return;
                            }
                        });
                    }
                } else {
                    Glide.with(SocialHomeActivity.this).load(item.getHeadPortrait()).into(ivMemberHead);
                    ivMemberHead.setOnClickListener(v -> {
                        if (!contentEnable || isInEditStatus) {
                            ToastUtils.showShort(R.string.cantdone);
                            return;
                        }
                    });
                }
            }
        };
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
                if (ivToolBarEnd.getVisibility() == View.GONE) {
                    ivToolBarEnd.setVisibility(View.VISIBLE);
                    ivToolBarStart.setImageResource(R.drawable.ic_social_back);
                }
                if (ivToolBarStart.getVisibility() == View.GONE) {
                    ivToolBarStart.setVisibility(View.VISIBLE);
                }
                if (tvTitle.getVisibility() == View.VISIBLE) {
                    tvTitle.setVisibility(View.INVISIBLE);
                    tvSocialCode.setVisibility(View.INVISIBLE);
                }
            } else if (absOffset < totalScrollRange) {
                if (ivToolBarEnd.getVisibility() == View.VISIBLE) {
                    ivToolBarEnd.setVisibility(View.GONE);
                    ivToolBarStart.setVisibility(View.GONE);
                }
                if (tvTitle.getVisibility() == View.VISIBLE) {
                    tvTitle.setVisibility(View.INVISIBLE);
                    tvSocialCode.setVisibility(View.INVISIBLE);
                }
            } else if (absOffset == totalScrollRange) {
                if (tvTitle.getVisibility() == View.INVISIBLE) {
                    tvTitle.setVisibility(View.VISIBLE);
                    tvSocialCode.setVisibility(View.VISIBLE);
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
        if (!contentEnable) {
            ToastUtils.showShort(R.string.cantdone);
            return;
        }
        if (menuPop == null) {
            menuPop = QuickPopupBuilder.with(this)
                    .contentView(R.layout.pop_social_top)
                    .config(new QuickPopupConfig()
                            .backgroundColor(android.R.color.transparent)
                            .gravity(Gravity.BOTTOM | Gravity.END)
                            .withShowAnimation(AnimationUtils.loadAnimation(this, R.anim.push_scale_in))
                            .withDismissAnimation(AnimationUtils.loadAnimation(this, R.anim.push_scale_out))
                            .withClick(R.id.ic_social_end_pop1, child -> ToastUtils.showShort(R.string.developing), true)
                            .withClick(R.id.ic_social_end_pop2, child -> {
                                if (response == null) return;
                                Intent intent = new Intent(this, SocialManageActivity.class);
                                intent.putExtra("data", response);
                                startActivityForResult(intent, REQUEST_SOCIALNAME);
                            }, true)
                            .withClick(R.id.ic_social_end_pop3, v -> ServiceFactory.getInstance().getBaseService(Api.class)
                                    .editListCommunityCulture(groupId)
                                    .compose(bindToLifecycle())
                                    .compose(RxSchedulers.normalTrans())
                                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(SocialHomeActivity.this)))
                                    .subscribe(r -> {
                                        isInEditStatus = true;
                                        ivToolBarEnd.setVisibility(View.GONE);
                                        tvSocialId.setVisibility(View.GONE);
                                        ivToolBarStart.setVisibility(View.GONE);
                                        tvTitle.setText(R.string.edit_social_calture);
                                        tvTitle.setVisibility(View.VISIBLE);
                                        socialCalturePage.change2Edit(r);
                                        app_bar.setExpanded(false, true);
                                        ivOpenConversation.animate().translationXBy(ivOpenConversation.getWidth()).start();
                                    }, this::handleApiError), true))
                    .build();
        }

        menuPop.showPopupWindow(view);
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
                    tvSlogan.setText("社群简介:" + response.getIntroduction());
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
