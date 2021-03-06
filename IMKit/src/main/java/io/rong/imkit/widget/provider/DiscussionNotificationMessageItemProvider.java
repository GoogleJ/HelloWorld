//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.rong.common.RLog;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utils.MessageProviderUserInfoHelper;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.imlib.model.UserInfo;
import io.rong.message.DiscussionNotificationMessage;

@ProviderTag(
        messageContent = DiscussionNotificationMessage.class,
        showPortrait = false,
        centerInHorizontal = true,
        showSummaryWithName = false
)
public class DiscussionNotificationMessageItemProvider extends MessageProvider<DiscussionNotificationMessage> {
    private static final String TAG = "DiscussionNotificationMessageItemProvider";
    private static final int DISCUSSION_ADD_MEMBER = 1;
    private static final int DISCUSSION_EXIT = 2;
    private static final int DISCUSSION_RENAME = 3;
    private static final int DISCUSSION_REMOVE = 4;
    private static final int DISCUSSION_MEMBER_INVITE = 5;

    public DiscussionNotificationMessageItemProvider() {
        RongContext.getInstance().getEventBus().register(this);
    }

    public void bindView(View v, int position, DiscussionNotificationMessage content, UIMessage message) {
        DiscussionNotificationMessageItemProvider.ViewHolder viewHolder = (DiscussionNotificationMessageItemProvider.ViewHolder) v.getTag();
        Spannable spannable = this.getContentSummary(v.getContext(), content);
        if (spannable != null && spannable.length() > 0) {
            viewHolder.contentTextView.setVisibility(0);
            viewHolder.contentTextView.setText(spannable);
        } else {
            viewHolder.contentTextView.setVisibility(8);
        }

    }

    public Spannable getContentSummary(DiscussionNotificationMessage data) {
        return null;
    }

    public Spannable getContentSummary(Context context, DiscussionNotificationMessage data) {
        if (data == null) {
            RLog.e("DiscussionNotificationMessageItemProvider", "getContentSummary DiscussionNotificationMessage is null;");
            return new SpannableString("");
        } else {
            RLog.i("DiscussionNotificationMessageItemProvider", "getContentSummary call getContentSummary()  method ");
            return new SpannableString(this.getWrapContent(RongContext.getInstance(), data));
        }
    }

    public void onItemClick(View view, int position, DiscussionNotificationMessage content, UIMessage message) {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(layout.rc_item_discussion_notification_message, (ViewGroup) null);
        DiscussionNotificationMessageItemProvider.ViewHolder viewHolder = new DiscussionNotificationMessageItemProvider.ViewHolder();
        viewHolder.contentTextView = (TextView) view.findViewById(id.rc_msg);
        viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        view.setTag(viewHolder);
        return view;
    }

    private final String getWrapContent(Context context, DiscussionNotificationMessage discussionNotificationMessage) {
        if (discussionNotificationMessage == null) {
            return "";
        } else {
            String[] operatedUserIds = null;
            String extension = discussionNotificationMessage.getExtension();
            String operatorId = discussionNotificationMessage.getOperator();
            String currentUserId = "";
            String content = "";
            int operatedUserIdsLength = 0;
            if (!TextUtils.isEmpty(extension)) {
                if (extension.indexOf(",") != -1) {
                    operatedUserIds = extension.split(",");
                } else {
                    operatedUserIds = new String[]{extension};
                }

                operatedUserIdsLength = operatedUserIds.length;
            }

            currentUserId = RongIM.getInstance().getCurrentUserId();
            if (TextUtils.isEmpty(currentUserId)) {
                return "";
            } else {
                int operatorType = discussionNotificationMessage.getType();
                UserInfo operator;
                String operatedUserId;
                String formatString;
                String openFormat;
                UserInfo userInfo;
                switch (operatorType) {
                    case 1:
                        if (operatedUserIds != null) {
                            String userId;
                            if (currentUserId.equals(operatorId)) {
                                if (operatedUserIdsLength == 1) {
                                    userId = operatedUserIds[0];
                                    userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
                                    if (userInfo != null) {
                                        formatString = context.getResources().getString(string.rc_discussion_nt_msg_for_you_added);
                                        content = String.format(formatString, userInfo.getName());
                                    } else {
                                        MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, userId);
                                    }
                                } else {
                                    userId = context.getResources().getString(string.rc_discussion_nt_msg_for_you_add);
                                    content = String.format(userId, operatedUserIdsLength);
                                }
                            } else if (operatedUserIdsLength == 1) {
                                userId = operatedUserIds[0];
                                userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
                                userInfo = RongUserInfoManager.getInstance().getUserInfo(operatorId);
                                if (userInfo != null && userInfo != null) {
                                    openFormat = context.getResources().getString(string.rc_discussion_nt_msg_for_added);
                                    content = String.format(openFormat, userInfo.getName(), userInfo.getName());
                                } else {
                                    if (userInfo == null) {
                                        MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, userId);
                                    }

                                    if (userInfo == null) {
                                        MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
                                    }
                                }
                            } else {
                                operator = RongUserInfoManager.getInstance().getUserInfo(operatorId);
                                if (operator != null) {
                                    operatedUserId = context.getResources().getString(string.rc_discussion_nt_msg_for_add);
                                    content = String.format(operatedUserId, operator.getName(), operatedUserIdsLength);
                                } else {
                                    MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
                                }
                            }
                        }
                        break;
                    case 2:
                        operator = RongUserInfoManager.getInstance().getUserInfo(operatorId);
                        if (operator != null) {
                            operatedUserId = context.getResources().getString(string.rc_discussion_nt_msg_for_exit);
                            content = String.format(operatedUserId, operator.getName());
                        } else {
                            MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
                        }
                        break;
                    case 3:
                        if (currentUserId.equals(operatorId)) {
                            operatedUserId = context.getResources().getString(string.rc_discussion_nt_msg_for_you_rename);
                            content = String.format(operatedUserId, extension);
                        } else {
                            userInfo = RongUserInfoManager.getInstance().getUserInfo(operatorId);
                            if (userInfo != null) {
                                formatString = context.getResources().getString(string.rc_discussion_nt_msg_for_rename);
                                content = String.format(formatString, userInfo.getName(), extension);
                            } else {
                                MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
                            }
                        }
                        break;
                    case 4:
                        if (operatedUserIds == null) {
                            return "";
                        }

                        operatedUserId = operatedUserIds[0];
                        if (currentUserId.equals(operatorId)) {
                            userInfo = RongUserInfoManager.getInstance().getUserInfo(operatedUserId);
                            if (userInfo != null) {
                                openFormat = context.getResources().getString(string.rc_discussion_nt_msg_for_you);
                                formatString = context.getResources().getString(string.rc_discussion_nt_msg_for_who_removed);
                                content = String.format(formatString, userInfo.getName(), openFormat);
                            } else {
                                MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
                            }
                        } else if (currentUserId.equals(operatedUserId)) {
                            userInfo = RongUserInfoManager.getInstance().getUserInfo(operatorId);
                            if (userInfo != null) {
                                openFormat = context.getResources().getString(string.rc_discussion_nt_msg_for_removed);
                                content = String.format(openFormat, userInfo.getName());
                            } else {
                                MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
                            }
                        } else {
                            userInfo = RongUserInfoManager.getInstance().getUserInfo(operatedUserId);
                            UserInfo operatorUserInfo = RongUserInfoManager.getInstance().getUserInfo(operatorId);
                            if (userInfo != null && operatorUserInfo != null) {
                                formatString = context.getResources().getString(string.rc_discussion_nt_msg_for_who_removed);
                                content = String.format(formatString, userInfo.getName(), operatorUserInfo.getName());
                            } else {
                                if (operatorUserInfo == null) {
                                    MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
                                }

                                if (userInfo == null) {
                                    MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatedUserId);
                                }
                            }
                        }
                        break;
                    case 5:
                        if (currentUserId.equals(operatorId)) {
                            if ("1".equals(extension)) {
                                formatString = context.getResources().getString(string.rc_discussion_nt_msg_for_you_is_open_invite_close);
                                content = String.format(formatString);
                            } else if ("0".equals(extension)) {
                                formatString = context.getResources().getString(string.rc_discussion_nt_msg_for_you_is_open_invite_open);
                                content = String.format(formatString);
                            }
                        } else {
                            userInfo = RongUserInfoManager.getInstance().getUserInfo(operatorId);
                            if (userInfo != null) {
                                if ("1".equals(extension)) {
                                    openFormat = context.getResources().getString(string.rc_discussion_nt_msg_for_is_open_invite_close);
                                    content = String.format(openFormat, userInfo.getName());
                                } else if ("0".equals(extension)) {
                                    openFormat = context.getResources().getString(string.rc_discussion_nt_msg_for_is_open_invite_open);
                                    content = String.format(openFormat, userInfo.getName());
                                }
                            } else {
                                MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
                            }
                        }
                        break;
                    default:
                        content = "";
                }

                RLog.i("DiscussionNotificationMessageItemProvider", "content return " + content);
                return content;
            }
        }
    }

    public void onEventMainThread(UserInfo userInfo) {
        if (userInfo.getName() != null) {
            if (MessageProviderUserInfoHelper.getInstance().isCacheUserId(userInfo.getUserId())) {
                MessageProviderUserInfoHelper.getInstance().notifyMessageUpdate(userInfo.getUserId());
            }

        }
    }

    private static class ViewHolder {
        TextView contentTextView;

        private ViewHolder() {
        }
    }
}