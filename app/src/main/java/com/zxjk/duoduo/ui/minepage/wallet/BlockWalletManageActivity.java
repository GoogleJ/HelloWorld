package com.zxjk.duoduo.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GetMainSymbolByCustomerIdBean;
import com.zxjk.duoduo.bean.response.GetMainSymbolByCustomerIdBeanSection;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Function;

@SuppressLint("CheckResult")
public class BlockWalletManageActivity extends BaseActivity {
    public static final int RESULT_DELETE = 1;
    public static final int RESULT_RENAME = 2;

    private RecyclerView recycler;
    private BaseSectionQuickAdapter<GetMainSymbolByCustomerIdBeanSection, BaseViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_wallet_manage);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.wallet_manage);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        initRecycler();

        initData();
    }

    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getMainSymbolByCustomerId()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .map((Function<List<GetMainSymbolByCustomerIdBean>,
                        List<GetMainSymbolByCustomerIdBeanSection>>) this::parseData)
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(resultList -> adapter.setNewData(resultList), this::handleApiError);
    }

    private void initRecycler() {
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseSectionQuickAdapter<GetMainSymbolByCustomerIdBeanSection, BaseViewHolder>(R.layout.item_wallet_manage, R.layout.item_wallet_manage_head, null) {
            @Override
            protected void convert(BaseViewHolder helper, GetMainSymbolByCustomerIdBeanSection item) {
                ImageView ivLogo = helper.getView(R.id.ivLogo);
                GlideUtil.loadNormalImg(ivLogo, item.t.getLogo());
                helper.setText(R.id.tvCoin, item.t.getWalletName())
                        .setText(R.id.tvAddress, item.t.getWalletAddress());
                helper.addOnClickListener(R.id.rlContent);
            }

            @Override
            protected void convertHead(BaseViewHolder helper, GetMainSymbolByCustomerIdBeanSection item) {
                helper.setText(R.id.tvHead, item.header);
                helper.setImageResource(R.id.ivHead, item.header.equals(getString(R.string.wallet_created)) ?
                        R.drawable.ic_item_walletmanage_create : R.drawable.ic_item_walletmanage_import);
            }
        };
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            GetMainSymbolByCustomerIdBeanSection b = (GetMainSymbolByCustomerIdBeanSection) adapter.getData().get(position);
            Intent intent = new Intent(this, BlockWalletManageDetailActivity.class);
            intent.putExtra("data", b.t);
            startActivityForResult(intent, 1);
        });
        recycler.setAdapter(adapter);
    }

    public void createWallet(View view) {
        startActivity(new Intent(this, CreateWalletActivity.class));
        finish();
    }

    public void importWallet(View view) {

    }

    private ArrayList<GetMainSymbolByCustomerIdBeanSection> parseData(List<GetMainSymbolByCustomerIdBean> originList) {
        ArrayList<GetMainSymbolByCustomerIdBeanSection> resultList = new ArrayList<>(originList.size());
        ArrayList<GetMainSymbolByCustomerIdBeanSection> listCreated = new ArrayList<>(originList.size());
        ArrayList<GetMainSymbolByCustomerIdBeanSection> listImported = new ArrayList<>(originList.size());
        for (int i = 0; i < originList.size(); i++) {
            GetMainSymbolByCustomerIdBean bean = originList.get(i);
            if (bean.getImportMethod().equals("3")) {
                listCreated.add(new GetMainSymbolByCustomerIdBeanSection(bean));
            } else {
                listImported.add(new GetMainSymbolByCustomerIdBeanSection(bean));
            }
        }
        if (listCreated.size() != 0) {
            resultList.add(new GetMainSymbolByCustomerIdBeanSection(true, getString(R.string.wallet_created)));
            resultList.addAll(listCreated);
        }
        if (listImported.size() != 0) {
            resultList.add(new GetMainSymbolByCustomerIdBeanSection(true, getString(R.string.wallet_imported)));
            resultList.addAll(listImported);
        }
        return resultList;
    }
}
