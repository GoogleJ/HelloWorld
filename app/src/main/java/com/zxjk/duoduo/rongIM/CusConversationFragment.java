package com.zxjk.duoduo.rongIM;

import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.VibrateUtils;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.DeleteClickActions;
import io.rong.imkit.actions.IClickActions;
import io.rong.imkit.fragment.ConversationFragment;

public class CusConversationFragment extends ConversationFragment {

    @Override
    public boolean showMoreClickItem() {
        return true;
    }

    @Override
    public List<IClickActions> getMoreClickActions() {
        ArrayList<IClickActions> actions = new ArrayList(2);
        actions.add(new DeleteClickActions());
        actions.add(new ForwardAction());
        return actions;
    }

    @Override
    public void onVoiceInputToggleTouch(View v, MotionEvent event) {
        super.onVoiceInputToggleTouch(v, event);
        if (event.getAction() == MotionEvent.ACTION_DOWN ||
                event.getAction() == MotionEvent.ACTION_UP) {
            VibrateUtils.vibrate(40);
        }
    }
}
