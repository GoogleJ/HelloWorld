package com.zxjk.duoduo.ui.wallet;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.ByBoinsResponse;
import com.zxjk.duoduo.ui.base.BaseActivity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BusinessDetailsActivity extends BaseActivity {

    private TextView tvBusinessName;
    private TextView tvRegisterTime;
    private TextView tvOrdersComplete;
    private TextView tvOrdersRate;
    private TextView tvIssueTimeAvg;
    private TextView tvKycLevel1;
    private TextView tvKycLevel2;
    private TextView tvPhone;
    private TextView tvEmail;
    private ByBoinsResponse byBoinsResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);
        setTrasnferStatusBar(true);

        initView();

        initData();
    }

    private void initView() {
        tvBusinessName = findViewById(R.id.tv_business_name);
        tvRegisterTime = findViewById(R.id.tv_register_time);
        tvOrdersComplete = findViewById(R.id.tv_orders_complete);
        tvOrdersRate = findViewById(R.id.tv_orders_rate);
        tvIssueTimeAvg = findViewById(R.id.tv_issue_time_avg);
        tvKycLevel1 = findViewById(R.id.tv_kycLevel1);
        tvKycLevel2 = findViewById(R.id.tv_kycLevel2);
        tvPhone = findViewById(R.id.tv_phone);
        tvEmail = findViewById(R.id.tv_email);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }

    private void initData() {
        byBoinsResponse = (ByBoinsResponse) getIntent().getSerializableExtra("ByBoinsResponse");

        tvBusinessName.setText(byBoinsResponse.getBusinessName());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String sd = sdf.format(new Date(Long.parseLong(byBoinsResponse.getRegisterTime())));
        tvRegisterTime.setText(getString(R.string.registration_time,sd));
        tvOrdersComplete.setText(byBoinsResponse.getOrdersComplete());
        tvOrdersRate.setText(byBoinsResponse.getOrdersRate()+"%");

        DecimalFormat df4 = new DecimalFormat("###");
        tvIssueTimeAvg.setText(df4.format(Double.parseDouble(byBoinsResponse.getIssueTimeAvg())/1000/60));

        setDrawables(getResources().getDrawable(R.drawable.ic_phone_authentication,null),getResources().getDrawable(R.drawable.ic_selected,null),tvPhone);
        setDrawables(getResources().getDrawable(R.drawable.ic_email_authentication,null),getResources().getDrawable(R.drawable.ic_selected,null),tvEmail);
        if(byBoinsResponse.getKycLevel().equals("1")){
            setDrawables(getResources().getDrawable(R.drawable.ic_real_name_authentication,null),getResources().getDrawable(R.drawable.ic_selected,null),tvKycLevel1);
            setDrawables(getResources().getDrawable(R.drawable.ic_senior_certification,null),getResources().getDrawable(R.drawable.ic_no_selected,null),tvKycLevel2);
        }else {
            setDrawables(getResources().getDrawable(R.drawable.ic_real_name_authentication,null),getResources().getDrawable(R.drawable.ic_selected,null),tvKycLevel1);
            setDrawables(getResources().getDrawable(R.drawable.ic_senior_certification,null),getResources().getDrawable(R.drawable.ic_selected,null),tvKycLevel2);
        }
    }

    private void setDrawables(Drawable drawable, Drawable drawable2, TextView textView) {

        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                .getMinimumHeight());// 设置边界
        if (drawable2 != null) {
            drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2
                    .getMinimumHeight());
        }
        textView.setCompoundDrawables(drawable, null, drawable2 != null ? drawable2 : null, null);
        textView.setCompoundDrawablePadding(8);
    }
}
