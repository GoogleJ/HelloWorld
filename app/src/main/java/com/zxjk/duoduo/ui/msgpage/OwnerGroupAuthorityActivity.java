package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.bean.response.PermissionInfoBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.RecyclerItemAverageDecoration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OwnerGroupAuthorityActivity extends BaseActivity {
    private TextView tv_title;
    private RelativeLayout rl_back;
    private RecyclerView recycler;
    private Switch sw1;
    private Switch sw2;
    private Switch sw3;
    private Switch sw4;

    private GroupResponse group;

    private BaseQuickAdapter<PermissionInfoBean, BaseViewHolder> adapter;
    private PermissionInfoBean currentCheckedB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_group_authority);

        group = (GroupResponse) getIntent().getSerializableExtra("group");

        initView();
        initData();
        bindData();
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        rl_back = findViewById(R.id.rl_back);
        recycler = findViewById(R.id.recycler);
        sw1 = findViewById(R.id.sw1);
        sw2 = findViewById(R.id.sw2);
        sw3 = findViewById(R.id.sw3);
        sw4 = findViewById(R.id.sw4);
    }

    private void initData() {
        tv_title.setText(R.string.authority_manage);
        rl_back.setOnClickListener(v -> finish());

        initAdapter();
        recycler.setLayoutManager(new GridLayoutManager(this, 5));
        recycler.addItemDecoration(new RecyclerItemAverageDecoration(0, 0, 5));
        recycler.setItemAnimator(null);
        recycler.setAdapter(adapter);

        sw1.setOnClickListener(v -> {
            if (currentCheckedB != null)
                currentCheckedB.setOpenAudio(sw1.isChecked() ? "1" : "0");
            else
                ToastUtils.showShort(R.string.selectPerson);
        });
        sw2.setOnClickListener(v -> {
            if (currentCheckedB != null)
                currentCheckedB.setOpenVideo(sw2.isChecked() ? "1" : "0");
            else
                ToastUtils.showShort(R.string.selectPerson);
        });
        sw3.setOnClickListener(v -> {
            if (currentCheckedB != null)
                currentCheckedB.setOpenBanned(sw3.isChecked() ? "1" : "0");
            else
                ToastUtils.showShort(R.string.selectPerson);
        });
        sw4.setOnClickListener(v -> {
            if (currentCheckedB != null)
                currentCheckedB.setForceRecall(sw4.isChecked() ? "1" : "0");
            else
                ToastUtils.showShort(R.string.selectPerson);
        });
    }

    @SuppressLint("CheckResult")
    private void bindData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getPermissionInfo(group.getGroupInfo().getId())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(list -> {
                    if (list.size() != 0) {
                        list.add(null);
                        list.add(null);
                    }
                    adapter.setNewData(list);
                }, this::handleApiError);
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<PermissionInfoBean, BaseViewHolder>(R.layout.item_permission_members) {
            int currentCheckedP;

            @Override
            protected void convert(BaseViewHolder h, PermissionInfoBean b) {
                int position = h.getAdapterPosition();
                FrameLayout containerTop = h.getView(R.id.containerTop);
                TextView nick_name = h.getView(R.id.nick_name);
                ImageView ivFunc = h.getView(R.id.ivFunc);
                ImageView ivChecked = h.getView(R.id.ivChecked);
                ImageView header_image = h.getView(R.id.header_image);

                if (b == null && position == getItemCount() - 1) {
                    containerTop.setVisibility(View.GONE);
                    nick_name.setVisibility(View.GONE);
                    ivFunc.setVisibility(View.VISIBLE);

                    ivFunc.setImageResource(R.drawable.icon_add_members);
                    ivFunc.setOnClickListener(v -> {
                        Intent intent = new Intent(OwnerGroupAuthorityActivity.this, CreateGroupActivity.class);
                        intent.putExtra("eventType", 4);
                        intent.putExtra("groupId", group.getGroupInfo().getId());
                        intent.putParcelableArrayListExtra("managers", (ArrayList<? extends Parcelable>) adapter.getData());
                        startActivityForResult(intent, 1);
                    });
                } else if (b == null && position == getItemCount() - 2) {
                    containerTop.setVisibility(View.GONE);
                    nick_name.setVisibility(View.GONE);
                    ivFunc.setVisibility(View.VISIBLE);

                    ivFunc.setImageResource(R.drawable.icon_delete_members);
                    ivFunc.setOnClickListener(v -> {
                        Intent intent = new Intent(OwnerGroupAuthorityActivity.this, CreateGroupActivity.class);
                        intent.putExtra("eventType", 5);
                        intent.putParcelableArrayListExtra("managers", (ArrayList<? extends Parcelable>) adapter.getData());
                        intent.putExtra("groupId", group.getGroupInfo().getId());
                        startActivityForResult(intent, 1);
                    });
                } else {
                    if (b.isChecked()) {
                        ivChecked.setVisibility(View.VISIBLE);
                    } else {
                        ivChecked.setVisibility(View.INVISIBLE);
                    }

                    containerTop.setVisibility(View.VISIBLE);
                    nick_name.setVisibility(View.VISIBLE);
                    ivFunc.setVisibility(View.GONE);

                    GlideUtil.loadCircleImg(header_image, b.getHeadPortrait());
                    h.setText(R.id.nick_name, b.getNick());

                    containerTop.setOnClickListener(v -> {
                        if (currentCheckedB != null) {
                            currentCheckedB.setChecked(false);
                            notifyItemChanged(currentCheckedP);
                        }
                        b.setChecked(true);
                        currentCheckedB = b;
                        currentCheckedP = position;
                        notifyItemChanged(currentCheckedP);

                        if (!sw1.isEnabled()) {
                            sw1.setEnabled(true);
                        }
                        if (!sw2.isEnabled()) {
                            sw2.setEnabled(true);
                        }
                        if (!sw3.isEnabled()) {
                            sw3.setEnabled(true);
                        }
                        if (!sw4.isEnabled()) {
                            sw4.setEnabled(true);
                        }
                        sw1.setChecked(!b.getOpenAudio().equals("0"));
                        sw2.setChecked(!b.getOpenVideo().equals("0"));
                        sw3.setChecked(!b.getOpenBanned().equals("0"));
                        sw4.setChecked(!b.getForceRecall().equals("0"));
                    });
                }
            }
        };

        TextView emptyView = new TextView(this);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setText("暂无管理员，点击添加");
        emptyView.setTextSize(15);
        emptyView.setTextColor(ContextCompat.getColor(this, R.color.textcolor2));

        adapter.setEmptyView(emptyView);

        emptyView.setOnClickListener(v -> {
            Intent intent = new Intent(OwnerGroupAuthorityActivity.this, CreateGroupActivity.class);
            intent.putExtra("eventType", 4);
            intent.putExtra("groupId", group.getGroupInfo().getId());
            startActivityForResult(intent, 1);
        });
    }

    @SuppressLint("CheckResult")
    public void save(View view) {
        String data;
        List<PermissionInfoBean> data1 = adapter.getData();
        if (data1.size() != 0) {
            data1.remove(data1.size() - 1);
            data1.remove(data1.size() - 1);
        }
        data = GsonUtils.toJson(data1);
        ServiceFactory.getInstance().getBaseService(Api.class)
                .updatePermissionInfo(data)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    ToastUtils.showShort(R.string.update_success);
                    finish();
                }, this::handleApiError);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            //addmanagers
            ArrayList<PermissionInfoBean> managers = data.getParcelableArrayListExtra("addmanagers");
            if (adapter.getData().size() == 0) {
                managers.add(null);
                managers.add(null);
                adapter.addData(adapter.getData().size(), managers);
            } else {
                adapter.addData(adapter.getData().size() - 2, managers);
            }
        }

        if (requestCode == 1 && resultCode == 2) {
            //deletemanagers
            ArrayList<String> managers = data.getStringArrayListExtra("deletemanagers");
            List<PermissionInfoBean> adapterData = adapter.getData();
            Iterator<PermissionInfoBean> iterator = adapterData.iterator();
            l:
            while (iterator.hasNext()) {
                PermissionInfoBean b = iterator.next();

                if (b == null) continue;

                for (String id : managers) {
                    if (id.equals(b.getCustomerId())) {
                        iterator.remove();
                        continue l;
                    }
                }
            }
            if (adapterData.size() == 2) {
                sw1.setEnabled(false);
                sw2.setEnabled(false);
                sw3.setEnabled(false);
                sw4.setEnabled(false);
                adapter.setNewData(new ArrayList<>(0));
            } else {
                adapter.setNewData(adapterData);
            }
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("group", group);
        setResult(4, intent);
        super.finish();
    }
}
