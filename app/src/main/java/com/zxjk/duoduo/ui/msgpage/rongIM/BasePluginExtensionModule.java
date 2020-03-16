package com.zxjk.duoduo.ui.msgpage.rongIM;

import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.BusinessCardPlugin;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.FilePlugin;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.PhotoSelectorPlugin;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.RedPacketPlugin;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.SightPlugin;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.TakePhotoPlugin;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.TransferPlugin;

import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.RongExtension;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

public class BasePluginExtensionModule extends DefaultExtensionModule {
    private String targetId;
    private Conversation.ConversationType conversationType;

    @Override
    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {

        List<IPluginModule> list = super.getPluginModules(conversationType);

        if (list != null) {
            list.clear();
            list.add(new PhotoSelectorPlugin());
            list.add(new TakePhotoPlugin());
            list.add(new TransferPlugin());
            list.add(new SightPlugin());
            list.add(new RedPacketPlugin());
            list.add(new BusinessCardPlugin());
            list.add(new FilePlugin());
        }

        return list;
    }

    @Override
    public List<IEmoticonTab> getEmoticonTabs() {
        List<IEmoticonTab> tabs = super.getEmoticonTabs();

        CusEmoteTab cusEmoteTab = new CusEmoteTab(targetId, conversationType);
        CusEmoteTab2 cusEmoteTab2 = new CusEmoteTab2(targetId, conversationType);

        tabs.add(cusEmoteTab);
        tabs.add(cusEmoteTab2);
        return tabs;
    }

    @Override
    public void onAttachedToExtension(RongExtension extension) {
        super.onAttachedToExtension(extension);
        targetId = extension.getTargetId();
        conversationType = extension.getConversationType();
    }
}
