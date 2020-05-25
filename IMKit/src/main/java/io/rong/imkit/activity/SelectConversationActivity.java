//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.R;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.RongBaseNoActionbarActivity;
import io.rong.imkit.RongContext;
import io.rong.imkit.fragment.IHistoryDataResultCallback;
import io.rong.imkit.utils.ForwardManager;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.RongSwipeRefreshLayout;
import io.rong.imkit.widget.RongSwipeRefreshLayout.OnLoadListener;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;

public class SelectConversationActivity extends RongBaseNoActionbarActivity implements OnClickListener, OnLoadListener {
  private static final String TAG = SelectConversationActivity.class.getSimpleName();
  private TextView btOK;
  private SelectConversationActivity.ListAdapter mAdapter;
  private RongSwipeRefreshLayout mRefreshLayout;
  private ArrayList<Conversation> selectedMember = new ArrayList();
  private static final ConversationType[] defConversationType;
  private long timestamp = 0L;
  private int pageSize = 100;

  public SelectConversationActivity() {
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.getWindow().setFlags(2048, 2048);
    this.requestWindowFeature(1);
    this.setContentView(layout.rc_activity_forward_select);
    this.btOK = (TextView)this.findViewById(id.rc_btn_ok);
    TextView btCancel = (TextView)this.findViewById(id.rc_btn_cancel);
    this.mRefreshLayout = (RongSwipeRefreshLayout)this.findViewById(id.rc_refresh);
    ListView listView = (ListView)this.findViewById(id.rc_list);
    this.btOK.setEnabled(false);
    this.btOK.setOnClickListener(this);
    btCancel.setOnClickListener(this);
    this.mRefreshLayout.setCanRefresh(false);
    this.mRefreshLayout.setCanLoading(true);
    this.mRefreshLayout.setOnLoadListener(this);
    this.mAdapter = new SelectConversationActivity.ListAdapter(this);
    listView.setAdapter(this.mAdapter);
    listView.setOnItemClickListener(new SelectConversationActivity.ForwardItemClickListener());
    Intent intent = this.getIntent();
    if (intent != null) {
      this.getConversationList(false);
    }
  }

  private void getConversationList(boolean isLoadMore) {
    this.getConversationList(defConversationType, new IHistoryDataResultCallback<List<Conversation>>() {
      public void onResult(List<Conversation> data) {
        if (data != null && data.size() > 0) {
          SelectConversationActivity.this.mAdapter.setAllMembers(data);
          SelectConversationActivity.this.mAdapter.notifyDataSetChanged();
        }

        if (data == null) {
          SelectConversationActivity.this.mRefreshLayout.setLoadMoreFinish(false);
        } else if (data.size() > 0 && data.size() <= SelectConversationActivity.this.pageSize) {
          SelectConversationActivity.this.mRefreshLayout.setLoadMoreFinish(false);
        } else if (data.size() == 0) {
          SelectConversationActivity.this.mRefreshLayout.setLoadMoreFinish(false);
          SelectConversationActivity.this.mRefreshLayout.setCanLoading(false);
        } else {
          SelectConversationActivity.this.mRefreshLayout.setLoadMoreFinish(false);
        }

      }

      public void onError() {
        SelectConversationActivity.this.mRefreshLayout.setLoadMoreFinish(false);
      }
    }, isLoadMore);
  }

  public void getConversationList(ConversationType[] conversationTypes, final IHistoryDataResultCallback<List<Conversation>> callback, boolean isLoadMore) {
    long lTimestamp = isLoadMore ? this.timestamp : 0L;
    RongIMClient.getInstance().getConversationListByPage(new ResultCallback<List<Conversation>>() {
      public void onSuccess(List<Conversation> conversations) {
        if (!SelectConversationActivity.this.isFinishing()) {
          if (callback != null) {
            if (conversations != null) {
              SelectConversationActivity.this.timestamp = ((Conversation)conversations.get(conversations.size() - 1)).getSentTime();
            }

            callback.onResult(conversations);
          }

        }
      }

      public void onError(ErrorCode e) {
        if (callback != null) {
          callback.onError();
        }

      }
    }, lTimestamp, this.pageSize, conversationTypes);
  }

  public void onLoad() {
    this.getConversationList(true);
  }

  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.rc_btn_ok) {
      this.showSendDialog();
    } else if (id == R.id.rc_btn_cancel) {
      this.finish();
    }

  }

  private void showSendDialog() {
    if (!this.isFinishing()) {
      ForwardManager.forwardMessage(this, this.selectedMember);
    }
  }

  static {
    defConversationType = new ConversationType[]{ConversationType.PRIVATE, ConversationType.GROUP};
  }

  private class ViewHolder {
    ImageView checkbox;
    AsyncImageView portrait;
    TextView name;

    private ViewHolder() {
    }
  }

  private class ListAdapter extends BaseAdapter {
    private Activity activity;
    private List<Conversation> allMembers;

    ListAdapter(Activity activity) {
      this.activity = activity;
    }

    void setAllMembers(List<Conversation> allMembers) {
      this.allMembers = allMembers;
    }

    public int getCount() {
      return this.allMembers == null ? 0 : this.allMembers.size();
    }

    public Object getItem(int position) {
      return this.allMembers == null ? null : this.allMembers.get(position);
    }

    public long getItemId(int position) {
      return 0L;
    }

    @SuppressLint({"InflateParams"})
    public View getView(int position, View convertView, ViewGroup parent) {
      SelectConversationActivity.ViewHolder holder;
      if (convertView == null) {
        holder = SelectConversationActivity.this.new ViewHolder();
        convertView = LayoutInflater.from(this.activity).inflate(layout.rc_listitem_forward_select_member, (ViewGroup)null);
        holder.checkbox = (ImageView)convertView.findViewById(id.rc_checkbox);
        holder.portrait = (AsyncImageView)convertView.findViewById(id.rc_user_portrait);
        holder.name = (TextView)convertView.findViewById(id.rc_user_name);
        convertView.setTag(holder);
      }

      holder = (SelectConversationActivity.ViewHolder)convertView.getTag();
      holder.checkbox.setTag(this.allMembers.get(position));
      holder.checkbox.setClickable(false);
      holder.checkbox.setImageResource(drawable.rc_select_conversation_checkbox);
      holder.checkbox.setEnabled(true);
      Conversation conversation = (Conversation)this.allMembers.get(position);
      holder.checkbox.setSelected(SelectConversationActivity.this.selectedMember.contains(conversation));
      String title = conversation.getConversationTitle();
      String portrait = conversation.getPortraitUrl();
      if (TextUtils.isEmpty(title)) {
        title = RongContext.getInstance().getConversationTemplate(conversation.getConversationType().getName()).getTitle(conversation.getTargetId());
      }

      if (TextUtils.isEmpty(portrait)) {
        Uri url = RongContext.getInstance().getConversationTemplate(conversation.getConversationType().getName()).getPortraitUri(conversation.getTargetId());
        portrait = url != null ? url.toString() : "";
      }

      if (!TextUtils.isEmpty(portrait)) {
        holder.portrait.setAvatar(Uri.parse(portrait));
      }

      if (!TextUtils.isEmpty(title)) {
        holder.name.setText(title);
      }

      return convertView;
    }
  }

  private class ForwardItemClickListener implements OnItemClickListener {
    private ForwardItemClickListener() {
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      View v = view.findViewById(R.id.rc_checkbox);
      Conversation member = (Conversation)v.getTag();
      SelectConversationActivity.this.selectedMember.remove(member);
      v.setSelected(!v.isSelected());
      if (v.isSelected()) {
        SelectConversationActivity.this.selectedMember.add(member);
      }

      if (SelectConversationActivity.this.selectedMember.size() > 0) {
        SelectConversationActivity.this.btOK.setEnabled(true);
      } else {
        SelectConversationActivity.this.btOK.setEnabled(false);
      }

    }
  }
}
