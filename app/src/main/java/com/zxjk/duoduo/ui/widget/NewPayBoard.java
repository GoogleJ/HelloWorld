package com.zxjk.duoduo.ui.widget;

import android.content.Context;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.ScreenUtils;
import com.zxjk.duoduo.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import razerdp.basepopup.BasePopupWindow;

public class NewPayBoard extends BasePopupWindow {

    private PayPsdInputView payPwd;

    private List<Integer> list = new ArrayList<>();
    private int[] commonButtonIds = new int[]{R.id.button00, R.id.button01, R.id.button02, R.id.button03,
            R.id.button04, R.id.button05, R.id.button06, R.id.button07, R.id.button08, R.id.button09};

    private OnFinish onFinish;

    public interface OnFinish {
        void onFinish(String result);
    }

    public NewPayBoard(Context context) {
        super(context);
        initConfig();
        initView();
    }

    private void initConfig() {
        forbidDefaultSoftKeyboard();
    }

    private void forbidDefaultSoftKeyboard() {
        if (payPwd == null) {
            return;
        }
        try {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            setShowSoftInputOnFocus.setAccessible(true);
            setShowSoftInputOnFocus.invoke(payPwd, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        payPwd = findViewById(R.id.pay_pwd);
        initKeyboardView();

        payPwd.setComparePassword(new PayPsdInputView.onPasswordListener() {
            @Override
            public void onDifference(String oldPsd, String newPsd) {
            }

            @Override
            public void onEqual(String psd) {
            }

            @Override
            public void inputFinished(String inputPsd) {
                if (onFinish != null) {
                    onFinish.onFinish(inputPsd);
                    onFinish = null;
                }
                dismiss();
            }
        });
    }

    private void initKeyboardView() {
        for (int commonButtonId : commonButtonIds) {
            final Button button = findViewById(commonButtonId);
            button.setOnClickListener(v -> {
                int curSelection = payPwd.getSelectionStart();
                int length = payPwd.getText().toString().length();
                if (curSelection < length) {
                    String content = payPwd.getText().toString();
                    payPwd.setText(content.substring(0, curSelection) + button.getText() + content.subSequence(curSelection, length));
                    payPwd.setSelection(curSelection + 1);
                } else {
                    payPwd.setText(payPwd.getText().toString() + button.getText());
                    payPwd.setSelection(payPwd.getText().toString().length());
                }
            });
        }

        findViewById(R.id.buttonCross).setOnClickListener(v -> {
            int length = payPwd.getText().toString().length();
            int curSelection = payPwd.getSelectionStart();
            if (length > 0 && curSelection > 0 && curSelection <= length) {
                String content = payPwd.getText().toString();
                payPwd.setText(content.substring(0, curSelection - 1) + content.subSequence(curSelection, length));
                payPwd.setSelection(curSelection - 1);
            }
        });
    }

    public void show(OnFinish onFinish) {
        TranslateAnimation showAnimation = new TranslateAnimation(0f, 0f, ScreenUtils.getScreenHeight(), 0f);
        showAnimation.setDuration(250);
        TranslateAnimation dismissAnimation = new TranslateAnimation(0f, 0f, 0f, ScreenUtils.getScreenHeight());
        dismissAnimation.setDuration(500);
        setShowAnimation(showAnimation);
        setDismissAnimation(dismissAnimation);

        this.onFinish = onFinish;
        doRandomSortOp();
        super.showPopupWindow();
    }

    private void doRandomSortOp() {
        //随机
//            for (int i = 0; i < commonButtonIds.length; i++) {
//                final Button button = parentView.findViewById(commonButtonIds[i]);
//                button.setText("" + i);
//            }
        list.clear();
        Random ran = new Random();
        while (list.size() < commonButtonIds.length) {
            int n = ran.nextInt(commonButtonIds.length);
            if (!list.contains(n)) {
                list.add(n);
            }
        }
        for (int i = 0; i < commonButtonIds.length; i++) {
            final Button button = findViewById(commonButtonIds[i]);
            button.setText(String.valueOf(list.get(i)));
        }
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.keyboadview);
    }
}
