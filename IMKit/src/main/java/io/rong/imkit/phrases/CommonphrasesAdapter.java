//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.phrases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;
import io.rong.imkit.R.dimen;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import java.util.ArrayList;
import java.util.List;

public class CommonphrasesAdapter {
  private static final String TAG = CommonphrasesAdapter.class.getSimpleName();
  private LayoutInflater mLayoutInflater;
  private ListView mListView;
  private ViewGroup mPhrasesPager;
  private List<String> phrasesList = new ArrayList();
  private CommonphrasesAdapter.PhrasesAdapter mAdapter;
  private boolean mInitialized;
  private IPhrasesClickListener mPhrasesClickListener;

  public CommonphrasesAdapter() {
  }

  public void setOnPhrasesClickListener(IPhrasesClickListener clickListener) {
    this.mPhrasesClickListener = clickListener;
  }

  public void addPhrases(List<String> phrases) {
    this.phrasesList.addAll(phrases);
  }

  public void bindView(ViewGroup viewGroup) {
    this.mInitialized = true;
    this.initView(viewGroup.getContext(), viewGroup);
  }

  private void initView(Context context, ViewGroup viewGroup) {
    this.mLayoutInflater = LayoutInflater.from(context);
    this.mPhrasesPager = (ViewGroup)this.mLayoutInflater.inflate(layout.rc_ext_phrases_pager, (ViewGroup)null);
    Integer height = (int)context.getResources().getDimension(dimen.rc_extension_board_height);
    this.mPhrasesPager.setLayoutParams(new LayoutParams(-1, height));
    viewGroup.addView(this.mPhrasesPager);
    this.mListView = (ListView)this.mPhrasesPager.findViewById(id.rc_list);
    this.mAdapter = new CommonphrasesAdapter.PhrasesAdapter();
    this.mListView.setAdapter(this.mAdapter);
    this.mListView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CommonphrasesAdapter.this.mPhrasesClickListener.onClick((String)CommonphrasesAdapter.this.phrasesList.get(position), position);
      }
    });
  }

  public void setVisibility(int visibility) {
    if (this.mPhrasesPager != null) {
      this.mPhrasesPager.setVisibility(visibility);
    }

  }

  public int getVisibility() {
    return this.mPhrasesPager != null ? this.mPhrasesPager.getVisibility() : 8;
  }

  public boolean isInitialized() {
    return this.mInitialized;
  }

  private class PhrasesAdapter extends BaseAdapter {
    private PhrasesAdapter() {
    }

    public int getCount() {
      return CommonphrasesAdapter.this.phrasesList.size();
    }

    public Object getItem(int position) {
      return CommonphrasesAdapter.this.phrasesList.get(position);
    }

    public long getItemId(int position) {
      return 0L;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(layout.rc_ext_phrases_list_item, (ViewGroup)null);
        Integer height = (int)parent.getContext().getResources().getDimension(dimen.rc_extension_board_height) / 5;
        LayoutParams layoutParams = new LayoutParams(-1, height);
        convertView.setLayoutParams(layoutParams);
      }

      TextView tvPhrases = (TextView)convertView.findViewById(id.rc_phrases_tv);
      tvPhrases.setText((CharSequence)CommonphrasesAdapter.this.phrasesList.get(position));
      return convertView;
    }
  }
}
