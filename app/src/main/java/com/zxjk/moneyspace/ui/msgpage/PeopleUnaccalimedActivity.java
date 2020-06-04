package com.zxjk.moneyspace.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetGroupRedPackageInfoResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.minepage.DetailListActivity;
import com.zxjk.moneyspace.ui.msgpage.adapter.RedPackageAdapter;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.GlideUtil;

import java.util.ArrayList;

public class PeopleUnaccalimedActivity extends BaseActivity {
    private TextView title;
    private ImageView tv_end;
    private ImageView head;
    private TextView name;
    private TextView tips;
    private RecyclerView recycler;
    private boolean isShow;
    private TextView tv_redEnvelope;
    private TextView tv_red_symbol;
    private String isGame;

    @SuppressLint({"CheckResult", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTrasnferStatusBar(true);
        setContentView(R.layout.activity_people_unaccalimed);

        title = findViewById(R.id.title);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        tv_end = findViewById(R.id.tv_end);
        tv_end.setOnClickListener(v ->
                startActivity(new Intent(PeopleUnaccalimedActivity.this, DetailListActivity.class)));
        head = findViewById(R.id.head);
        name = findViewById(R.id.name);
        tips = findViewById(R.id.tips);
        tv_redEnvelope = findViewById(R.id.tv_redEnvelope);
        tv_red_symbol = findViewById(R.id.tv_red_symbol);
        recycler = findViewById(R.id.recycler);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        RedPackageAdapter adapter = new RedPackageAdapter();
        adapter.setData(new ArrayList<>());
        recycler.setAdapter(adapter);

        String id = getIntent().getStringExtra("id");
        isShow = getIntent().getBooleanExtra("isShow", true);
        if (!isShow) {
            tv_redEnvelope.setVisibility(View.GONE);
        } else {
            tv_redEnvelope.setVisibility(View.VISIBLE);
        }
        isGame = getIntent().getStringExtra("isGame");
        if (TextUtils.isEmpty(isGame)) {
            isGame = "1";
        }
        if (isGame.equals("0")) {
            tips.setVisibility(View.GONE);
        }
        boolean fromList = getIntent().getBooleanExtra("fromList", false);
        if (fromList) {
            tv_end.setVisibility(View.GONE);
        }
        Api baseService = ServiceFactory.getInstance().getBaseService(Api.class);
        baseService.getRedPackageStatus(id, isGame)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(getRedPackageStatusResponse -> {
                    title.setText(getRedPackageStatusResponse.getMessage());
                    GlideUtil.loadCircleImg(head, getRedPackageStatusResponse.getHeadPortrait());
                    name.setText(getString(R.string.xxx_red, getRedPackageStatusResponse.getUsernick()));
                    if (getRedPackageStatusResponse.getRedPackageType() == 1) {
                        //群组红包
                        baseService
                                .getGroupRedPackageInfo(id)
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.normalTrans())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                                .subscribe(response -> {
                                    for (int i = 0; i < response.getCustomerInfo().size(); i++) {
                                        if (String.valueOf(response.getCustomerInfo().get(i).getCustomerId()).equals(Constant.currentUser.getId())) {
                                            tv_redEnvelope.setText(response.getCustomerInfo().get(i).getMoney());
                                            tv_red_symbol.setText(response.getRedPackageInfo().getSymbol());
                                        }
                                    }
                                    String text = getString(R.string.current_receive, response.getRedPackageInfo().getReceiveCount(), String.valueOf(response.getRedPackageInfo().getNumber()));
                                    int number = response.getRedPackageInfo().getNumber();
                                    tips.setText(getString(R.string.xxx_red_total_xxx, String.valueOf(number), response.getRedPackageInfo().getMoney() + response.getRedPackageInfo().getSymbol() + text));
                                    adapter.setSymbol(response.getRedPackageInfo().getSymbol());
                                    adapter.setData(response.getCustomerInfo());
                                }, this::handleApiError);
                    } else {
                        //个人红包
                        baseService
                                .personalRedPackageInfo(id, Integer.parseInt(Constant.userId))
                                .compose(bindToLifecycle())
                                .compose(RxSchedulers.normalTrans())
                                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                                .subscribe(response -> {
                                    String money = response.getRedPachageInfo().getMoney();
                                    tv_redEnvelope.setText(money + " " + response.getRedPachageInfo().getSymbol());
                                    if (response.getRedPachageInfo().getStatus().equals("0")) {
                                        //未领取
                                        tips.setText(getString(R.string.red_money, money + response.getRedPachageInfo().getSymbol()));
                                    } else {
                                        //已领取
                                        tips.setText(getString(R.string.red_money1, money + response.getRedPachageInfo().getSymbol()));
                                    }
                                    GetGroupRedPackageInfoResponse.CustomerInfoBean bean = new GetGroupRedPackageInfoResponse.CustomerInfoBean();
                                    if (response.getRedPachageInfo().getStatus().equals("1")) {
                                        bean.setHeadPortrait(response.getReceiveInfo().getHeadPortrait());
                                        bean.setNick(response.getReceiveInfo().getUsernick());
                                        bean.setMoney(response.getRedPachageInfo().getMoney());
                                        bean.setCreateTime(response.getReceiveInfo().getTime());
                                        bean.setSymbol(response.getRedPachageInfo().getSymbol());
                                        ArrayList<GetGroupRedPackageInfoResponse.CustomerInfoBean> objects = new ArrayList<>(1);
                                        objects.add(bean);
                                        adapter.setSymbol(response.getRedPachageInfo().getSymbol());
                                        adapter.setData(objects);
                                    }
                                }, this::handleApiError);
                    }
                }, this::handleApiError);
    }
}
