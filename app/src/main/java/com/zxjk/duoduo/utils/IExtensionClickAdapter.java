package com.zxjk.duoduo.utils;

import android.net.Uri;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.LinkedHashMap;

import io.rong.imkit.IExtensionClickListener;
import io.rong.imkit.plugin.IPluginModule;

public class IExtensionClickAdapter implements IExtensionClickListener {
    @Override
    public void onSendToggleClick(View view, String s) {
    }

    @Override
    public void onImageResult(LinkedHashMap<String, Integer> linkedHashMap, boolean b) {
    }

    @Override
    public void onLocationResult(double v, double v1, String s, Uri uri) {
    }

    @Override
    public void onSwitchToggleClick(View view, ViewGroup viewGroup) {
    }

    @Override
    public void onVoiceInputToggleTouch(View view, MotionEvent motionEvent) {

    }

    @Override
    public void onEmoticonToggleClick(View view, ViewGroup viewGroup) {
    }

    @Override
    public void onPluginToggleClick(View view, ViewGroup viewGroup) {
    }

    @Override
    public void onMenuClick(int i, int i1) {

    }

    @Override
    public void onEditTextClick(EditText editText) {

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return false;
    }

    @Override
    public void onExtensionCollapsed() {

    }

    @Override
    public void onExtensionExpanded(int i) {

    }

    @Override
    public void onPluginClicked(IPluginModule iPluginModule, int i) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public void fragmentInitDone() {

    }
}
