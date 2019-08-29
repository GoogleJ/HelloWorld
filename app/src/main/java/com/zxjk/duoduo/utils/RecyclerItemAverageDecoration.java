package com.zxjk.duoduo.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemAverageDecoration extends RecyclerView.ItemDecoration {
    private int itemSpaceLeft;
    private int itemSpaceCenter;
    private int itemNum;

    public RecyclerItemAverageDecoration(int itemSpaceLeft, int itemSpaceCenter, int itemNum) {
        this.itemSpaceLeft = itemSpaceLeft;
        this.itemSpaceCenter = itemSpaceCenter;
        this.itemNum = itemNum;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildCount() > 0) {
            int position = parent.getChildAdapterPosition(view) % itemNum;
            if (position == 0) {
                outRect.left = itemSpaceLeft;
                outRect.right = itemSpaceCenter / 2;
            } else if (position % itemNum == itemNum - 1) {
                outRect.left = itemSpaceCenter / 2;
                outRect.right = itemSpaceLeft;
            } else {
                outRect.left = itemSpaceCenter / 2;
                outRect.right = itemSpaceCenter / 2;
            }
        }
    }
}
