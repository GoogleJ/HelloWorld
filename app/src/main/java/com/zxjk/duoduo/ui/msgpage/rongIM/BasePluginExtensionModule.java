package com.zxjk.duoduo.ui.msgpage.rongIM;

import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.AudioVideoPlugin;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.BusinessCardPlugin;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.FilePlugin;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.MyCombineLocationPlugin;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.PhotoSelectorPlugin;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.RedPacketPlugin;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.SightPlugin;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.TakePhotoPlugin;
import com.zxjk.duoduo.ui.msgpage.rongIM.plugin.TransferPlugin;
import java.util.List;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

public class BasePluginExtensionModule extends DefaultExtensionModule {


    @Override
    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {

        List<IPluginModule> list = super.getPluginModules(conversationType);

        if (list != null) {
            list.clear();
            list.add(new PhotoSelectorPlugin());
            list.add(new TakePhotoPlugin());
            list.add(new TransferPlugin());
            list.add(new SightPlugin());
            list.add(new AudioVideoPlugin());
            list.add(new RedPacketPlugin());
            list.add(new BusinessCardPlugin());
            list.add(new MyCombineLocationPlugin());
            list.add(new FilePlugin());
        }

        return list;
    }

    @Override
    public List<IEmoticonTab> getEmoticonTabs() {
        return null;
    }
}
