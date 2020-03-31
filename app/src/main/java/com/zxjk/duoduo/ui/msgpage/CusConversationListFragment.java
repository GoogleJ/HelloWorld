package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.TimeUtils;
import com.zxjk.duoduo.Application;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.CastDao;
import com.zxjk.duoduo.bean.response.CastListBean;
import com.zxjk.duoduo.db.Cast;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.msgpage.rongIM.CusConversationListAdapter;
import com.zxjk.duoduo.ui.webcast.CastListActivity;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.widget.QuickPopup;

public class CusConversationListFragment extends ConversationListFragment {
    private LinearLayout llCastTitle;
    private TextView tvCastTitle;
    private TextView tvAction;

    @Override
    public ConversationListAdapter onResolveAdapter(Context context) {
        return new CusConversationListAdapter(context);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        handleLongClick((UIConversation) parent.getItemAtPosition(position));
        return true;
    }

    @Override
    public boolean onPortraitItemLongClick(View v, UIConversation data) {
        handleLongClick(data);
        return true;
    }

    private void handleLongClick(UIConversation c) {
        QuickPopup longClickPop = QuickPopupBuilder.with(((Fragment) this).getContext())
                .contentView(R.layout.pop_conversationlist)
                .config(new QuickPopupConfig()
                        .withClick(R.id.tv1, v -> RongIM.getInstance().setConversationToTop(c.getConversationType(),
                                c.getConversationTargetId(), !c.isTop(), null), true)
                        .withClick(R.id.tv2, v -> {
                            RongIM.getInstance().removeConversation(c.getConversationType(), c.getConversationTargetId(), null);
                            RongIM.getInstance().clearMessages(c.getConversationType(), c.getConversationTargetId(), null);
                        }, true))
                .show();

        String cancelTop = ((Fragment) this).getContext().getString(R.string.cancelTop);
        String setTop = ((Fragment) this).getContext().getString(R.string.setTop);
        TextView tvSetTop = longClickPop.findViewById(R.id.tv1);
        tvSetTop.setText(c.isTop() ? cancelTop : setTop);
    }

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        llCastTitle = rootView.findViewById(R.id.llCastTitle);
        llCastTitle.setOnClickListener(v -> ((Fragment) this).startActivity(new Intent(((Fragment) this).getContext(), CastListActivity.class)));
        tvCastTitle = rootView.findViewById(R.id.tvCastTitle);
        tvAction = rootView.findViewById(R.id.tvAction);

        ServiceFactory.getInstance().getBaseService(Api.class)
                .toLiveList()
                .compose(RxSchedulers.normalTrans())
                .doOnNext(origin -> {
                    if (origin.size() == 0) {
                        Application.daoSession.getCastDao().deleteAll();
                    } else {
                        List<Cast> result = new ArrayList<>(origin.size());
                        for (CastListBean castListBean : origin) {
                            result.add(castListBean.convert2TableBean());
                        }
                        Application.daoSession.getCastDao().insertOrReplaceInTx(result);
                    }
                })
                .compose(RxSchedulers.ioObserver())
                .subscribe(list -> {
                }, t -> {
                });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Cast> list = Application.daoSession.getCastDao().queryBuilder().orderDesc(CastDao.Properties.StartTimeStamp).list();
        if (list.size() != 0) {
            llCastTitle.setVisibility(View.VISIBLE);
            refreshCastTitleUI(list);
        } else {
            llCastTitle.setVisibility(View.GONE);
        }
    }

    private void refreshCastTitleUI(List<Cast> list) {
        if (System.currentTimeMillis() > list.get(0).getStartTimeStamp()) {
            tvCastTitle.setText(R.string.cast_title_ing);
            tvAction.setText(R.string.continue_cast);
        } else {
            tvAction.setText(R.string.cast_title_view);
            tvCastTitle.setText(((Fragment) this).getString(R.string.cast_title_date, TimeUtils.millis2String(list.get(0).getStartTimeStamp(), "MM月dd日HH:mm")));
        }
    }
}
