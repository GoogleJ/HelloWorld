package com.zxjk.duoduo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.NiceDialog;
import com.shehuan.nicedialog.ViewConvertListener;
import com.shehuan.nicedialog.ViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.scanuri.Action1;
import com.zxjk.duoduo.ui.minepage.scanuri.BaseUri;
import com.zxjk.duoduo.ui.msgpage.AgreeGroupChatActivity;
import com.zxjk.duoduo.ui.msgpage.TransferActivity;
import com.zxjk.duoduo.ui.socialspace.SocialHomeActivity;
import com.zxjk.duoduo.ui.socialspace.SocialQRCodeActivity;
import com.zxjk.duoduo.ui.wallet.PayAliActivity;
import com.zxjk.duoduo.utils.AesUtil;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.SaveImageUtil;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;

public class EnlargeImageActivity extends BaseActivity {
    @BindView(R.id.pager)
    ViewPager pager;

    private Bitmap currentBitmap;
    private ArrayList<Message> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BarUtils.setStatusBarVisibility(this, false);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setNavigationBarColor(0x00000000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

        setContentView(R.layout.activity_enlarge_image);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getBundleExtra("images");
        images = bundle.getParcelableArrayList("images");

        PagerAdapter pagerAdapter = new PagerAdapter();

        pager.setAdapter(pagerAdapter);

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentBitmap = null;
                getNowSelectedImgBitmap(position);
            }
        });
        int index = getIntent().getIntExtra("index", 0);
        pager.setCurrentItem(index);
        if (index == 0) {
            getNowSelectedImgBitmap(0);
        }
    }

    private void getNowSelectedImgBitmap(int position) {
        if (images.size() == 0) {
            return;
        }
        String sMessage;
        Message message = images.get(position);
        if (((ImageMessage) message.getContent()).getLocalUri() == null) {
            if (((ImageMessage) message.getContent()).getMediaUrl() == null) {
                if (((ImageMessage) message.getContent()).getThumUri() == null) {
                    sMessage = "";
                } else {
                    sMessage = ((ImageMessage) message.getContent()).getThumUri().toString();
                }
            } else {
                sMessage = ((ImageMessage) message.getContent()).getMediaUrl().toString();
            }
        } else {
            sMessage = ((ImageMessage) message.getContent()).getLocalUri().toString();
        }
        Glide.with(EnlargeImageActivity.this)
                .asBitmap()
                .load(sMessage)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        currentBitmap = resource;
                        return false;
                    }
                }).submit();
    }

    @SuppressLint("StaticFieldLeak")
    private void decode(final Bitmap bitmap) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return QRCodeDecoder.syncDecodeQRCode(bitmap);
            }

            @Override
            protected void onPostExecute(String result) {
                if (TextUtils.isEmpty(result)) {
                    ToastUtils.showShort(R.string.decode_qr_failure);
                } else {
                    parseResult(result);
                }
            }

            @SuppressLint("CheckResult")
            private void parseResult(String result) {
                if (parseShareResult(result)) return;
                if (!TextUtils.isEmpty(result) && result.contains("qr.alipay.com") || result.contains("QR.ALIPAY.COM")) {
                    Intent intent = new Intent(EnlargeImageActivity.this, PayAliActivity.class);
                    intent.putExtra("qrdata", result);
                    startActivity(intent);
                    finish();
                    return;
                }

                String regexUrl = "^https?:/{2}\\w.+$";
                if (RegexUtils.isMatch(regexUrl, result)) {
                    Intent intent = new Intent(EnlargeImageActivity.this, WebActivity.class);
                    intent.putExtra("url", result);
                    startActivity(intent);
                    finish();
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Object schem = jsonObject.opt("schem");
                    if (!schem.equals("com.zxjk.duoduo")) {
                        throw new RuntimeException();
                    }

                    Object action = jsonObject.opt("action");
                    if (action.equals("action1")) {
                        BaseUri<Action1> uri = new Gson().fromJson(result, new TypeToken<BaseUri<Action1>>() {
                        }.getType());
                        Intent intent = new Intent(EnlargeImageActivity.this, TransferActivity.class);
                        intent.putExtra("fromScan", true);
                        intent.putExtra("money", uri.data.money);
                        intent.putExtra("userId", uri.data.userId);
                        intent.putExtra("symbol", uri.data.symbol);
                        intent.putExtra("logo", uri.data.logo);
                        startActivity(intent);
                        finish();
                    } else if (action.equals("action2")) {
                        BaseUri<String> uri = new Gson().fromJson(result, new TypeToken<BaseUri<String>>() {
                        }.getType());
                        String userId = uri.data;
                        CommonUtils.resolveFriendList(EnlargeImageActivity.this, userId);
                    } else if (action.equals("action3")) {
                        ToastUtils.showShort(R.string.decode_qr_failure);
                    } else if (action.equals("action4")) {
                        BaseUri<SocialQRCodeActivity.QRCodeData> uri = new Gson().fromJson(result, new TypeToken<BaseUri<SocialQRCodeActivity.QRCodeData>>() {
                        }.getType());
                        Intent intent = new Intent(EnlargeImageActivity.this, SocialHomeActivity.class);
                        intent.putExtra("id", uri.data.groupId);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    ToastUtils.showShort(R.string.decode_qr_failure);
                }
            }
        }.execute();
    }


    private boolean parseShareResult(String result) {
        if (result.contains(Constant.APP_SHARE_URL)) {
            try {
                String[] shareStrings = result.split("\\?");

                String decryptResult = AesUtil.getInstance().decrypt(shareStrings[1]);

                String resultUri = "http://hilamg-share.zhumengxuanang.com/?"+decryptResult;
                if (decryptResult.contains("groupId")) {

                    //groupQR
//                    String groupId = decryptResult.split("=")[1];

                    Uri uri = Uri.parse(resultUri);
                    String groupId = uri.getQueryParameter("groupId");

                    Intent intent = new Intent(this, AgreeGroupChatActivity.class);
                    intent.putExtra("groupId", groupId);

                    startActivity(intent);
                    finish();
                } else {
                    //userQR
                    Uri uri = Uri.parse(resultUri);
                    String userId = uri.getQueryParameter("id");
                    CommonUtils.resolveFriendList(this, userId, true);
                }
            } catch (Exception e) {
                return false;
            }

            return true;
        }
        return false;
    }

    private float getInitImageScale(Bitmap bitmap) {
        int width = ScreenUtils.getScreenWidth();
        int height = ScreenUtils.getScreenHeight();
        // 拿到图片的宽和高
        int dw = bitmap.getWidth();
        int dh = bitmap.getHeight();
        float scale = 1.0f;
        //图片宽度大于屏幕，但高度小于屏幕，则缩小图片至填满屏幕宽
        if (dw > width && dh <= height) {
            scale = width * 1.0f / dw;
        }
        //图片宽度小于屏幕，但高度大于屏幕，则放大图片至填满屏幕宽
        if (dw <= width && dh > height) {
            scale = width * 1.0f / dw;
        }
        //图片高度和宽度都小于屏幕，则放大图片至填满屏幕宽
        if (dw < width && dh < height) {
            scale = width * 1.0f / dw;
        }
        //图片高度和宽度都大于屏幕，则缩小图片至填满屏幕宽
        if (dw > width && dh > height) {
            scale = width * 1.0f / dw;
        }
        return scale;
    }

    class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {

        @Override
        public int getCount() {
            return EnlargeImageActivity.this.images.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            View view = initView(position);

            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        private View initView(int position) {
            RelativeLayout layout = new RelativeLayout(EnlargeImageActivity.this);
            SubsamplingScaleImageView imageView = new SubsamplingScaleImageView(EnlargeImageActivity.this);
//            SpinKitView progressBar = new SpinKitView(EnlargeImageActivity.this);
//            progressBar.setIndeterminateDrawable(SpriteFactory.create(Style.FADING_CIRCLE));

            TextView textView = new TextView(EnlargeImageActivity.this);
            textView.setText(R.string.click_retry);
            textView.setTextSize(17);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(ContextCompat.getColor(EnlargeImageActivity.this, R.color.white));
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(CommonUtils.dip2px(EnlargeImageActivity.this, 32), CommonUtils.dip2px(EnlargeImageActivity.this, 32));
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            layout.addView(imageView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            layout.addView(textView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            layout.addView(progressBar, layoutParams);
//            progressBar.setVisibility(View.GONE);

            textView.setOnClickListener(v -> {
                textView.setVisibility(View.GONE);
//                progressBar.setVisibility(View.VISIBLE);
                String sMessage;
                Message message = images.get(position);
                if (((ImageMessage) message.getContent()).getLocalUri() == null) {
                    if (((ImageMessage) message.getContent()).getMediaUrl() == null) {
                        if (((ImageMessage) message.getContent()).getThumUri() == null) {
                            sMessage = "";
                        } else {
                            sMessage = ((ImageMessage) message.getContent()).getThumUri().toString();
                        }
                    } else {
                        sMessage = ((ImageMessage) message.getContent()).getMediaUrl().toString();
                    }
                } else {
                    sMessage = ((ImageMessage) message.getContent()).getLocalUri().toString();
                }

                Glide.with(EnlargeImageActivity.this).load(sMessage).into(new CustomViewTarget<SubsamplingScaleImageView, Drawable>(imageView) {
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                        progressBar.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                        progressBar.setVisibility(View.GONE);
                        Bitmap bitmap = ImageUtils.drawable2Bitmap(resource);
                        float initImageScale = getInitImageScale(bitmap);
                        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
                        imageView.setMinScale(initImageScale);//最小显示比例
                        imageView.setMaxScale(initImageScale + 2.0f);//最大显示比例
                        imageView.setImage((ImageSource.bitmap(bitmap)), new ImageViewState(initImageScale, new PointF(0, 0), 0));
                    }

                    @Override
                    protected void onResourceCleared(@Nullable Drawable placeholder) {
                        textView.setVisibility(View.GONE);
//                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onStop() {
//                        progressBar.setVisibility(View.GONE);
                        super.onStop();
                    }

                    @Override
                    public void onDestroy() {
//                        progressBar.setVisibility(View.GONE);
                        super.onDestroy();
                    }

                });
            });

            textView.performClick();

            imageView.setOnClickListener(v -> finishAfterTransition());

            imageView.setOnLongClickListener(v -> {
                NiceDialog.init().setLayoutId(R.layout.layout_general_dialog6).setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        holder.setText(R.id.tv_photograph, R.string.save_picture);
                        holder.setText(R.id.tv_photo_select, R.string.parse_qr);

                        //识别二维码
                        holder.setOnClickListener(R.id.tv_photo_select, v -> {
                            dialog.dismiss();
                            if (currentBitmap == null) {
                                return;
                            }
                            decode(currentBitmap);
                        });

                        //保存图片
                        holder.setOnClickListener(R.id.tv_photograph, v1 -> getPermisson(g -> {
                            //保存到手机
                            dialog.dismiss();
                            if (currentBitmap == null) {
                                return;
                            }
                            SaveImageUtil.get().savePic(currentBitmap, success -> {
                                if (success) {
                                    ToastUtils.showShort(R.string.savesucceed);
                                    return;
                                }
                                ToastUtils.showShort(R.string.savefailed);
                            });
                        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE));

                        //取消
                        holder.setOnClickListener(R.id.tv_cancel, v -> dialog.dismiss());
                    }
                }).setShowBottom(true)
                        .setOutCancel(true)
                        .setDimAmount(0.5f)
                        .show(getSupportFragmentManager());
                return true;
            });
            return layout;
        }
    }

}
