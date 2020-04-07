package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetMainSymbolByCustomerIdBean;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.GlideUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Function;

@SuppressLint("CheckResult")
public class BlockWalletManageActivity extends BaseActivity {
    public static final int REQUEST_DETAIL = 3;

    private boolean dataChanged;
    private RecyclerView recycler;
    private BaseQuickAdapter<GetMainSymbolByCustomerIdBean, BaseViewHolder> adapter;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_wallet_manage);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.wallet_manage);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        initRecycler();

        initData();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dataChanged = true;
            }
        };
        IntentFilter intentFilter = new IntentFilter(Constant.ACTION_BROADCAST1);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getMainSymbolByCustomerId()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .map((Function<List<GetMainSymbolByCustomerIdBean>, List<GetMainSymbolByCustomerIdBean>>) list -> {
                    ArrayList<GetMainSymbolByCustomerIdBean> list1 = new ArrayList<>();
                    ArrayList<GetMainSymbolByCustomerIdBean> list2 = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getImportMethod().equals("3")) {
                            list1.add(list.get(i));
                        } else {
                            list2.add(list.get(i));
                        }
                    }
                    ArrayList<GetMainSymbolByCustomerIdBean> resultList = new ArrayList<>(list.size());
                    resultList.addAll(list1);
                    resultList.addAll(list2);
                    return resultList;
                })
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(resultList -> adapter.setNewData(resultList), this::handleApiError);
    }

    private void initRecycler() {
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickAdapter<GetMainSymbolByCustomerIdBean, BaseViewHolder>(R.layout.item_wallet_manage, null) {
            @Override
            protected void convert(BaseViewHolder helper, GetMainSymbolByCustomerIdBean item) {
                List<GetMainSymbolByCustomerIdBean> data = getData();

                ImageView ivLogo = helper.getView(R.id.ivLogo);
                GlideUtil.loadNormalImg(ivLogo, item.getLogo());
                helper.setText(R.id.tvCoin, item.getWalletName())
                        .setText(R.id.tvAddress, item.getWalletAddress());
                helper.addOnClickListener(R.id.rlContent);

                LinearLayout llHead = helper.getView(R.id.llHead);
                if (helper.getAdapterPosition() == 0) {
                    llHead.setVisibility(View.VISIBLE);
                } else if (judgeEquals(item, data.get(helper.getAdapterPosition() - 1))) {
                    llHead.setVisibility(View.GONE);
                } else {
                    llHead.setVisibility(View.VISIBLE);
                }

                helper.setText(R.id.tvHead, item.getImportMethod().equals("3") ? R.string.wallet_created : R.string.wallet_imported);
                helper.setImageResource(R.id.ivHead, item.getImportMethod().equals("3") ?
                        R.drawable.ic_item_walletmanage_create : R.drawable.ic_item_walletmanage_import);

                View line = helper.getView(R.id.line);
                if (helper.getAdapterPosition() + 1 != data.size()) {
                    if (judgeEquals(item, data.get(helper.getAdapterPosition() + 1))) {
                        line.setVisibility(View.VISIBLE);
                    } else {
                        line.setVisibility(View.GONE);
                    }
                } else {
                    line.setVisibility(View.GONE);
                }
            }
        };
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            GetMainSymbolByCustomerIdBean b = (GetMainSymbolByCustomerIdBean) adapter.getData().get(position);
            Intent intent = new Intent(this, BlockWalletManageDetailActivity.class);
            intent.putExtra("data", b);
            startActivityForResult(intent, REQUEST_DETAIL);
        });
        View inflate = LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, null, false);
        TextView tv = inflate.findViewById(R.id.tv);
        ImageView iv = inflate.findViewById(R.id.iv);
        tv.setText(R.string.emptylist2);
        iv.setImageResource(R.drawable.ic_empty_orders);
        adapter.setEmptyView(inflate);
        recycler.setAdapter(adapter);
    }

    public void createWallet(View view) {
        startActivity(new Intent(this, CreateWalletActivity.class));
    }

    public void importWallet(View view) {
        Intent intent = new Intent(this, CreateWalletActivity.class);
        intent.putExtra("isImport", true);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_DETAIL:
                if (resultCode == 1) {
                    //changename
                    initData();
                }
                if (resultCode == 2) {
                    //delete
                    dataChanged = true;
                    initData();
                }
                break;
        }
    }

    @Override
    protected void onRestart() {
        if (dataChanged) {
            initData();
        }
        super.onRestart();
    }

    private boolean judgeEquals(GetMainSymbolByCustomerIdBean b1, GetMainSymbolByCustomerIdBean b2) {
        return (!b1.getImportMethod().equals("3") && !b2.getImportMethod().equals("3")) ||
                (b1.getImportMethod().equals("3") && b2.getImportMethod().equals("3"));
    }

    @Override
    public void finish() {
        if (dataChanged) {
            setResult(1);
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }
}
