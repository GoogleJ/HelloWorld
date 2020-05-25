package io.rong.imkit.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.R;

public class SingleChoiceAdapter<T> extends BaseAdapter {
    private Context mContext;
    private List<T> mObjects = new ArrayList();
    private int mCheckBoxResourceID = 0;
    private int mSelectItem = -1;
    private LayoutInflater mInflater;

    public SingleChoiceAdapter(Context context, int checkBoxResourceId) {
        init(context, checkBoxResourceId);
    }

    public SingleChoiceAdapter(Context context, List<T> objects, int checkBoxResourceId) {
        init(context, checkBoxResourceId);
        if (objects != null)
            this.mObjects = objects;
    }

    private void init(Context context, int checkBoResourceId) {
        this.mContext = context;
        this.mInflater =
                ((LayoutInflater) context
                        .getSystemService("layout_inflater"));

        this.mCheckBoxResourceID = checkBoResourceId;
    }

    public void refreshData(List<T> objects) {
        if (objects != null) {
            this.mObjects = objects;
            setSelectItem(0);
        }
    }

    public void setSelectItem(int selectItem) {
        if ((selectItem >= 0) && (selectItem < this.mObjects.size())) {
            this.mSelectItem = selectItem;
            notifyDataSetChanged();
        }
    }

    public int getSelectItem() {
        return this.mSelectItem;
    }

    public void clear() {
        this.mObjects.clear();
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.mObjects.size();
    }

    public T getItem(int position) {
        return this.mObjects.get(position);
    }

    public int getPosition(T item) {
        return this.mObjects.indexOf(item);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.rc_cs_item_single_choice, null);

            viewHolder = new ViewHolder();
            viewHolder.mTextView =
                    ((TextView) convertView
                            .findViewById(R.id.rc_cs_tv_group_name));

            viewHolder.mCheckBox =
                    ((CheckBox) convertView
                            .findViewById(R.id.rc_cs_group_checkBox));

            convertView.setTag(viewHolder);

            if (this.mCheckBoxResourceID != 0)
                viewHolder.mCheckBox.setButtonDrawable(this.mCheckBoxResourceID);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mCheckBox.setChecked(this.mSelectItem == position);

        Object item = getItem(position);
        if (item instanceof CharSequence)
            viewHolder.mTextView.setText((CharSequence) item);
        else {
            viewHolder.mTextView.setText(item.toString());
        }

        return convertView;
    }

    public static class ViewHolder {
        public TextView mTextView;
        public CheckBox mCheckBox;
    }
}