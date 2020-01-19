package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private LinearLayout mLlGroupTab;
    private Button mBtnGroupSave;

    private boolean isOpen;

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

        mLlGroupTab = findViewById(R.id.ll_group_tab);
        mBtnGroupSave = findViewById(R.id.btn_group_save);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        GetRedNewPersonInfoResponse request = new GetRedNewPersonInfoResponse();
        groupId = getIntent().getStringExtra("groupId");
        Intent intent = new Intent(EnterGroupGetRedActivity.this, DropRedRecordActivity.class);
        intent.putExtra("groupId", groupId);
        mRecord.setOnClickListener(v -> startActivity(intent));
        Api api = ServiceFactory.getInstance().getBaseService(Api.class);
        mBtnGroupSave.setOnClickListener(v -> {
            if (isOpen) {
                ToastUtils.showShort(R.string.close_payenter_first);
                return;
            }
            if (result == null) {
                ToastUtils.showShort(R.string.select_cointype);
                return;
            }
            request.setSymbol(result.getSymbol());
            if (tvAll.getText().equals("0") || tvEach.getText().equals("0") ||
                    tvEndTime.getText().equals("请设置") || tvStartTime.getText().equals("请设置")) {
                ToastUtils.showShort(R.string.please_setall);
                return;
            } else if (Float.parseFloat(tvAll.getText().toString()) < Float.parseFloat(tvEach.getText().toString())) {
                ToastUtils.showShort(R.string.all_less_each);
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
                api.upRedNewPersonInfo(GsonUtils.toJson(request, false))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .compose(RxSchedulers.normalTrans())
                        .subscribe(s -> {
                            ToastUtils.showShort(R.string.update_success);
                            isOpen = true;
                        }, t -> {
                            handleApiError(t);
                            sw.setChecked(!sw.isChecked());
                            isOpen = false;
                        });
            }
        });

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLlGroupTab.setVisibility(View.VISIBLE);
                    mBtnGroupSave.setVisibility(View.VISIBLE);
                } else {
                    request.setSymbol(result.getSymbol());
                    request.setRedNewPersonStatus("0");
                    mLlGroupTab.setVisibility(View.GONE);
                    mBtnGroupSave.setVisibility(View.GONE);
                    isOpen = false;
                }
            }
        });

        sw.setOnClickListener(v -> {
            request.setGroupId(groupId);
            if (sw.isChecked()) {
                mLlGroupTab.setVisibility(View.VISIBLE);
                mBtnGroupSave.setVisibility(View.VISIBLE);
            } else {
                //close
                request.setSymbol(result.getSymbol());
                request.setRedNewPersonStatus("0");
                mLlGroupTab.setVisibility(View.GONE);
                mBtnGroupSave.setVisibility(View.GONE);
                api.upRedNewPersonInfo(GsonUtils.toJson(request, false))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(EnterGroupGetRedActivity.this)))
                        .compose(RxSchedulers.normalTrans())
                        .subscribe(s -> {
                            ToastUtils.showShort(R.string.update_success);
                            isOpen = false;
                        }, t -> {
                            handleApiError(t);
                            sw.setChecked(!sw.isChecked());
                        });
            }
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
                            isOpen = true;
                            mLlGroupTab.setVisibility(View.VISIBLE);
                            mBtnGroupSave.setVisibility(View.VISIBLE);
                        } else {
                            sw.setChecked(false);
                            isOpen = false;
                            mLlGroupTab.setVisibility(View.GONE);
                            mBtnGroupSave.setVisibility(View.GONE);
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
        if (isOpen) {
            ToastUtils.showShort(R.string.close_payenter_first);
            return;
        } else {
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
//                if (sw.isChecked()) {
//                    GetRedNewPersonInfoResponse request = new GetRedNewPersonInfoResponse();
//                    request.setGroupId(groupId);
//                    request.setEveryoneAwardCount(str);
//                    request.setSymbol(result.getSymbol());
//                    ServiceFactory.getInstance().getBaseService(Api.class)
//                            .upRedNewPersonInfo(GsonUtils.toJson(request))
//                            .compose(bindToLifecycle())
//                            .compose(RxSchedulers.normalTrans())
//                            .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
//                            .subscribe(s -> {
//                                tvEach.setText(str);
//                                ToastUtils.showShort(R.string.update_success);
//                            }, this::handleApiError);
//                    return;
//                }
                tvEach.setText(str);
            });
            payEnterDialog.show(getString(R.string.setupeach));
        }
    }

    @SuppressLint("CheckResult")
    public void setupAll(View view) {
        if (isOpen) {
            ToastUtils.showShort(R.string.close_payenter_first);
            return;
        } else {
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

                tvAll.setText(str);
            });
            payEnterDialog.show(getString(R.string.setupall));
        }
    }

    @SuppressLint("CheckResult")
    public void setupStart(View view) {
        if (isOpen) {
            ToastUtils.showShort(R.string.close_payenter_first);
            return;
        } else {
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

                tvStartTime.setText(year + "-" + month + "-" + day);
            });
            String[] nowString = TimeUtils.getNowString(new SimpleDateFormat("yyyy-MM-dd")).split("-");
            datePicker.setRangeStart(Integer.parseInt(nowString[0]),
                    Integer.parseInt(nowString[1]), Integer.parseInt(nowString[2]));
            datePicker.setRangeEnd(Integer.parseInt(nowString[0]) + 3, Integer.parseInt(nowString[1]), 1);
            initPicker(datePicker);
            datePicker.show();
        }
    }

    @SuppressLint("CheckResult")
    public void setupEnd(View view) {
        if (isOpen) {
            ToastUtils.showShort(R.string.close_payenter_first);
            return;
        } else {
            if (tvStartTime.getText().equals("请设置")) {
                ToastUtils.showShort(R.string.please_set_start_time);
                return;
            }
            DatePicker datePicker = new DatePicker(this, DateTimePicker.YEAR_MONTH_DAY);
            datePicker.setOnDatePickListener((DatePicker.OnYearMonthDayPickListener) (year, month, day) -> {

                tvEndTime.setText(year + "-" + month + "-" + day);
            });
            String[] nowString = TimeUtils.getNowString(new SimpleDateFormat("yyyy-MM-dd")).split("-");
            datePicker.setRangeStart(Integer.parseInt(nowString[0]),
                    Integer.parseInt(nowString[1]), Integer.parseInt(nowString[2]));
            datePicker.setRangeEnd(Integer.parseInt(nowString[0]) + 3, Integer.parseInt(nowString[1]), 1);
            initPicker(datePicker);
            datePicker.show();
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
        if (isOpen) {
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
