package com.zxjk.moneyspace.ui.msgpage.rongIM;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.zxjk.moneyspace.ui.msgpage.rongIM.message.CusEmoteTabMessage;
import com.zxjk.moneyspace.utils.CommonUtils;

import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.utilities.ExtensionHistoryUtil;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

public class CusEmoteTab2 implements IEmoticonTab {
    private int[] tabIds = new int[]{io.rong.imkit.R.drawable.emoji2_1, io.rong.imkit.R.drawable.emoji2_2,
            io.rong.imkit.R.drawable.emoji2_3, io.rong.imkit.R.drawable.emoji2_4, io.rong.imkit.R.drawable.emoji2_5,
            io.rong.imkit.R.drawable.emoji2_6, io.rong.imkit.R.drawable.emoji2_7, io.rong.imkit.R.drawable.emoji2_8,
            io.rong.imkit.R.drawable.emoji2_9, io.rong.imkit.R.drawable.emoji2_10, io.rong.imkit.R.drawable.emoji2_11,
            io.rong.imkit.R.drawable.emoji2_12, io.rong.imkit.R.drawable.emoji2_13, io.rong.imkit.R.drawable.emoji2_14};
    private LayoutInflater mLayoutInflater;
    private LinearLayout mIndicator;
    private int selected = 0;
    private String mUserId;
    private int mEmojiCountPerPage = 8;
    private int mEmojiCount = 14;
    private int width;

    private String targetId;
    private Conversation.ConversationType conversationType;

    public CusEmoteTab2(String targetId, Conversation.ConversationType conversationType) {
        this.targetId = targetId;
        this.conversationType = conversationType;
    }

    public Drawable obtainTabDrawable(Context context) {
        return context.getResources().getDrawable(io.rong.imkit.R.drawable.rc_tab_emoji2);
    }

    public View obtainTabPager(Context context) {
        this.mUserId = RongIMClient.getInstance().getCurrentUserId();
        width = ScreenUtils.getScreenWidth() / (mEmojiCountPerPage / 2) - CommonUtils.dip2px(context, 20);
        return this.initView(context);
    }

    public void onTableSelected(int position) {
    }

    private View initView(final Context context) {
        int pages = mEmojiCount / this.mEmojiCountPerPage + (mEmojiCount % this.mEmojiCountPerPage != 0 ? 1 : 0);
        View view = LayoutInflater.from(context).inflate(io.rong.imkit.R.layout.rc_ext_emoji_pager, null);
        ViewPager viewPager = view.findViewById(io.rong.imkit.R.id.rc_view_pager);
        this.mIndicator = view.findViewById(io.rong.imkit.R.id.rc_indicator);
        this.mLayoutInflater = LayoutInflater.from(context);
        viewPager.setAdapter(new CusEmoteTab2.EmojiPagerAdapter(pages));
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                ExtensionHistoryUtil.setEmojiPosition(context, mUserId, position);
                onIndicatorChanged(selected, position);
                selected = position;
            }
        });

        viewPager.setOffscreenPageLimit(1);
        this.initIndicator(pages, this.mIndicator);
        int position = ExtensionHistoryUtil.getEmojiPosition(context, this.mUserId);
        viewPager.setCurrentItem(position);
        this.onIndicatorChanged(-1, position);
        return view;
    }

    private void initIndicator(int pages, LinearLayout indicator) {
        for (int i = 0; i < pages; ++i) {
            ImageView imageView = (ImageView) this.mLayoutInflater.inflate(io.rong.imkit.R.layout.rc_ext_indicator, null);
            imageView.setImageResource(io.rong.imkit.R.drawable.rc_ext_indicator);
            indicator.addView(imageView);
        }
    }

    private void onIndicatorChanged(int pre, int cur) {
        int count = this.mIndicator.getChildCount();
        if (count > 0 && pre < count && cur < count) {
            ImageView curView;
            if (pre >= 0) {
                curView = (ImageView) this.mIndicator.getChildAt(pre);
                curView.setImageResource(io.rong.imkit.R.drawable.rc_ext_indicator);
            }

            if (cur >= 0) {
                curView = (ImageView) this.mIndicator.getChildAt(cur);
                curView.setImageResource(io.rong.imkit.R.drawable.rc_ext_indicator_hover);
            }
        }

    }

    private class ViewHolder {
        ImageView emojiIV;

        private ViewHolder() {
        }
    }

    private class EmojiAdapter extends BaseAdapter {
        int count;
        int index;

        public EmojiAdapter(int index, int count) {
            this.count = Math.min(mEmojiCountPerPage, count - index);
            this.index = index;
        }

        public int getCount() {
            return this.count;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0L;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            CusEmoteTab2.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new CusEmoteTab2.ViewHolder();
                convertView = mLayoutInflater.inflate(io.rong.imkit.R.layout.rc_ext_emoji_item, null);
                viewHolder.emojiIV = convertView.findViewById(io.rong.imkit.R.id.rc_ext_emoji_item);
                ViewGroup.LayoutParams params = viewHolder.emojiIV.getLayoutParams();
                params.width = width;
                params.height = width;
                viewHolder.emojiIV.setLayoutParams(params);
                convertView.setTag(viewHolder);
            }

            viewHolder = (CusEmoteTab2.ViewHolder) convertView.getTag();

            Glide.with(viewHolder.emojiIV.getContext()).load(tabIds[this.index + position]).into(viewHolder.emojiIV);

            return convertView;
        }
    }

    private class EmojiPagerAdapter extends PagerAdapter {
        int count;

        EmojiPagerAdapter(int count) {
            this.count = count;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            GridView gridView = (GridView) mLayoutInflater.inflate(io.rong.imkit.R.layout.rc_ext_emoji_grid_view, null);
            gridView.setAdapter(new CusEmoteTab2.EmojiAdapter(position * mEmojiCountPerPage, mEmojiCount));
            gridView.setNumColumns(mEmojiCountPerPage / 2);
            gridView.setOnItemClickListener((parent, view, position1, id) -> {
                if (TextUtils.isEmpty(targetId)) {
                    return;
                }

                int index = position1 + selected * mEmojiCountPerPage;

                CusEmoteTabMessage msgContent = new CusEmoteTabMessage();
                msgContent.setId(String.valueOf(index + 11));
                msgContent.setIsGif("0");

                Message message = Message.obtain(targetId, conversationType, msgContent);
                RongIM.getInstance().sendMessage(message, "", "", (IRongCallback.ISendMessageCallback) null);
            });
            container.addView(gridView);
            return gridView;
        }

        public int getItemPosition(Object object) {
            return -2;
        }

        public int getCount() {
            return this.count;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            View layout = (View) object;
            container.removeView(layout);
        }
    }
}