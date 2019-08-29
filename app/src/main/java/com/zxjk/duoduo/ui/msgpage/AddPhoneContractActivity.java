package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.BaseResponse;
import com.zxjk.duoduo.bean.response.GetFriendsByMobilesResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.widget.IndexView;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.GlideUtil;
import com.zxjk.duoduo.utils.PinYinUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

@SuppressLint("CheckResult")
public class AddPhoneContractActivity extends BaseActivity {

    private String numbers;
    private RecyclerView recycler;
    private IndexView index_view;
    private BaseQuickAdapter<GetFriendsByMobilesResponse, BaseViewHolder> adapter;
    private List<GetFriendsByMobilesResponse> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone_contract);

        initView();

        initData();

        EditText search_edit = findViewById(R.id.search_edit);
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (adapter == null) {
                    return;
                }
                List<GetFriendsByMobilesResponse> groupnamelist = search(editable.toString());
                adapter.setNewData(groupnamelist);
            }
        });
    }

    private List<GetFriendsByMobilesResponse> search(String str) {
        List<GetFriendsByMobilesResponse> filterList = new ArrayList<>();// 过滤后的list
        if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
            String simpleStr = str.replaceAll("\\-|\\s", "");
            for (GetFriendsByMobilesResponse contact : data) {
                if (contact.getNick().contains(simpleStr) || contact.getFriendRemarkName().contains(simpleStr) ||
                        contact.getMobile().contains(simpleStr)) {
                    filterList.add(contact);
                }
            }
        } else {
            for (GetFriendsByMobilesResponse contact : data) {
                boolean isNameContains = contact.getFriendRemarkName().toLowerCase(Locale.CHINESE)
                        .contains(str.toLowerCase(Locale.CHINESE)) ||
                        contact.getNick().toLowerCase(Locale.CHINESE)
                                .contains(str.toLowerCase(Locale.CHINESE));
                if (isNameContains) {
                    filterList.add(contact);
                }
            }
        }
        return filterList;
    }

    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.phone_friend);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        recycler = findViewById(R.id.recycler);

        index_view = findViewById(R.id.index_view);
        index_view.setShowTextDialog(findViewById(R.id.tvLetter));
    }

    private void initData() {
        Observable.create((ObservableOnSubscribe<String>) e -> {
            numbers = getPhoneNumberFromMobile(AddPhoneContractActivity.this);
            e.onNext(numbers);
        }).flatMap((Function<String, ObservableSource<BaseResponse<List<GetFriendsByMobilesResponse>>>>) s -> ServiceFactory.getInstance().getBaseService(Api.class).getFriendsByMobiles(s, ""))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .doOnNext(this::mapList)
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(list -> {
                    data = list;
                    index_view.setOnTouchingLetterChangedListener(letter -> {
                        for (int i = 0; i < list.size(); i++) {
                            String letters = list.get(i).getSortLetters();
                            if (letters.equals(letter)) {
                                recycler.scrollToPosition(i);
                                break;
                            }
                        }
                    });
                    initAdapter(list);
                }, this::handleApiError);
    }

    private void initAdapter(List<GetFriendsByMobilesResponse> list) {
        adapter = new BaseQuickAdapter<GetFriendsByMobilesResponse, BaseViewHolder>(R.layout.item_new_friend1, list) {
            @Override
            protected void convert(BaseViewHolder helper, GetFriendsByMobilesResponse item) {
                helper.setText(R.id.m_item_new_friend_user_name_text, item.getNick())
                        .setText(R.id.m_item_new_friend_message_label, item.getFriendRemarkName())
                        .setText(R.id.tvFirstLetter, item.getSortLetters())
                        .addOnClickListener(R.id.m_item_new_friend_type_btn)
                        .addOnClickListener(R.id.m_add_btn_layout);
                if (helper.getAdapterPosition() == 0) {
                    helper.setVisible(R.id.tvFirstLetter, true);
                } else {
                    if (adapter.getData().get(helper.getAdapterPosition() - 1).getSortLetters().equals(item.getSortLetters())) {
                        helper.setVisible(R.id.tvFirstLetter, false);
                    } else {
                        helper.setVisible(R.id.tvFirstLetter, true);
                    }
                }
                ImageView headerImage = helper.getView(R.id.m_item_new_friend_icon);
                GlideUtil.loadCircleImg(headerImage, item.getHeadPortrait());
                TextView typeBtn = helper.getView(R.id.m_item_new_friend_type_btn);
                if (item.getId().equals(Constant.userId) || !TextUtils.isEmpty(item.getFriendId())) {
                    typeBtn.setText(mContext.getString(R.string.m_item_contact_type_text));
                    typeBtn.setBackground(null);
                    typeBtn.setTextColor(ContextCompat.getColor(typeBtn.getContext(), R.color.color9));
                } else {
                    typeBtn.setText(R.string.add);
                    typeBtn.setBackgroundResource(R.drawable.shape_circular_bead_btn);
                    typeBtn.setTextColor(ContextCompat.getColor(typeBtn.getContext(), R.color.white));
                }
            }
        };

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        adapter.setOnItemChildClickListener((adapter, view, position) -> CommonUtils.resolveFriendList(this, ((GetFriendsByMobilesResponse) adapter.getData().get(position)).getId()));
    }

    private void mapList(List<GetFriendsByMobilesResponse> list) {
        for (GetFriendsByMobilesResponse f : list) {
            f.setSortLetters(PinYinUtils.converterToFirstSpell(TextUtils.isEmpty(f.getFriendRemarkName()) ? f.getNick() : f.getFriendRemarkName()));
        }
        Comparator<GetFriendsByMobilesResponse> comparator = (o1, o2) -> o1.getSortLetters().compareTo(o2.getSortLetters());
        Collections.sort(list, comparator);
    }

    private String getPhoneNumberFromMobile(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        while (cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "");
            stringBuilder.append(number + ",");
        }

        cursor.close();
        if (stringBuilder.length() <= 0) {
            return "";
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }
}
