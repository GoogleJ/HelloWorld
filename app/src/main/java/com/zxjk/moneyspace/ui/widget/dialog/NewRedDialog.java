package com.zxjk.moneyspace.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.msgpage.PeopleUnaccalimedActivity;
import com.zxjk.moneyspace.utils.GlideUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class NewRedDialog extends Dialog {

    private View view;
    private Context context;

    private LinearLayout llContent1;
    private ImageView ivOPen;
    private ImageView ivHead;
    private TextView tvNick;
    private TextView tvNote;

    private LinearLayout llContent2;
    private LinearLayout llTop;
    private TextView tvMoney;
    private TextView tv1;
    private TextView tv2;
    private TextView tvUnit;
    private Button btnClose;

    private TextView tvShowDetail;

    private @Type
    int dialogType;

    //默认可领取
    public static final int TYPE1_NORMAL = 1;
    //已领取
    public static final int TYPE2_EXPIRED = 2;
    //已过期
    public static final int TYPE3_RECEIVED = 3;

    @IntDef({TYPE1_NORMAL, TYPE2_EXPIRED, TYPE3_RECEIVED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    private OpenListener openListener;

    public void setOpenListener(OpenListener openListener) {
        this.openListener = openListener;
    }

    public interface OpenListener {
        void open();
    }

    public NewRedDialog(@NonNull Context context, @Type int type) {
        super(context, R.style.dialogstyle);
        this.view = View.inflate(context, R.layout.dialog_new_red, null);
        this.context = context;
        dialogType = type;
        initUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        setCancelable(true);
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
    }

    private void initUI() {
        llContent1 = view.findViewById(R.id.llContent1);
        ivOPen = view.findViewById(R.id.ivOPen);
        ivHead = view.findViewById(R.id.ivHead);
        tvNick = view.findViewById(R.id.tvNick);
        tvNote = view.findViewById(R.id.tvNote);

        llContent2 = view.findViewById(R.id.llContent2);
        llTop = view.findViewById(R.id.llTop);
        tvMoney = view.findViewById(R.id.tvMoney);
        tv1 = view.findViewById(R.id.tv1);
        tv2 = view.findViewById(R.id.tv2);
        tvUnit = view.findViewById(R.id.tvUnit);
        btnClose = view.findViewById(R.id.btnClose);

        tvShowDetail = view.findViewById(R.id.tvShowDetail);

        view.findViewById(R.id.img_back).setOnClickListener(v -> {
            dismiss();
        });

        switch (dialogType) {
            case TYPE1_NORMAL:
                setupType1();
                break;
            case TYPE2_EXPIRED:
                setupType2();
                break;
            case TYPE3_RECEIVED:
                setupType3();
                break;
        }

        ivOPen.setOnClickListener(v -> {
            dismiss();
            if (openListener != null) {
                openListener.open();
            }
        });

        btnClose.setOnClickListener(v -> dismiss());
    }

    private void setupType1() {
        llContent1.setVisibility(View.VISIBLE);
        llContent2.setVisibility(View.GONE);
    }

    private void setupType2() {
        llContent1.setVisibility(View.GONE);
        llContent2.setVisibility(View.VISIBLE);
        llTop.setVisibility(View.INVISIBLE);
        llContent2.setBackgroundResource(R.drawable.new_red_dialog_bg3);
    }

    private void setupType3() {
        llContent1.setVisibility(View.GONE);
        llContent2.setVisibility(View.VISIBLE);
        llTop.setVisibility(View.VISIBLE);
        llContent2.setBackgroundResource(R.drawable.new_red_dialog_bg2);
    }

    public void show(String headUrl, String nick, String note) {
        GlideUtil.loadCircleImg(ivHead, headUrl);
        if (TextUtils.isEmpty(nick)) {
            tvNick.setText(R.string.red_packet);
        } else {
            tvNick.setText(getContext().getString(R.string.xxx_red, nick));
        }
        tvNote.setText(note);
        show();
    }

    public void show(String money, String note1, String note2, String unit) {
        tvMoney.setText(money);
        tv1.setText(note1);
        tv2.setText(note2);
        tvUnit.setText(unit);
        show();
    }

    public void showExpired1() {
        show("", getContext().getString(R.string.late), getContext().getString(R.string.red_invidate), "");
    }

    public void showExpired2(String redId) {
        tvShowDetail.setVisibility(View.VISIBLE);
        tvShowDetail.setOnClickListener(V -> {
            dismiss();
            Intent intent1 = new Intent(context, PeopleUnaccalimedActivity.class);
            intent1.putExtra("id", redId);
            intent1.putExtra("isShow", false);
            context.startActivity(intent1);
        });
        show("", getContext().getString(R.string.late), getContext().getString(R.string.no_red_left), "");
    }

    public void showReceived(String money, String symbol) {
        show(money, getContext().getString(R.string.receive_success), getContext().getString(R.string.redtip3), symbol);
    }
}
