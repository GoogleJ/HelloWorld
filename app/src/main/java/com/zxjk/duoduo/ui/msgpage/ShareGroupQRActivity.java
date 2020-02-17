package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.adapter.ShareGroupQRAdapter;
import com.zxjk.duoduo.utils.CommonUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.rong.imkit.RongIM;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;

public class ShareGroupQRActivity extends BaseActivity {

    private EditText search_edit;
    private RecyclerView recycler;
    private ShareGroupQRAdapter adapter;
    private ArrayList<Conversation> data;
    private boolean isTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_group_qr);

        String action = getIntent().getStringExtra("action");
        if (action != null && action.equals("transfer")) {
            isTransfer = true;
        }

        data = getIntent().getParcelableArrayListExtra("data");
        if (data == null) {
            data = new ArrayList<>();
        }
        Iterator<Conversation> iterator = data.iterator();
        while (iterator.hasNext()) {
            Conversation next = iterator.next();
            if (next.getConversationType().equals(Conversation.ConversationType.GROUP)) {
                Group groupInfo = RongUserInfoManager.getInstance().getGroupInfo(next.getTargetId());
                if (groupInfo == null) {
                    iterator.remove();
                    continue;
                }
                next.setConversationTitle(groupInfo.getName());
                next.setPortraitUrl(groupInfo.getPortraitUri().toString());
            } else {
                UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(next.getTargetId());
                if (userInfo == null) {
                    iterator.remove();
                    continue;
                }
                next.setConversationTitle(userInfo.getName());
                next.setPortraitUrl(userInfo.getPortraitUri().toString());
            }
        }

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.fasongdao);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        recycler = findViewById(R.id.recycler);
        search_edit = findViewById(R.id.search_edit);
        search_edit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //按下搜索
                if (TextUtils.isEmpty(search_edit.getText().toString().trim())) {
                    adapter.setNewData(data);
                } else {
                    doSearch(data);
                }
                return true;
            }
            return false;
        });

        View emptyView = getLayoutInflater().inflate(R.layout.view_app_null_type, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ImageView app_type = emptyView.findViewById(R.id.app_type);
        TextView app_prompt_text = emptyView.findViewById(R.id.app_prompt_text);
        app_type.setImageResource(R.drawable.ic_empty_nosearch);
        app_prompt_text.setText(R.string.noChatHistory);

        adapter = new ShareGroupQRAdapter();
        adapter.setNewData(data);
        adapter.setEmptyView(emptyView);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (isTransfer) {
                //消息转发
                handleTransfer(position);
            } else {
                //分享(群名片、游戏群名片)
                handleShare(position);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void handleTransfer(int position) {
        MessageContent messageContent = getIntent().getParcelableExtra("messagecontent");
        if (messageContent == null) {
            ArrayList<Message> messagelist = getIntent().getParcelableArrayListExtra("messagelist");
            Observable.interval(0, 250, TimeUnit.MILLISECONDS)
                    .take(messagelist.size())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(d -> CommonUtils.initDialog(ShareGroupQRActivity.this, getString(R.string.forwarding)).show())
                    .doOnError(t -> CommonUtils.destoryDialog())
                    .doOnDispose(CommonUtils::destoryDialog)
                    .doOnComplete(() -> {
                        ToastUtils.showShort(R.string.forward_success);
                        CommonUtils.destoryDialog();
                        finish();
                    })
                    .compose(bindToLifecycle())
                    .subscribe(l -> {
                        MessageContent content = messagelist.get(l.intValue()).getContent();
                        Message message = Message.obtain(data.get(position).getTargetId(), data.get(position).getConversationType(), content);
                        RongIM.getInstance().sendMessage(message, null, null, (IRongCallback.ISendMessageCallback) null);
                    });
            return;
        }
        Message message = Message.obtain(data.get(position).getTargetId(), data.get(position).getConversationType(), messageContent);
        RongIM.getInstance().sendMessage(message, "", "", new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {

            }

            @Override
            public void onSuccess(Message message) {
                ToastUtils.showShort(R.string.transfermessagesuccess);
                finish();
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtils.showShort(R.string.transfermessagefail);
                finish();
            }
        });
    }

    private void handleShare(int position) {
        CommonUtils.initDialog(this).show();
        saveBitmapFile(Constant.shareGroupQR);
        Uri uri = Uri.fromFile(new File(getExternalCacheDir(), "1.jpg"));
        ImageMessage obtain = ImageMessage.obtain(uri, uri, false);
        Message obtain1 = Message.obtain(data.get(position).getTargetId(), data.get(position).getConversationType(), obtain);
        RongIM.getInstance().sendImageMessage(obtain1, null, null, new RongIMClient.SendImageMessageCallback() {
            @Override
            public void onAttached(Message message) {

            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                CommonUtils.destoryDialog();
            }

            @Override
            public void onSuccess(Message message) {
                Constant.shareGroupQR = null;
                CommonUtils.destoryDialog();
                ToastUtils.showShort(R.string.share_success);
                finish();
            }

            @Override
            public void onProgress(Message message, int i) {

            }
        });
    }

    private void doSearch(ArrayList<Conversation> data) {
        ArrayList<Conversation> conversations = new ArrayList<>();
        for (Conversation c : data) {
            if (c.getConversationTitle().equals(search_edit.getText().toString().trim())) {
                conversations.add(c);
            }
        }
        adapter.setNewData(conversations);
    }

    //创建新聊天
    public void createNewChat(View view) {
        Intent intent = new Intent(this, SelectContactForCardActivity.class);
        intent.putExtra("fromShare", true);
        if (isTransfer) {
            intent.putExtra("action", "transfer");
            if (null != intent.getParcelableExtra("messagecontent")) {
                intent.putExtra("messagecontent", (Parcelable) getIntent().getParcelableExtra("messagecontent"));
            } else {
                intent.putParcelableArrayListExtra("messagelist", getIntent().getParcelableArrayListExtra("messagelist"));
            }
        }
        startActivity(intent);
        finish();
    }

    public void saveBitmapFile(Bitmap bitmap) {
        File file = new File(getExternalCacheDir(), "1.jpg");//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
