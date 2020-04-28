package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetOrderInfoById;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.minepage.SelfSelectionFragment;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.Sha256;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressLint("CheckResult")
public class AcceptorOrderActivity extends BaseActivity {
    private String currency;
    private String number;
    private String price;
    private String maxNum;
    private String minNum;
    private String orderId;
    private String count;
    private String timestamp;
    private String sign;
    private GetOrderInfoById getOrderInfoById;

    private TextView tvPrice;
    private TextView tvNumber;
    private TextView tvOrderTime;
    private TextView tvLimit;
    private LinearLayout llBuyCoin;
    private ImageView imgCopy;
    private TextView tvOrderId;
    private TextView tvTotalPrices;
    private RecyclerView recyclerView;
    private List<PayType> payTypeList = new ArrayList<>();
    private BaseQuickAdapter<PayType, BaseViewHolder> adapter;
    private String payType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceptor_order);
        setTrasnferStatusBar(true);
        Intent intent = getIntent();
        currency = intent.getStringExtra("currency");
        number = intent.getStringExtra("number");
        price = intent.getStringExtra("price");
        maxNum = intent.getStringExtra("maxNum");
        minNum = intent.getStringExtra("minNum");
        count = intent.getStringExtra("count");
        orderId = intent.getStringExtra("orderId");
        payType = intent.getStringExtra("payType");
        getOrderInfoById = (GetOrderInfoById) getIntent().getExtras().getSerializable("GetOrderInfoById");

        initView();
        initData();
    }

    private void initView() {
        tvPrice = findViewById(R.id.tv_price);
        tvNumber = findViewById(R.id.tv_number);
        tvOrderTime = findViewById(R.id.tv_order_time);
        tvLimit = findViewById(R.id.tv_limit);
        llBuyCoin = findViewById(R.id.ll_buy_coin);
        imgCopy = findViewById(R.id.img_copy);
        tvOrderId = findViewById(R.id.tv_order_id);
        tvTotalPrices = findViewById(R.id.tv_total_prices);
        recyclerView = findViewById(R.id.recycler_view);
        findViewById(R.id.rl_back).setOnClickListener(v ->
                finish()
        );
    }

    private void initData() {
        if (getOrderInfoById != null) {
            tvPrice.setText(getOrderInfoById.getPrice() + " CNY/" + getOrderInfoById.getCurrency());
            tvOrderId.setText(orderId);
            tvNumber.setText(getOrderInfoById.getNumber());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            tvOrderTime.setText(simpleDateFormat.format(date));
            tvLimit.setText(getOrderInfoById.getMinNum() + "-" + getOrderInfoById.getMaxNum());
            if (TextUtils.isEmpty(count)) {
                count = getIntent().getStringExtra("count");
            }
            tvTotalPrices.setText(getOrderInfoById.getMoney());
        } else {
            tvPrice.setText(price + " CNY/" + currency);
            tvOrderId.setText(orderId);
            tvNumber.setText(number);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            tvOrderTime.setText(simpleDateFormat.format(date));
            tvLimit.setText(minNum + "-" + maxNum);

            DecimalFormat decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String p=decimalFormat.format(Float.valueOf(price)*Float.valueOf(number));//format 返回的是字符串
            tvTotalPrices.setText(p);
            if (TextUtils.isEmpty(count)) {
                count = getIntent().getStringExtra("count");
            }
        }

        String getPayType = payType ;
        String[] strArr = getPayType.split(",");
        for (int i = 0; i < strArr.length; i++) {
            PayType p = new PayType();
            p.setPayType(strArr[i]);
            payTypeList.add(p);
        }


        adapter = new BaseQuickAdapter<PayType, BaseViewHolder>(R.layout.item_rc_paytype) {
            @Override
            protected void convert(BaseViewHolder helper, PayType item) {
                ImageView img = helper.getView(R.id.img_pay_type);

                if (item.getPayType().equals("1")) {
                    img.setBackground(getResources().getDrawable(R.drawable.ic_selfselection_wechat, null));
                } else if (item.getPayType().equals("2")) {
                    img.setBackground(getResources().getDrawable(R.drawable.ic_selfselection_alipay, null));
                } else {
                    img.setBackground(getResources().getDrawable(R.drawable.ic_selfselection_bank, null));
                }
            }
        };

        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.HORIZONTAL);//设置为横向排列
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);

        adapter.setNewData(payTypeList);

        long currentTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date2 = new Date(currentTime);
        timestamp = dataOne(formatter.format(date2));


        llBuyCoin.setOnClickListener(v -> {
            if (count.equals("0")) {
                String secret = "buyOrderId=" + orderId +
                        "&nonce=" + timestamp + Constant.SECRET;
                sign = Sha256.getSHA256(secret);
                ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                        .acceptorCancelBuy(timestamp, orderId)
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(d ->
                                {
                                    ToastUtils.showShort("挂单取消成功！");
                                    finish();
                                },
                                this::handleApiError);
            } else {
                String secret = "nonce=" + timestamp +
                        "&sellOrderId=" + orderId + Constant.SECRET;
                sign = Sha256.getSHA256(secret);
                ServiceFactory.getInstance().otcService(Constant.BASE_URL, sign, Api.class)
                        .acceptorCloseSell(orderId, timestamp)
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(d ->
                                {
                                    ToastUtils.showShort("挂单取消成功！");
                                    finish();
                                },
                                this::handleApiError);
            }
        });
    }

    public String dataOne(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss",
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }

    public class PayType {
        private String payType;

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }
    }
}