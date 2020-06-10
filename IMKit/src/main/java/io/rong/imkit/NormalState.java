//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit;

import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import io.rong.imkit.R.color;
import io.rong.imkit.R.drawable;
import io.rong.imkit.emoticon.EmoticonTabAdapter;

public class NormalState implements IRongExtensionState {
    NormalState() {
    }

    public void changeView(RongExtension pExtension) {
        pExtension.refreshQuickView();
        ImageView voiceToggle = pExtension.getVoiceToggle();
        if (voiceToggle != null) {
            voiceToggle.setImageResource(drawable.rc_voice_toggle_selector);
        }

        ImageView pluginToggle = pExtension.getPluginToggle();
        if (pluginToggle != null) {
            pluginToggle.setImageResource(drawable.rc_plugin_toggle_selector);
        }

        ImageView emoticonToggle = pExtension.getEmoticonToggle();
        if (emoticonToggle != null) {
            emoticonToggle.setImageResource(drawable.rc_emotion_toggle_selector);
        }

        EditText editText = pExtension.getEditText();
        if (editText != null) {
            editText.setBackgroundResource(drawable.rc_edit_text_background_selector);
        }

        Button voiceInputToggle = pExtension.getVoiceInputToggle();
        if (voiceInputToggle != null) {
            voiceInputToggle.setTextColor(pExtension.getContext().getResources().getColor(color.rc_text_voice));
        }

    }

    public void onClick(final RongExtension pExtension, View v) {
        int id = v.getId();
        if (id == R.id.rc_plugin_toggle) {
            if (pExtension.getExtensionClickListener() != null) {
                pExtension.getExtensionClickListener().onPluginToggleClick(v, pExtension);
            }

            pExtension.setPluginBoard();
        } else if (id == R.id.rc_emoticon_toggle) {
            if (pExtension.getExtensionClickListener() != null) {
                pExtension.getExtensionClickListener().onEmoticonToggleClick(v, pExtension);
            }

            if (pExtension.isKeyBoardActive()) {
                pExtension.hideInputKeyBoard();
                pExtension.getHandler().postDelayed(new Runnable() {
                    public void run() {
                        pExtension.setEmoticonBoard();
                    }
                }, 200L);
            } else {
                pExtension.setEmoticonBoard();
            }

            pExtension.hidePluginBoard();
            pExtension.hidePhrasesBoard();
        } else if (id == R.id.rc_voice_toggle) {
            pExtension.clickVoice(pExtension.isRobotFirst(), pExtension, v, drawable.rc_emotion_toggle_selector);
        }

    }

    public boolean onEditTextTouch(RongExtension pExtension, View v, MotionEvent event) {
        if (0 == event.getAction()) {
            EditText editText = pExtension.getEditText();
            if (pExtension.getExtensionClickListener() != null) {
                pExtension.getExtensionClickListener().onEditTextClick(editText);
            }

            if (Build.BRAND.toLowerCase().contains("meizu")) {
                editText.requestFocus();
                pExtension.getEmoticonToggle().setSelected(false);
                pExtension.setKeyBoardActive(true);
            } else {
                pExtension.showInputKeyBoard();
            }

            pExtension.getContainerLayout().setSelected(true);
            pExtension.hidePluginBoard();
            pExtension.hideEmoticonBoard();
            pExtension.hidePhrasesBoard();
        }

        return false;
    }

    public void hideEmoticonBoard(ImageView pEmoticonToggle, EmoticonTabAdapter pEmotionTabAdapter) {
        pEmotionTabAdapter.setVisibility(8);
        pEmoticonToggle.setImageResource(drawable.rc_emotion_toggle_selector);
    }
}