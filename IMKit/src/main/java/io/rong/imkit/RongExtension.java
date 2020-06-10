//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import io.rong.common.RLog;
import io.rong.imkit.InputBar.Style;
import io.rong.imkit.R.color;
import io.rong.imkit.R.dimen;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.styleable;
import io.rong.imkit.actions.IClickActions;
import io.rong.imkit.actions.IMoreClickAdapter;
import io.rong.imkit.actions.MoreClickAdapter;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.emoticon.EmoticonTabAdapter;
import io.rong.imkit.emoticon.IEmoticonClickListener;
import io.rong.imkit.emoticon.IEmoticonSettingClickListener;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.menu.ISubMenuItemClickListener;
import io.rong.imkit.menu.InputSubMenu;
import io.rong.imkit.phrases.CommonphrasesAdapter;
import io.rong.imkit.phrases.IPhrasesClickListener;
import io.rong.imkit.plugin.IPluginClickListener;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.IPluginRequestPermissionResultCallback;
import io.rong.imkit.plugin.ImagePlugin;
import io.rong.imkit.plugin.PluginAdapter;
import io.rong.imkit.utilities.ExtensionHistoryUtil;
import io.rong.imkit.utilities.ExtensionHistoryUtil.ExtensionBarState;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.utilities.RongUtils;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.common.DeviceUtils;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.CustomServiceMode;

public class RongExtension extends LinearLayout implements OnClickListener, OnTouchListener {
    private static final String TAG = "RongExtension";
    private ImageView mPSMenu;
    private View mPSDivider;
    private List<InputMenu> mInputMenuList;
    private LinearLayout mMainBar;
    private View mDivider;
    private ViewGroup mExtensionBar;
    private ViewGroup mSwitchLayout;
    private ViewGroup mContainerLayout;
    private ViewGroup mPluginLayout;
    private ViewGroup mMenuContainer;
    private View mEditTextLayout;
    private EditText mEditText;
    private Button mVoiceInputToggle;
    private PluginAdapter mPluginAdapter;
    private EmoticonTabAdapter mEmotionTabAdapter;
    private CommonphrasesAdapter mPhrasesAdapter;
    private IMoreClickAdapter moreClickAdapter;
    private FrameLayout mSendToggle;
    private ImageView mEmoticonToggle;
    private ImageView mPluginToggle;
    private ImageView mVoiceToggle;
    private TextView mPhraseseToggle;
    private boolean isRobotFirst = false;
    private IRongExtensionState mFireState;
    private IRongExtensionState mNormalState;
    private Fragment mFragment;
    private IExtensionClickListener mExtensionClickListener;
    private ConversationType mConversationType;
    private String mTargetId;
    private List<IExtensionModule> mExtensionModuleList;
    private List<String> mPhrasesList;
    private Style mStyle;
    private RongExtension.VisibilityState lastState;
    private boolean hasEverDrawn;
    private String mUserId;
    private boolean isBurnMode;
    public static final int TRIGGER_MODE_SYSTEM = 1;
    public static final int TRIGGER_MODE_TOUCH = 2;
    private int triggerMode;
    private boolean isKeyBoardActive;
    boolean collapsed;
    int originalTop;
    int originalBottom;

    public RongExtension(Context context) {
        super(context);
        this.lastState = RongExtension.VisibilityState.EXTENSION_VISIBLE;
        this.hasEverDrawn = false;
        this.triggerMode = 1;
        this.isKeyBoardActive = false;
        this.collapsed = true;
        this.originalTop = 0;
        this.originalBottom = 0;
        this.initView();
        this.initData();
    }

    public RongExtension(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.lastState = RongExtension.VisibilityState.EXTENSION_VISIBLE;
        this.hasEverDrawn = false;
        this.triggerMode = 1;
        this.isKeyBoardActive = false;
        this.collapsed = true;
        this.originalTop = 0;
        this.originalBottom = 0;
        TypedArray a = context.obtainStyledAttributes(attrs, styleable.RongExtension);
        int attr = a.getInt(styleable.RongExtension_RCStyle, 291);
        a.recycle();
        this.initView();
        this.initData();
        this.mStyle = Style.getStyle(attr);
        if (this.mStyle != null) {
            this.setInputBarStyle(this.mStyle);
        }

    }

    public void onDestroy() {
        RLog.d("RongExtension", "onDestroy");
        Iterator var1 = this.mExtensionModuleList.iterator();

        while (var1.hasNext()) {
            IExtensionModule module = (IExtensionModule) var1.next();
            module.onDetachedFromExtension();
        }

        this.mExtensionClickListener = null;
        this.hideInputKeyBoard();
    }

    public void collapseExtension() {
        this.hidePluginBoard();
        this.hideEmoticonBoard();
        this.hidePhrasesBoard();
        this.hideInputKeyBoard();
    }

    public void showSoftInput() {
        this.showInputKeyBoard();
        this.mContainerLayout.setSelected(true);
    }

    public boolean isExtensionExpanded() {
        return this.mPluginAdapter != null && this.mPluginAdapter.getVisibility() == 0 || this.mEmotionTabAdapter != null && this.mEmotionTabAdapter.getVisibility() == 0 || this.mPhrasesAdapter != null && this.mPhrasesAdapter.getVisibility() == 0;
    }

    public void setInputBarStyle(Style style) {
        switch (style) {
            case STYLE_SWITCH_CONTAINER_EXTENSION:
                this.setSCE();
                break;
            case STYLE_CONTAINER:
                this.setC();
                break;
            case STYLE_CONTAINER_EXTENSION:
                this.setCE();
                break;
            case STYLE_EXTENSION_CONTAINER:
                this.setEC();
                break;
            case STYLE_SWITCH_CONTAINER:
                this.setSC();
        }

    }

    public void setConversation(ConversationType conversationType, String targetId) {
        if (this.mConversationType == null && this.mTargetId == null) {
            this.mConversationType = conversationType;
            this.mTargetId = targetId;
            Iterator var3 = this.mExtensionModuleList.iterator();

            while (var3.hasNext()) {
                IExtensionModule module = (IExtensionModule) var3.next();
                module.onAttachedToExtension(this);
            }

            this.refreshQuickView();
            this.initPlugins();
            this.initEmoticons();
            this.initPanelStyle();
        }

        this.mConversationType = conversationType;
        this.mTargetId = targetId;
        SharedPreferences sp = this.getContext().getSharedPreferences("RongKitConfig", 0);
        boolean isBurn = sp.getBoolean("burn_" + this.getTargetId(), false);
        if (isBurn) {
            this.enterBurnMode();
        }

    }

    void refreshQuickView() {
        if (this.mConversationType == ConversationType.PRIVATE && this.mPhrasesList.size() > 0 && !this.isBurnMode) {
            this.mPhraseseToggle.setVisibility(0);
            int padding = RongUtils.dip2px(6.0F);
            this.mMainBar.setPadding(padding, padding, padding, padding);
            this.mDivider.setVisibility(8);
            this.initPhrases();
        } else {
            this.mPhraseseToggle.setVisibility(8);
            this.mDivider.setVisibility(0);
        }

    }

    private void initPhrases() {
        if (this.mPhrasesList != null && this.mPhrasesList.size() > 0) {
            this.mPhrasesAdapter.addPhrases(this.mPhrasesList);
        }

    }

    private void initPlugins() {
        Iterator var1 = this.mExtensionModuleList.iterator();

        while (var1.hasNext()) {
            IExtensionModule module = (IExtensionModule) var1.next();
            List<IPluginModule> pluginModules = module.getPluginModules(this.mConversationType);
            if (pluginModules != null && this.mPluginAdapter != null) {
                this.mPluginAdapter.addPlugins(pluginModules);
            }
        }

        IExtensionProxy proxy = RongExtensionManager.getExtensionProxy();
        if (proxy != null && this.mPluginAdapter != null) {
            proxy.onPreLoadPlugins(this.mConversationType, this.mTargetId, this.mPluginAdapter.getPluginModules());
        }

    }

    private void initEmoticons() {
        IExtensionProxy proxy = RongExtensionManager.getExtensionProxy();
        Iterator var2 = this.mExtensionModuleList.iterator();

        while (var2.hasNext()) {
            IExtensionModule module = (IExtensionModule) var2.next();
            IExtensionModule handledResult = null;
            List tabs;
            if (proxy != null) {
                handledResult = proxy.onPreLoadEmoticons(this.mConversationType, this.mTargetId, module);
                if (handledResult != null) {
                    tabs = module.getEmoticonTabs();
                    this.mEmotionTabAdapter.initTabs(tabs, module.getClass().getCanonicalName());
                }
            } else {
                tabs = module.getEmoticonTabs();
                this.mEmotionTabAdapter.initTabs(tabs, module.getClass().getCanonicalName());
            }
        }

    }

    public void setInputMenu(List<InputMenu> inputMenuList, boolean showFirst) {
        if (inputMenuList != null && inputMenuList.size() > 0) {
            this.mPSMenu.setVisibility(0);
            this.mPSDivider.setVisibility(0);
            this.mInputMenuList = inputMenuList;
            if (showFirst) {
                this.setExtensionBarVisibility(8);
                this.setMenuVisibility(0, inputMenuList);
            }

        } else {
            RLog.e("RongExtension", "setInputMenu no item");
        }
    }

    private void setExtensionBarVisibility(int visibility) {
        if (visibility == 8) {
            this.hideEmoticonBoard();
            this.hidePluginBoard();
            this.hidePhrasesBoard();
            this.hideInputKeyBoard();
        }

        this.mExtensionBar.setVisibility(visibility);
    }

    private void setMenuVisibility(int visibility, List<InputMenu> inputMenuList) {
        if (this.mMenuContainer == null) {
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            this.mMenuContainer = (ViewGroup) inflater.inflate(layout.rc_ext_menu_container, (ViewGroup) null);
            this.mMenuContainer.findViewById(id.rc_switch_to_keyboard).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    RongExtension.this.setExtensionBarVisibility(0);
                    RongExtension.this.mMenuContainer.setVisibility(8);
                }
            });

            for (int i = 0; i < inputMenuList.size(); ++i) {
                final InputMenu menu = (InputMenu) inputMenuList.get(i);
                LinearLayout rootMenu = (LinearLayout) inflater.inflate(layout.rc_ext_root_menu_item, (ViewGroup) null);
                LayoutParams lp = new LayoutParams(-1, -1, 1.0F);
                rootMenu.setLayoutParams(lp);
                TextView title = (TextView) rootMenu.findViewById(id.rc_menu_title);
                title.setText(menu.title);
                ImageView iv = (ImageView) rootMenu.findViewById(id.rc_menu_icon);
                if (menu.subMenuList != null && menu.subMenuList.size() > 0) {
                    iv.setVisibility(0);
                    iv.setImageResource(drawable.rc_menu_trangle);
                }

                final int finalI = i;
                final int finalI1 = i;
                rootMenu.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        List<String> subMenuList = menu.subMenuList;
                        if (subMenuList != null && subMenuList.size() > 0) {
                            InputSubMenu subMenu = new InputSubMenu(RongExtension.this.getContext(), subMenuList);
                            subMenu.setOnItemClickListener(new ISubMenuItemClickListener() {
                                public void onClick(int index) {
                                    if (RongExtension.this.mExtensionClickListener != null) {
                                        RongExtension.this.mExtensionClickListener.onMenuClick(finalI, index);
                                    }

                                }
                            });
                            subMenu.showAtLocation(v);
                        } else if (RongExtension.this.mExtensionClickListener != null) {
                            RongExtension.this.mExtensionClickListener.onMenuClick(finalI1, -1);
                        }

                    }
                });
                ViewGroup menuBar = (ViewGroup) this.mMenuContainer.findViewById(id.rc_menu_bar);
                menuBar.addView(rootMenu);
            }

            this.addView(this.mMenuContainer);
        }

        if (visibility == 8) {
            this.mMenuContainer.setVisibility(8);
        } else {
            this.mMenuContainer.setVisibility(0);
        }

    }

    public void setMenuVisibility(int visibility) {
        if (this.mMenuContainer != null) {
            this.mMenuContainer.setVisibility(visibility);
        }

    }

    public int getMenuVisibility() {
        return this.mMenuContainer != null ? this.mMenuContainer.getVisibility() : 8;
    }

    public void setExtensionBarMode(CustomServiceMode mode) {
        switch (mode) {
            case CUSTOM_SERVICE_MODE_NO_SERVICE:
                this.setC();
                break;
            case CUSTOM_SERVICE_MODE_HUMAN:
            case CUSTOM_SERVICE_MODE_HUMAN_FIRST:
                this.isRobotFirst = false;
                if (this.mStyle != null) {
                    this.setInputBarStyle(this.mStyle);
                }

                this.mVoiceToggle.setImageResource(drawable.rc_voice_toggle_selector);
                this.mVoiceToggle.setOnClickListener(this);
                break;
            case CUSTOM_SERVICE_MODE_ROBOT:
                this.setC();
                break;
            case CUSTOM_SERVICE_MODE_ROBOT_FIRST:
                this.mVoiceToggle.setImageResource(drawable.rc_cs_admin_selector);
                this.isRobotFirst = true;
                this.mVoiceToggle.setOnClickListener(this);
                this.setSC();
        }

    }

    public EditText getInputEditText() {
        return this.mEditText;
    }

    public void refreshEmoticonTabIcon(IEmoticonTab tab, Drawable icon) {
        if (icon != null && this.mEmotionTabAdapter != null && tab != null) {
            this.mEmotionTabAdapter.refreshTabIcon(tab, icon);
        }

    }

    public void addPlugin(IPluginModule pluginModule) {
        if (pluginModule != null) {
            this.mPluginAdapter.addPlugin(pluginModule);
        }

    }

    public void removePlugin(IPluginModule pluginModule) {
        if (pluginModule != null) {
            this.mPluginAdapter.removePlugin(pluginModule);
        }

    }

    public List<IPluginModule> getPluginModules() {
        return this.mPluginAdapter.getPluginModules();
    }

    public void addPluginPager(View v) {
        if (null != this.mPluginAdapter) {
            this.mPluginAdapter.addPager(v);
        }

    }

    public void removePluginPager(View v) {
        if (this.mPluginAdapter != null && v != null) {
            this.mPluginAdapter.removePager(v);
        }

    }

    public boolean addEmoticonTab(int index, IEmoticonTab tab, String tag) {
        if (this.mEmotionTabAdapter != null && tab != null && !TextUtils.isEmpty(tag)) {
            return this.mEmotionTabAdapter.addTab(index, tab, tag);
        } else {
            RLog.e("RongExtension", "addEmoticonTab Failure");
            return false;
        }
    }

    public void addEmoticonTab(IEmoticonTab tab, String tag) {
        if (this.mEmotionTabAdapter != null && tab != null && !TextUtils.isEmpty(tag)) {
            this.mEmotionTabAdapter.addTab(tab, tag);
        }

    }

    public List<IEmoticonTab> getEmoticonTabs(String tag) {
        return this.mEmotionTabAdapter != null && !TextUtils.isEmpty(tag) ? this.mEmotionTabAdapter.getTagTabs(tag) : null;
    }

    public int getEmoticonTabIndex(String tag) {
        return this.mEmotionTabAdapter != null && !TextUtils.isEmpty(tag) ? this.mEmotionTabAdapter.getTagTabIndex(tag) : -1;
    }

    public boolean removeEmoticonTab(IEmoticonTab tab, String tag) {
        boolean result = false;
        if (this.mEmotionTabAdapter != null && tab != null && !TextUtils.isEmpty(tag)) {
            result = this.mEmotionTabAdapter.removeTab(tab, tag);
        }

        return result;
    }

    public void setCurrentEmoticonTab(IEmoticonTab tab, String tag) {
        if (this.mEmotionTabAdapter != null && tab != null && !TextUtils.isEmpty(tag)) {
            this.mEmotionTabAdapter.setCurrentTab(tab, tag);
        }

    }

    public void setEmoticonTabBarEnable(boolean enable) {
        if (this.mEmotionTabAdapter != null) {
            this.mEmotionTabAdapter.setTabViewEnable(enable);
        }

    }

    public void setEmoticonTabBarAddEnable(boolean enable) {
        if (this.mEmotionTabAdapter != null) {
            this.mEmotionTabAdapter.setAddEnable(enable);
        }

    }

    public void setEmoticonTabBarAddClickListener(IEmoticonClickListener listener) {
        if (this.mEmotionTabAdapter != null) {
            this.mEmotionTabAdapter.setOnEmoticonClickListener(listener);
        }

    }

    public void setEmoticonTabBarSettingEnable(boolean enable) {
        if (this.mEmotionTabAdapter != null) {
            this.mEmotionTabAdapter.setSettingEnable(enable);
        }

    }

    public void setEmoticonTabBarSettingClickListener(IEmoticonSettingClickListener listener) {
        if (this.mEmotionTabAdapter != null) {
            this.mEmotionTabAdapter.setOnEmoticonSettingClickListener(listener);
        }

    }

    public void addEmoticonExtraTab(Context context, Drawable drawable, OnClickListener clickListener) {
        if (this.mEmotionTabAdapter != null) {
            this.mEmotionTabAdapter.addExtraTab(context, drawable, clickListener);
        }

    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public ConversationType getConversationType() {
        return this.mConversationType;
    }

    public String getTargetId() {
        return this.mTargetId;
    }

    public void setExtensionClickListener(IExtensionClickListener clickListener) {
        this.mExtensionClickListener = clickListener;
    }

    public void onActivityPluginResult(int requestCode, int resultCode, Intent data) {
        int position = (requestCode >> 8) - 1;
        int reqCode = requestCode & 255;
        IPluginModule pluginModule = this.mPluginAdapter.getPluginModule(position);
        if (pluginModule != null) {
            if (this.mExtensionClickListener != null && resultCode == -1) {
                if (pluginModule instanceof ImagePlugin) {
                    boolean sendOrigin = data.getBooleanExtra("sendOrigin", false);
                    String mediaList = data.getStringExtra("android.intent.extra.RETURN_RESULT");
                    Gson gson = new Gson();
                    Type entityType = (new TypeToken<LinkedHashMap<String, Integer>>() {
                    }).getType();
                    LinkedHashMap<String, Integer> mLinkedHashMap = (LinkedHashMap) gson.fromJson(mediaList, entityType);
                    this.mExtensionClickListener.onImageResult(mLinkedHashMap, sendOrigin);
                }
            }

            pluginModule.onActivityResult(reqCode, resultCode, data);
        }

    }

    public boolean onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int position = (requestCode >> 8) - 1;
        int reqCode = requestCode & 255;
        IPluginModule pluginModule = this.mPluginAdapter.getPluginModule(position);
        return pluginModule instanceof IPluginRequestPermissionResultCallback ? ((IPluginRequestPermissionResultCallback) pluginModule).onRequestPermissionResult(this.mFragment, this, reqCode, permissions, grantResults) : false;
    }

    public void startActivityForPluginResult(Intent intent, int requestCode, IPluginModule pluginModule) {
        if ((requestCode & -256) != 0) {
            throw new IllegalArgumentException("requestCode must less than 256.");
        } else {
            int position = this.mPluginAdapter.getPluginPosition(pluginModule);
            this.mFragment.startActivityForResult(intent, (position + 1 << 8) + (requestCode & 255));
        }
    }

    public void requestPermissionForPluginResult(String[] permissions, int requestCode, IPluginModule pluginModule) {
        if ((requestCode & -256) != 0) {
            throw new IllegalArgumentException("requestCode must less than 256");
        } else {
            int position = this.mPluginAdapter.getPluginPosition(pluginModule);
            int req = (position + 1 << 8) + (requestCode & 255);
            PermissionCheckUtil.requestPermissions(this.mFragment, permissions, req);
        }
    }

    private void initData() {
        this.mPhrasesList = RongExtensionManager.getInstance().getPhrasesList();
        this.mPhrasesAdapter = new CommonphrasesAdapter();
        this.mPhrasesAdapter.setOnPhrasesClickListener(new IPhrasesClickListener() {
            public void onClick(String phrases, int position) {
                if (RongExtension.this.mExtensionClickListener != null) {
                    RongExtension.this.mExtensionClickListener.onPhrasesClicked(phrases, position);
                }

            }
        });
        this.mExtensionModuleList = RongExtensionManager.getInstance().getExtensionModules();
        this.mPluginAdapter = new PluginAdapter();
        this.mPluginAdapter.setOnPluginClickListener(new IPluginClickListener() {
            public void onClick(IPluginModule pluginModule, int position) {
                if (RongExtension.this.mExtensionClickListener != null) {
                    RongExtension.this.mExtensionClickListener.onPluginClicked(pluginModule, position);
                }

                pluginModule.onClick(RongExtension.this.mFragment, RongExtension.this);
            }
        });
        this.mEmotionTabAdapter = new EmoticonTabAdapter();
        this.moreClickAdapter = new MoreClickAdapter();
        this.mUserId = RongIMClient.getInstance().getCurrentUserId();

        try {
            boolean enable = this.getResources().getBoolean(this.getResources().getIdentifier("rc_extension_history", "bool", this.getContext().getPackageName()));
            ExtensionHistoryUtil.setEnableHistory(enable);
            ExtensionHistoryUtil.addExceptConversationType(ConversationType.CUSTOMER_SERVICE);
        } catch (NotFoundException var2) {
            RLog.i("RongExtension", "rc_extension_history not configure in rc_configuration.xml");
        }

    }

    private void initView() {
        this.setOrientation(1);
        this.setBackgroundColor(this.getContext().getResources().getColor(color.rc_extension_normal));
        this.mExtensionBar = (ViewGroup) LayoutInflater.from(this.getContext()).inflate(layout.rc_ext_extension_bar, (ViewGroup) null);
        this.mMainBar = (LinearLayout) this.mExtensionBar.findViewById(id.ext_main_bar);
        this.mSwitchLayout = (ViewGroup) this.mExtensionBar.findViewById(id.rc_switch_layout);
        this.mContainerLayout = (ViewGroup) this.mExtensionBar.findViewById(id.rc_container_layout);
        this.mPluginLayout = (ViewGroup) this.mExtensionBar.findViewById(id.rc_plugin_layout);
        this.mEditTextLayout = LayoutInflater.from(this.getContext()).inflate(layout.rc_ext_input_edit_text, (ViewGroup) null);
        this.mEditTextLayout.setVisibility(0);
        this.mContainerLayout.addView(this.mEditTextLayout);
        LayoutInflater.from(this.getContext()).inflate(layout.rc_ext_voice_input, this.mContainerLayout, true);
        this.mVoiceInputToggle = (Button) this.mContainerLayout.findViewById(id.rc_audio_input_toggle);
        this.mVoiceInputToggle.setVisibility(8);
        this.mEditText = (EditText) this.mExtensionBar.findViewById(id.rc_edit_text);
        this.mSendToggle = (FrameLayout) this.mExtensionBar.findViewById(id.rc_send_toggle);
        this.mPluginToggle = (ImageView) this.mExtensionBar.findViewById(id.rc_plugin_toggle);
        this.mPhraseseToggle = (TextView) this.mExtensionBar.findViewById(id.ext_common_phrases);
        this.mDivider = this.mExtensionBar.findViewById(id.rc_divider);
        this.mEditText.setOnTouchListener(this);
        this.mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !TextUtils.isEmpty(RongExtension.this.mEditText.getText()) && RongExtension.this.mEditTextLayout.getVisibility() == 0) {
                    RongExtension.this.mSendToggle.setVisibility(0);
                    RongExtension.this.mPluginLayout.setVisibility(8);
                }

            }
        });
        this.mEditText.addTextChangedListener(new TextWatcher() {
            private int start;
            private int count;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (RongExtension.this.mExtensionClickListener != null) {
                    RongExtension.this.mExtensionClickListener.beforeTextChanged(s, start, count, after);
                }

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.start = start;
                this.count = count;
                if (RongExtension.this.mExtensionClickListener != null) {
                    RongExtension.this.mExtensionClickListener.onTextChanged(s, start, before, count);
                }

                if (RongExtension.this.mVoiceInputToggle.getVisibility() == 0) {
                    RongExtension.this.mSendToggle.setVisibility(8);
                    RongExtension.this.mPluginLayout.setVisibility(0);
                } else if (s != null && s.length() != 0) {
                    RongExtension.this.mSendToggle.setVisibility(0);
                    RongExtension.this.mPluginLayout.setVisibility(8);
                } else {
                    RongExtension.this.mSendToggle.setVisibility(8);
                    RongExtension.this.mPluginLayout.setVisibility(0);
                }

            }

            public void afterTextChanged(Editable s) {
                if (AndroidEmoji.isEmoji(s.subSequence(this.start, this.start + this.count).toString())) {
                    RongExtension.this.mEditText.removeTextChangedListener(this);
                    String resultStr = AndroidEmoji.replaceEmojiWithText(s.toString());
                    RongExtension.this.mEditText.setText(AndroidEmoji.ensure(resultStr), BufferType.SPANNABLE);
                    RongExtension.this.mEditText.setSelection(RongExtension.this.mEditText.getText().length());
                    RongExtension.this.mEditText.addTextChangedListener(this);
                }

                if (RongExtension.this.mExtensionClickListener != null) {
                    RongExtension.this.mExtensionClickListener.afterTextChanged(s);
                }

            }
        });
        this.mEditText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return RongExtension.this.mExtensionClickListener != null && RongExtension.this.mExtensionClickListener.onKey(RongExtension.this.mEditText, keyCode, event);
            }
        });
        this.mEditText.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (RongExtension.this.mEditText.getText().length() > 0 && RongExtension.this.mEditText.isFocused() && !RongExtension.this.hasEverDrawn) {
                    Rect rect = new Rect();
                    RongExtension.this.mEditText.getWindowVisibleDisplayFrame(rect);
                    int keypadHeight = RongExtension.this.mEditText.getRootView().getHeight() - rect.bottom;
                    int inputbarHeight = (int) RongExtension.this.mEditText.getContext().getResources().getDimension(dimen.rc_extension_bar_min_height);
                    if (keypadHeight > inputbarHeight * 2) {
                        RongExtension.this.hasEverDrawn = true;
                    }

                    if (RongExtension.this.mExtensionClickListener != null) {
                        RongExtension.this.mExtensionClickListener.onEditTextClick(RongExtension.this.mEditText);
                    }

                    RongExtension.this.showInputKeyBoard();
                    RongExtension.this.mContainerLayout.setSelected(true);
                    RongExtension.this.hidePluginBoard();
                    RongExtension.this.hideEmoticonBoard();
                    RongExtension.this.hidePhrasesBoard();
                }

            }
        });
        this.mVoiceToggle = (ImageView) this.mExtensionBar.findViewById(id.rc_voice_toggle);
        this.mVoiceToggle.setOnClickListener(this);
        this.mVoiceInputToggle.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (RongExtension.this.mExtensionClickListener != null) {
                    RongExtension.this.mExtensionClickListener.onVoiceInputToggleTouch(v, event);
                }

                return false;
            }
        });
        this.mSendToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String text = RongExtension.this.mEditText.getText().toString();
                RongExtension.this.mEditText.setText("");
                if (RongExtension.this.mExtensionClickListener != null) {
                    RongExtension.this.mExtensionClickListener.onSendToggleClick(v, text);
                }

            }
        });
        this.mPluginToggle.setOnClickListener(this);
        this.mPhraseseToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                RongExtension.this.setPhrasesBoard();
            }
        });
        this.mEmoticonToggle = (ImageView) this.mExtensionBar.findViewById(id.rc_emoticon_toggle);
        this.mEmoticonToggle.setOnClickListener(this);
        this.mPSMenu = (ImageView) this.mExtensionBar.findViewById(id.rc_switch_to_menu);
        this.mPSMenu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                RongExtension.this.setExtensionBarVisibility(8);
                RongExtension.this.setMenuVisibility(0, RongExtension.this.mInputMenuList);
            }
        });
        this.mPSDivider = this.mExtensionBar.findViewById(id.rc_switch_divider);
        this.addView(this.mExtensionBar);
    }

    public void showMoreActionLayout(List<IClickActions> actions) {
        this.lastState = this.getMenuVisibility() == 0 ? RongExtension.VisibilityState.MENUCONTAINER_VISIBLE : RongExtension.VisibilityState.EXTENSION_VISIBLE;
        this.setExtensionBarVisibility(8);
        this.setMenuVisibility(8);
        this.moreClickAdapter.bindView(this, this.mFragment, actions);
    }

    public void hideMoreActionLayout() {
        if (!this.getConversationType().equals(ConversationType.APP_PUBLIC_SERVICE) && !this.getConversationType().equals(ConversationType.PUBLIC_SERVICE)) {
            this.setExtensionBarVisibility(0);
        } else if (this.mInputMenuList != null) {
            if (this.lastState == RongExtension.VisibilityState.MENUCONTAINER_VISIBLE) {
                this.setExtensionBarVisibility(8);
                this.setMenuVisibility(0);
            } else {
                this.setExtensionBarVisibility(0);
                this.mPSMenu.setVisibility(0);
                this.mPSDivider.setVisibility(0);
            }
        } else {
            this.setExtensionBarVisibility(0);
        }

        this.moreClickAdapter.hideMoreActionLayout();
    }

    public void setMoreActionEnable(boolean enable) {
        this.moreClickAdapter.setMoreActionEnable(enable);
    }

    public boolean isMoreActionShown() {
        return this.moreClickAdapter.isMoreActionShown();
    }

    void hideVoiceInputToggle() {
        this.mVoiceToggle.setImageResource(this.isBurnMode ? drawable.rc_destruct_voice_toggle_selector : drawable.rc_voice_toggle_selector);
        this.mVoiceInputToggle.setVisibility(8);
        String saveId = DeviceUtils.ShortMD5(new String[]{this.mUserId, this.mTargetId, this.mConversationType.getName()});
        ExtensionHistoryUtil.setExtensionBarState(this.getContext(), saveId, this.mConversationType, ExtensionBarState.NORMAL);
    }

    void showVoiceInputToggle() {
        this.mVoiceInputToggle.setVisibility(0);
        this.mVoiceInputToggle.setTextColor(this.isBurnMode ? this.getContext().getResources().getColor(color.rc_destruct_voice_color) : this.getContext().getResources().getColor(color.rc_text_voice));
        this.mVoiceToggle.setImageResource(this.isBurnMode ? drawable.rc_destruct_keyboard_selector : drawable.rc_keyboard_selector);
        String saveId = DeviceUtils.ShortMD5(new String[]{this.mUserId, this.mTargetId, this.mConversationType.getName()});
        ExtensionHistoryUtil.setExtensionBarState(this.getContext(), saveId, this.mConversationType, ExtensionBarState.VOICE);
    }

    void hideEmoticonBoard() {
        this.getRongExtensionState().hideEmoticonBoard(this.mEmoticonToggle, this.mEmotionTabAdapter);
    }

    void setEmoticonBoard() {
        if (this.mEmotionTabAdapter.isInitialized()) {
            if (this.mEmotionTabAdapter.getVisibility() == 0) {
                this.mEmotionTabAdapter.setVisibility(8);
                this.mEmoticonToggle.setSelected(false);
                this.mEmoticonToggle.setImageResource(drawable.rc_emotion_toggle_selector);
                this.showInputKeyBoard();
            } else {
                this.mEmotionTabAdapter.setVisibility(0);
                this.mContainerLayout.setSelected(true);
                this.mEmoticonToggle.setSelected(true);
                this.mEmoticonToggle.setImageResource(drawable.rc_keyboard_selector);
            }
        } else {
            this.mEmotionTabAdapter.bindView(this);
            this.mEmotionTabAdapter.setVisibility(0);
            this.mContainerLayout.setSelected(true);
            this.mEmoticonToggle.setSelected(true);
            this.mEmoticonToggle.setImageResource(drawable.rc_keyboard_selector);
        }

        if (!TextUtils.isEmpty(this.mEditText.getText())) {
            this.mSendToggle.setVisibility(0);
            this.mPluginLayout.setVisibility(8);
        }

    }

    void hidePluginBoard() {
        if (this.mPluginAdapter != null) {
            this.mPluginAdapter.setVisibility(8);
            View pager = this.mPluginAdapter.getPager();
            this.mPluginAdapter.removePager(pager);
        }

    }

    void setPluginBoard() {
        if (this.mPluginAdapter.isInitialized()) {
            if (this.mPluginAdapter.getVisibility() == 0) {
                View pager = this.mPluginAdapter.getPager();
                if (pager != null) {
                    pager.setVisibility(pager.getVisibility() == 8 ? 0 : 8);
                } else {
                    this.mPluginAdapter.setVisibility(8);
                    this.mContainerLayout.setSelected(true);
                    this.showInputKeyBoard();
                }
            } else {
                this.mEmoticonToggle.setImageResource(drawable.rc_emotion_toggle_selector);
                if (this.isKeyBoardActive()) {
                    this.getHandler().postDelayed(new Runnable() {
                        public void run() {
                            RongExtension.this.mPluginAdapter.setVisibility(0);
                        }
                    }, 200L);
                } else {
                    this.mPluginAdapter.setVisibility(0);
                }

                this.hideInputKeyBoard();
                this.hideEmoticonBoard();
                this.hidePhrasesBoard();
                this.mContainerLayout.setSelected(false);
            }
        } else {
            this.mEmoticonToggle.setImageResource(drawable.rc_emotion_toggle_selector);
            this.mPluginAdapter.bindView(this);
            this.mPluginAdapter.setVisibility(0);
            this.mContainerLayout.setSelected(false);
            this.hideInputKeyBoard();
            this.hideEmoticonBoard();
            this.hidePhrasesBoard();
        }

        this.hideVoiceInputToggle();
        this.mEditTextLayout.setVisibility(0);
    }

    void hidePhrasesBoard() {
        if (this.mPhrasesAdapter != null) {
            this.mPhrasesAdapter.setVisibility(8);
        }

    }

    private void setPhrasesBoard() {
        if (this.mPhrasesAdapter.isInitialized()) {
            if (this.mPhrasesAdapter.getVisibility() == 0) {
                this.mPhrasesAdapter.setVisibility(8);
                this.mContainerLayout.setSelected(true);
            } else {
                this.mEmoticonToggle.setImageResource(drawable.rc_emotion_toggle_selector);
                if (this.isKeyBoardActive()) {
                    this.getHandler().postDelayed(new Runnable() {
                        public void run() {
                            RongExtension.this.mPhrasesAdapter.setVisibility(0);
                        }
                    }, 200L);
                } else {
                    this.mPhrasesAdapter.setVisibility(0);
                }

                this.hideInputKeyBoard();
                this.hideEmoticonBoard();
                this.hidePluginBoard();
                this.mContainerLayout.setSelected(false);
            }
        } else {
            this.mEmoticonToggle.setImageResource(drawable.rc_emotion_toggle_selector);
            this.mPhrasesAdapter.bindView(this);
            this.mPhrasesAdapter.setVisibility(0);
            this.mContainerLayout.setSelected(false);
            this.hideInputKeyBoard();
            this.hideEmoticonBoard();
            this.hidePluginBoard();
        }

        this.hideVoiceInputToggle();
        this.mEditTextLayout.setVisibility(0);
    }

    boolean isKeyBoardActive() {
        return this.isKeyBoardActive;
    }

    void setKeyBoardActive(boolean pIsKeyBoardActive) {
        this.isKeyBoardActive = pIsKeyBoardActive;
    }

    void hideInputKeyBoard() {
        InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService("input_method");
        imm.hideSoftInputFromWindow(this.mEditText.getWindowToken(), 0);
        this.mEditText.clearFocus();
        this.isKeyBoardActive = false;
    }

    void showInputKeyBoard() {
        this.mEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService("input_method");
        imm.showSoftInput(this.mEditText, 0);
        this.mEmoticonToggle.setSelected(false);
        this.isKeyBoardActive = true;
    }

    public int getTriggerMode() {
        return this.triggerMode;
    }

    private void setSCE() {
        this.mSwitchLayout.setVisibility(0);
        if (this.mSendToggle.getVisibility() == 0) {
            this.mPluginLayout.setVisibility(8);
        } else {
            this.mPluginLayout.setVisibility(0);
        }

        this.mMainBar.removeAllViews();
        this.mMainBar.addView(this.mSwitchLayout);
        this.mMainBar.addView(this.mContainerLayout);
        this.mMainBar.addView(this.mPluginLayout);
    }

    private void setSC() {
        this.mSwitchLayout.setVisibility(0);
        this.mMainBar.removeAllViews();
        this.mMainBar.addView(this.mSwitchLayout);
        this.mMainBar.addView(this.mContainerLayout);
    }

    private void setCE() {
        if (this.mSendToggle.getVisibility() == 0) {
            this.mPluginLayout.setVisibility(8);
        } else {
            this.mPluginLayout.setVisibility(0);
        }

        this.mMainBar.removeAllViews();
        this.mMainBar.addView(this.mContainerLayout);
        this.mMainBar.addView(this.mPluginLayout);
    }

    private void setEC() {
        if (this.mSendToggle.getVisibility() == 0) {
            this.mPluginLayout.setVisibility(8);
        } else {
            this.mPluginLayout.setVisibility(0);
        }

        this.mMainBar.removeAllViews();
        this.mMainBar.addView(this.mPluginLayout);
        this.mMainBar.addView(this.mContainerLayout);
    }

    private void setC() {
        this.mMainBar.removeAllViews();
        this.mMainBar.addView(this.mContainerLayout);
    }

    private void initPanelStyle() {
        String saveId = DeviceUtils.ShortMD5(new String[]{this.mUserId, this.mTargetId, this.mConversationType.getName()});
        ExtensionBarState state = ExtensionHistoryUtil.getExtensionBarState(this.getContext(), saveId, this.mConversationType);
        if (state == ExtensionBarState.NORMAL) {
            this.mVoiceToggle.setImageResource(drawable.rc_voice_toggle_selector);
            this.mEditTextLayout.setVisibility(0);
            this.mVoiceInputToggle.setVisibility(8);
        } else {
            this.mVoiceToggle.setImageResource(drawable.rc_keyboard_selector);
            this.mEditTextLayout.setVisibility(8);
            this.mVoiceInputToggle.setVisibility(0);
            this.mSendToggle.setVisibility(8);
            this.mPluginLayout.setVisibility(0);
        }

    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.originalTop != 0) {
            if (this.originalTop > t) {
                if (this.originalBottom > b && this.mExtensionClickListener != null && this.collapsed) {
                    this.collapsed = false;
                    this.mExtensionClickListener.onExtensionExpanded(this.originalBottom - t);
                } else if (this.collapsed && this.mExtensionClickListener != null) {
                    this.collapsed = false;
                    this.mExtensionClickListener.onExtensionExpanded(b - t);
                }
            } else if (!this.collapsed && this.mExtensionClickListener != null) {
                this.collapsed = true;
                this.mExtensionClickListener.onExtensionCollapsed();
            }
        }

        if (this.originalTop == 0) {
            this.originalTop = t;
            this.originalBottom = b;
        }

    }

    public void resetEditTextLayoutDrawnStatus() {
        this.hasEverDrawn = false;
    }

    public void showRequestPermissionFailedAlter(String content) {
        Context context = this.mFragment.getActivity();
        PermissionCheckUtil.showRequestPermissionFailedAlter(context, content);
    }

    public void enterBurnMode() {
        this.isBurnMode = true;
        SharedPreferences sp = this.getContext().getSharedPreferences("RongKitConfig", 0);
        sp.edit().putBoolean("burn_" + this.getTargetId(), true).apply();
        this.hidePluginBoard();
        this.refreshBurnMode();
    }

    public void exitBurnMode() {
        SharedPreferences sp = this.getContext().getSharedPreferences("RongKitConfig", 0);
        sp.edit().remove("burn_" + this.getTargetId()).apply();
        this.isBurnMode = false;
        this.refreshBurnMode();
    }

    private void refreshBurnMode() {
        this.getRongExtensionState().changeView(this);
    }

    public IRongExtensionState getRongExtensionState() {
        if (this.isBurnMode) {
            if (this.mFireState == null) {
                this.mFireState = new DestructState();
            }

            return this.mFireState;
        } else {
            if (this.mNormalState == null) {
                this.mNormalState = new NormalState();
            }

            return this.mNormalState;
        }
    }

    ImageView getVoiceToggle() {
        return this.mVoiceToggle;
    }

    ImageView getPluginToggle() {
        return this.mPluginToggle;
    }

    ImageView getEmoticonToggle() {
        return this.mEmoticonToggle;
    }

    boolean isRobotFirst() {
        return this.isRobotFirst;
    }

    ViewGroup getContainerLayout() {
        return this.mContainerLayout;
    }

    IExtensionClickListener getExtensionClickListener() {
        return this.mExtensionClickListener;
    }

    View getEditTextLayout() {
        return this.mEditTextLayout;
    }

    FrameLayout getSendToggle() {
        return this.mSendToggle;
    }

    ViewGroup getPluginLayout() {
        return this.mPluginLayout;
    }

    EditText getEditText() {
        return this.mEditText;
    }

    Button getVoiceInputToggle() {
        return this.mVoiceInputToggle;
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rc_plugin_toggle || id == R.id.rc_emoticon_toggle || id == R.id.rc_voice_toggle) {
            this.getRongExtensionState().onClick(this, v);
        }

    }

    public boolean onTouch(View v, MotionEvent event) {
        if (this.triggerMode != 2) {
            this.triggerMode = 2;
        }

        return this.getRongExtensionState().onEditTextTouch(this, v, event);
    }

    public boolean isFireStatus() {
        return this.isBurnMode;
    }

    void clickVoice(boolean pRobotFirst, RongExtension pExtension, View pV, @DrawableRes int emotionDrawable) {
        if (pExtension.getExtensionClickListener() != null) {
            pExtension.getExtensionClickListener().onSwitchToggleClick(pV, pExtension.getContainerLayout());
        }

        if (!pRobotFirst) {
            if (pExtension.getVoiceInputToggle().getVisibility() == 8) {
                pExtension.getEditTextLayout().setVisibility(8);
                pExtension.getSendToggle().setVisibility(8);
                pExtension.getPluginLayout().setVisibility(0);
                pExtension.hideInputKeyBoard();
                pExtension.showVoiceInputToggle();
                pExtension.getContainerLayout().setClickable(true);
                pExtension.getContainerLayout().setSelected(false);
            } else {
                pExtension.getEditTextLayout().setVisibility(0);
                pExtension.hideVoiceInputToggle();
                pExtension.getEmoticonToggle().setImageResource(emotionDrawable);
                if (pExtension.getEditText().getText().length() > 0) {
                    pExtension.getSendToggle().setVisibility(0);
                    pExtension.getPluginLayout().setVisibility(8);
                } else {
                    pExtension.getSendToggle().setVisibility(8);
                    pExtension.getPluginLayout().setVisibility(0);
                }

                pExtension.showInputKeyBoard();
                pExtension.getContainerLayout().setSelected(true);
            }

            pExtension.hidePluginBoard();
            pExtension.hideEmoticonBoard();
            pExtension.hidePhrasesBoard();
        }
    }

    static enum VisibilityState {
        EXTENSION_VISIBLE,
        MENUCONTAINER_VISIBLE;

        private VisibilityState() {
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @interface TRIGGERMODE {
    }
}