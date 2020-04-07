package com.zxjk.moneyspace.bean.response;

public class GetGroupChatInfoByGroupIdResponse {

    /**
     * groupType : 1
     * headPortrait : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/2541575268416621,https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/2631575259481792,https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/1381575270599427,https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/7A27CE48-950A-43A6-8D2F-F901A9856201.jpg,https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/4qnihrkux4nk4cqw3wq5.jpg
     * groupNikeName : 测试新人进群红包
     */

    private int groupType;
    private String headPortrait;
    private String groupNikeName;

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getGroupNikeName() {
        return groupNikeName;
    }

    public void setGroupNikeName(String groupNikeName) {
        this.groupNikeName = groupNikeName;
    }
}
