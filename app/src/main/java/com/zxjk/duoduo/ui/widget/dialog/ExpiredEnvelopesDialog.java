package com.zxjk.duoduo.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.msgpage.PeopleUnaccalimedActivity;
import com.zxjk.duoduo.utils.GlideUtil;

import io.rong.imlib.model.UserInfo;

public class ExpiredEnvelopesDialog extends Dialog {

    ImageView close_red_packet;

    private View view;
    private Context context;

    private ImageView m_transfer_envelopes_heard;
    private TextView m_red_envelopes_user;
    private TextView m_expired_envelopes_text;
    private TextView m_expired_envelopes_info;

    public ExpiredEnvelopesDialog(@NonNull Context context) {
        super(context, R.style.dialogstyle);
        this.view = View.inflate(context, R.layout.dialog_expired_envelopes, null);
        this.context = context;
        initUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
    }

    private void initUI() {
        close_red_packet = view.findViewById(R.id.close_red_packet);
        m_transfer_envelopes_heard = view.findViewById(R.id.m_transfer_envelopes_heard);
        m_red_envelopes_user = view.findViewById(R.id.m_red_envelopes_user);
        m_expired_envelopes_text = view.findViewById(R.id.m_expired_envelopes_text);
        m_expired_envelopes_info = view.findViewById(R.id.m_expired_envelopes_info);

        close_red_packet.setOnClickListener(v -> dismiss());
    }

    public void show(UserInfo userInfo, boolean isExpired, String redId) {
        GlideUtil.loadCircleImg(m_transfer_envelopes_heard, userInfo.getPortraitUri().toString());
        m_red_envelopes_user.setText(userInfo.getName() + context.getString(R.string.dehongbao));

        m_expired_envelopes_info.setOnClickListener(v -> {
            Intent intent1 = new Intent(context, PeopleUnaccalimedActivity.class);
            intent1.putExtra("id", redId);
            intent1.putExtra("isShow", false);
            context.startActivity(intent1);
            dismiss();
        });

        if (!isExpired) {
            m_expired_envelopes_text.setText(R.string.red_packet_tips2);
        }

        show();
    }

}
