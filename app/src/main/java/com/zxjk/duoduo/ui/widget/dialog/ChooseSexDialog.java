package com.zxjk.duoduo.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;

public class ChooseSexDialog extends Dialog {

    private View contentView;

    private String sex = "0";

    RadioGroup group;

    public interface OnChooseSexListener {
        void chooseSex(String sex);
    }

    public ChooseSexDialog(Context context, OnChooseSexListener listener) {
        super(context);

        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_choosesex, null);
        TextView tvDialogConfirm = contentView.findViewById(R.id.tvDialogConfirm);
        group = contentView.findViewById(R.id.rgChooseSex);

        group.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbMale) {
                sex = "0";
            } else {
                sex = "1";
            }
        });

        tvDialogConfirm.setOnClickListener(v -> {
            dismiss();
            listener.chooseSex(sex);
        });

        setContentView(contentView);
    }

    public void show() {
        String sex = Constant.currentUser.getSex();
        if (sex.equals("0")) {
            group.check(R.id.rbMale);
        } else {
            group.check(R.id.rbFemale);
        }
        super.show();
    }
}
