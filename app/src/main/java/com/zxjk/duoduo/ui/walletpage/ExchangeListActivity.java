package com.zxjk.duoduo.ui.walletpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetOverOrderResponse;
import com.zxjk.duoduo.bean.response.ReleasePurchaseResponse;
import com.zxjk.duoduo.bean.response.ReleaseSaleResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.walletpage.adapter.ExchangeListAdapter;
import com.zxjk.duoduo.utils.CommonUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressLint("CheckResult")
public class ExchangeListActivity extends BaseActivity {

    private RecyclerView recyclerExchangeMyOrder;
    private RadioGroup rgExchangeTop;
    private SwipeRefreshLayout refreshLayout;

    // 1.进行中 2.已完成
    private int flag = 1;

    ExchangeListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_list);

        String rate = getIntent().getStringExtra("rate");

        mAdapter = new ExchangeListAdapter(rate);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            GetOverOrderResponse g = (GetOverOrderResponse) adapter.getData().get(position);
            Intent intent = new Intent();
            if (g.getStatus().equals("3")) {
                //交易中
                if (g.getBuyId().equals(Constant.userId)) {
                    //买方
                    if (g.getIsBuyPay().equals("1")) {
                        //确认支付
                        intent = new Intent(this, ConfirmBuyActivity.class);
                        ReleaseSaleResponse data = new ReleaseSaleResponse();
                        data.setNick(g.getSellNick());
                        data.setCurrency(g.getCurrency());
                        if (g.getPayType().equals("1")) {
                            data.setReceiptNumber(g.getWechatNick());
                        } else if (g.getPayType().equals("2")) {
                            data.setReceiptNumber(g.getZhifubaoNumber());
                        } else {
                            data.setReceiptNumber(g.getPayNumber());
                        }
                        data.setBothOrderId(g.getBothOrderId());
                        data.setNumber(g.getNumber());
                        data.setCreateTime(g.getCreateTime());
                        data.setMoney(g.getMoney());
                        data.setBothOrderId(g.getBothOrderId());
                        data.setSellOrderId(g.getSellOrderId());
                        data.setBuyOrderId(g.getBuyOrderId());
                        data.setOpenBank(g.getOpenBank());
                        data.setMobile(g.getMobile());
                        data.setReceiptPicture(g.getPayPicture());
                        data.setDefaultLimitTransactions(g.getDefaultLimitTransactions());
                        intent.putExtra("data", data);
                        intent.putExtra("buytype", g.getPayType());
                        intent.putExtra("rate", rate);
                    } else {
                        //等待审核
                        intent = new Intent(this, WaitForJudgeActivity.class);
                        ReleaseSaleResponse data = new ReleaseSaleResponse();
                        data.setCurrency(g.getCurrency());
                        if (g.getPayType().equals("1")) {
                            data.setReceiptNumber(g.getWechatNick());
                        } else if (g.getPayType().equals("2")) {
                            data.setReceiptNumber(g.getZhifubaoNumber());
                        } else {
                            data.setReceiptNumber(g.getPayNumber());
                        }
                        data.setBothOrderId(g.getBothOrderId());
                        data.setMoney(g.getMoney());
                        data.setNumber(g.getNumber());
                        data.setCreateTime(g.getCreateTime());
                        data.setNick(g.getSellNick());
                        data.setPayTime(g.getPayTime());
                        data.setMobile(g.getMobile());
                        data.setReceiptPicture(g.getPayPicture());
                        data.setCustomerId(Integer.parseInt(g.getSellId()));
                        intent.putExtra("buytype", g.getPayType());
                        intent.putExtra("data", data);
                        intent.putExtra("rate", rate);
                    }
                } else {
                    if (g.getIsBuyPay().equals("1")) {
                        //对方正在支付中
                        intent = new Intent(this, WaitForPayOverActivity.class);
                        intent.putExtra("data", g);
                        intent.putExtra("rate", rate);
                    } else if (g.getIsBuyPay().equals("0")) {
                        //对方已支付，审核
                        intent = new Intent(this, OverOrderActivity.class);
                        intent.putExtra("data", g);
                        intent.putExtra("rate", rate);
                    }
                }
            } else if (g.getStatus().equals("5")) {
                String money;
                if (!TextUtils.isEmpty(mAdapter.getItem(position).getMoney())) {
                    money = mAdapter.getItem(position).getMoney();
                } else {
                    String r = rate.split(" ")[0];
                    money = new DecimalFormat("0.00").format(CommonUtils.mul(Double.parseDouble(r), Double.parseDouble(mAdapter.getItem(position).getNumber())));
                }
                //挂单中
                intent = new Intent(this, ConfirmSaleActivity.class);
                ReleasePurchaseResponse data = new ReleasePurchaseResponse();
                data.setPayType(g.getPayType());  //收款方式
                data.setMoney(money);//出售总金额
                data.setNumber(g.getNumber());//出售数量
                data.setMinNum(g.getMinNum());//出售最小数量
                data.setMaxNum(g.getMaxNum());//出售最大数量
                data.setUnSaledNum(g.getUnSaledNum());//出售剩余数量
                data.setCurrency(g.getCurrency());
                data.setSellOrderId(g.getSellOrderId());
                intent.putExtra("data", data);
                intent.putExtra("rate", rate);
            } else if (g.getStatus().equals("4")) {
                //申诉完成
                intent = new Intent(this, OrderComplaintActivity.class);
                intent.putExtra("data", g);
                intent.putExtra("rate", rate);
            } else if (g.getStatus().equals("6")) {
                //申诉中
                intent = new Intent(this, OrderComplaintActivity.class);
                intent.putExtra("data", g);
                intent.putExtra("rate", rate);
            } else {
                //交易完成（1取消、2失败、0完成）
                if (g.getStatus().equals("0")) {
                    intent = new Intent(this, ExchangeOrderSuccessActivity.class);
                    intent.putExtra("data", g);
                    intent.putExtra("rate", rate);
                }
                if (g.getStatus().equals("1")) {
                    intent = new Intent(this, ExchangeOrderCancelActivity.class);
                    intent.putExtra("data", g);
                    intent.putExtra("rate", rate);
                }
                if (g.getStatus().equals("2")) {
                    intent = new Intent(this, ExchangeOrderFailedActivity.class);
                    intent.putExtra("data", g);
                    intent.putExtra("rate", rate);
                }
            }
            startActivity(intent);
        });

        rgExchangeTop = findViewById(R.id.rgExchangeTop);
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(Color.parseColor("#4585F5"));
        recyclerExchangeMyOrder = findViewById(R.id.recyclerExchangeMyOrder);
        recyclerExchangeMyOrder.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerExchangeMyOrder.setAdapter(mAdapter);

        rgExchangeTop.check(R.id.rb1);

        rgExchangeTop.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb1) {
                flag = 1;
            } else {
                flag = 2;
            }
            reloadData();
        });

        reloadData();
        refreshLayout.setOnRefreshListener(this::reloadData);
    }

    @Override
    protected void onRestart() {
        reloadData();
        super.onRestart();
    }

    private void reloadData() {
        refreshLayout.setRefreshing(true);
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getOverOrder()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver())
                .subscribe(getOverOrderResponses -> {
                    if (refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing(false);
                    }
                    List<GetOverOrderResponse> temp = new ArrayList<>();
                    for (GetOverOrderResponse r : getOverOrderResponses) {
                        if (flag == 1) {
                            if (r.getStatus().equals("3") || r.getStatus().equals("5") || r.getStatus().equals("6")) {
                                temp.add(r);
                            }
                        } else if (flag == 2) {
                            if (r.getStatus().equals("0") || r.getStatus().equals("1") ||
                                    r.getStatus().equals("2") || r.getStatus().equals("4")) {
                                temp.add(r);
                            }
                        }
                    }
                    if (flag == 1) {
                        Collections.sort(temp, (o1, o2) -> -o1.getCreateTime().compareTo(o2.getCreateTime()));
                    } else if (flag == 2) {
                        Collections.sort(temp, (o1, o2) -> -o1.getCloseTime().compareTo(o2.getCloseTime()));
                    }
                    mAdapter.setNewData(temp);
                }, t -> {
                    if (refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing(false);
                    }
                    handleApiError(t);
                });
    }

    public void back(View view) {
        finish();
    }
}
