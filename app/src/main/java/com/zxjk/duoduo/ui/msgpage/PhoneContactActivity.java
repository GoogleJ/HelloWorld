package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.PhoneInfo;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.adapter.PhoneContactAdapter;
import com.zxjk.duoduo.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PhoneContactActivity extends BaseActivity implements TextWatcher {
    private RecyclerView mRecyclerView;
    private List<PhoneInfo> list = new ArrayList<>();
    private EditText searchEdit;
    private PhoneContactAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_contact);
        TextView tv_title = findViewById(R.id.tv_title);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        tv_title.setText(getString(R.string.m_add_friend_contact_label_1));
        mRecyclerView = findViewById(R.id.phone_contact_recycler_view);

        searchEdit = findViewById(R.id.search_edit);
        searchEdit.addTextChangedListener(this);
        list = getPhoneNumberFromMobile(this);
        getFriendListById();
    }

    private List<PhoneInfo> getPhoneNumberFromMobile(Context context) {
        List<PhoneInfo> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        //moveToNext方法返回的是一个boolean类型的数据
        while (cursor.moveToNext()) {
            //读取通讯录的姓名
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            //读取通讯录的号码
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "");
            PhoneInfo phoneInfo = new PhoneInfo(name, number);
            if (list.contains(phoneInfo)) {
                continue;
            }
            list.add(phoneInfo);
        }
        cursor.close();
        return list;
    }

    //获取好友列表
    @SuppressLint("CheckResult")
    public void getFriendListById() {
//        if (Constant.friendsList == null) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getFriendListById()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(this::handleResult, this::handleApiError);
//        } else {
//            handleResult(Constant.friendsList);
//        }
    }

    @SuppressLint("CheckResult")
    private void handleResult(List<FriendInfoResponse> friendInfoResponses) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < friendInfoResponses.size(); j++) {
                if (list.get(i).getNumber().equals(friendInfoResponses.get(j).getMobile())) {
                    list.get(i).setAdd(true);
                }
            }
        }
        LinearLayoutManager manager = new LinearLayoutManager(PhoneContactActivity.this);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new PhoneContactAdapter(this);
        mAdapter.setNewData(list);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (list.get(position).isAdd()) {
                return;
            }
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .searchCustomerInfo(list.get(position).getNumber(), "1", "10")
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(PhoneContactActivity.this)))
                    .subscribe(friendInfoResponses1 -> {
                        if (friendInfoResponses1.size() == 0) {
                            sendSMS("您的好友在Hilamg给您留言了啦，赶快注册去查看吧。" + Constant.APP_DOWNLOAD_URL, position);
                        } else {
                            CommonUtils.resolveFriendList(PhoneContactActivity.this, friendInfoResponses1.get(0).getId());
                        }
                    }, this::handleApiError);
        });
    }

    private void sendSMS(String smsBody, int position) {
        if (list.get(position).isAdd()) {
            return;
        }
        Uri smsToUri = Uri.parse("smsto:" + list.get(position).getNumber());
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", smsBody);
        startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mAdapter == null) {
            return;
        }
        String groupname = s.toString();
        List<PhoneInfo> groupnamelist = search(groupname);
        mAdapter.setNewData(groupnamelist);
    }

    /**
     * 模糊查询
     *
     * @param str
     * @return
     */
    private List<PhoneInfo> search(String str) {
        List<PhoneInfo> filterList = new ArrayList<>();// 过滤后的list
        if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
            String simpleStr = str.replaceAll("\\-|\\s", "");
            for (PhoneInfo contact : list) {
                if (contact.getName() != null) {
                    if (contact.getName().contains(simpleStr) || contact.getNumber().contains(simpleStr)) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        } else {
            for (PhoneInfo contact : list) {
                if (contact.getName() != null) {
                    //姓名全匹配,姓名首字母简拼匹配,姓名全字母匹配
                    boolean isNameContains = contact.getName().toLowerCase(Locale.CHINESE)
                            .contains(str.toLowerCase(Locale.CHINESE));
                    if (isNameContains) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        }
        return filterList;
    }
}
