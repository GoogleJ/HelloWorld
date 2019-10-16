package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetMainSymbolByCustomerIdBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.AesUtil;
import com.zxjk.duoduo.utils.CommonUtils;

public class DeleteBlockWalletActivity extends BaseActivity {

    private EditText etInput;
    private GetMainSymbolByCustomerIdBean data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_block_wallet);

        data = getIntent().getParcelableExtra("data");
        if (data == null) return;

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.delete_wallet);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        etInput = findViewById(R.id.etInput);
    }

    @SuppressLint("CheckResult")
    public void delete(View view) {
        String input = etInput.getText().toString().trim();

        if (TextUtils.isEmpty(input)) {
            ToastUtils.showShort(R.string.input_empty);
            return;
        }

        ServiceFactory.getInstance().getBaseService(Api.class)
                .deleteWalletByWords(data.getWalletAddress(), data.getImportMethod(), data.getSymbol(),
                        AesUtil.getInstance().encrypt(input))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(s -> {
                    ToastUtils.showShort(R.string.delete_wallet_success);
                    setResult(1);
                    finish();
                }, this::handleApiError);
    }
}
