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

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GroupResponse;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.minepage.scanuri.BaseUri;
import com.zxjk.moneyspace.ui.msgpage.ShareGroupQRActivity;
import com.zxjk.moneyspace.utils.ImageUtil;
import com.zxjk.moneyspace.utils.SaveImageUtil;

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
    private BaseUri uri = new BaseUri("action3");
    private String uri2Code;

    private CircleImageView ivHead;
    private ImageView ivRecipetImg;
    private TextView tvGroupName;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_qr);

        ivHead = findViewById(R.id.ivHead);
        tvGroupName = findViewById(R.id.tvGroupName);
        ivRecipetImg = findViewById(R.id.ivRecipetImg);

        GroupResponse data = (GroupResponse) getIntent().getSerializableExtra("data");

        uri.data = new GroupQRData();
        ((GroupQRData) uri.data).groupId = data.getGroupInfo().getId();
        ((GroupQRData) uri.data).inviterId = Constant.userId;
        ((GroupQRData) uri.data).groupName = data.getGroupInfo().getGroupNikeName();
        uri2Code = new Gson().toJson(uri);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.group_qr);

        ivHead = findViewById(R.id.ivHead);

        getCodeBitmap();

        save2Phone();

        loadHead(data);

        tvGroupName.setText(data.getGroupInfo().getGroupNikeName());

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    private void save2Phone() {
        getPermisson(findViewById(R.id.cardSave), g -> {
            //保存到手机
            if (bitmap == null) {
                return;
            }

            ivRecipetImg.buildDrawingCache();

            SaveImageUtil.get().savePic(ivRecipetImg.getDrawingCache(), success -> {
                if (success) {
                    ToastUtils.showShort(R.string.savesucceed);
                    return;
                }
                ToastUtils.showShort(R.string.savefailed);
            });
        }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void loadHead(GroupResponse data) {
        String s = "";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < data.getCustomers().size(); i++) {
            stringBuilder.append(data.getCustomers().get(i).getHeadPortrait() + ",");
            if (i == data.getCustomers().size() - 1 || i == 8) {
                s = stringBuilder.substring(0, stringBuilder.length() - 1);
                break;
            }
        }

        ImageUtil.loadGroupPortrait(ivHead, s);
    }

    @SuppressLint("CheckResult")
    private void getCodeBitmap() {
        Observable.create((ObservableOnSubscribe<Bitmap>) e -> {
            bitmap = QRCodeEncoder.syncEncodeQRCode(uri2Code, UIUtil.dip2px(this, 192), Color.BLACK);
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
