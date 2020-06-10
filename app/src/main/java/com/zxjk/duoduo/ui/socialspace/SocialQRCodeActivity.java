package com.zxjk.duoduo.ui.socialspace;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.CommunityInfoResponse;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.ShareGroupQRActivity;
import com.zxjk.duoduo.utils.AesUtil;
import com.zxjk.duoduo.utils.SaveImageUtil;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class SocialQRCodeActivity extends BaseActivity {
    private CommunityInfoResponse data;
    private String uri2Code;

    private TextView tvSocialName;
    private TextView tvSocialId;
    private CircleImageView ivHead;
    private ImageView ivQR;
    private FrameLayout flContainer;

    private Bitmap bitmap;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_qrcode);

        initView();

        data = getIntent().getParcelableExtra("data");
        tvSocialName.setText(data.getName());
        tvSocialId.setText(getString(R.string.social_code) + data.getCode());

        uri2Code = Constant.APP_SHARE_URL + AesUtil.getInstance().encrypt("id=" + Constant.userId + "&groupId=" + getIntent().getStringExtra("groupId") + "&type=" + "1");

        getCodeBitmap();

        Glide.with(this).load(data.getLogo()).into(ivHead);

        getPermisson(findViewById(R.id.llSave), g -> {
            if (bitmap == null) {
                return;
            }

            flContainer.buildDrawingCache();

            SaveImageUtil.get().savePic(flContainer.getDrawingCache(), success -> {
                if (success) {
                    ToastUtils.showShort(R.string.savesucceed);
                    return;
                }
                ToastUtils.showShort(R.string.savefailed);
            });
        }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void initView() {
        ivQR = findViewById(R.id.ivQR);
        tvSocialId = findViewById(R.id.tvSocialId);
        tvSocialName = findViewById(R.id.tvSocialName);
        ivHead = findViewById(R.id.ivHead);
        flContainer = findViewById(R.id.flContainer);

        CardView card = findViewById(R.id.card);
        card.setClipToOutline(false);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.qrcodename);
    }

    public void share1(View view) {
        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                flContainer.buildDrawingCache();
                Constant.shareGroupQR = flContainer.getDrawingCache();
                Intent intent = new Intent(SocialQRCodeActivity.this, ShareGroupQRActivity.class);
                intent.putParcelableArrayListExtra("data", (ArrayList<Conversation>) conversations);
                startActivity(intent);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
            }
        });
    }

    public void share2(View view) {
        UMWeb link = new UMWeb(uri2Code);
        link.setTitle("邀请你加入群聊");
        link.setDescription("邀请你加入“" + data.getName() + "”");
        link.setThumb(new UMImage(this, R.drawable.ic_hilamglogo4));
        new ShareAction(this).withMedia(link).setPlatform(SHARE_MEDIA.WEIXIN).share();
    }

    public void share3(View view) {
        UMWeb link = new UMWeb(uri2Code);
        link.setTitle("邀请你加入群聊");
        link.setDescription("邀请你加入“" + data.getName() + "”");
        link.setThumb(new UMImage(this, R.drawable.ic_hilamglogo4));
        new ShareAction(this).withMedia(link).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).share();
    }

    @SuppressLint("CheckResult")
    private void getCodeBitmap() {
        Observable.create((ObservableOnSubscribe<Bitmap>) e -> {
            bitmap = QRCodeEncoder.syncEncodeQRCode(uri2Code, UIUtil.dip2px(this, 224), Color.BLACK);
            e.onNext(bitmap);
        })
                .compose(RxSchedulers.ioObserver())
                .compose(bindToLifecycle())
                .subscribe(b -> ivQR.setImageBitmap(b));

    }

    public static class QRCodeData {
        public String groupId;
        public String inviterId;
        public String groupName;
    }

}