package io.rong.imkit.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import io.rong.imkit.R;

public class SingleChoiceDialog extends Dialog {
    protected Context mContext;
    protected View mRootView;
    protected TextView mTVTitle;
    protected TextView mButtonOK;
    protected TextView mButtonCancel;
    protected ListView mListView;
    protected List<String> mList;
    protected DialogInterface.OnClickListener mOkClickListener;
    protected DialogInterface.OnClickListener mCancelClickListener;
    private SingleChoiceAdapter<String> mSingleChoiceAdapter;

    public SingleChoiceDialog(Context context, List<String> list) {
        super(context);

        this.mContext = context;
        this.mList = list;

        initView(this.mContext);
        initData();
    }

    protected void initView(Context context) {
        requestWindowFeature(1);
        setContentView(R.layout.rc_cs_single_choice_layout);
        this.mRootView = findViewById(R.id.rc_cs_rootView);
        this.mRootView.setBackgroundDrawable(new ColorDrawable(0));
        this.mTVTitle = ((TextView) findViewById(R.id.rc_cs_tv_title));
        this.mButtonOK = ((Button) findViewById(R.id.rc_cs_btn_ok));
        this.mButtonOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SingleChoiceDialog.this.onButtonOK();
            }

        });
        this.mButtonCancel = ((Button) findViewById(R.id.rc_cs_btn_cancel));
        this.mButtonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SingleChoiceDialog.this.onButtonCancel();
            }

        });
        this.mListView = ((ListView) findViewById(R.id.rc_cs_group_dialog_listView));

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        ColorDrawable dw = new ColorDrawable(0);
        dialogWindow.setBackgroundDrawable(dw);
    }

    public void setTitle(String title) {
        this.mTVTitle.setText(title);
    }

    public void setOnOKButtonListener(DialogInterface.OnClickListener onClickListener) {
        this.mOkClickListener = onClickListener;
    }

    public void setOnCancelButtonListener(DialogInterface.OnClickListener onClickListener) {
        this.mCancelClickListener = onClickListener;
    }

    protected void onButtonOK() {
        dismiss();
        if (this.mOkClickListener != null)
            this.mOkClickListener.onClick(this, 0);
    }

    protected void onButtonCancel() {
        dismiss();
        if (this.mCancelClickListener != null)
            this.mCancelClickListener.onClick(this, 0);
    }

    protected void initData() {
        this.mSingleChoiceAdapter = new SingleChoiceAdapter(this.mContext, this.mList, R.drawable.rc_cs_group_checkbox_selector);

        this.mListView.setAdapter(this.mSingleChoiceAdapter);
        this.mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != SingleChoiceDialog.this.mSingleChoiceAdapter.getSelectItem()) {
                    if (!(SingleChoiceDialog.this.mButtonOK.isEnabled()))
                        SingleChoiceDialog.this.mButtonOK.setEnabled(true);

                    SingleChoiceDialog.this.mSingleChoiceAdapter.setSelectItem(position);
                    SingleChoiceDialog.this.mSingleChoiceAdapter.notifyDataSetChanged();
                }

            }

        });
        setListViewHeightBasedOnChildren(this.mListView);
    }

    public int getSelectItem() {
        return this.mSingleChoiceAdapter.getSelectItem();
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); ++i) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        totalHeight += 10;
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = (totalHeight + listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}