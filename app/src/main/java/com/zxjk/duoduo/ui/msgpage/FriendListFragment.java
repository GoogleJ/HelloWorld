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

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    List<FriendInfoResponse> list = new ArrayList<>();
    private BaseContactAdapter mAdapter;
    private TextView footview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friend_list, container, false);

        ButterKnife.bind(this, rootView);

        refreshLayout.setOnRefreshListener(this::getFriendListInfoById);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorTheme));

        initIndexView();

        initRecycler();

        initFoot();

        return rootView;
    }

    private void initIndexView() {
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
    }

    private void initRecycler() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new BaseContactAdapter();

        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            FriendInfoResponse friendInfoResponse = mAdapter.getData().get(position);
            Intent intent = new Intent(getActivity(), FriendDetailsActivity.class);
            intent.putExtra("friendId", friendInfoResponse.getId());
            startActivity(intent);
        });

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_app_null_type, null);
        ImageView iv = view.findViewById(R.id.app_type);
        iv.setImageResource(R.drawable.ic_emptyview_nofriend);
        mAdapter.setEmptyView(view);

        mRecyclerView.setAdapter(mAdapter);
    }

    private void initFoot() {
        if (mAdapter.getFooterLayoutCount() == 0) {
            footview = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_contract_foot, null);
            footview.setId(R.id.tv);
            mAdapter.addFooterView(footview);
        } else {
            footview = mAdapter.getFooterLayout().findViewById(R.id.tv);
        }

        if (footview == null) return;

        footview.setText(getString(R.string.total_xx_contact, String.valueOf(list.size())));
    }

    @SuppressLint("CheckResult")
    private void getFriendListInfoById() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getFriendListById()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .map(friendList -> {
                    mapList(friendList);
                    return friendList;
                })
                .compose(RxSchedulers.ioObserver())
                .doOnSubscribe(disposable -> {
                    if (null != refreshLayout) {
                        refreshLayout.setRefreshing(true);
                    }
                })
                .doOnTerminate(() -> {
                    if (null != refreshLayout) {
                        refreshLayout.setRefreshing(false);
                    }
                })
                .subscribe(friendList -> {
                    list = friendList;

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
        getFriendListInfoById();
    }
}
