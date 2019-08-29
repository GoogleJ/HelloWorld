package com.zxjk.duoduo.ui.msgpage.rongIM.plugin;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.msgpage.TransferActivity;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.model.UserInfo;

public class TransferPlugin implements IPluginModule {
    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.icon_transfer);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.transfer_money_title);
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        Intent intent = new Intent(fragment.getContext(), TransferActivity.class);
        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(rongExtension.getTargetId());
        intent.putExtra("user", userInfo);
        fragment.startActivity(intent);
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}
