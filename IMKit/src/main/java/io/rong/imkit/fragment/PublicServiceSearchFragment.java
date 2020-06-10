//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.model.Event.PublicServiceFollowableEvent;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.LoadingDialogFragment;
import io.rong.imkit.widget.adapter.BaseAdapter;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.RongIMClient.SearchType;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.PublicServiceProfileList;

public class PublicServiceSearchFragment extends DispatchResultFragment {
  private static final String TAG = "PublicServiceSearchFragment";
  private EditText mEditText;
  private Button mSearchBtn;
  private ListView mListView;
  private PublicServiceSearchFragment.PublicServiceListAdapter mAdapter;
  private LoadingDialogFragment mLoadingDialogFragment;

  public PublicServiceSearchFragment() {
  }

  protected void initFragment(Uri uri) {
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(layout.rc_fr_public_service_search, container, false);
    this.mEditText = (EditText)view.findViewById(id.rc_search_ed);
    this.mSearchBtn = (Button)view.findViewById(id.rc_search_btn);
    this.mListView = (ListView)view.findViewById(id.rc_search_list);
    RongContext.getInstance().getEventBus().register(this);
    return view;
  }

  public boolean handleMessage(Message msg) {
    return false;
  }

  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.mLoadingDialogFragment = LoadingDialogFragment.newInstance("", this.getResources().getString(string.rc_notice_data_is_loading));
    this.mSearchBtn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        PublicServiceSearchFragment.this.mLoadingDialogFragment.show(PublicServiceSearchFragment.this.getFragmentManager());
        RongIM.getInstance().searchPublicService(SearchType.FUZZY, PublicServiceSearchFragment.this.mEditText.getText().toString(), new ResultCallback<PublicServiceProfileList>() {
          public void onError(ErrorCode e) {
            PublicServiceSearchFragment.this.mLoadingDialogFragment.dismiss();
          }

          public void onSuccess(PublicServiceProfileList list) {
            PublicServiceSearchFragment.this.mAdapter.clear();
            PublicServiceSearchFragment.this.mAdapter.addCollection(list.getPublicServiceData());
            PublicServiceSearchFragment.this.mAdapter.notifyDataSetChanged();
            PublicServiceSearchFragment.this.mLoadingDialogFragment.dismiss();
          }
        });
      }
    });
    this.mAdapter = new PublicServiceSearchFragment.PublicServiceListAdapter(this.getActivity());
    this.mListView.setAdapter(this.mAdapter);
    this.mListView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PublicServiceProfile info = PublicServiceSearchFragment.this.mAdapter.getItem(position);
        if (info.isFollow()) {
          RongIM.getInstance().startConversation(PublicServiceSearchFragment.this.getActivity(), info.getConversationType(), info.getTargetId(), info.getName());
        } else {
          Uri uri = Uri.parse("rong://" + view.getContext().getApplicationInfo().packageName).buildUpon().appendPath("publicServiceProfile").appendPath(info.getConversationType().getName().toLowerCase()).appendQueryParameter("targetId", info.getTargetId()).build();
          Intent intent = new Intent("android.intent.action.VIEW", uri);
          intent.putExtra("arg_public_account_info", info);
          PublicServiceSearchFragment.this.startActivity(intent);
        }

      }
    });
  }

  public void onDestroy() {
    RongContext.getInstance().getEventBus().unregister(this);
    super.onDestroy();
  }

  public void onEventMainThread(PublicServiceFollowableEvent event) {
    RLog.d("PublicServiceSearchFragment", "onEventMainThread PublicAccountIsFollowEvent, follow=" + event.isFollow());
    if (event != null) {
      this.getActivity().finish();
    }

  }

  private class PublicServiceListAdapter extends BaseAdapter<PublicServiceProfile> {
    LayoutInflater mInflater;

    public PublicServiceListAdapter(Context context) {
      this.mInflater = LayoutInflater.from(context);
    }

    protected View newView(Context context, int position, ViewGroup group) {
      View view = this.mInflater.inflate(layout.rc_item_public_service_search, (ViewGroup)null);
      PublicServiceSearchFragment.PublicServiceListAdapter.ViewHolder viewHolder = new PublicServiceSearchFragment.PublicServiceListAdapter.ViewHolder();
      viewHolder.portrait = (AsyncImageView)view.findViewById(id.rc_portrait);
      viewHolder.title = (TextView)view.findViewById(id.rc_title);
      viewHolder.description = (TextView)view.findViewById(id.rc_description);
      view.setTag(viewHolder);
      return view;
    }

    protected void bindView(View v, int position, PublicServiceProfile data) {
      PublicServiceSearchFragment.PublicServiceListAdapter.ViewHolder viewHolder = (PublicServiceSearchFragment.PublicServiceListAdapter.ViewHolder)v.getTag();
      if (data != null) {
        viewHolder.portrait.setResource(data.getPortraitUri());
        viewHolder.title.setText(data.getName());
        viewHolder.description.setText(data.getIntroduction());
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
      TextView title;
      TextView description;

      ViewHolder() {
      }
    }
  }
}
