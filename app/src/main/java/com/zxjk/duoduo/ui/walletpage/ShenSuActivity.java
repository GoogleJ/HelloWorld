package com.zxjk.moneyspace.ui.walletpage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.request.ShenSuRequest;
import com.zxjk.moneyspace.bean.response.GetOverOrderResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.OssUtils;
import com.zxjk.moneyspace.utils.TakePicUtil;

import java.io.File;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShenSuActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    private EditText et;

    private LinearLayout llContainer;

    private FrameLayout fl1;
    private ImageView iv1;
    private ImageView ivedit1;

    private FrameLayout fl2;
    private ImageView iv2;
    private ImageView ivedit2;

    private FrameLayout fl3;
    private ImageView iv3;
    private ImageView ivedit3;

    private String url1 = "";
    private String url2 = "";
    private String url3 = "";
    private int currentImg = 1;
    private String appealType = "";


    private ImageView ivadd1, ivadd2, ivadd3;

    private TextView tv_appealType;


    private static final int REQUEST_TAKE = 1;
    private static final int REQUEST_ALBUM = 2;
    String rate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shen_su);
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.shensu));
        rate = getIntent().getStringExtra("rate");
        ivadd1 = findViewById(R.id.iv_add1);
        ivadd2 = findViewById(R.id.iv_add2);
        ivadd3 = findViewById(R.id.iv_add3);

        et = findViewById(R.id.et);
        llContainer = findViewById(R.id.llContainer);

        fl1 = findViewById(R.id.fl1);
        iv1 = findViewById(R.id.iv1);
        ivedit1 = findViewById(R.id.ivedit1);
        fl2 = findViewById(R.id.fl2);
        iv2 = findViewById(R.id.iv2);
        ivedit2 = findViewById(R.id.ivedit2);
        fl3 = findViewById(R.id.fl3);
        iv3 = findViewById(R.id.iv3);
        ivedit3 = findViewById(R.id.ivedit3);

        tv_appealType = findViewById(R.id.tv_appealType);
        RelativeLayout rl_appealType = findViewById(R.id.rl_appealType);


        iv1.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            ViewGroup.LayoutParams layoutParams = llContainer.getLayoutParams();
            layoutParams.height = iv1.getWidth();
            llContainer.setLayoutParams(layoutParams);
        });

        getPermisson(fl1, result -> {
            if (result) {
                currentImg = 1;
                dialogType();
            }
        }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        getPermisson(fl2, result -> {
            if (result) {
                currentImg = 2;
                dialogType();
            }
        }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        getPermisson(fl3, result -> {
            if (result) {
                currentImg = 3;
                dialogType();
            }
        }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        fl1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fl1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ViewGroup.LayoutParams layoutParams = iv1.getLayoutParams();
                layoutParams.width = fl1.getWidth();
                layoutParams.height = fl1.getWidth();
                iv1.setLayoutParams(layoutParams);
                iv2.setLayoutParams(layoutParams);
                iv3.setLayoutParams(layoutParams);


            }
        });

        rl_appealType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appealType();

            }
        });
    }

    private void appealType() {
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog12).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                holder.setOnClickListener(R.id.tv_appealType0, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appealType = "0";
                        dialog.dismiss();
                        tv_appealType.setText("恶意买卖");
                    }
                });
                holder.setOnClickListener(R.id.tv_appealType1, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appealType = "1";
                        dialog.dismiss();
                        tv_appealType.setText("买家未付款");
                    }
                });
                holder.setOnClickListener(R.id.tv_appealType2, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appealType = "2";
                        dialog.dismiss();
                        tv_appealType.setText("卖家不释放币");
                    }
                });
                holder.setOnClickListener(R.id.tv_appealType3, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appealType = "3";
                        dialog.dismiss();
                        tv_appealType.setText("长时间无回应");
                    }
                });
                holder.setOnClickListener(R.id.tv_appealType4, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appealType = "4";
                        dialog.dismiss();
                        tv_appealType.setText("其它");
                    }
                });
                holder.setOnClickListener(R.id.rl_close, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        }).setShowBottom(true)
                .setOutCancel(false)
                .setDimAmount(0.5f)
                .show(getSupportFragmentManager());
    }

    //提交
    @SuppressLint("CheckResult")
    public void commit(View view) {
        if (TextUtils.isEmpty(et.getText().toString().trim())) {
            ToastUtils.showShort(R.string.input_reason);
            return;
        }

        if (TextUtils.isEmpty(tv_appealType.getText().toString().trim())) {
            ToastUtils.showShort("请选择申诉类型");
            return;
        }

        if (TextUtils.isEmpty(url1)) {
            ToastUtils.showShort(R.string.upload_picture);
            return;
        }

        GetOverOrderResponse data = (GetOverOrderResponse) getIntent().getSerializableExtra("data");
        ShenSuRequest request = new ShenSuRequest();
        request.setPlaintiffId(Constant.userId);
        request.setPlaintiffDuoduoId(Constant.currentUser.getDuoduoId());
        request.setBothOrderId(data.getBothOrderId());
        request.setAppealReason(et.getText().toString().trim());
        request.setCurrency(data.getCurrency());
        request.setPictureOne(url1);
        request.setPictureTwo(url2);
        request.setPictureThree(url3);
        request.setContact(Constant.currentUser.getMobile());
        request.setAppealType(appealType);
        if (Constant.userId.equals(data.getSellId())) {
            request.setIndicteeId(data.getBuyId());
            request.setIndicteeDuoduoId(data.getBuyDuoduoId());
        } else {
            request.setIndicteeId(data.getSellId());
            request.setIndicteeDuoduoId(data.getSellDuoduoId());
        }
        ServiceFactory.getInstance().getBaseService(Api.class)
                .addAppeal(new Gson().toJson(request))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    ToastUtils.showShort(getString(R.string.shensu_success));
                    Intent intent = new Intent(this, com.zxjk.moneyspace.ui.walletpage.ExchangeListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("rate", rate);
                    startActivity(intent);
                }, this::handleApiError);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (corpFile != null) {
            zipFile(Collections.singletonList(corpFile.getPath()), files -> {
                File file = files.get(0);
                OssUtils.uploadFile(file.getAbsolutePath(), url -> {
                    if (currentImg == 1) {
                        url1 = url;
                        Glide.with(this).load(file).into(iv1);
                        ivedit1.setVisibility(View.VISIBLE);
                        fl2.setVisibility(View.VISIBLE);
                        ivadd1.setVisibility(View.GONE);
                    } else if (currentImg == 2) {
                        url2 = url;
                        Glide.with(this).load(file).into(iv2);
                        ivedit2.setVisibility(View.VISIBLE);
                        fl3.setVisibility(View.VISIBLE);
                        ivadd2.setVisibility(View.GONE);
                    } else {
                        url3 = url;
                        Glide.with(this).load(file).into(iv3);
                        ivedit3.setVisibility(View.VISIBLE);
                        ivadd3.setVisibility(View.GONE);
                    }
                });
            });
        }
    }

    @OnClick(R.id.rl_back)
    public void onClick() {
        finish();
    }

    private void dialogType() {
        KeyboardUtils.hideSoftInput(ShenSuActivity.this);
        NiceDialog.init().setLayoutId(R.layout.layout_general_dialog6).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                //拍照
                holder.setOnClickListener(R.id.tv_photograph, v -> {
                    dialog.dismiss();
                    TakePicUtil.takePicture(ShenSuActivity.this);
                });
                //相册选择
                holder.setOnClickListener(R.id.tv_photo_select, v -> {
                    dialog.dismiss();
                    TakePicUtil.albumPhoto(ShenSuActivity.this);
                });
                //取消
                holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());

            }
        }).setShowBottom(true)
                .setOutCancel(true)
                .setDimAmount(0.5f)
                .show(getSupportFragmentManager());
    }

}
