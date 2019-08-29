package com.zxjk.duoduo.ui.walletpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetOverOrderResponse;
import com.zxjk.duoduo.ui.ProofComplaintActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.DataUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderComplaintActivity extends BaseActivity {
    //标题
    @BindView(R.id.tv_title)
    TextView tvTitle;
    //订单申诉状态图标
    @BindView(R.id.iv_complaint)
    ImageView ivComplaint;
    //订单申诉状态文字描述
    @BindView(R.id.tv_complaint)
    TextView tvComplaint;
    //交易单号
    @BindView(R.id.tv_transactionNumber)
    TextView tvTransactionNumber;
    //申诉人
    @BindView(R.id.tv_plaintiff)
    TextView tvPlaintiff;
    //被申诉人
    @BindView(R.id.tv_byPlaintiff)
    TextView tvByPlaintiff;
    //数量
    @BindView(R.id.tv_number)
    TextView tvNumber;
    //金额
    @BindView(R.id.tv_amount)
    TextView tvAmount;
    //申诉凭证
    @BindView(R.id.iv_proofComplaint)
    ImageView ivProofComplaint;
    //处理时间
    @BindView(R.id.tv_7)
    TextView tv7;
    @BindView(R.id.tv_processingTime)
    TextView tvProcessingTime;
    //申诉结果
    @BindView(R.id.tv_8)
    TextView tv8;
    @BindView(R.id.tv_complaintResults)
    TextView tvComplaintResults;
    //申诉类型
    @BindView(R.id.tv_appealType)
    TextView tvAppealType;

    private GetOverOrderResponse data;
    private String rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complaint);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        tvTitle.setText("订单申诉");
        data = (GetOverOrderResponse) getIntent().getSerializableExtra("data");
        rate = getIntent().getStringExtra("rate");
        switch (data.getStatus()) {
            case "6":
                tvComplaint.setText("订单正在申诉");
                ivComplaint.setImageResource(R.drawable.icon_pending);
                tv7.setVisibility(View.GONE);
                tvProcessingTime.setVisibility(View.GONE);
                tv8.setVisibility(View.GONE);
                tvComplaintResults.setVisibility(View.GONE);
                break;
            case "4":
                tvComplaint.setText("订单申诉完成");
                ivComplaint.setImageResource(R.drawable.icon_transfer_successful);
                break;
        }
    }

    private void initData() {
        //交易单号
        tvTransactionNumber.setText(data.getBothOrderId());
        //申诉人
        tvPlaintiff.setText(data.getPlaintiffNick());
        //被申诉人
        tvByPlaintiff.setText(data.getIndicteeNick());
        //数量
        tvNumber.setText(data.getNumber());
        //金额
        tvAmount.setText(DataUtils.getTwoDecimals(data.getMoney()));
        //申诉类型
        tvAppealType.setText(data.getAppealType());
        //处理时间
        tvProcessingTime.setText(DataUtils.timeStamp2Date(Long.parseLong(data.getCreateTime()), "yyyy-MM-dd HH:mm:ss"));
        //申诉结果
        tvComplaintResults.setText(data.getProcessResult());
    }

    @OnClick({R.id.rl_back, R.id.iv_proofComplaint})
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.rl_back:
                finish();
                break;
            //查看凭证
            case R.id.iv_proofComplaint:
                Intent intent = new Intent(OrderComplaintActivity.this, ProofComplaintActivity.class);
                intent.putExtra("images", data.getPicture());
                startActivity(intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                                ivProofComplaint, "12").toBundle());
                break;
        }
    }
}
