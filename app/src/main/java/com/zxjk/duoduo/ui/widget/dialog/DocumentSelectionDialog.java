package com.zxjk.duoduo.ui.widget.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxjk.duoduo.R;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DocumentSelectionDialog extends Dialog implements View.OnClickListener {
    private View view;
    Context context;
    @BindView(R.id.id_card_icon)
    ImageView idCardIcon;
    @BindView(R.id.passport_icon)
    ImageView passportIcon;
    @BindView(R.id.other_identity_card_icon)
    ImageView otherIdentityCardIcon;
    @BindView(R.id.id_card)
    TextView idCard;
    @BindView(R.id.passport_text)
    TextView passport;
    @BindView(R.id.other_id_card)
    TextView other;


    public DocumentSelectionDialog(@NonNull Context context) {
        super(context, R.style.dialogstyle);
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_document_selection, null);
        ButterKnife.bind(this, view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);

    }

    public void show(String type) {
        idCardIcon.setImageResource(R.drawable.icon_document_selection_no);
        passportIcon.setImageResource(R.drawable.icon_document_selection_no);
        otherIdentityCardIcon.setImageResource(R.drawable.icon_document_selection_no);
        if (type.equals("1")) {
            idCardIcon.setImageResource(R.drawable.icon_document_selection_successful);
        } else if (type.equals("2")) {
            passportIcon.setImageResource(R.drawable.icon_document_selection_successful);
        } else {
            otherIdentityCardIcon.setImageResource(R.drawable.icon_document_selection_successful);
        }
        show();
    }

    @SuppressLint("NewApi")
    @OnClick({R.id.id_card_icon, R.id.passport_icon, R.id.other_identity_card_icon})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_card_icon:
                idCardIcon.setImageResource(R.drawable.icon_document_selection_successful);
                passportIcon.setImageResource(R.drawable.icon_document_selection_no);
                otherIdentityCardIcon.setImageResource(R.drawable.icon_document_selection_no);
                if (onClickListener != null) {
                    onClickListener.onSelectedIdCard(idCard.getText().toString());
                }
                break;
            case R.id.passport_icon:
                idCardIcon.setImageResource(R.drawable.icon_document_selection_no);
                passportIcon.setImageResource(R.drawable.icon_document_selection_successful);
                otherIdentityCardIcon.setImageResource(R.drawable.icon_document_selection_no);
                if (onClickListener != null) {
                    onClickListener.onSelectedPassport(passport.getText().toString());
                }
                break;
            case R.id.other_identity_card_icon:
                idCardIcon.setImageResource(R.drawable.icon_document_selection_no);
                passportIcon.setImageResource(R.drawable.icon_document_selection_no);
                otherIdentityCardIcon.setImageResource(R.drawable.icon_document_selection_successful);
                if (onClickListener != null) {
                    onClickListener.onSelectedOther(other.getText().toString());
                }
                break;
            default:

        }
    }

    public OnClickListener onClickListener;

    /**
     * 自定义点击事件
     * onSelectedIdCard 为选择身份证的时候的返回
     * onSelectedPassport 为选择护照的时候的返回
     * onSelectedOther 为选择其他时候的返回
     */
    public interface OnClickListener {
        void onSelectedIdCard(String idCard);

        void onSelectedPassport(String passport);

        void onSelectedOther(String other);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
