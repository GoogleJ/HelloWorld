package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.OnlineServiceActivity;
import com.zxjk.duoduo.ui.msgpage.QrCodeActivity;
import com.zxjk.duoduo.ui.widget.NewPayBoard;
import com.zxjk.duoduo.utils.AesUtil;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MD5Utils;

import io.reactivex.functions.Consumer;

public class ImportWalletActivity extends BaseActivity {

    private RelativeLayout rlend;
    private ImageView ivRight;
    private ImageView mIvCode;
    private TextView tvWords;
    private TextView tvKey;
    private TextView tvKeystore;
    private View line1;
    private View line2;
    private View line3;
    private TextView tvImportTips1;
    private EditText etInput;
    private EditText etKeystorePwd;
    private LinearLayout llKeystoreTips;
    private CheckBox cb;
    private Button btnImport;

    private int colorTheme;
    private int colorBlack;
    private int colorWhite;
    private int colorCACACA;

    //1:words 2:key 3:keystore
    private int importType = 1;

    private String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet);

        symbol = getIntent().getStringExtra("symbol");

        colorTheme = ContextCompat.getColor(this, R.color.colorTheme);
        colorBlack = ContextCompat.getColor(this, R.color.black);
        colorWhite = ContextCompat.getColor(this, R.color.white);
        colorCACACA = Color.parseColor("#CACACA");

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.importwallet);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());


        rlend = findViewById(R.id.rl_end);
        ivRight = findViewById(R.id.iv_end);
        tvWords = findViewById(R.id.tvWords);
        tvKey = findViewById(R.id.tvKey);
        tvKeystore = findViewById(R.id.tvKeystore);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);
        tvImportTips1 = findViewById(R.id.tvImportTips1);
        etInput = findViewById(R.id.etInput);
        etKeystorePwd = findViewById(R.id.etKeystorePwd);
        llKeystoreTips = findViewById(R.id.llKeystoreTips);
        cb = findViewById(R.id.cb);
        btnImport = findViewById(R.id.btnImport);
        mIvCode = findViewById(R.id.img_code);
        mIvCode.setOnClickListener( v-> {
            Intent intent = new Intent(this, QrCodeActivity.class);
            intent.putExtra("actionType", QrCodeActivity.ACTION_IMPORT_WALLET);
            startActivityForResult(intent, 1);
        });

        ivRight.setVisibility(View.GONE);

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && cb.isChecked() && btnImport.getCurrentTextColor() == colorCACACA) {
                    if (importType != 3 || !TextUtils.isEmpty(etKeystorePwd.getText().toString().trim())) {
                        btnImport.setEnabled(true);
                        btnImport.setBackgroundResource(R.drawable.shape_theme);
                        btnImport.setTextColor(colorWhite);
                    }
                } else if ((TextUtils.isEmpty(s) || !cb.isChecked() && btnImport.getCurrentTextColor() == colorWhite)) {
                    btnImport.setEnabled(false);
                    btnImport.setBackgroundResource(R.drawable.shape_f7f8fa_5);
                    btnImport.setTextColor(colorCACACA);
                }
            }
        });

        etKeystorePwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && cb.isChecked() && btnImport.getCurrentTextColor() == colorCACACA) {
                    if (importType != 3 || !TextUtils.isEmpty(etInput.getText().toString().trim())) {
                        btnImport.setEnabled(true);
                        btnImport.setBackgroundResource(R.drawable.shape_theme);
                        btnImport.setTextColor(colorWhite);
                    }
                } else if ((TextUtils.isEmpty(s) || !cb.isChecked()) && btnImport.getCurrentTextColor() == colorWhite) {
                    btnImport.setEnabled(false);
                    btnImport.setBackgroundResource(R.drawable.shape_f7f8fa_5);
                    btnImport.setTextColor(colorCACACA);
                }
            }
        });

        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String s = etInput.getText().toString().trim();
            if (!TextUtils.isEmpty(s) && isChecked && btnImport.getCurrentTextColor() == colorCACACA) {
                if (importType != 3 || !TextUtils.isEmpty(etKeystorePwd.getText().toString().trim())) {
                    btnImport.setEnabled(true);
                    btnImport.setBackgroundResource(R.drawable.shape_theme);
                    btnImport.setTextColor(colorWhite);
                }
            } else if ((TextUtils.isEmpty(s) || !isChecked) && btnImport.getCurrentTextColor() == colorWhite) {
                if (importType != 3 || !TextUtils.isEmpty(etKeystorePwd.getText().toString().trim())) {
                    btnImport.setEnabled(false);
                    btnImport.setBackgroundResource(R.drawable.shape_f7f8fa_5);
                    btnImport.setTextColor(colorCACACA);
                }
            }
        });
    }

    public void words(View view) {
        importType = 1;
        cb.setChecked(false);
        etInput.setText("");
        etKeystorePwd.setText("");
        rlend.setVisibility(View.INVISIBLE);
        tvWords.setTextColor(colorTheme);
        mIvCode.setVisibility(View.GONE);
        tvKey.setTextColor(colorBlack);
        tvKeystore.setTextColor(colorBlack);
        line1.setVisibility(View.VISIBLE);
        line2.setVisibility(View.INVISIBLE);
        line3.setVisibility(View.INVISIBLE);
        llKeystoreTips.setVisibility(View.GONE);
        tvImportTips1.setText(R.string.importwallet_tips4);
        etInput.setHint(R.string.input_words);
    }

    public void key(View view) {
        importType = 2;
        cb.setChecked(false);
        etInput.setText("");
        etKeystorePwd.setText("");
        rlend.setVisibility(View.INVISIBLE);
        mIvCode.setVisibility(View.GONE);
        tvWords.setTextColor(colorBlack);
        tvKey.setTextColor(colorTheme);
        tvKeystore.setTextColor(colorBlack);
        line1.setVisibility(View.INVISIBLE);
        line2.setVisibility(View.VISIBLE);
        line3.setVisibility(View.INVISIBLE);
        llKeystoreTips.setVisibility(View.GONE);
        tvImportTips1.setText(R.string.input_key);
        etInput.setHint(R.string.input_key);
    }

    public void keystore(View view) {
        importType = 3;
        cb.setChecked(false);
        etInput.setText("");
        etKeystorePwd.setText("");
        rlend.setVisibility(View.INVISIBLE);
        mIvCode.setVisibility(View.VISIBLE);
        tvWords.setTextColor(colorBlack);
        tvKey.setTextColor(colorBlack);
        tvKeystore.setTextColor(colorTheme);
        line1.setVisibility(View.INVISIBLE);
        line2.setVisibility(View.INVISIBLE);
        line3.setVisibility(View.VISIBLE);
        llKeystoreTips.setVisibility(View.VISIBLE);
        tvImportTips1.setText(R.string.importwallet_tips5);
        etInput.setHint(R.string.importwallet_tips6);
    }

    private Api api = ServiceFactory.getInstance().getBaseService(Api.class);

    @SuppressLint("CheckResult")
    public void importWallet(View view) {
        String str = etInput.getText().toString();
        Consumer<String> resultConsumer = s -> {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constant.ACTION_BROADCAST1));
            ToastUtils.showShort(R.string.importwallet_success);
            finish();
        };
        if (importType == 3) {
            api.importKeyStore(symbol, AesUtil.getInstance().encrypt(str), AesUtil.getInstance().encrypt(etKeystorePwd.getText().toString().trim()))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ImportWalletActivity.this)))
                    .compose(RxSchedulers.normalTrans())
                    .subscribe(resultConsumer, this::handleApiError);
            return;
        }
        new NewPayBoard(this).show(pwd -> {
            switch (importType) {
                case 1:
                    api.importByMnemonic(symbol, AesUtil.getInstance().encrypt(str), MD5Utils.getMD5(pwd))
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ImportWalletActivity.this)))
                            .compose(RxSchedulers.normalTrans())
                            .subscribe(resultConsumer, this::handleApiError);
                    break;
                case 2:
                    api.importPrivateKey(symbol, AesUtil.getInstance().encrypt(str), MD5Utils.getMD5(pwd))
                            .compose(bindToLifecycle())
                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ImportWalletActivity.this)))
                            .compose(RxSchedulers.normalTrans())
                            .subscribe(resultConsumer, this::handleApiError);
                    break;
            }
        });
    }

    public void agreement(View view) {
        Intent intent = new Intent(this, OnlineServiceActivity.class);
        intent.putExtra("url", "https://wq0725.github.io/duoduo.statement.io/");
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1 && data != null) {
            etInput.setText("");
            etInput.setText(data.getStringExtra("result"));
        }
    }
}
