//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.menu;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.utilities.RongUtils;
import java.util.List;

public class InputSubMenu {
  PopupWindow mPopupWindow;
  ViewGroup container;
  LayoutInflater mInflater;
  ISubMenuItemClickListener mOnClickListener;

  public InputSubMenu(Context context, List<String> menus) {
    this.mInflater = LayoutInflater.from(context);
    this.container = (ViewGroup)this.mInflater.inflate(layout.rc_ext_sub_menu_container, (ViewGroup)null);
    this.mPopupWindow = new PopupWindow(this.container, -2, -2);
    this.setupSubMenus(this.container, menus);
  }

  public void showAtLocation(View parent) {
    this.mPopupWindow.setBackgroundDrawable(new ColorDrawable());
    this.container.measure(0, 0);
    int[] location = new int[2];
    int w = this.container.getMeasuredWidth();
    int h = this.container.getMeasuredHeight();
    parent.getLocationOnScreen(location);
    int x = location[0] + (parent.getWidth() - w) / 2;
    int maxRightX = RongUtils.getScreenWidth() - RongUtils.dip2px(10.0F);
    if (x + w > maxRightX) {
      x = maxRightX - w;
    }

    int y = location[1] - h - RongUtils.dip2px(3.0F);
    this.mPopupWindow.showAtLocation(parent, 8388659, x, y);
    this.mPopupWindow.setOutsideTouchable(true);
    this.mPopupWindow.setFocusable(true);
    this.mPopupWindow.update();
  }

  public void setOnItemClickListener(ISubMenuItemClickListener onClickListener) {
    this.mOnClickListener = onClickListener;
  }

  private void setupSubMenus(ViewGroup viewGroup, List<String> menus) {
    for(int i = 0; i < menus.size(); ++i) {
      View view = this.mInflater.inflate(layout.rc_ext_sub_menu_item, (ViewGroup)null);
      TextView tv = (TextView)view.findViewById(id.rc_sub_menu_title);
      View divider = view.findViewById(id.rc_sub_menu_divider_line);
      String title = (String)menus.get(i);
      tv.setText(title);
      if (i < menus.size() - 1) {
        divider.setVisibility(0);
      }

      view.setTag(i);
      view.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          int index = (Integer)v.getTag();
          InputSubMenu.this.mOnClickListener.onClick(index);
          InputSubMenu.this.mPopupWindow.dismiss();
        }
      });
      viewGroup.addView(view);
    }

  }
}
