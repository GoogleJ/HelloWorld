package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetRedNewPersonInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.dialog.PayEnterDialog;
import com.zxjk.duoduo.utils.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.DateTimePicker;

public class EnterGroupGetRedActivity extends BaseActivity {
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private String groupId;

    private Switch sw;

    private TextView tvTitle;
    private TextView tvBalanceLeft;
    private TextView tvNowPay;

    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvEach;
    private TextView tvAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_group_get_red);

        initView();
        initData();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.newpay);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        sw = findViewById(R.id.sw);
        tvBalanceLeft = findViewById(R.id.tvBalanceLeft);
        tvNowPay = findViewById(R.id.tvNowPay);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvEach = findViewById(R.id.tvEach);
        tvAll = findViewById(R.id.tvAll);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        groupId = getIntent().getStringExtra("groupId");

        sw.setOnClickListener(v -> {
            GetRedNewPersonInfoResponse request = new GetRedNewPersonInfoResponse();
            request.setGroupId(groupId);
            if (sw.isChecked()) {
                if (tvAll.getText().equals("0") || tvEach.getText().equals("0") ||
                        tvEndTime.getText().equals("请设置") || tvStartTime.getText().equals("请设置")) {
                    ToastUtils.showShort(R.string.please_setall);
                    sw.setChecked(false);
                    return;
                } else if (Float.parseFloat(tvAll.getText().toString()) < Float.parseFloat(tvEach.getText().toString())) {
                    ToastUtils.showShort(R.string.all_less_each);
                    sw.setChecked(false);
                    return;
                } else if (Float.parseFloat(tvAll.getText().toString()) >
                        Float.parseFloat(tvBalanceLeft.getText().toString())) {
                    ToastUtils.showShort(R.string.nobalance);
                    sw.setChecked(false);
                    return;
                } else {
                    //open
                    try {
                        request.setRedNewPersonStartTime(String.valueOf(df.parse(tvStartTime.getText().toString()).getTime()));
                        request.setRedNewPersonEndTime(String.valueOf(df.parse(tvEndTime.getText().toString()).getTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    request.setEveryoneAwardCount(tvEach.getText().toString());
                    request.setAwardSum(tvAll.getText().toString());
                    request.setRedNewPersonStatus("1");
                }
            } else {
                //close
                request.setRedNewPersonStatus("0");
            }
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .upRedNewPersonInfo(GsonUtils.toJson(request, false))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .compose(RxSchedulers.normalTrans())
                    .subscribe(s -> ToastUtils.showShort(R.string.update_success), t -> {
                        handleApiError(t);
                        sw.setChecked(!sw.isChecked());
                    });
        });

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getRedNewPersonInfo(groupId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(response -> {
                    tvBalanceLeft.setText(response.getBalance());
                    if (response.getRedNewPersonStatus().equals("1")) {
                        tvStartTime.setText(df.format(Long.parseLong(response.getRedNewPersonStartTime())));
                        tvEndTime.setText(df.format(Long.parseLong(response.getRedNewPersonEndTime())));
                        tvEach.setText(response.getEveryoneAwardCount());
                        tvAll.setText(response.getAwardSum());
                        tvNowPay.setText(response.getReceivedCount());
                        sw.setChecked(true);
                    } else {
                        sw.setChecked(false);
                    }
                }, this::handleApiError);
    }

    @SuppressLint("CheckResult")
    public void setupEach(View view) {
        PayEnterDialog payEnterDialog = new PayEnterDialog(this);
        payEnterDialog.setOnCommitClick(str -> {
            payEnterDialog.dismiss();
            if (Float.parseFloat(tvAll.getText().toString()) != 0 && Float.parseFloat(str) > Float.parseFloat(tvAll.getText().toString())) {
                ToastUtils.showShort(R.string.each_more_all);
                return;
            }

            if (sw.isChecked()) {
                GetRedNewPersonInfoResponse request = new GetRedNewPersonInfoResponse();
                request.setGroupId(groupId);
                request.setEveryoneAwardCount(str);
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .upRedNewPersonInfo(GsonUtils.toJson(request))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            tvEach.setText(str);
                            ToastUtils.showShort(R.string.update_success);
                        }, this::handleApiError);
                return;
            }
            tvEach.setText(str);
        });
        payEnterDialog.show(getString(R.string.setupeach));
    }

    @SuppressLint("CheckResult")
    public void setupAll(View view) {
        PayEnterDialog payEnterDialog = new PayEnterDialog(this);
        payEnterDialog.setOnCommitClick(str -> {
            payEnterDialog.dismiss();
            if (Float.parseFloat(str) < Float.parseFloat(tvEach.getText().toString())) {
                ToastUtils.showShort(R.string.all_less_each);
                return;
            }

            if (sw.isChecked()) {
                if (Float.parseFloat(str) >
                        Float.parseFloat(tvBalanceLeft.getText().toString())) {
                    ToastUtils.showShort(R.string.nobalance);
                    return;
                }
                GetRedNewPersonInfoResponse request = new GetRedNewPersonInfoResponse();
                request.setGroupId(groupId);
                request.setAwardSum(str);
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .upRedNewPersonInfo(GsonUtils.toJson(request))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            tvAll.setText(str);
                            ToastUtils.showShort(R.string.update_success);
                        }, this::handleApiError);
                return;
            }
            tvAll.setText(str);
        });
        payEnterDialog.show(getString(R.string.setupall));
    }

    @SuppressLint("CheckResult")
    public void setupStart(View view) {
        DatePicker datePicker = new DatePicker(this, DateTimePicker.YEAR_MONTH_DAY);
        datePicker.setOnDatePickListener((DatePicker.OnYearMonthDayPickListener) (year, month, day)
                -> {
            String[] endTime = tvEndTime.getText().toString().split("-");
            if (!tvEndTime.getText().toString().equals("请设置") &&
                    (Integer.parseInt(year + month + day))
                            > (Integer.parseInt(endTime[0] + endTime[1] + endTime[2]))) {
                ToastUtils.showShort(R.string.starttime_less_end);
                return;
            }
            if (sw.isChecked()) {
                GetRedNewPersonInfoResponse request = new GetRedNewPersonInfoResponse();
                request.setGroupId(groupId);
                try {
                    request.setRedNewPersonStartTime(String.valueOf(df.parse(year + "-" + month + "-" + day).getTime()));
                } catch (Exception e) {
                }
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .upRedNewPersonInfo(GsonUtils.toJson(request))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            tvStartTime.setText(year + "-" + month + "-" + day);
                            ToastUtils.showShort(R.string.update_success);
                        }, this::handleApiError);
                return;
            }
            tvStartTime.setText(year + "-" + month + "-" + day);
        });
        String[] nowString = TimeUtils.getNowString(new SimpleDateFormat("yyyy-MM-dd")).split("-");
        datePicker.setRangeStart(Integer.parseInt(nowString[0]),
                Integer.parseInt(nowString[1]), Integer.parseInt(nowString[2]));
        datePicker.setRangeEnd(Integer.parseInt(nowString[0]) + 3, Integer.parseInt(nowString[1]), 1);
        initPicker(datePicker);
        datePicker.show();
    }

    @SuppressLint("CheckResult")
    public void setupEnd(View view) throws ParseException {
        if (tvStartTime.getText().equals("请设置")) {
            ToastUtils.showShort(R.string.please_set_start_time);
            return;
        }
        DatePicker datePicker = new DatePicker(this, DateTimePicker.YEAR_MONTH_DAY);
        datePicker.setOnDatePickListener((DatePicker.OnYearMonthDayPickListener) (year, month, day)
                -> {
            if (sw.isChecked()) {
                GetRedNewPersonInfoResponse request = new GetRedNewPersonInfoResponse();
                request.setGroupId(groupId);
                try {
                    request.setRedNewPersonEndTime(String.valueOf(df.parse(year + "-" + month + "-" + day).getTime()));
                } catch (Exception e) {
                }
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .upRedNewPersonInfo(GsonUtils.toJson(request))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            tvEndTime.setText(year + "-" + month + "-" + day);
                            ToastUtils.showShort(R.string.update_success);
                        }, this::handleApiError);
                return;
            }
            tvEndTime.setText(year + "-" + month + "-" + day);
        });
        String[] start = tvStartTime.getText().toString().split("-");
        long time = df.parse(start[0] + "-" + start[1] + "-" + start[2]).getTime();
        time += 86400000;
        String format = df.format(new Date(time));
        String[] split = format.split("-");

        datePicker.setRangeStart(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        datePicker.setRangeEnd(Integer.parseInt(split[0]) + 3, Integer.parseInt(split[1]), 1);
        initPicker(datePicker);
        datePicker.show();
    }

    private void initPicker(DatePicker datePicker) {
        datePicker.setTitleText("选择时间");
        datePicker.setTitleTextSize(17);
        datePicker.setTitleTextColor(ContextCompat.getColor(this, R.color.textcolor1));
        datePicker.setCancelTextColor(ContextCompat.getColor(this, R.color.textcolor3));
        datePicker.setSubmitTextColor(ContextCompat.getColor(this, R.color.colorTheme));
        datePicker.setTopLineColor(Color.parseColor("#E5E5E5"));
        datePicker.setLabel("", "", "");
        datePicker.setDividerVisible(false);
        datePicker.setTextColor(Color.parseColor("#000000"), Color.parseColor("#bababa"));
        datePicker.setTextSize(18);
        View foot = new View(this);
        foot.setBackgroundColor(Color.parseColor("#ffffff"));
        datePicker.setFooterView(foot);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommonUtils.dip2px(this, 24));
        foot.setLayoutParams(layoutParams);
    }

}
