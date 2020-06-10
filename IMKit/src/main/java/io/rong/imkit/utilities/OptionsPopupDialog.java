//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import io.rong.imkit.R.dimen;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;

public class OptionsPopupDialog extends AlertDialog {
  private Context mContext;
  private ListView mListView;
  private String[] arrays;
  private OptionsPopupDialog.OnOptionsItemClickedListener mItemClickedListener;

  public static OptionsPopupDialog newInstance(Context context, String[] arrays) {
    OptionsPopupDialog optionsPopupDialog = new OptionsPopupDialog(context, arrays);
    return optionsPopupDialog;
  }

  public OptionsPopupDialog(Context context, String[] arrays) {
    super(context);
    this.mContext = context;
    this.arrays = arrays;
  }

  protected void onStart() {
    super.onStart();
    LayoutInflater inflater = (LayoutInflater)this.mContext.getSystemService("layout_inflater");
    View view = inflater.inflate(layout.rc_dialog_popup_options, (ViewGroup)null);
    this.mListView = (ListView)view.findViewById(id.rc_list_dialog_popup_options);
    ArrayAdapter<String> adapter = new ArrayAdapter(this.mContext, layout.rc_dialog_popup_options_item, id.rc_dialog_popup_item_name, this.arrays);
    this.mListView.setAdapter(adapter);
    this.mListView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (OptionsPopupDialog.this.mItemClickedListener != null) {
          OptionsPopupDialog.this.mItemClickedListener.onOptionsItemClicked(position);
          OptionsPopupDialog.this.dismiss();
        }

      }
    });
    this.setContentView(view);
    LayoutParams layoutParams = this.getWindow().getAttributes();
    layoutParams.width = this.getPopupWidth();
    layoutParams.height = -2;
    this.getWindow().setAttributes(layoutParams);
  }

  public OptionsPopupDialog setOptionsPopupDialogListener(OptionsPopupDialog.OnOptionsItemClickedListener itemListener) {
    this.mItemClickedListener = itemListener;
    return this;
  }

  private int getPopupWidth() {
    int distanceToBorder = (int)this.mContext.getResources().getDimension(dimen.rc_popup_dialog_distance_to_edge);
    return this.getScreenWidth() - 2 * distanceToBorder;
  }

  private int getScreenWidth() {
    return ((WindowManager)((WindowManager)this.mContext.getSystemService("window"))).getDefaultDisplay().getWidth();
  }

  public void show() {
    if (this.mContext instanceof Activity) {
      Activity activity = (Activity)this.mContext;
      if (activity.isFinishing()) {
        return;
      }
    }

    super.show();
  }

  public interface OnOptionsItemClickedListener {
    void onOptionsItemClicked(int var1);
  }
}