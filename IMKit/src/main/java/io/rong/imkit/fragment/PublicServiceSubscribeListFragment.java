//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.model.Event.PublicServiceFollowableEvent;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.utilities.OptionsPopupDialog.OnOptionsItemClickedListener;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.adapter.BaseAdapter;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.OperationCallback;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.PublicServiceProfileList;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Conversation.PublicServiceType;
import java.util.Iterator;

public class PublicServiceSubscribeListFragment extends DispatchResultFragment {
  private ListView mListView;
  private PublicServiceSubscribeListFragment.PublicServiceListAdapter mAdapter;

  public PublicServiceSubscribeListFragment() {
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(layout.rc_fr_public_service_sub_list, container, false);
    return view;
  }

  public void onViewCreated(View view, Bundle savedInstanceState) {
    this.mListView = (ListView)view.findViewById(id.rc_list);
    this.mListView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PublicServiceProfile info = PublicServiceSubscribeListFragment.this.mAdapter.getItem(position);
        RongIM.getInstance().startConversation(PublicServiceSubscribeListFragment.this.getActivity(), info.getConversationType(), info.getTargetId(), info.getName());
      }
    });
    this.mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
      public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        String[] item = new String[1];
        final PublicServiceProfile info = PublicServiceSubscribeListFragment.this.mAdapter.getItem(position);
        if (info.getConversationType() == ConversationType.PUBLIC_SERVICE) {
          item[0] = PublicServiceSubscribeListFragment.this.getActivity().getString(string.rc_pub_service_info_unfollow);
          OptionsPopupDialog.newInstance(view.getContext(), item).setOptionsPopupDialogListener(new OnOptionsItemClickedListener() {
            public void onOptionsItemClicked(int which) {
              PublicServiceType publicServiceType = null;
              if (info.getConversationType() == ConversationType.APP_PUBLIC_SERVICE) {
                publicServiceType = PublicServiceType.APP_PUBLIC_SERVICE;
              } else if (info.getConversationType() == ConversationType.PUBLIC_SERVICE) {
                publicServiceType = PublicServiceType.PUBLIC_SERVICE;
              } else {
                System.err.print("the public service type is error!!");
              }

              RongIMClient.getInstance().unsubscribePublicService(publicServiceType, info.getTargetId(), new OperationCallback() {
                public void onSuccess() {
                  PublicServiceSubscribeListFragment.this.mAdapter.remove(position);
                  PublicServiceSubscribeListFragment.this.mAdapter.notifyDataSetChanged();
                }

                public void onError(ErrorCode errorCode) {
                }
              });
            }
          }).show();
        }

        return true;
      }
    });
    this.mAdapter = new PublicServiceSubscribeListFragment.PublicServiceListAdapter(this.getActivity());
    this.mListView.setAdapter(this.mAdapter);
    this.getDBData();
    RongContext.getInstance().getEventBus().register(this);
  }

  private void getDBData() {
    RongIM.getInstance().getPublicServiceList(new ResultCallback<PublicServiceProfileList>() {
      public void onSuccess(PublicServiceProfileList infoList) {
        Iterator var2 = infoList.getPublicServiceData().iterator();

        while(var2.hasNext()) {
          PublicServiceProfile info = (PublicServiceProfile)var2.next();
          RongUserInfoManager.getInstance().setPublicServiceProfile(info);
        }

        PublicServiceSubscribeListFragment.this.mAdapter.clear();
        PublicServiceSubscribeListFragment.this.mAdapter.addCollection(infoList.getPublicServiceData());
        PublicServiceSubscribeListFragment.this.mAdapter.notifyDataSetChanged();
      }

      public void onError(ErrorCode e) {
      }
    });
  }

  protected void initFragment(Uri uri) {
  }

  public boolean handleMessage(Message msg) {
    return false;
  }

  public void onEvent(PublicServiceFollowableEvent event) {
    if (event != null) {
      this.getDBData();
    }

  }

  public void onDestroyView() {
    RongContext.getInstance().getEventBus().unregister(this);
    super.onDestroyView();
  }

  private class PublicServiceListAdapter extends BaseAdapter<PublicServiceProfile> {
    LayoutInflater mInflater;

    public PublicServiceListAdapter(Context context) {
      this.mInflater = LayoutInflater.from(context);
    }

    protected View newView(Context context, int position, ViewGroup group) {
      View view = this.mInflater.inflate(layout.rc_item_public_service_list, (ViewGroup)null);
      PublicServiceSubscribeListFragment.PublicServiceListAdapter.ViewHolder viewHolder = new PublicServiceSubscribeListFragment.PublicServiceListAdapter.ViewHolder();
      viewHolder.portrait = (AsyncImageView)view.findViewById(id.portrait);
      viewHolder.name = (TextView)view.findViewById(id.name);
      viewHolder.introduction = (TextView)view.findViewById(id.introduction);
      view.setTag(viewHolder);
      return view;
    }

    protected void bindView(View v, int position, PublicServiceProfile data) {
      PublicServiceSubscribeListFragment.PublicServiceListAdapter.ViewHolder viewHolder = (PublicServiceSubscribeListFragment.PublicServiceListAdapter.ViewHolder)v.getTag();
      if (data != null) {
        viewHolder.portrait.setResource(data.getPortraitUri());
        viewHolder.name.setText(data.getName());
        viewHolder.introduction.setText(data.getIntroduction());
      }

    }

    public int getCount() {
      return super.getCount();
    }

    public PublicServiceProfile getItem(int position) {
      return (PublicServiceProfile)super.getItem(position);
    }

    public long getItemId(int position) {
      return 0L;
    }

    class ViewHolder {
      AsyncImageView portrait;
      TextView name;
      TextView introduction;

      ViewHolder() {
      }
    }
  }
}
