//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;

import io.rong.common.RLog;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.PublicServiceBehaviorListener;
import io.rong.imkit.model.Event.PublicServiceFollowableEvent;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.LoadingDialogFragment;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.OperationCallback;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Conversation.PublicServiceType;
import io.rong.imlib.model.PublicServiceProfile;

public class PublicServiceProfileFragment extends DispatchResultFragment {
    public static final String AGS_PUBLIC_ACCOUNT_INFO = "arg_public_account_info";
    PublicServiceProfile mPublicAccountInfo;
    private AsyncImageView mPortraitIV;
    private TextView mNameTV;
    private TextView mAccountTV;
    private TextView mDescriptionTV;
    private Button mEnterBtn;
    private Button mFollowBtn;
    private Button mUnfollowBtn;
    private String mTargetId;
    private ConversationType mConversationType;
    private String name;
    private LoadingDialogFragment mLoadingDialogFragment;

    public PublicServiceProfileFragment() {
    }

    protected void initFragment(Uri uri) {
        if (this.getActivity().getIntent() != null) {
            this.mPublicAccountInfo = (PublicServiceProfile) this.getActivity().getIntent().getParcelableExtra("arg_public_account_info");
        }

        if (uri != null) {
            if (this.mPublicAccountInfo == null) {
                String typeStr = !TextUtils.isEmpty(uri.getLastPathSegment()) ? uri.getLastPathSegment().toUpperCase(Locale.US) : "";
                this.mConversationType = ConversationType.valueOf(typeStr);
                this.mTargetId = uri.getQueryParameter("targetId");
                this.name = uri.getQueryParameter("name");
            } else {
                this.mConversationType = this.mPublicAccountInfo.getConversationType();
                this.mTargetId = this.mPublicAccountInfo.getTargetId();
                this.name = this.mPublicAccountInfo.getName();
            }
        }

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.rc_fr_public_service_inf, container, false);
        this.mPortraitIV = (AsyncImageView) view.findViewById(id.portrait);
        this.mNameTV = (TextView) view.findViewById(id.name);
        this.mAccountTV = (TextView) view.findViewById(id.account);
        this.mDescriptionTV = (TextView) view.findViewById(id.description);
        this.mEnterBtn = (Button) view.findViewById(id.enter);
        this.mFollowBtn = (Button) view.findViewById(id.follow);
        this.mUnfollowBtn = (Button) view.findViewById(id.unfollow);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mLoadingDialogFragment = LoadingDialogFragment.newInstance("", this.getResources().getString(string.rc_notice_data_is_loading));
        if (this.mPublicAccountInfo != null) {
            this.initData(this.mPublicAccountInfo);
        } else if (!TextUtils.isEmpty(this.mTargetId)) {
            PublicServiceType publicServiceType = null;
            if (this.mConversationType == ConversationType.APP_PUBLIC_SERVICE) {
                publicServiceType = PublicServiceType.APP_PUBLIC_SERVICE;
            } else if (this.mConversationType == ConversationType.PUBLIC_SERVICE) {
                publicServiceType = PublicServiceType.PUBLIC_SERVICE;
            } else {
                System.err.print("the public service type is error!!");
            }

            RongIM.getInstance().getPublicServiceProfile(publicServiceType, this.mTargetId, new ResultCallback<PublicServiceProfile>() {
                public void onSuccess(PublicServiceProfile info) {
                    if (info != null) {
                        PublicServiceProfileFragment.this.initData(info);
                        RongUserInfoManager.getInstance().setPublicServiceProfile(info);
                    }

                }

                public void onError(ErrorCode e) {
                    RLog.e("PublicServiceProfileFragment", "Failure to get data!!!");
                }
            });
        }

    }

    private void initData(final PublicServiceProfile info) {
        if (info != null) {
            this.mPortraitIV.setResource(info.getPortraitUri());
            this.mNameTV.setText(info.getName());
            this.mAccountTV.setText(String.format(this.getResources().getString(string.rc_pub_service_info_account), info.getTargetId()));
            this.mDescriptionTV.setText(info.getIntroduction());
            boolean isFollow = info.isFollow();
            boolean isGlobal = info.isGlobal();
            FragmentTransaction ft;
            if (isGlobal) {
                ft = this.getFragmentManager().beginTransaction();
                ft.add(id.rc_layout, SetConversationNotificationFragment.newInstance());
                ft.commitAllowingStateLoss();
                this.mFollowBtn.setVisibility(8);
                this.mEnterBtn.setVisibility(0);
                this.mUnfollowBtn.setVisibility(8);
            } else if (isFollow) {
                ft = this.getFragmentManager().beginTransaction();
                ft.add(id.rc_layout, SetConversationNotificationFragment.newInstance());
                ft.commitAllowingStateLoss();
                this.mFollowBtn.setVisibility(8);
                this.mEnterBtn.setVisibility(0);
                this.mUnfollowBtn.setVisibility(0);
            } else {
                this.mFollowBtn.setVisibility(0);
                this.mEnterBtn.setVisibility(8);
                this.mUnfollowBtn.setVisibility(8);
            }

            this.mEnterBtn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    PublicServiceBehaviorListener listener = RongContext.getInstance().getPublicServiceBehaviorListener();
                    if (listener == null || !listener.onEnterConversationClick(v.getContext(), info)) {
                        PublicServiceProfileFragment.this.getActivity().finish();
                        RongIM.getInstance().startConversation(PublicServiceProfileFragment.this.getActivity(), info.getConversationType(), info.getTargetId(), info.getName());
                    }
                }
            });
            this.mFollowBtn.setOnClickListener(new OnClickListener() {
                public void onClick(final View v) {
                    PublicServiceType publicServiceType = null;
                    if (PublicServiceProfileFragment.this.mConversationType == ConversationType.APP_PUBLIC_SERVICE) {
                        publicServiceType = PublicServiceType.APP_PUBLIC_SERVICE;
                    } else if (PublicServiceProfileFragment.this.mConversationType == ConversationType.PUBLIC_SERVICE) {
                        publicServiceType = PublicServiceType.PUBLIC_SERVICE;
                    } else {
                        System.err.print("the public service type is error!!");
                    }

                    RongIM.getInstance().subscribePublicService(publicServiceType, info.getTargetId(), new OperationCallback() {
                        public void onSuccess() {
                            PublicServiceProfileFragment.this.mLoadingDialogFragment.dismiss();
                            PublicServiceProfileFragment.this.mFollowBtn.setVisibility(8);
                            PublicServiceProfileFragment.this.mEnterBtn.setVisibility(0);
                            PublicServiceProfileFragment.this.mUnfollowBtn.setVisibility(0);
                            RongUserInfoManager.getInstance().setPublicServiceProfile(info);
                            RongContext.getInstance().getEventBus().post(PublicServiceFollowableEvent.obtain(info.getTargetId(), info.getConversationType(), true));
                            PublicServiceBehaviorListener listener = RongContext.getInstance().getPublicServiceBehaviorListener();
                            if (listener == null || !listener.onFollowClick(v.getContext(), info)) {
                                PublicServiceProfileFragment.this.getActivity().finish();
                                RongIM.getInstance().startConversation(PublicServiceProfileFragment.this.getActivity(), info.getConversationType(), info.getTargetId(), info.getName());
                            }
                        }

                        public void onError(ErrorCode errorCode) {
                            PublicServiceProfileFragment.this.mLoadingDialogFragment.dismiss();
                        }
                    });
                    PublicServiceProfileFragment.this.mLoadingDialogFragment.show(PublicServiceProfileFragment.this.getFragmentManager());
                }
            });
            this.mUnfollowBtn.setOnClickListener(new OnClickListener() {
                public void onClick(final View v) {
                    PublicServiceType publicServiceType = null;
                    if (PublicServiceProfileFragment.this.mConversationType == ConversationType.APP_PUBLIC_SERVICE) {
                        publicServiceType = PublicServiceType.APP_PUBLIC_SERVICE;
                    } else if (PublicServiceProfileFragment.this.mConversationType == ConversationType.PUBLIC_SERVICE) {
                        publicServiceType = PublicServiceType.PUBLIC_SERVICE;
                    } else {
                        System.err.print("the public service type is error!!");
                    }

                    RongIM.getInstance().unsubscribePublicService(publicServiceType, info.getTargetId(), new OperationCallback() {
                        public void onSuccess() {
                            PublicServiceProfileFragment.this.mFollowBtn.setVisibility(0);
                            PublicServiceProfileFragment.this.mEnterBtn.setVisibility(8);
                            PublicServiceProfileFragment.this.mUnfollowBtn.setVisibility(8);
                            RongContext.getInstance().getEventBus().post(PublicServiceFollowableEvent.obtain(info.getTargetId(), info.getConversationType(), false));
                            PublicServiceBehaviorListener listener = RongContext.getInstance().getPublicServiceBehaviorListener();
                            if (listener == null || !listener.onUnFollowClick(v.getContext(), info)) {
                                PublicServiceProfileFragment.this.getActivity().finish();
                            }
                        }

                        public void onError(ErrorCode errorCode) {
                        }
                    });
                }
            });
        }

    }

    public boolean handleMessage(Message msg) {
        return false;
    }
}
