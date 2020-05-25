package io.rong.imkit;

import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.userInfoCache.IRongCacheListener;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UserInfo;

public class RongUserCacheListener
  implements IRongCacheListener
{
  public void onUserInfoUpdated(UserInfo info)
  {
    if (info != null)
      RongContext.getInstance().getEventBus().post(info);
  }

  public void onGroupUserInfoUpdated(GroupUserInfo info)
  {
    if (info != null)
      RongContext.getInstance().getEventBus().post(info);
  }

  public void onGroupUpdated(Group group)
  {
    if (group != null)
      RongContext.getInstance().getEventBus().post(group);
  }

  public void onDiscussionUpdated(Discussion discussion)
  {
    if (discussion != null)
      RongContext.getInstance().getEventBus().post(discussion);
  }

  public void onPublicServiceProfileUpdated(PublicServiceProfile profile)
  {
    if (profile != null)
      RongContext.getInstance().getEventBus().post(profile);
  }

  public UserInfo getUserInfo(String id)
  {
    if (RongContext.getInstance().getUserInfoProvider() != null)
      return RongContext.getInstance().getUserInfoProvider().getUserInfo(id);

    return null;
  }

  public GroupUserInfo getGroupUserInfo(String group, String id)
  {
    if (RongContext.getInstance().getGroupUserInfoProvider() != null)
      return RongContext.getInstance().getGroupUserInfoProvider().getGroupUserInfo(group, id);

    return null;
  }

  public Group getGroupInfo(String id)
  {
    if (RongContext.getInstance().getGroupInfoProvider() != null)
      return RongContext.getInstance().getGroupInfoProvider().getGroupInfo(id);

    return null;
  }
}