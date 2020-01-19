package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.BaseResponse;
import com.zxjk.duoduo.bean.response.GetPaymentListBean;
import com.zxjk.duoduo.bean.response.GetRedNewPersonInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.minepage.wallet.ChooseCoinActivity;
import com.zxjk.duoduo.ui.widget.dialog.PayEnterDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.DateTimePicker;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class EnterGroupGetRedActivity extends BaseActivity {
    private GetPaymentListBean result;
    private ArrayList<GetPaymentListBean> list = new ArrayList<>();

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String groupId;

    private Switch sw;
    private TextView tvTitle;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvEach;
    private TextView tvAll;

    private ImageView ivCoinIcon;
    private TextView tvCoin;
    private TextView tvUnit1;
    private TextView tvUnit2;
    private TextView mRecord;

    private ImageView imgIc1;
    private ImageView imgIc2;
    private ImageView imgIc3;
    private ImageView imgIc4;
    private ImageView imgIc5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_group_get_red);

        initView();

        initData();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.new_redpackage_manage);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        mRecord = findViewById(R.id.tv_end);
        mRecord.setVisibility(View.VISIBLE);
        mRecord.setText("空投记录");

        sw = findViewById(R.id.sw);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvEach = findViewById(R.id.tvEach);
        tvAll = findViewById(R.id.tvAll);

        ivCoinIcon = findViewById(R.id.ivCoinIcon);
        tvCoin = findViewById(R.id.tvCoin);
        tvUnit1 = findViewById(R.id.tvUnit1);
        tvUnit2 = findViewById(R.id.tvUnit2);

        imgIc1 = findViewById(R.id.img_ic1);
        imgIc2 = findViewById(R.id.img_ic2);
        imgIc3 = findViewById(R.id.img_ic3);
        imgIc4 = findViewById(R.id.img_ic4);
        imgIc5 = findViewById(R.id.img_ic5);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        groupId = getIntent().getStringExtra("groupId");
        Intent intent = new Intent(EnterGroupGetRedActivity.this,DropRedRecordActivity.class);
        intent.putExtra("groupId",groupId);
        mRecord.setOnClickListener(v -> startActivity(intent));
        Api api = ServiceFactory.getInstance().getBaseService(Api.class);
        sw.setOnClickListener(v -> {
            GetRedNewPersonInfoResponse request = new GetRedNewPersonInfoResponse();
            request.setGroupId(groupId);
            if (sw.isChecked()) {
                imgIc1.setVisibility(View.GONE);
                imgIc2.setVisibility(View.GONE);
                imgIc3.setVisibility(View.GONE);
                imgIc4.setVisibility(View.GONE);
                imgIc5.setVisibility(View.GONE);
                if (result == null) {
                    ToastUtils.showShort(R.string.select_cointype);
                    return;
                }
                request.setSymbol(result.getSymbol());
                if (tvAll.getText().equals("0") || tvEach.getText().equals("0") ||
                        tvEndTime.getText().equals("请设置") || tvStartTime.getText().equals("请设置")) {
                    ToastUtils.showShort(R.string.please_setall);
                    sw.setChecked(false);
                    return;
                } else if (Float.parseFloat(tvAll.getText().toString()) < Float.parseFloat(tvEach.getText().toString())) {
                    ToastUtils.showShort(R.string.all_less_each);
                    sw.setChecked(false);
                    return;
                } else {
                    //open
                    try {
                        request.setRedNewPersonStartTime(String.valueOf(df.parse(tvStartTime.getText().toString()).getTime()));
                        request.setRedNewPersonEndTime(String.valueOf(df1.parse(tvEndTime.getText().toString() + " 23:59:59").getTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    request.setEveryoneAwardCount(tvEach.getText().toString());
                    request.setAwardSum(tvAll.getText().toString());
                    request.setRedNewPersonStatus("1");
                }
            } else {
                //close
                request.setSymbol(result.getSymbol());
                request.setRedNewPersonStatus("0");
                imgIc1.setVisibility(View.VISIBLE);
                imgIc2.setVisibility(View.VISIBLE);
                imgIc3.setVisibility(View.VISIBLE);
                imgIc4.setVisibility(View.VISIBLE);
                imgIc5.setVisibility(View.VISIBLE);
            }
            api.upRedNewPersonInfo(GsonUtils.toJson(request, false))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .compose(RxSchedulers.normalTrans())
                    .subscribe(s -> ToastUtils.showShort(R.string.update_success), t -> {
                        handleApiError(t);
                        sw.setChecked(!sw.isChecked());
                    });
        });

        api.getRedNewPersonInfo(groupId)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .flatMap((Function<GetRedNewPersonInfoResponse, Observable<BaseResponse<List<GetPaymentListBean>>>>) r -> {
                    runOnUiThread(() -> {
                        if (!TextUtils.isEmpty(r.getSymbol())) {
                            result = new GetPaymentListBean();
                            result.setSymbol(r.getSymbol());
                            result.setLogo(r.getLogo());
                        }
                        if (r.getRedNewPersonStatus().equals("1")) {
                            tvStartTime.setText(df.format(Long.parseLong(r.getRedNewPersonStartTime())));
                            tvEndTime.setText(df.format(Long.parseLong(r.getRedNewPersonEndTime())));
                            tvEach.setText(r.getEveryoneAwardCount());
                            tvAll.setText(r.getAwardSum());
                            sw.setChecked(true);
                        } else {
                            sw.setChecked(false);
                        }
                        if(sw.isChecked()){
                            imgIc1.setVisibility(View.GONE);
                            imgIc2.setVisibility(View.GONE);
                            imgIc3.setVisibility(View.GONE);
                            imgIc4.setVisibility(View.GONE);
                            imgIc5.setVisibility(View.GONE);
                        }else {
                            imgIc1.setVisibility(View.VISIBLE);
                            imgIc2.setVisibility(View.VISIBLE);
                            imgIc3.setVisibility(View.VISIBLE);
                            imgIc4.setVisibility(View.VISIBLE);
                            imgIc5.setVisibility(View.VISIBLE);
                        }
                    });
                    return api.getPaymentList();
                })
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(l -> {
                    list.addAll(l);
                    if (result == null) {
                        result = list.get(0);
                    }
                    GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
                    tvCoin.setText(result.getSymbol());
                    tvUnit1.setText(result.getSymbol());
                    tvUnit2.setText(result.getSymbol());
                }, t -> {
                    handleApiError(t);
                    finish();
                });
    }

    @SuppressLint("CheckResult")
    public void setupEach(View view) {
        if(!sw.isChecked()){
            PayEnterDialog payEnterDialog = new PayEnterDialog(this);
            payEnterDialog.setOnCommitClick(str -> {
                payEnterDialog.dismiss();
                if (Float.parseFloat(str) == 0) {
                    ToastUtils.showShort(R.string.input_money1);
                    return;
                }

                if (Float.parseFloat(tvAll.getText().toString()) != 0 && Float.parseFloat(str) > Float.parseFloat(tvAll.getText().toString())) {
                    ToastUtils.showShort(R.string.each_more_all);
                    return;
                }

                if (sw.isChecked()) {
                    GetRedNewPersonInfoResponse request = new GetRedNewPersonInfoResponse();
                    request.setGroupId(groupId);
                    request.setEveryoneAwardCount(str);
                    request.setSymbol(result.getSymbol());
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
        }else {
            ToastUtils.showShort(R.string.close_payenter_first);
            return;
        }
    }

    @SuppressLint("CheckResult")
    public void setupAll(View view) {
        if(!sw.isChecked()){
            PayEnterDialog payEnterDialog = new PayEnterDialog(this);
            payEnterDialog.setOnCommitClick(str -> {
                payEnterDialog.dismiss();
                if (Float.parseFloat(str) == 0) {
                    ToastUtils.showShort(R.string.input_money1);
                    return;
                }
                if (Float.parseFloat(str) < Float.parseFloat(tvEach.getText().toString())) {
                    ToastUtils.showShort(R.string.all_less_each);
                    return;
                }

                if (sw.isChecked()) {
                    GetRedNewPersonInfoResponse request = new GetRedNewPersonInfoResponse();
                    request.setGroupId(groupId);
                    request.setAwardSum(str);
                    request.setSymbol(result.getSymbol());
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
        }else {
            ToastUtils.showShort(R.string.close_payenter_first);
            return;
        }
    }

    @SuppressLint("CheckResult")
    public void setupStart(View view) {
        if(!sw.isChecked()){
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
                    request.setSymbol(result.getSymbol());
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
        }else {
            ToastUtils.showShort(R.string.close_payenter_first);
            return;
        }
    }

    @SuppressLint("CheckResult")
    public void setupEnd(View view) {
        if(!sw.isChecked()){
            if (tvStartTime.getText().equals("请设置")) {
                ToastUtils.showShort(R.string.please_set_start_time);
                return;
            }
            DatePicker datePicker = new DatePicker(this, DateTimePicker.YEAR_MONTH_DAY);
            datePicker.setOnDatePickListener((DatePicker.OnYearMonthDayPickListener) (year, month, day) -> {
                if (sw.isChecked()) {
                    GetRedNewPersonInfoResponse request = new GetRedNewPersonInfoResponse();
                    request.setGroupId(groupId);
                    request.setSymbol(result.getSymbol());
                    try {
                        request.setRedNewPersonEndTime(String.valueOf(df1.parse(year + "-" + month + "-" + day + " 23:59:59").getTime()));
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
            String[] nowString = TimeUtils.getNowString(new SimpleDateFormat("yyyy-MM-dd")).split("-");
            datePicker.setRangeStart(Integer.parseInt(nowString[0]),
                    Integer.parseInt(nowString[1]), Integer.parseInt(nowString[2]));
            datePicker.setRangeEnd(Integer.parseInt(nowString[0]) + 3, Integer.parseInt(nowString[1]), 1);
            initPicker(datePicker);
            datePicker.show();
        }else {
            ToastUtils.showShort(R.string.close_payenter_first);
            return;
        }
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

    public void chooseCoin(View view) {
        if (sw.isChecked()) {
            ToastUtils.showShort(R.string.close_payenter_first);
            return;
        }
        Intent intent = new Intent(this, ChooseCoinActivity.class);
        intent.putExtra("data", list);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == 1 && resultCode == 1) {
            result = data.getParcelableExtra("result");
            GlideUtil.loadCircleImg(ivCoinIcon, result.getLogo());
            tvCoin.setText(result.getSymbol());
            tvUnit1.setText(result.getSymbol());
            tvUnit2.setText(result.getSymbol());
        }
    }
}
