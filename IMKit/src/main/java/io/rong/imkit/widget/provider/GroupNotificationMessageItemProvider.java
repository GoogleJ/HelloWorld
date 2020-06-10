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
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.model.GroupNotificationMessageData;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.imlib.model.UserInfo;
import io.rong.message.GroupNotificationMessage;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

@ProviderTag(
        messageContent = GroupNotificationMessage.class,
        showPortrait = false,
        centerInHorizontal = true,
        showProgress = false,
        showSummaryWithName = false
)
public class GroupNotificationMessageItemProvider extends MessageProvider<GroupNotificationMessage> {
  private static final String TAG = GroupNotificationMessageItemProvider.class.getSimpleName();

  public GroupNotificationMessageItemProvider() {
  }

  public void bindView(View view, int i, GroupNotificationMessage groupNotificationMessage, UIMessage uiMessage) {
    GroupNotificationMessageItemProvider.ViewHolder viewHolder = (GroupNotificationMessageItemProvider.ViewHolder)view.getTag();

    try {
      if (groupNotificationMessage != null && uiMessage != null) {
        if (groupNotificationMessage != null && groupNotificationMessage.getData() == null) {
          return;
        }

        GroupNotificationMessageData data;
        try {
          data = this.jsonToBean(groupNotificationMessage.getData());
        } catch (Exception var22) {
          RLog.e(TAG, "bindView", var22);
          return;
        }

        String operation = groupNotificationMessage.getOperation();
        String operatorNickname = data.getOperatorNickname();
        String operatorUserId = groupNotificationMessage.getOperatorUserId();
        String currentUserId = RongIM.getInstance().getCurrentUserId();
        if (operatorNickname == null) {
          UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(operatorUserId);
          if (userInfo != null) {
            operatorNickname = userInfo.getName();
            if (operatorNickname == null) {
              operatorNickname = groupNotificationMessage.getOperatorUserId();
            }
          }
        }

        List<String> memberList = data.getTargetUserDisplayNames();
        List<String> memberIdList = data.getTargetUserIds();
        String memberName = null;
        String memberUserId = null;
        Context context = RongContext.getInstance();
        if (memberIdList != null && memberIdList.size() == 1) {
          memberUserId = (String)memberIdList.get(0);
        }

        String changeMsg;
        String groupName;
        if (memberList != null) {
          if (memberList.size() == 1) {
            memberName = (String)memberList.get(0);
          } else if (memberIdList.size() > 1) {
            StringBuilder sb = new StringBuilder();
            Iterator var17 = memberList.iterator();

            while(var17.hasNext()) {
              changeMsg = (String)var17.next();
              sb.append(changeMsg);
              sb.append(context.getString(string.rc_item_divided_string));
            }

            groupName = sb.toString();
            memberName = groupName.substring(0, groupName.length() - 1);
          }
        }

        if (!TextUtils.isEmpty(operation)) {
          if (operation.equals("Add")) {
            if (operatorUserId.equals(memberUserId)) {
              viewHolder.contentTextView.setText(memberName + context.getString(string.rc_item_join_group));
            } else {
              if (!groupNotificationMessage.getOperatorUserId().equals(RongIM.getInstance().getCurrentUserId())) {
                changeMsg = context.getString(string.rc_item_invitation, new Object[]{operatorNickname, memberName});
              } else {
                changeMsg = context.getString(string.rc_item_you_invitation, new Object[]{memberName});
              }

              viewHolder.contentTextView.setText(changeMsg);
            }
          } else if (operation.equals("Kicked")) {
            if (memberIdList != null) {
              Iterator var26 = memberIdList.iterator();

              while(var26.hasNext()) {
                String userId = (String)var26.next();
                if (currentUserId.equals(userId)) {
                  viewHolder.contentTextView.setText(context.getString(string.rc_item_you_remove_self, new Object[]{operatorNickname}));
                } else {
                  String removeMsg;
                  if (!operatorUserId.equals(currentUserId)) {
                    removeMsg = context.getString(string.rc_item_remove_group_member, new Object[]{operatorNickname, memberName});
                  } else {
                    removeMsg = context.getString(string.rc_item_you_remove_group_member, new Object[]{memberName});
                  }

                  viewHolder.contentTextView.setText(removeMsg);
                }
              }
            }
          } else if (operation.equals("Create")) {
            new GroupNotificationMessageData();

            try {
              this.jsonToBean(groupNotificationMessage.getData());
            } catch (Exception var21) {
              RLog.e(TAG, "bindView", var21);
              return;
            }

            if (!operatorUserId.equals(currentUserId)) {
              changeMsg = context.getString(string.rc_item_created_group, new Object[]{operatorNickname});
            } else {
              changeMsg = context.getString(string.rc_item_you_created_group);
            }

            viewHolder.contentTextView.setText(changeMsg);
          } else if (operation.equals("Dismiss")) {
            viewHolder.contentTextView.setText(operatorNickname + context.getString(string.rc_item_dismiss_groups));
          } else if (operation.equals("Quit")) {
            viewHolder.contentTextView.setText(operatorNickname + context.getString(string.rc_item_quit_groups));
          } else if (operation.equals("Rename")) {
            if (!operatorUserId.equals(currentUserId)) {
              groupName = data.getTargetGroupName();
              changeMsg = context.getString(string.rc_item_change_group_name, new Object[]{operatorNickname, groupName});
            } else {
              groupName = data.getTargetGroupName();
              changeMsg = context.getString(string.rc_item_you_change_group_name, new Object[]{groupName});
            }

            viewHolder.contentTextView.setText(changeMsg);
          }
        }
      }
    } catch (Exception var23) {
      RLog.e(TAG, "bindView", var23);
    }

  }

  public Spannable getContentSummary(GroupNotificationMessage groupNotificationMessage) {
    return null;
  }

  public Spannable getContentSummary(Context context, GroupNotificationMessage groupNotificationMessage) {
    try {
      if (groupNotificationMessage != null && groupNotificationMessage.getData() == null) {
        return null;
      } else {
        GroupNotificationMessageData data;
        try {
          data = this.jsonToBean(groupNotificationMessage.getData());
        } catch (Exception var19) {
          RLog.e(TAG, "getContentSummary", var19);
          return null;
        }

        String operation = groupNotificationMessage.getOperation();
        String operatorNickname = data.getOperatorNickname();
        String operatorUserId = groupNotificationMessage.getOperatorUserId();
        String currentUserId = RongIM.getInstance().getCurrentUserId();
        if (operatorNickname == null) {
          UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(operatorUserId);
          if (userInfo != null) {
            operatorNickname = userInfo.getName();
          }

          if (operatorNickname == null) {
            operatorNickname = groupNotificationMessage.getOperatorUserId();
          }
        }

        List<String> memberList = data.getTargetUserDisplayNames();
        List<String> memberIdList = data.getTargetUserIds();
        String memberName = null;
        String memberUserId = null;
        if (memberIdList != null && memberIdList.size() == 1) {
          memberUserId = (String)memberIdList.get(0);
        }

        String groupName;
        if (memberList != null) {
          if (memberList.size() == 1) {
            memberName = (String)memberList.get(0);
          } else if (memberIdList.size() > 1) {
            StringBuilder sb = new StringBuilder();
            Iterator var13 = memberList.iterator();

            while(var13.hasNext()) {
              groupName = (String)var13.next();
              sb.append(groupName);
              sb.append(context.getString(string.rc_item_divided_string));
            }

            String str = sb.toString();
            memberName = str.substring(0, str.length() - 1);
          }
        }

        SpannableString spannableStringSummary = new SpannableString("");
        String changeMsg;
        if (operation.equals("Add")) {
          try {
            if (operatorUserId.equals(memberUserId)) {
              spannableStringSummary = new SpannableString(operatorNickname + context.getString(string.rc_item_join_group));
            } else {
              if (!operatorUserId.equals(currentUserId)) {
                changeMsg = context.getString(string.rc_item_invitation, new Object[]{operatorNickname, memberName});
              } else {
                changeMsg = context.getString(string.rc_item_you_invitation, new Object[]{memberName});
              }

              spannableStringSummary = new SpannableString(changeMsg);
            }
          } catch (Exception var18) {
            RLog.e(TAG, "getContentSummary", var18);
          }
        } else if (operation.equals("Kicked")) {
          if (memberIdList != null) {
            Iterator var24 = memberIdList.iterator();

            while(var24.hasNext()) {
              String userId = (String)var24.next();
              if (currentUserId.equals(userId)) {
                spannableStringSummary = new SpannableString(context.getString(string.rc_item_you_remove_self, new Object[]{operatorNickname}));
              } else {
                String removeMsg;
                if (!operatorUserId.equals(currentUserId)) {
                  removeMsg = context.getString(string.rc_item_remove_group_member, new Object[]{operatorNickname, memberName});
                } else {
                  removeMsg = context.getString(string.rc_item_you_remove_group_member, new Object[]{memberName});
                }

                spannableStringSummary = new SpannableString(removeMsg);
              }
            }
          }
        } else if (operation.equals("Create")) {
          if (!operatorUserId.equals(currentUserId)) {
            groupName = context.getString(string.rc_item_created_group, new Object[]{operatorNickname});
          } else {
            groupName = context.getString(string.rc_item_you_created_group);
          }

          spannableStringSummary = new SpannableString(groupName);
        } else if (operation.equals("Dismiss")) {
          spannableStringSummary = new SpannableString(operatorNickname + context.getString(string.rc_item_dismiss_groups));
        } else if (operation.equals("Quit")) {
          spannableStringSummary = new SpannableString(operatorNickname + context.getString(string.rc_item_quit_groups));
        } else if (operation.equals("Rename")) {
          if (!operatorUserId.equals(currentUserId)) {
            groupName = data.getTargetGroupName();
            changeMsg = context.getString(string.rc_item_change_group_name, new Object[]{operatorNickname, groupName});
          } else {
            groupName = data.getTargetGroupName();
            changeMsg = context.getString(string.rc_item_you_change_group_name, new Object[]{groupName});
          }

          spannableStringSummary = new SpannableString(changeMsg);
        }

        return spannableStringSummary;
      }
    } catch (Exception var20) {
      RLog.e(TAG, "getContentSummary", var20);
      return new SpannableString(context.getString(string.rc_item_group_notification_summary));
    }
  }

  public void onItemClick(View view, int i, GroupNotificationMessage groupNotificationMessage, UIMessage uiMessage) {
  }

  public void onItemLongClick(View view, int i, GroupNotificationMessage groupNotificationMessage, UIMessage uiMessage) {
  }

  public View newView(Context context, ViewGroup viewGroup) {
    View view = LayoutInflater.from(context).inflate(layout.rc_item_group_information_notification_message, (ViewGroup)null);
    GroupNotificationMessageItemProvider.ViewHolder viewHolder = new GroupNotificationMessageItemProvider.ViewHolder();
    viewHolder.contentTextView = (TextView)view.findViewById(id.rc_msg);
    viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
    view.setTag(viewHolder);
    return view;
  }

  private GroupNotificationMessageData jsonToBean(String data) {
    GroupNotificationMessageData dataEntity = new GroupNotificationMessageData();

    try {
      JSONObject jsonObject = new JSONObject(data);
      if (jsonObject.has("operatorNickname")) {
        dataEntity.setOperatorNickname(jsonObject.getString("operatorNickname"));
      }

      if (jsonObject.has("targetGroupName")) {
        dataEntity.setTargetGroupName(jsonObject.getString("targetGroupName"));
      }

      if (jsonObject.has("timestamp")) {
        dataEntity.setTimestamp(jsonObject.getLong("timestamp"));
      }

      JSONArray jsonArray;
      int i;
      if (jsonObject.has("targetUserIds")) {
        jsonArray = jsonObject.getJSONArray("targetUserIds");

        for(i = 0; i < jsonArray.length(); ++i) {
          dataEntity.getTargetUserIds().add(jsonArray.getString(i));
        }
      }

      if (jsonObject.has("targetUserDisplayNames")) {
        jsonArray = jsonObject.getJSONArray("targetUserDisplayNames");

        for(i = 0; i < jsonArray.length(); ++i) {
          dataEntity.getTargetUserDisplayNames().add(jsonArray.getString(i));
        }
      }

      if (jsonObject.has("oldCreatorId")) {
        dataEntity.setOldCreatorId(jsonObject.getString("oldCreatorId"));
      }

      if (jsonObject.has("oldCreatorName")) {
        dataEntity.setOldCreatorName(jsonObject.getString("oldCreatorName"));
      }

      if (jsonObject.has("newCreatorId")) {
        dataEntity.setNewCreatorId(jsonObject.getString("newCreatorId"));
      }

      if (jsonObject.has("newCreatorName")) {
        dataEntity.setNewCreatorName(jsonObject.getString("newCreatorName"));
      }
    } catch (Exception var6) {
      RLog.e(TAG, "jsonToBean", var6);
    }

    return dataEntity;
  }

  private static class ViewHolder {
    TextView contentTextView;

    private ViewHolder() {
    }
  }
}