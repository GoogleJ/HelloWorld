package com.zxjk.duoduo.ui.msgpage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.ui.minepage.NearByActivity;
import com.zxjk.duoduo.ui.msgpage.adapter.BaseContactAdapter;
import com.zxjk.duoduo.ui.msgpage.widget.IndexView;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MMKVUtils;
import com.zxjk.duoduo.utils.PinYinUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactFragment extends BaseFragment {

    @BindView(R.id.m_contact_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.index_view)
    IndexView indexView;
    @BindView(R.id.m_constacts_dialog)
    TextView constactsDialog;
    @BindView(R.id.layout_contract_head)
    View layout_contract_head;
    @BindView(R.id.m_contact_search_edit_1)
    TextView textView;

    private BaseContactAdapter mAdapter;
    private View dotNewFriend;
    private TextView footview;

    List<FriendInfoResponse> list = new ArrayList<>();

    public View getDotNewFriend() {
        return dotNewFriend;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.activity_constacts_new_friend, container, false);
        ButterKnife.bind(this, rootView);

        textView.setOnClickListener(v -> startActivity(new Intent(getContext(), GlobalSearchActivity.class)));

        initRecycler();

        return rootView;
    }

    private void initRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new BaseContactAdapter();
        indexView.setShowTextDialog(constactsDialog);
        indexView.setOnTouchingLetterChangedListener(letter -> {
            for (int i = 0; i < list.size(); i++) {
                String letters = list.get(i).getSortLetters();
                if (letters.equals(letter)) {
                    mRecyclerView.scrollToPosition(i);
                    break;
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            FriendInfoResponse friendInfoResponse = mAdapter.getData().get(position);
            Intent intent = new Intent(getActivity(), FriendDetailsActivity.class);
            intent.putExtra("friendResponse", friendInfoResponse);
            startActivity(intent);
        });
        if (mAdapter.getData().size() == 0) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_app_null_type, null);
            ImageView iv = view.findViewById(R.id.app_type);
            iv.setImageResource(R.drawable.ic_emptyview_nofriend);
            mAdapter.setEmptyView(view);
        }
    }

    private View headView;
    private void initHead(boolean isEmpty) {
        LinearLayout ll_contract_top1;
        LinearLayout ll_contract_top2;
        LinearLayout ll_contract_top3;
        if (isEmpty) {
            layout_contract_head.setVisibility(View.VISIBLE);
            headView = layout_contract_head;
        } else {
            layout_contract_head.setVisibility(View.GONE);
            if (mAdapter.getHeaderLayoutCount() == 0) {
                headView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_contract_head, null);
                mAdapter.addHeaderView(headView);
            }
        }
        ll_contract_top1 = headView.findViewById(R.id.ll_contract_top1);
        ll_contract_top2 = headView.findViewById(R.id.ll_contract_top2);
        ll_contract_top3 = headView.findViewById(R.id.ll_contract_top3);
        dotNewFriend = headView.findViewById(R.id.dotNewFriend);

        if (MMKVUtils.getInstance().decodeLong("newFriendCount") != 0) {
            dotNewFriend.setVisibility(View.VISIBLE);
        }

        ll_contract_top1.setOnClickListener(v -> {
            ((HomeActivity) getActivity()).badgeItem2.hide();
            MMKVUtils.getInstance().enCode("newFriendCount", 0);
            dotNewFriend.setVisibility(View.INVISIBLE);
            startActivity(new Intent(getActivity(), NewFriendActivity.class));
        });

        ll_contract_top2.setOnClickListener(v -> startActivity(new Intent(getActivity(), GroupChatActivity.class)));

        getPermisson(ll_contract_top3, granted -> {
            if (!granted) {
                return;
            }
            AMapLocationClient mLocationClient = new AMapLocationClient(Utils.getApp());
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.setLocationListener(location -> {
                CommonUtils.destoryDialog();
                if (location.getErrorCode() == 0) {
                    Intent intent = new Intent(getContext(), NearByActivity.class);
                    intent.putExtra("location", location);
                    startActivity(intent);
                } else {
                    ToastUtils.showShort(R.string.getlocation_fail);
                }
            });
            CommonUtils.initDialog(getActivity(), getString(R.string.getlocation)).show();
            mLocationClient.startLocation();
        }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void initFoot() {
        footview = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_contract_foot, null);
        footview.setId(R.id.tv);
        if (mAdapter.getFooterLayoutCount() == 0) {
            mAdapter.addFooterView(footview);
        } else {
            footview = mAdapter.getFooterLayout().findViewById(R.id.tv);
        }
        footview.setText("共 " + list.size() + " 位联系人");
    }

    /**
     * 获取好友列表
     */
    @SuppressLint("CheckResult")
    private void getFriendListInfoById() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getFriendListById()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver())
                .compose(RxSchedulers.normalTrans())
                .subscribe(data -> {
                    list = data;

                    mapList(list);

                    mAdapter.setNewData(list);

                    initFoot();

                    initHead(data.size() == 0);
                }, this::handleApiError);
    }

    private void mapList(List<FriendInfoResponse> list) {
        for (FriendInfoResponse f : list) {
            f.setSortLetters(PinYinUtils.converterToFirstSpell(TextUtils.isEmpty(f.getRemark()) ? f.getNick() : f.getRemark()));
        }
        Comparator<FriendInfoResponse> comparator = (o1, o2) -> o1.getSortLetters().compareTo(o2.getSortLetters());
        Collections.sort(list, comparator);
    }

    @Override
    public void onResume() {
        super.onResume();
        getFriendListInfoById();
    }
}
