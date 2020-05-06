package com.zxjk.moneyspace.ui.minepage.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GetEcologyByType;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.WebActivity;
import com.zxjk.moneyspace.ui.base.BaseFragment;
import com.zxjk.moneyspace.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class EcologyFragment extends BaseFragment {
    private RecyclerView rcEcology;
    private BaseQuickAdapter<GetEcologyByType, BaseViewHolder> ecologyAdapter;
    private int count;
    private List<GetEcologyByType> ecologyByTypes = new ArrayList<>();

    public static EcologyFragment newInstance(int count) {
        EcologyFragment fragment = new EcologyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("count", count);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ecology, container, false);

        initView();

        initData();

        return rootView;
    }

    private void initView() {
        rcEcology = rootView.findViewById(R.id.rc_ecology);
        count = getArguments().getInt("count");
    }

    @SuppressLint("CheckResult")
    private void initData() {

        ecologyAdapter = new BaseQuickAdapter<GetEcologyByType, BaseViewHolder>(R.layout.rc_item_ecology) {
            @Override
            protected void convert(BaseViewHolder helper, GetEcologyByType item) {
                TextView textView = helper.getView(R.id.tv1);
                Glide.with(getActivity())
                        .load(item.getLogo())
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                setDrawable(resource, textView, item.getName());
                            }
                        });
            }
        };

        ecologyAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (!TextUtils.isEmpty(ecologyByTypes.get(position).getUrl())) {
                Intent intent = new Intent(getContext(), WebActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", ecologyByTypes.get(position).getUrl());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            } else {
                ToastUtils.showShort(getString(R.string.developing));
            }
        });

        rcEcology.setAdapter(ecologyAdapter);
        rcEcology.setItemAnimator(new DefaultItemAnimator());
        rcEcology.setLayoutManager(new LinearLayoutManager(getActivity()));

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getEcologyByType(String.valueOf(count))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(getActivity())))
                .subscribe(d -> {
                    ecologyByTypes.addAll(d);
                    ecologyAdapter.setNewData(d);
                }, this::handleApiError);
    }

    private void setDrawable(Drawable drawable, TextView textView, String text) {
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                .getMinimumHeight());// 设置边界
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }
        textView.setCompoundDrawables(drawable, null, null, null);
        textView.setCompoundDrawablePadding(8);
    }
}
