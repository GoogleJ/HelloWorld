package com.zxjk.moneyspace.ui.msgpage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.msgpage.rongIM.message.BusinessCardMessage;
import com.zxjk.moneyspace.ui.msgpage.rongIM.message.GroupCardMessage;
import com.zxjk.moneyspace.ui.msgpage.rongIM.message.NewsCardMessage;
import com.zxjk.moneyspace.ui.msgpage.rongIM.message.RedPacketMessage;
import com.zxjk.moneyspace.ui.widget.NewsLoadMoreView;
import com.zxjk.moneyspace.utils.GlideUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.SightMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

public class ChattingRecordsActivity extends BaseActivity {
    List<Message> chattingMessages = new ArrayList<>();
    private int oldestMessageId = -1;
    private RecyclerView recyclerView;
    private BaseQuickAdapter<Message, BaseViewHolder> chattingAdapter;
    private TextMessage textMessage;
    private VoiceMessage voiceMessage;
    private ImageMessage imageMessage;
    private FileMessage fileMessage;

    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_records);

        initView();

        initData();

        sdf = new SimpleDateFormat("MM/dd HH:mm");

    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler_view);
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(getIntent().getStringExtra("nick") + "的发言记录");
    }

    private void initData() {
        chattingAdapter = new BaseQuickAdapter<Message, BaseViewHolder>(R.layout.item_chatting_records) {
            @Override
            protected void convert(BaseViewHolder helper, Message item) {

                TextView tvContent = helper.getView(R.id.tv_content);
                ImageView imageView = helper.getView(R.id.img_headPortrait);
                TextView tvSentTime = helper.getView(R.id.tv_sentTime);

                helper.setText(R.id.tv_nickname, getIntent().getStringExtra("nick"));
                if (item.getContent() instanceof TextMessage) {
                    textMessage = (TextMessage) item.getContent();
                    tvContent.setText(textMessage.getContent());
                } else if (item.getContent() instanceof ImageMessage) {
                    imageMessage = (ImageMessage) item.getContent();
                    tvContent.setText("[图片]");
                } else if (item.getContent() instanceof VoiceMessage) {
                    voiceMessage = (VoiceMessage) item.getContent();
                    tvContent.setText("[语音]");
                } else if (item.getContent() instanceof FileMessage) {
                    fileMessage = (FileMessage) item.getContent();
                    tvContent.setText("[文件]");
                } else if(item.getContent() instanceof RedPacketMessage){
                    tvContent.setText("[红包]");
                } else if(item.getContent() instanceof NewsCardMessage || item.getContent() instanceof GroupCardMessage || item.getContent() instanceof BusinessCardMessage){
                    tvContent.setText("[名片]");
                } else if(item.getContent() instanceof SightMessage){
                    tvContent.setText("[视频]");
                }
                tvSentTime.setText(sdf.format(new Date(item.getSentTime())));
                GlideUtil.loadCircleImg(imageView, getIntent().getStringExtra("imageUrl"));
            }
        };

        chattingAdapter.setOnItemClickListener((adapter, view, position) -> {
            Message message = (Message) adapter.getData().get(position);
            long fixedMsgSentTime = message.getSentTime();
            RongIM.getInstance().startConversation(this,
                    Conversation.ConversationType.GROUP, getIntent().getStringExtra("groupId"),
                    "世界的支点的讨论组\uD83D\uDE01wwwwwwwww",
                    fixedMsgSentTime);
        });

        View inflate = LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, null, false);
        TextView tv = inflate.findViewById(R.id.tv);
        ImageView iv = inflate.findViewById(R.id.iv);
        iv.setImageResource(R.drawable.ic_empty_orders);
        tv.setText(getString(R.string.no_data));
        chattingAdapter.setEmptyView(inflate);


        chattingAdapter.setLoadMoreView(new NewsLoadMoreView());
        chattingAdapter.setEnableLoadMore(true);
        chattingAdapter.setOnLoadMoreListener(this::getChattingRecords, recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chattingAdapter);

        getChattingRecords();
    }

    private void getChattingRecords() {
        RongIMClient.getInstance().getHistoryMessages(Conversation.ConversationType.GROUP, getIntent().getStringExtra("groupId"), oldestMessageId, 100, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                for (int i = 0; i < messages.size(); i++) {
                    Log.i("tag", "onSuccess: "+messages.get(i).getContent());
                    if (messages.get(i).getSenderUserId().equals(getIntent().getStringExtra("friendId"))) {
                        chattingMessages.add(messages.get(i));
                    }
                }
                if (oldestMessageId == -1) {
                    chattingAdapter.setNewData(chattingMessages);
                    chattingAdapter.disableLoadMoreIfNotFullPage();
                    if (messages.size() != 100) {
                        chattingAdapter.loadMoreEnd(false);
                    }
                    oldestMessageId = messages.get(messages.size() - 1).getMessageId();
                } else {
                    if (messages.size() >= 100) {
                        chattingAdapter.loadMoreComplete();
                        oldestMessageId = messages.get(messages.size() - 1).getMessageId();
                    } else {
                        chattingAdapter.loadMoreEnd(false);
                    }
                    chattingAdapter.addData(messages);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }
}