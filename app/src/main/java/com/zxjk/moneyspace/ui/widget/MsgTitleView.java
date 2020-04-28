package com.zxjk.moneyspace.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zxjk.moneyspace.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

public class MsgTitleView extends CommonPagerTitleView {
    private TextView badgeView;
    private TextView titleView;

    private int selectColor = -12222;
    private int normalColor = -12222;

    public MsgTitleView(Context context) {
        super(context);
        View inflate = LayoutInflater.from(context).inflate(R.layout.titleview_msgtitle, null);

        setContentView(inflate);

        badgeView = inflate.findViewById(R.id.tvBadge);
        titleView = inflate.findViewById(R.id.tvTitle);
    }

    public TextView getBadgeView() {
        return badgeView;
    }

    public TextView getTitleView() {
        return titleView;
    }

    public void setupSelectColor(int color) {
        selectColor = color;
    }

    public void setupNormalColor(int color) {
        normalColor = color;
    }

    public void setTextColor(int color) {
        titleView.setTextColor(color);
    }

    @Override
    public void onSelected(int index, int totalCount) {
        titleView.getPaint().setFakeBoldText(true);
        if (-12222 != selectColor) {
            titleView.setTextColor(selectColor);
        }
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        titleView.getPaint().setFakeBoldText(false);

        if (-12222 != normalColor) {
            titleView.setTextColor(normalColor);
        }
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        super.onEnter(index, totalCount, enterPercent, leftToRight);
        titleView.setTextSize(16 + 3 * enterPercent);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        super.onLeave(index, totalCount, leavePercent, leftToRight);
        titleView.setTextSize(19 - 3 * leavePercent);
    }
}
