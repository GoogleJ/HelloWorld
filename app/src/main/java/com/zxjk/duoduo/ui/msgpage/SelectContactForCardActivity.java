package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.adapter.SelectForCardAdapter;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.BusinessCardMessage;
import com.zxjk.duoduo.utils.CommonUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;

@SuppressLint("CheckResult")
public class SelectContactForCardActivity extends BaseActivity implements TextWatcher {

    private EditText searchEdit;
    private RecyclerView mRecyclerView;
    private SelectForCardAdapter mAdapter;
    private String userId;
    private boolean fromPulgin;

    private List<FriendInfoResponse> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact_for_card);

        initView();

        initRecyclerView();
    }

    private void initRecyclerView() {
        userId = getIntent().getStringExtra("userId");
        fromPulgin = getIntent().getBooleanExtra("fromPulgin", false);

        searchEdit.addTextChangedListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SelectForCardAdapter();
        mRecyclerView.setAdapter(mAdapter);

        getFriendListById();

        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            boolean fromShare = getIntent().getBooleanExtra("fromShare", false);
            if (fromShare) {
                String action = getIntent().getStringExtra("action");
                if (action != null && action.equals("transfer")) {
                    //消息转发
                    handleTransfer(position);
                } else {
                    //分享群二维码
                    handleShareQR(position);
                }
            } else {
                shareCard(position);
            }
        });
    }

    private void handleTransfer(int position) {
        MessageContent messageContent = getIntent().getParcelableExtra("messagecontent");
        if (null == messageContent) {
            ArrayList<Message> messagelist = getIntent().getParcelableArrayListExtra("messagelist");
            Observable.interval(0, 250, TimeUnit.MILLISECONDS)
                    .take(messagelist.size())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(d -> CommonUtils.initDialog(SelectContactForCardActivity.this, getString(R.string.forwarding)).show())
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
                        Message message = Message.obtain(list.get(position).getId(), Conversation.ConversationType.PRIVATE, content);
                        RongIM.getInstance().sendMessage(message, null, null, (IRongCallback.ISendMessageCallback) null);
                    });
            return;
        }
        Message message = Message.obtain(list.get(position).getId(), Conversation.ConversationType.PRIVATE, messageContent);
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

    private void handleShareQR(int position) {
        CommonUtils.initDialog(this).show();
        saveBitmapFile(Constant.shareGroupQR);
        Uri uri = Uri.fromFile(new File(getExternalCacheDir(), "1.jpg"));
        ImageMessage obtain = ImageMessage.obtain(uri, uri, false);
        Message obtain1 = Message.obtain(list.get(position).getId(), Conversation.ConversationType.PRIVATE, obtain);

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

    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.select_contacts));
        searchEdit = findViewById(R.id.search_select_contact);
        mRecyclerView = findViewById(R.id.card_recycler_view);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    /**
     * 获取好友列表
     */
    private void getFriendListById() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getFriendListById()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(friendInfoResponses -> {
                    this.list = friendInfoResponses;
                    mAdapter.setNewData(friendInfoResponses);
                }, this::handleApiError);
    }

    /**
     * 获取好友详情
     *
     * @param position
     */
    private void shareCard(int position) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getCustomerInfoById(userId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(c -> {
                    NiceDialog.init().setLayoutId(R.layout.layout_general_dialog4).setConvertListener(new ViewConvertListener() {
                        @Override
                        protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                            holder.setText(R.id.tv_content, getString(R.string.share_business_card));
                            holder.setText(R.id.tv_cancel, getString(R.string.cancel));
                            holder.setText(R.id.tv_notarize, getString(R.string.ok));
                            holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
                            holder.setOnClickListener(R.id.tv_notarize, v -> {
                                dialog.dismiss();
                                FriendInfoResponse listBean = mAdapter.getData().get(position);

                                String toId;
                                String cardUserId;
                                String cardUserNick;
                                String cardUserDuoDuoId;
                                String cardUserPortrait;

                                if (fromPulgin) {
                                    toId = c.getId();
                                    cardUserId = listBean.getId();
                                    cardUserNick = listBean.getNick();
                                    cardUserDuoDuoId = listBean.getDuoduoId();
                                    cardUserPortrait = listBean.getHeadPortrait();
                                } else {
                                    toId = listBean.getId();
                                    cardUserId = c.getId();
                                    cardUserNick = c.getNick();
                                    cardUserDuoDuoId = c.getDuoduoId();
                                    cardUserPortrait = c.getHeadPortrait();
                                }

                                BusinessCardMessage message = new BusinessCardMessage();
                                message.setUserId(cardUserId);
                                message.setDuoduo(cardUserDuoDuoId);
                                message.setIcon(cardUserPortrait);
                                message.setName(cardUserNick);

                                Message message1 = Message.obtain(toId, Conversation.ConversationType.PRIVATE, message);
                                RongIM.getInstance().sendMessage(message1, null, null, new IRongCallback.ISendMessageCallback() {
                                    @Override
                                    public void onAttached(Message message) {
                                    }

                                    @Override
                                    public void onSuccess(Message message) {
                                        if (fromPulgin) finish();
                                        ToastUtils.showShort(R.string.has_bean_sent);
                                    }

                                    @Override
                                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                                    }
                                });
                            });
                        }
                    }).setDimAmount(0.5f).setOutCancel(false).show(getSupportFragmentManager());
                }, this::handleApiError);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String groupname = s.toString();
        List<FriendInfoResponse> groupnamelist = search(groupname);
        mAdapter.setNewData(groupnamelist);
    }

    /**
     * 模糊查询
     *
     * @param str
     * @return
     */
    private List<FriendInfoResponse> search(String str) {
        List<FriendInfoResponse> filterList = new ArrayList<>();// 过滤后的list
        if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
            String simpleStr = str.replaceAll("\\-|\\s", "");
            for (FriendInfoResponse contact : list) {
                if (contact.getNick() != null) {
                    if (contact.getNick().contains(simpleStr) || contact.getNick().contains(str)) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        } else {
            for (FriendInfoResponse contact : list) {
                if (contact.getNick() != null) {
                    //姓名全匹配,姓名首字母简拼匹配,姓名全字母匹配
                    boolean isNameContains = contact.getNick().toLowerCase(Locale.CHINESE)
                            .contains(str.toLowerCase(Locale.CHINESE));
                    if (isNameContains) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        }
        return filterList;
    }

    private void saveBitmapFile(Bitmap bitmap) {
        File file = new File(getExternalCacheDir(), "1.jpg");
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
