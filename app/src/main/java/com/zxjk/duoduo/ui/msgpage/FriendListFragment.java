package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.ui.msgpage.adapter.BaseContactAdapter;
import com.zxjk.duoduo.ui.msgpage.widget.IndexView;
import com.zxjk.duoduo.utils.PinYinUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendListFragment extends BaseFragment {

    @BindView(R.id.m_contact_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.index_view)
    IndexView indexView;
    @BindView(R.id.m_constacts_dialog)
    TextView constactsDialog;
    List<FriendInfoResponse> list = new ArrayList<>();
    private BaseContactAdapter mAdapter;
    private TextView footview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_friend_list, container, false);
        ButterKnife.bind(this, rootView);
        initRecycler();

        initFoot();

        getFriendListInfoById();
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

                    break;
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            FriendInfoResponse friendInfoResponse = mAdapter.getData().get(position);
            Intent intent = new Intent(getActivity(), FriendDetailsActivity.class);
            intent.putExtra("friendId", friendInfoResponse.getId());
            startActivity(intent);
        });
        if (mAdapter.getData().size() == 0) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_app_null_type, null);
            ImageView iv = view.findViewById(R.id.app_type);
            iv.setImageResource(R.drawable.ic_emptyview_nofriend);
            mAdapter.setEmptyView(view);
        }
    }


    private void initFoot() {
        footview = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_contract_foot, null);
        footview.setId(R.id.tv);
        if (mAdapter.getFooterLayoutCount() == 0) {
            mAdapter.addFooterView(footview);
        } else {
            footview = mAdapter.getFooterLayout().findViewById(R.id.tv);
        }
        footview.setText(getString(R.string.total_xx_contact, String.valueOf(list.size())));
    }


    /**
     * 获取好友列表
     */
    @SuppressLint("CheckResult")
    private void getFriendListInfoById() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getFriendListById()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .map(friendInfoResponses -> {

                    list = friendInfoResponses;

                    mapList(list);

                    return list;
                })
                .compose(RxSchedulers.ioObserver())

                .subscribe(data -> {

                    mAdapter.setNewData(list);

                    initFoot();

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
        if (mAdapter == null) {
            return;
        }
        getFriendListInfoById();
    }
}
