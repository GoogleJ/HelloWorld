package com.zxjk.moneyspace.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetUpgradeGroupsResponnse;
import com.zxjk.moneyspace.bean.response.GroupResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.widget.NewPayBoard;
import com.zxjk.moneyspace.ui.widget.dialog.MuteRemoveDialog;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.MD5Utils;

@SuppressLint("CheckResult")
public class UpdateGroupLimitActivity extends BaseActivity {
    private GroupResponse group;
    private TextView tvGroupLimit;
    private RecyclerView recycler;
    private BaseQuickAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_group_limit);

        group = (GroupResponse) getIntent().getSerializableExtra("group");
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.update_group_limit);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        tvGroupLimit = findViewById(R.id.tvGroupLimit);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        float itemHeight = (ScreenUtils.getScreenWidth() - CommonUtils.dip2px(this, 36)) / 2f;
        adapter = new BaseQuickAdapter<GetUpgradeGroupsResponnse.GroupLevelsInfoListBean, BaseViewHolder>(
                R.layout.item_updategrouplimit) {

            @Override
            protected void convert(BaseViewHolder helper, GetUpgradeGroupsResponnse.GroupLevelsInfoListBean item) {
                helper.setText(R.id.tvtips, item.getFee() + item.getSymbol() + getString(R.string.upgrade))
                        .setText(R.id.tvNum, item.getLimitNumber())
                        .setText(R.id.btnUpdate, item.getIsUpgrade().equals("1") ? getString(R.string.upgrade3) : getString(R.string.upgrade1));
                helper.setBackgroundRes(R.id.btnUpdate, item.getIsUpgrade().equals("1") ? R.drawable.shape_unable
                        : R.drawable.shape_theme1);
                LinearLayout ll = helper.getView(R.id.ll);
                ViewGroup.LayoutParams layoutParams = ll.getLayoutParams();
                layoutParams.height = (int) itemHeight;
                ll.setLayoutParams(layoutParams);

                Button btnUpdate = helper.getView(R.id.btnUpdate);
                if (!item.getIsUpgrade().equals("1")) {
                    btnUpdate.setOnClickListener(v -> {
                        MuteRemoveDialog dialog = new MuteRemoveDialog(UpdateGroupLimitActivity.this, getString(R.string.cancel),getString(R.string.queding),
                                 getString(R.string.hinttext), getString(R.string.confirm_upgrade_social));
                        dialog.setOnCommitListener(() -> new NewPayBoard(UpdateGroupLimitActivity.this)
                                .show(psw -> ServiceFactory.getInstance().getBaseService(Api.class)
                                        .payToUpgradeGroup(group.getGroupInfo().getId(),
                                                MD5Utils.getMD5(psw), item.getGroupTag(), item.getFee())
                                        .compose(bindToLifecycle())
                                        .compose(RxSchedulers.normalTrans())
                                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(UpdateGroupLimitActivity.this)))
                                        .subscribe(s -> {
                                            ToastUtils.showShort(R.string.update_success1);
                                            group.getGroupInfo().setLimitNumber(item.getLimitNumber());
                                            finish();
                                        }, UpdateGroupLimitActivity.this::handleApiError)));
                        dialog.show();
                    });
                }
            }
        };
        recycler.setAdapter(adapter);

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getUpgradeGroups(group.getGroupInfo().getId())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(r -> {
                    tvGroupLimit.setText(r.getGroupLevelInfo().getLimitNumber());
                    adapter.setNewData(r.getGroupLevelsInfoList());
                }, this::handleApiError);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("group", group);
        setResult(1, intent);
        super.finish();
    }
}
