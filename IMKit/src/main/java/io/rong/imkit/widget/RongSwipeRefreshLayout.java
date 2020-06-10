//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.rong.imkit.R.layout;

public class RongSwipeRefreshLayout extends SwipeRefreshLayout {
    private int mScaledTouchSlop;
    private View mFooterView;
    private ListView mListView;
    private RongSwipeRefreshLayout.OnLoadListener mOnLoadListener;
    private RongSwipeRefreshLayout.OnFlushListener mFlushListener;
    public boolean isRefreshFinish;
    public boolean isLoadMoreFinish;
    private boolean condition4;
    private boolean condition5;
    private boolean loadMoreEnabled;
    private boolean refreshEnabled;
    private boolean autoLoading;
    private float mDownY;
    private float mUpY;

    public RongSwipeRefreshLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public RongSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.condition4 = false;
        this.condition5 = false;
        this.loadMoreEnabled = true;
        this.refreshEnabled = true;
        this.initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        this.mFooterView = View.inflate(context, layout.view_footer, (ViewGroup) null);
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mListView == null) {
            if (this.getChildCount() > 0 && this.getChildAt(0) instanceof ListView) {
                this.mListView = (ListView) this.getChildAt(0);
                this.setListViewOnScroll();
            }

            this.setOnRefresh();
        }

    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case 0:
                this.mDownY = ev.getY();
            case 1:
            default:
                break;
            case 2:
                this.mUpY = ev.getY();
                if (this.canLoadMore() && !this.autoLoading) {
                    this.loadData();
                }
        }

        if (this.refreshEnabled) {
            if (this.isLoadMoreFinish) {
                this.setEnabled(false);
            } else {
                this.setEnabled(true);
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private boolean canLoadMore() {
        boolean condition1 = this.mDownY - this.mUpY >= (float) this.mScaledTouchSlop;
        boolean condition2 = !this.isLoadMoreFinish;
        boolean condition3 = !this.isRefreshFinish;
        return condition1 && condition2 && condition3 && this.condition4 && this.condition5 && this.loadMoreEnabled;
    }

    private void loadData() {
        this.setLoadMoreFinish(true);
        if (this.mOnLoadListener != null) {
            (new Handler()).postDelayed(new Runnable() {
                public void run() {
                    RongSwipeRefreshLayout.this.mOnLoadListener.onLoad();
                }
            }, 3000L);
        } else {
            (new Handler()).postDelayed(new Runnable() {
                public void run() {
                    RongSwipeRefreshLayout.this.setLoadMoreFinish(false);
                }
            }, 2000L);
        }

    }

    public void setAutoLoading(boolean autoLoading) {
        this.autoLoading = autoLoading;
    }

    public void setRefreshing(boolean flushing) {
        this.isRefreshFinish = flushing;
        this.setRefreshing(flushing);
    }

    public void setLoadMoreFinish(boolean loading) {
        this.isLoadMoreFinish = loading;
        if (this.isLoadMoreFinish) {
            if (this.mListView != null) {
                this.mListView.addFooterView(this.mFooterView);
                if (this.mListView.getAdapter() != null) {
                    this.mListView.smoothScrollToPosition(this.mListView.getAdapter().getCount() - 1);
                }
            }
        } else {
            if (this.mListView != null && this.mListView.getFooterViewsCount() > 0) {
                this.mListView.removeFooterView(this.mFooterView);
            }

            this.mDownY = 0.0F;
            this.mUpY = 0.0F;
        }

    }

    private void setListViewOnScroll() {
        this.mListView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (RongSwipeRefreshLayout.this.canLoadMore() && RongSwipeRefreshLayout.this.autoLoading) {
                    RongSwipeRefreshLayout.this.loadData();
                }

            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                RongSwipeRefreshLayout.this.condition4 = false;
                RongSwipeRefreshLayout.this.condition5 = false;
                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    View lastVisibleItemView = RongSwipeRefreshLayout.this.mListView.getChildAt(RongSwipeRefreshLayout.this.mListView.getChildCount() - 1);
                    if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == RongSwipeRefreshLayout.this.mListView.getHeight()) {
                        RongSwipeRefreshLayout.this.condition4 = true;
                    }
                }

                if (totalItemCount > visibleItemCount) {
                    RongSwipeRefreshLayout.this.condition5 = true;
                }

            }
        });
    }

    private void setOnRefresh() {
        if (!this.isLoadMoreFinish) {
            this.setOnRefreshListener(new OnRefreshListener() {
                public void onRefresh() {
                    RongSwipeRefreshLayout.this.setRefreshing(true);
                    if (RongSwipeRefreshLayout.this.mFlushListener != null) {
                        RongSwipeRefreshLayout.this.mFlushListener.onFlush();
                    } else {
                        (new Handler()).postDelayed(new Runnable() {
                            public void run() {
                                RongSwipeRefreshLayout.this.setRefreshing(false);
                            }
                        }, 2000L);
                    }

                }
            });
        } else {
            this.setRefreshing(false);
        }

    }

    public void setCanRefresh(boolean enabled) {
        this.refreshEnabled = enabled;
        this.setEnabled(enabled);
    }

    public void setCanLoading(boolean enabled) {
        this.loadMoreEnabled = enabled;
    }

    public void setOnLoadListener(RongSwipeRefreshLayout.OnLoadListener listener) {
        this.mOnLoadListener = listener;
    }

    public void setOnFlushListener(RongSwipeRefreshLayout.OnFlushListener listener) {
        this.mFlushListener = listener;
    }

    public interface OnFlushListener {
        void onFlush();
    }

    public interface OnLoadListener {
        void onLoad();
    }
}