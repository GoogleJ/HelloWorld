package io.rong.imkit;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.view.KeyEvent;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.rong.common.RLog;
import io.rong.imkit.emoticon.EmojiTab;
import io.rong.imkit.emoticon.IEmojiItemClickListener;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.manager.InternalModuleManager;
import io.rong.imkit.plugin.DestructPlugin;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.ImagePlugin;
import io.rong.imkit.widget.provider.FilePlugin;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

public class DefaultExtensionModule
        implements IExtensionModule {
    private static final String TAG = DefaultExtensionModule.class.getSimpleName();
    private EditText mEditText;
    private Stack<EditText> stack;
    private String[] types = null;

    public DefaultExtensionModule(Context context) {
        Resources resources = context.getResources();
        try {
            this.types = resources.getStringArray(resources.getIdentifier("rc_realtime_support_conversation_types", "array", context.getPackageName()));
        } catch (NotFoundException e) {
            RLog.i(TAG, "not config rc_realtime_support_conversation_types in rc_config.xml");
        }
    }

    public DefaultExtensionModule() {
    }

    public void onInit(String appKey) {
        this.stack = new Stack();
    }

    public void onConnect(String token) {
    }

    public void onAttachedToExtension(RongExtension extension) {
        this.mEditText = extension.getInputEditText();
        RLog.i(TAG, "attach " + this.stack.size());
        this.stack.push(this.mEditText);
    }

    public void onDetachedFromExtension() {
        RLog.i(TAG, "detach " + this.stack.size());
        if (this.stack.size() > 0) {
            this.stack.pop();
            this.mEditText = ((this.stack.size() > 0) ? (EditText) this.stack.peek() : null);
        }
    }

    public void onReceivedMessage(Message message) {
    }

    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {
        List pluginModuleList = new ArrayList();
        pluginModuleList.add(new ImagePlugin());
        if ((conversationType.equals(Conversation.ConversationType.GROUP)) ||
                (conversationType
                        .equals(Conversation.ConversationType.DISCUSSION)) ||
                (conversationType
                        .equals(Conversation.ConversationType.PRIVATE))) {
            pluginModuleList.addAll(InternalModuleManager.getInstance().getInternalPlugins(conversationType));
        }

        pluginModuleList.add(new FilePlugin());

        if ((Conversation.ConversationType.PRIVATE.equals(conversationType)) && (RongContext.getInstance().getResources().getBoolean(R.bool.rc_open_destruct_plugin)))
            pluginModuleList.add(new DestructPlugin());

        return pluginModuleList;
    }

    public List<IEmoticonTab> getEmoticonTabs() {
        EmojiTab emojiTab = new EmojiTab();
        emojiTab.setOnItemClickListener(new IEmojiItemClickListener() {
            public void onEmojiClick(String emoji) {
                EditText editText = DefaultExtensionModule.this.mEditText;
                if (editText != null) {
                    int start = editText.getSelectionStart();
                    editText.getText().insert(start, emoji);
                }
            }

            public void onDeleteClick() {
                EditText editText = DefaultExtensionModule.this.mEditText;
                if (editText != null)
                    editText.dispatchKeyEvent(new KeyEvent(0, 67));

            }

        });
        List list = new ArrayList();
        list.add(emojiTab);
        return list;
    }

    public void onDisconnect() {
    }
}