package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.bean.response.PermissionInfoBean;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.HomeActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.msgpage.adapter.CreateGroupAdapter;
import com.zxjk.duoduo.ui.msgpage.adapter.CreateGroupTopAdapter;
import com.zxjk.duoduo.ui.msgpage.adapter.GroupMemberAdapter;
import com.zxjk.duoduo.ui.msgpage.adapter.GroupMemberTopAdapter;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.GroupCardMessage;
import com.zxjk.duoduo.ui.msgpage.rongIM.message.SocialGroupCardMessage;
import com.zxjk.duoduo.ui.msgpage.widget.IndexView;
import com.zxjk.duoduo.ui.widget.MaxWidthRecyclerView;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.PinYinUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.InformationNotificationMessage;

@SuppressLint("CheckResult")
public class CreateGroupActivity extends BaseActivity implements TextWatcher {
    private EditText etSearch;
    private ArrayList<String> selectedIds = new ArrayList<>();

    private MaxWidthRecyclerView recycler1;
    private RecyclerView recycler2;
    private CreateGroupTopAdapter adapter1; //建群适配器（顶部）
    private CreateGroupAdapter adapter2; //建群列表适配器
    private TextView tv_hit_letter;
    private IndexView indexCreateGroup;

    private List<FriendInfoResponse> data = new ArrayList<>(); //总list
    private List<FriendInfoResponse> data1 = new ArrayList<>(); //top数据

    private List<GroupResponse.CustomersBean> data2 = new ArrayList<>(); //总list（群成员）
    private List<GroupResponse.CustomersBean> data3 = new ArrayList<>(); //top数据（群成员）

    private GroupMemberTopAdapter adapter3; //群成员适配器（顶部）
    private GroupMemberAdapter adapter4; //群成员列表适配器

    CharSequence confirmText;

    private int eventType;

    private final int EVENT_CREATEGROUP = 1; //创建群
    private final int EVENT_ADDMENBER = 2; //添加成员
    private final int EVENT_DELETEMEMBER = 3; //删除成员
    private final int EVENT_ADDMANAGER = 4; //添加管理员
    private final int EVENT_DELETEMANAGER = 5; //删除管理员

    //temp
    private List<GroupResponse.CustomersBean> c = new ArrayList<>();

    private TextView tv_commit;

    private GroupResponse groupResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        tv_commit = findViewById(R.id.tv_commit);
        etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(this);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_commit.setVisibility(View.VISIBLE);
        tv_commit.setText(getString(R.string.ok));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        confirmText = tv_commit.getText();
        recycler1 = findViewById(R.id.recycler1);
        recycler2 = findViewById(R.id.recycler2);
        tv_hit_letter = findViewById(R.id.tv_hit_letter);
        indexCreateGroup = findViewById(R.id.indexCreateGroup);
        indexCreateGroup.setShowTextDialog(tv_hit_letter);
        indexCreateGroup.setOnTouchingLetterChangedListener(letter -> recycler2.scrollToPosition(getScrollPosition(letter)));
        tv_commit.setOnClickListener(v -> confirm());

        eventType = getIntent().getIntExtra("eventType", -1);
        groupResponse = (GroupResponse) getIntent().getSerializableExtra("members");

        if (eventType == EVENT_CREATEGROUP){
            tv_title.setText(getString(R.string.select_contacts));
            handleCreateGroup();
            return;
        }
        if (groupResponse == null) {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getGroupByGroupId(getIntent().getStringExtra("groupId"))
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(groupResponse -> {
                        this.groupResponse = groupResponse;
                        handleResult(tv_title, groupResponse);
                    }, this::handleApiError);
        } else {
            handleResult(tv_title, groupResponse);
        }
    }

    private void handleResult(TextView tv_title, GroupResponse groupResponse) {
        List<GroupResponse.CustomersBean> customers = groupResponse.getCustomers();
        c.addAll(customers);
        if (eventType == EVENT_ADDMENBER) {
            tv_title.setText(getString(R.string.select_contacts));
            handleAddMember();
        } else if (eventType == EVENT_DELETEMEMBER) {
            tv_title.setText(R.string.remove_from_group);
            handleDeleteMember();
        } else if (eventType == EVENT_ADDMANAGER) {
            tv_title.setText(R.string.add_manager);
            handleAddManager();
        } else if (eventType == EVENT_DELETEMANAGER) {
            tv_title.setText(R.string.delete_manager);
            handleDeleteManager();
        }
    }

    //删除管理员逻辑
    private void handleDeleteManager() {
        initData3();
        addFirstLetterForGroupList(data2);

        recycler1.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView,
                                               RecyclerView.State state, final int position) {

                LinearSmoothScroller smoothScroller =
                        new LinearSmoothScroller(recyclerView.getContext()) {
                            @Override
                            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                return 150f / displayMetrics.densityDpi;
                            }
                        };

                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        });
        recycler2.setLayoutManager(new LinearLayoutManager(this));
        recycler2.setItemAnimator(null);
        adapter3 = new GroupMemberTopAdapter();
        adapter4 = new GroupMemberAdapter();
        recycler1.setAdapter(adapter3);
        recycler2.setAdapter(adapter4);
        adapter4.setData(data2);
        adapter3.setData(data3);
        adapter4.setOnClickListener((item, check, position) -> {
            if (data3.contains(item)) {
                selectedIds.remove(item.getId());
                data3.remove(item);
                adapter3.notifyDataSetChanged();
            } else {
                selectedIds.add(item.getId());
                data3.add(item);
                adapter3.notifyItemInserted(data3.size() - 1);
                recycler1.smoothScrollToPosition(data3.size() - 1);
            }
            tv_commit.setText(confirmText + "(" + data3.size() + ")");
        });
    }

    //添加管理员逻辑
    private void handleAddManager() {
        recycler1.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView,
                                               RecyclerView.State state, final int position) {

                LinearSmoothScroller smoothScroller =
                        new LinearSmoothScroller(recyclerView.getContext()) {
                            @Override
                            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                return 150f / displayMetrics.densityDpi;
                            }
                        };

                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        });
        recycler2.setLayoutManager(new LinearLayoutManager(this));
        recycler2.setItemAnimator(null);
        adapter3 = new GroupMemberTopAdapter();
        adapter4 = new GroupMemberAdapter();
        recycler1.setAdapter(adapter3);
        recycler2.setAdapter(adapter4);
        adapter4.setData(data2);
        adapter3.setData(data3);
        adapter4.setOnClickListener((item, check, position) -> {
            if (data3.contains(item)) {
                selectedIds.remove(item.getId());
                data3.remove(item);
                adapter3.notifyDataSetChanged();
            } else {
                selectedIds.add(item.getId());
                data3.add(item);
                adapter3.notifyItemInserted(data3.size() - 1);
                recycler1.smoothScrollToPosition(data3.size() - 1);
            }
            tv_commit.setText(confirmText + "(" + data3.size() + ")");
        });

        initData4();
    }

    //删除成员逻辑
    private void handleDeleteMember() {
        initData2();
        addFirstLetterForGroupList(data2);
        recycler1.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView,
                                               RecyclerView.State state, final int position) {

                LinearSmoothScroller smoothScroller =
                        new LinearSmoothScroller(recyclerView.getContext()) {
                            @Override
                            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                return 150f / displayMetrics.densityDpi;
                            }
                        };

                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        });
        recycler2.setLayoutManager(new LinearLayoutManager(this));
        recycler2.setItemAnimator(null);
        adapter3 = new GroupMemberTopAdapter();
        adapter4 = new GroupMemberAdapter();
        recycler1.setAdapter(adapter3);
        recycler2.setAdapter(adapter4);
        adapter4.setData(data2);
        adapter3.setData(data3);
        adapter4.setOnClickListener((item, check, position) -> {
            if (data3.contains(item)) {
                selectedIds.remove(item.getId());
                data3.remove(item);
                adapter3.notifyDataSetChanged();
            } else {
                selectedIds.add(item.getId());
                data3.add(item);
                adapter3.notifyItemInserted(data3.size() - 1);
                recycler1.smoothScrollToPosition(data3.size() - 1);
            }
            tv_commit.setText(confirmText + "(" + data3.size() + ")");
        });
    }

    //添加成员逻辑
    private void handleAddMember() {
        initData2();
        recycler1.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView,
                                               RecyclerView.State state, final int position) {
                LinearSmoothScroller smoothScroller =
                        new LinearSmoothScroller(recyclerView.getContext()) {
                            @Override
                            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                return 150f / displayMetrics.densityDpi;
                            }
                        };

                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        });
        adapter1 = new CreateGroupTopAdapter();
        recycler1.setAdapter(adapter1);

        adapter2 = new CreateGroupAdapter(true);
        recycler2.setLayoutManager(new LinearLayoutManager(this));
        recycler2.setAdapter(adapter2);
        recycler2.setItemAnimator(null);

        adapter2.setOnClickListener((item, check, position) -> {
            item.setChecked(!item.isChecked());
            adapter2.notifyItemChanged(position);
            if (data1.contains(item)) {
                selectedIds.remove(item.getId());
                data1.remove(item);
                adapter1.notifyDataSetChanged();
            } else {
                selectedIds.add(item.getId());
                data1.add(item);
                adapter1.notifyItemInserted(data1.size() - 1);
                recycler1.smoothScrollToPosition(data1.size() - 1);
            }

            tv_commit.setText(confirmText + "(" + data1.size() + ")");
        });

        adapter2.setData1(data2);
        initData();
    }

    //创建群逻辑
    private void handleCreateGroup() {
        recycler1.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView,
                                               RecyclerView.State state, final int position) {

                LinearSmoothScroller smoothScroller =
                        new LinearSmoothScroller(recyclerView.getContext()) {
                            @Override
                            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                return 150f / displayMetrics.densityDpi;
                            }
                        };

                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        });
        adapter1 = new CreateGroupTopAdapter();
        recycler1.setAdapter(adapter1);

        adapter2 = new CreateGroupAdapter();
        recycler2.setLayoutManager(new LinearLayoutManager(this));
        recycler2.setAdapter(adapter2);
        recycler2.setItemAnimator(null);

        adapter2.setOnClickListener((item, check, position) -> {
            if (data1.contains(item)) {
                selectedIds.remove(item.getId());
                data1.remove(item);
                adapter1.notifyDataSetChanged();
            } else {
                selectedIds.add(item.getId());
                data1.add(item);
                adapter1.notifyItemInserted(data1.size() - 1);
                recycler1.smoothScrollToPosition(data1.size() - 1);
            }

            tv_commit.setText(confirmText + "(" + data1.size() + ")");
        });

        initData();
    }

    private void initData() {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getFriendListById()
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(response -> {
                    addFirstLetterForFriendList(response);
                    data = response;
                    adapter2.setData(data);
                    adapter1.setData(data1);
                }, this::handleApiError);
    }

    //获取群成员
    private void initData2() {
        data2 = groupResponse.getCustomers();
        Iterator<GroupResponse.CustomersBean> iterator = data2.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals(Constant.userId)) {
                iterator.remove();
            }
            if (iterator.next().getId().equals(groupResponse.getGroupInfo().getGroupOwnerId())) {
                iterator.remove();
            }
        }
    }

    //获取群管理员
    private void initData3() {
        ArrayList<PermissionInfoBean> managers = getIntent().getParcelableArrayListExtra("managers");
        data2 = new ArrayList<>(managers.size());
        for (PermissionInfoBean manager : managers) {
            if (manager == null) continue;

            GroupResponse.CustomersBean bean = new GroupResponse.CustomersBean();
            bean.setHeadPortrait(manager.getHeadPortrait());
            bean.setId(manager.getCustomerId());
            bean.setNick(manager.getNick());
            bean.setRemark(manager.getNick());
            data2.add(bean);
        }
    }

    //获取普通群成员（不是管理员）
    private void initData4() {
        ArrayList<PermissionInfoBean> managers = getIntent().getParcelableArrayListExtra("managers");
        data2 = groupResponse.getCustomers();
        Iterator<GroupResponse.CustomersBean> iterator = data2.iterator();

        while (iterator.hasNext()) {
            GroupResponse.CustomersBean member = iterator.next();

            if (member.getId().equals(Constant.currentUser.getId())) iterator.remove();

            if (managers != null && managers.size() != 0) {
                for (PermissionInfoBean b : managers) {
                    if (b == null) continue;
                    if (b.getCustomerId().equals(member.getId())) iterator.remove();
                }
            }
        }

        addFirstLetterForGroupList(data2);
        adapter4.setData(data2);
    }

    private void addFirstLetterForGroupList(List<GroupResponse.CustomersBean> list) {
        Comparator<GroupResponse.CustomersBean> comparator = (f1, f2) -> f1.getFirstLetter().compareTo(f2.getFirstLetter());
        for (GroupResponse.CustomersBean c : list) {
            c.setFirstLetter(PinYinUtils.converterToFirstSpell(TextUtils.isEmpty(c.getRemark()) ? c.getNick() : c.getRemark()));
        }
        Collections.sort(list, comparator);
    }

    private void addFirstLetterForFriendList(List<FriendInfoResponse> list) {
        Comparator<FriendInfoResponse> comparator = (f1, f2) -> f1.getFirstLeter().compareTo(f2.getFirstLeter());
        for (FriendInfoResponse f : list) {
            f.setFirstLeter(PinYinUtils.converterToFirstSpell(TextUtils.isEmpty(f.getRemark()) ? f.getNick() : f.getRemark()));
        }
        Collections.sort(list, comparator);
    }

    private int getScrollPosition(String letter) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getFirstLeter().equals(letter)) {
                return i;
            }
        }
        return -1;
    }

    public void confirm() {
        if (eventType == EVENT_CREATEGROUP) {
            //创建群
            createGroup();
        } else if (eventType == EVENT_ADDMENBER) {
            //添加成员
            inviteMember();
        } else if (eventType == EVENT_DELETEMEMBER) {
            //删除成员
            deleteMember();
        } else if (eventType == EVENT_ADDMANAGER) {
            //添加管理员
            inviteManager();
        } else if (eventType == EVENT_DELETEMANAGER) {
            //删除管理员
            deleteManager();
        }
    }

    //添加管理员
    private void inviteManager() {
        if (data3.size() == 0) {
            ToastUtils.showShort(R.string.select_invite_manager);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String str : selectedIds) {
            sb.append(str + ",");
        }

        ServiceFactory.getInstance().getBaseService(Api.class)
                .addPermissionInfo(groupResponse.getGroupInfo().getId(), sb.substring(0, sb.length() - 1))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    ArrayList<PermissionInfoBean> data = new ArrayList<>(data3.size());
                    for (GroupResponse.CustomersBean b : data3) {
                        PermissionInfoBean bean = new PermissionInfoBean();
                        bean.setGroupId(groupResponse.getGroupInfo().getId());
                        bean.setCustomerId(b.getId());
                        bean.setNick(b.getNick());
                        bean.setHeadPortrait(b.getHeadPortrait());
                        data.add(bean);
                    }
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("addmanagers", data);
                    setResult(1, intent);
                    finish();
                }, this::handleApiError);
    }

    //删除管理员
    private void deleteManager() {
        if (data3.size() == 0) {
            ToastUtils.showShort(R.string.select_delete_manager);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String str : selectedIds) {
            sb.append(str + ",");
        }

        ServiceFactory.getInstance().getBaseService(Api.class)
                .removePermissionInfo(groupResponse.getGroupInfo().getId(), sb.substring(0, sb.length() - 1))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("deletemanagers", selectedIds);
                    setResult(2, intent);
                    finish();
                }, this::handleApiError);
    }

    //添加成员
    private void inviteMember() {
        if (data1.size() == 0) {
            ToastUtils.showShort(R.string.select_invite_member);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (GroupResponse.CustomersBean b : c) {
            stringBuilder.append(b.getHeadPortrait() + ",");
        }

        if (getIntent().getBooleanExtra("fromSocial", false)) {
            SocialGroupCardMessage socialGroupCardMessage = new SocialGroupCardMessage();
            socialGroupCardMessage.setIcon(getIntent().getStringExtra("socialLogo"));
            socialGroupCardMessage.setGroupId(groupResponse.getGroupInfo().getId());
            socialGroupCardMessage.setInviterId(Constant.userId);
            socialGroupCardMessage.setName(Constant.currentUser.getNick());
            socialGroupCardMessage.setGroupName(groupResponse.getGroupInfo().getGroupNikeName());
            socialGroupCardMessage.setMemberNum(groupResponse.getCustomers().size() + "");

            Observable.interval(0, 250, TimeUnit.MILLISECONDS)
                    .take(data1.size())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(d -> CommonUtils.initDialog(CreateGroupActivity.this, getString(R.string.inviting)).show())
                    .doOnDispose(CommonUtils::destoryDialog)
                    .doOnComplete(() -> {
                        ToastUtils.showShort(R.string.invite_success);
                        CommonUtils.destoryDialog();
                        finish();
                    })
                    .compose(bindToLifecycle())
                    .subscribe(l -> {
                        Message message = Message.obtain(data1.get(l.intValue()).getId(), Conversation.ConversationType.PRIVATE, socialGroupCardMessage);
                        RongIM.getInstance().sendMessage(message, null, null, (IRongCallback.ISendMessageCallback) null);
                    });
        } else {
            GroupCardMessage groupCardMessage = new GroupCardMessage();
            groupCardMessage.setIcon(stringBuilder.substring(0, stringBuilder.length() - 1));
            groupCardMessage.setGroupId(groupResponse.getGroupInfo().getId());
            groupCardMessage.setInviterId(Constant.userId);
            groupCardMessage.setName(Constant.currentUser.getNick());
            groupCardMessage.setGroupName(groupResponse.getGroupInfo().getGroupNikeName());

            Observable.interval(0, 250, TimeUnit.MILLISECONDS)
                    .take(data1.size())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(d -> CommonUtils.initDialog(CreateGroupActivity.this, getString(R.string.inviting)).show())
                    .doOnDispose(CommonUtils::destoryDialog)
                    .doOnComplete(() -> {
                        ToastUtils.showShort(R.string.invite_success);
                        CommonUtils.destoryDialog();
                        finish();
                    })
                    .compose(bindToLifecycle())
                    .subscribe(l -> {
                        Message message = Message.obtain(data1.get(l.intValue()).getId(), Conversation.ConversationType.PRIVATE, groupCardMessage);
                        RongIM.getInstance().sendMessage(message, null, null, (IRongCallback.ISendMessageCallback) null);
                    });
        }
    }

    //删除成员
    private void deleteMember() {
        if (data3.size() == 0) {
            ToastUtils.showShort(R.string.select_remove_member);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String ids : selectedIds) {
            sb.append(ids + ",");
        }
        ServiceFactory.getInstance().getBaseService(Api.class)
                .moveOutGroup(groupResponse.getGroupInfo().getId(), sb.substring(0, sb.length() - 1))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(CreateGroupActivity.this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    if (getIntent().getBooleanExtra("fromSocial", false)) {
                        finish();
                    } else {
                        Intent intent = new Intent(this, ConversationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                }, this::handleApiError);
    }

    //创建群
    private void createGroup() {
        if (data1.size() < 2) {
            ToastUtils.showShort(R.string.create_group_tips);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String ids : selectedIds) {
            sb.append(ids + ",");
        }
        sb.append(Constant.userId);

        ServiceFactory.getInstance().getBaseService(Api.class)
                .makeGroup(Constant.userId, sb.toString())
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    selectedIds.add(Constant.userId);
                    InformationNotificationMessage message = InformationNotificationMessage.obtain(Constant.currentUser.getNick() + getString(R.string.notice_creategroup));
                    RongIM.getInstance().sendDirectionalMessage(Conversation.ConversationType.GROUP, s.getId(), message, selectedIds.toArray(new String[]{}), null, null, new IRongCallback.ISendMessageCallback() {
                        @Override
                        public void onAttached(Message message) {
                        }

                        @Override
                        public void onSuccess(Message message) {
                            ToastUtils.showShort(R.string.create_game_group_success);
                            Intent intent = new Intent(CreateGroupActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            RongIM.getInstance().startGroupChat(CreateGroupActivity.this, s.getId(), s.getGroupNikeName());
                        }

                        @Override
                        public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                            ToastUtils.showShort(R.string.function_fail);
                        }
                    });
                }, this::handleApiError);
    }

    private void search1(String str) {
        List<FriendInfoResponse> filterList = new ArrayList<>(data.size());

        for (FriendInfoResponse contact : data) {
            boolean isNameContains = contact.getMobile().contains(str) || contact.getNick()
                    .contains(str) || contact.getRemark().contains(str);
            if (isNameContains) {
                if (!filterList.contains(contact)) {
                    filterList.add(contact);
                }
            }

        }
        adapter2.setData(filterList);
        adapter2.notifyDataSetChanged();
    }

    private void search2(String str) {
        List<GroupResponse.CustomersBean> filterList = new ArrayList<>(data2.size());

        for (GroupResponse.CustomersBean contact : data2) {
            boolean isNameContains = contact.getMobile().contains(str) || contact.getNick()
                    .contains(str) || contact.getRemark().contains(str);
            if (isNameContains) {
                if (!filterList.contains(contact)) {
                    filterList.add(contact);
                }
            }
        }
        adapter4.setData(filterList);
        adapter4.notifyDataSetChanged();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(etSearch.getText().toString().trim())) {
            if (adapter2 != null) {
                adapter2.setData(data);
            } else {
                adapter4.setData(data2);
            }
        } else {
            if (adapter2 != null) {
                search1(etSearch.getText().toString().trim());
            } else {
                search2(etSearch.getText().toString().trim());
            }
        }
    }

}
