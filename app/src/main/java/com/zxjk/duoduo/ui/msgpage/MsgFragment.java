package com.zxjk.duoduo.ui.msgpage;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.BarUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.ui.socialspace.CreateSocialActivity1;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.Conversation;

public class MsgFragment extends BaseFragment implements View.OnClickListener {
    private int[] rootViews = {R.id.ll_plus_menu1, R.id.ll_plus_menu2, R.id.ll_plus_menu3};
    private int[] menuTv = {R.id.tv_plus_menu1, R.id.tv_plus_menu2, R.id.tv_plus_menu3};
    private int[] menuImg = {R.id.img_plus_menu1, R.id.img_plus_menu2, R.id.img_plus_menu3};
    private List<LinearLayout> rootViewList = new ArrayList<>(3);
    private List<TextView> menuTvList = new ArrayList<>(3);
    private List<ImageView> menuImgList = new ArrayList<>(3);
    private boolean menuOpenFlag = true;
    private int dp1;
    private int dp2;
    private long menuAnimTime = 200;

    private ImageView imgPlus;
    private ImageView ivScan;
    private ImageView ivHead;
    private CusConversationListFragment listFragment;

    private OnHeadClick onHeadClick;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message, container, false);

        initView();

        dp1 = CommonUtils.dip2px(getContext(), 56);
        dp2 = CommonUtils.dip2px(getContext(), 56 + 24);

        initOutLine();

        createConversationList();

        return rootView;
    }

    private void initView() {
        View topmask = rootView.findViewById(R.id.topmask);
        ViewGroup.LayoutParams layoutParams = topmask.getLayoutParams();
        layoutParams.height = BarUtils.getStatusBarHeight();
        topmask.setLayoutParams(layoutParams);

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        listFragment = (CusConversationListFragment) fragments.get(0);
        ivHead = rootView.findViewById(R.id.ivHead);
        ivScan = rootView.findViewById(R.id.ivScan);

        getPermisson(ivScan, granted -> {
            if (granted) startActivity(new Intent(getActivity(), QrCodeActivity.class));
        }, Manifest.permission.CAMERA);

        ivHead.setOnClickListener(v -> {
            if (onHeadClick != null) {
                onHeadClick.onClick();
            }
        });

        imgPlus = rootView.findViewById(R.id.img_plus);

        for (int view : rootViews) {
            rootViewList.add(rootView.findViewById(view));
        }

        for (int tv : menuTv) {
            menuTvList.add(rootView.findViewById(tv));
        }

        for (int img : menuImg) {
            menuImgList.add(rootView.findViewById(img));
        }

        rootView.findViewById(R.id.llRoot).setOnClickListener(v -> {
            if (menuOpenFlag) {
                start();
            } else {
                close(null);
            }
        });
    }

    private void createConversationList() {
        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")
                .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "true")
                .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "true")
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")
                .build();
        listFragment.setUri(uri);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_plus_menu1:
                close(new Intent(getContext(), CreateSocialActivity1.class));
                break;
            case R.id.img_plus_menu2:
                close(new Intent(getContext(), NewFriendActivity.class));
                break;
            case R.id.img_plus_menu3:
                close(new Intent(getContext(), SearchGroupActivity.class));
                break;
        }
    }

    public void setOnHeadClick(OnHeadClick onHeadClick) {
        this.onHeadClick = onHeadClick;
    }

    private void initOutLine() {
        imgPlus.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, dp1, dp1);
            }
        });
        imgPlus.setClipToOutline(true);

        for (int i = 0; i < menuImgList.size(); i++) {
            menuImgList.get(i).setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, dp1, dp1);
                }
            });
            menuImgList.get(i).setClipToOutline(true);
            menuImgList.get(i).setOnClickListener(this);
        }
    }

    private void start() {
        startAlpha();
        startRotation();
        startTranslationXAnim();
        startScale();
        menuOpenFlag = !menuOpenFlag;
    }

    private void close(Intent target) {
        long originAnimTime = menuAnimTime;
        if (target != null) {
            menuAnimTime = 50;
        }
        closeAlpha(target);
        closeRotation();
        closeTranslationXAnim();
        closeScale();
        menuOpenFlag = !menuOpenFlag;
        menuAnimTime = originAnimTime;
    }

    private void startAlpha() {
        ObjectAnimator animator = ObjectAnimator.ofObject(imgPlus, "backgroundColor", new ArgbEvaluator(), 0xff0083BF, 0xff9EA0A4);
        animator.setDuration(menuAnimTime);
        animator.start();
    }

    private void closeAlpha(Intent target) {
        ObjectAnimator animator = ObjectAnimator.ofObject(imgPlus, "backgroundColor", new ArgbEvaluator(), 0xff9EA0A4, 0xff0083BF);
        animator.setDuration(menuAnimTime);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (null != target) {
                    startActivity(target);
                }
            }
        });
        animator.start();
    }

    private void startRotation() {
        ViewPropertyAnimator rotationAnim = imgPlus.animate().rotationBy(45f);
        rotationAnim.setDuration(menuAnimTime);
        rotationAnim.start();
    }

    private void closeRotation() {
        ViewPropertyAnimator rotationAnim = imgPlus.animate().rotation(0);
        rotationAnim.setDuration(menuAnimTime);
        rotationAnim.start();
    }

    private void startTranslationXAnim() {
        for (int i = 0; i < rootViews.length; i++) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rootViewList.get(i), "translationX", -(i + 1) * dp2);
            objectAnimator.setDuration(menuAnimTime);
            objectAnimator.start();
        }
    }

    private void closeTranslationXAnim() {
        for (int i = rootViews.length; i > 0; i--) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rootViewList.get(i - 1), "translationX", 0);
            objectAnimator.setDuration(menuAnimTime);
            objectAnimator.start();
        }
    }

    private void startScale() {
        for (int i = 0; i < rootViews.length; i++) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rootViewList.get(i), "scaleX", 1f);
            objectAnimator.setDuration(menuAnimTime);
            objectAnimator.start();
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(rootViewList.get(i), "scaleY", 1f);
            objectAnimator2.setDuration(menuAnimTime);
            objectAnimator2.start();
        }
    }

    private void closeScale() {
        for (int i = 0; i < rootViews.length; i++) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rootViewList.get(i), "scaleX", 0f);
            objectAnimator.setDuration(menuAnimTime);
            objectAnimator.start();
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(rootViewList.get(i), "scaleY", 0f);
            objectAnimator2.setDuration(menuAnimTime);
            objectAnimator2.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        GlideUtil.loadCircleImg(ivHead, Constant.currentUser.getHeadPortrait());
    }

    public interface OnHeadClick {
        void onClick();
    }
}
