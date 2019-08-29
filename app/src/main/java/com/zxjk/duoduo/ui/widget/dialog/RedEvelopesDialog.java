package com.zxjk.duoduo.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.RedPacketMessage;
import com.zxjk.duoduo.utils.GlideUtil;

import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

public class RedEvelopesDialog extends Dialog implements View.OnClickListener {
    ImageView redOpenBtn;
    ImageView redCloseBtn;
    ImageView m_transfer_envelopes_heard;
    TextView m_red_envelopes_user;
    TextView m_red_envelopes_signature_text;

    private View view;

    private OnOpenListener onOpenListener;

    public void setOnOpenListener(OnOpenListener onOpenListener) {
        this.onOpenListener = onOpenListener;
    }

    public interface OnOpenListener {
        void onOpen();
    }

    public RedEvelopesDialog(@NonNull Context context) {
        super(context, R.style.dialogstyle);
        this.view = View.inflate(context, R.layout.dialog_red_evelopes, null);

        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
        initView();
    }

    private void initView() {
        redOpenBtn = view.findViewById(R.id.m_red_envelopes_open);
        redCloseBtn = view.findViewById(R.id.m_red_evelopes_close);
        m_transfer_envelopes_heard = view.findViewById(R.id.m_transfer_envelopes_heard);
        m_red_envelopes_user = view.findViewById(R.id.m_red_envelopes_user);
        m_red_envelopes_signature_text = view.findViewById(R.id.m_red_envelopes_signature_text);
        redOpenBtn.setOnClickListener(this);
        redCloseBtn.setOnClickListener(this);
    }

    public void show(Message message, UserInfo userInfo) {
        GlideUtil.loadCircleImg(m_transfer_envelopes_heard, userInfo.getPortraitUri().toString());
        m_red_envelopes_user.setText(userInfo.getName() + "的红包");
        RedPacketMessage m = (RedPacketMessage) message.getContent();

        if (TextUtils.isEmpty(m.getRemark())) {
            m_red_envelopes_signature_text.setText(R.string.m_red_envelopes_signature_text);
        } else {
            m_red_envelopes_signature_text.setText(m.getRemark());
        }
        show();
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (v.getId() == R.id.m_red_envelopes_open) {
            if (null != onOpenListener) {
                onOpenListener.onOpen();
            }
        }
    }
}
