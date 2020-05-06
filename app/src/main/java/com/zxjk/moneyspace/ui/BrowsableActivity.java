package com.zxjk.moneyspace.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.msgpage.AgreeGroupChatActivity;
import com.zxjk.moneyspace.utils.CommonUtils;

public class BrowsableActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getData() != null) {
            String action = getIntent().getData().getQueryParameter("action");
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case "joinGroup":
                        String groupId = getIntent().getData().getQueryParameter("groupId");
                        Intent intent = new Intent(this, AgreeGroupChatActivity.class);
                        intent.putExtra("groupId", groupId);
                        startActivity(intent);
                        break;
                    case "addFriend":
                        String userId = getIntent().getData().getQueryParameter("id");
                        CommonUtils.resolveFriendList(this, userId, false);
                        break;
                }
            }
            finish();
        }
    }
}
