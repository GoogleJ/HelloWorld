package com.zxjk.moneyspace.ui.msgpage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GroupResponse;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.AesUtil;
import com.zxjk.moneyspace.utils.ImageUtil;
import com.zxjk.moneyspace.utils.ShareUtil;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class GroupQRActivity extends BaseActivity {
    private String uri2Code;

    private CircleImageView ivHead;
    private ImageView ivRecipetImg;
    private TextView tvGroupName;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BarUtils.setStatusBarColor(this, Color.parseColor("#272E3F"));
        setContentView(R.layout.activity_group_qr);

        ivHead = findViewById(R.id.ivHead);
        tvGroupName = findViewById(R.id.tvUserName);
        ivRecipetImg = findViewById(R.id.ivQRImg);

        GroupResponse data = (GroupResponse) getIntent().getSerializableExtra("data");

        uri2Code = Constant.APP_SHARE_URL + AesUtil.getInstance().encrypt("id=" + Constant.userId + "&groupId=" + data.getGroupInfo().getId());

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.group_qr);

        ivHead = findViewById(R.id.ivHead);

        getCodeBitmap();

        share2Wechat();

        loadHead(data);

        tvGroupName.setText(data.getGroupInfo().getGroupNikeName());

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    private void share2Wechat() {
        getPermisson(findViewById(R.id.savetophone), granted -> {
            UMWeb umWeb = new UMWeb(uri2Code);
            umWeb.setTitle("入群通知");
            umWeb.setDescription("快加入我们的MoneySpace群");
            umWeb.setThumb(new UMImage(this, R.drawable.ic_shareeeee));
            ShareUtil.shareLink(this, umWeb);
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void loadHead(GroupResponse data) {
        ImageUtil.loadGroupPortrait(ivHead, data.getGroupInfo().getHeadPortrait());
    }

    @SuppressLint("CheckResult")
    private void getCodeBitmap() {
        Observable.create((ObservableOnSubscribe<Bitmap>) e -> {
            bitmap = QRCodeEncoder.syncEncodeQRCode(uri2Code, UIUtil.dip2px(this, 180), Color.BLACK);
            e.onNext(bitmap);
        })
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(b -> ivRecipetImg.setImageBitmap(b));
    }

    //分享二维码
    public void share(View view) {
        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                ivRecipetImg.buildDrawingCache();
                Constant.shareGroupQR = ivRecipetImg.getDrawingCache();
                Intent intent = new Intent(GroupQRActivity.this, ShareGroupQRActivity.class);
                intent.putParcelableArrayListExtra("data", (ArrayList<Conversation>) conversations);
                startActivity(intent);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    public static class GroupQRData {
        public String groupId;
        public String inviterId;
        public String groupName;
    }
}
