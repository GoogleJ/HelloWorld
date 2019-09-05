package com.zxjk.duoduo.bean.response;

import java.io.Serializable;
import java.util.List;

public class GroupResponse implements Serializable {


    /**
     * groupInfo : {"id":"71","groupType":"0","groupNikeName":"Zhaochen_4的讨论组","groupHeadPortrait":"","groupSign":"","groupNotice":"","groupOwnerId":"17","updateTime":"","createTime":"1553581323053","isDelete":"0","isInviteConfirm":"0","headPortrait":""}
     * customers : [{"id":"17","duoduoId":"","nick":"Zhaochen_4","realname":"","mobile":"","password":"","address":"","email":"","headPortrait":"https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/A324124A-A577-4980-AF66-684D1CE44376.jpg","sex":"","signature":"","walletAddress":"","idCard":"","isShowRealname":"","updateTime":"","createTime":"","isDelete":"","token":"","remark":"","rongToken":"","payPwd":"","isFirstLogin":"","renegeNumber":"","isConfine":"","status":"","isAuthentication":"","onlineService":""},{"id":"14","duoduoId":"","nick":"14号用户","realname":"","mobile":"","password":"","address":"","email":"","headPortrait":"https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/FC888443-8DAF-4501-B7A7-5EAD7139657A.jpg","sex":"","signature":"","walletAddress":"","idCard":"","isShowRealname":"","updateTime":"","createTime":"","isDelete":"","token":"","remark":"","rongToken":"","payPwd":"","isFirstLogin":"","renegeNumber":"","isConfine":"","status":"","isAuthentication":"","onlineService":""},{"id":"4","duoduoId":"","nick":"丁浩","realname":"","mobile":"","password":"","address":"","email":"","headPortrait":"https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/E1795CE5-2A56-4C53-A9EE-E916FBBFC383.jpg","sex":"","signature":"","walletAddress":"","idCard":"","isShowRealname":"","updateTime":"","createTime":"","isDelete":"","token":"","remark":"","rongToken":"","payPwd":"","isFirstLogin":"","renegeNumber":"","isConfine":"","status":"","isAuthentication":"","onlineService":""},{"id":"26","duoduoId":"","nick":"王倩","realname":"","mobile":"","password":"","address":"","email":"","headPortrait":"https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/C5B24F00-AD58-4DF4-83FD-DC6217CC24BA.jpg","sex":"","signature":"","walletAddress":"","idCard":"","isShowRealname":"","updateTime":"","createTime":"","isDelete":"","token":"","remark":"","rongToken":"","payPwd":"","isFirstLogin":"","renegeNumber":"","isConfine":"","status":"","isAuthentication":"","onlineService":""}]
     */

    private GroupInfoBean groupInfo;
    private String maxNumber;
    private List<CustomersBean> customers;
    private ChatInfoBean chatInfo;
    private String isAdmin;
    private PermissionBean groupPermission;
    private GroupPayBean groupPay;
    private RedPacketInfoBean redPacketInfo;

    public GroupPayBean getGroupPay() {
        return groupPay;
    }

    public void setGroupPay(GroupPayBean groupPay) {
        this.groupPay = groupPay;
    }

    public RedPacketInfoBean getRedPacketInfo() {
        return redPacketInfo;
    }

    public void setRedPacketInfo(RedPacketInfoBean redPacketInfo) {
        this.redPacketInfo = redPacketInfo;
    }

    public GroupPayBean getGroupPayBean() {
        return groupPay;
    }

    public void setGroupPayBean(GroupPayBean groupPayBean) {
        this.groupPay = groupPayBean;
    }

    public static class RedPacketInfoBean implements Serializable {

        /**
         * redNewPersonStatus : 1
         * isGetNewPersonRed : 2
         */

        private String redNewPersonStatus;
        private String isGetNewPersonRed;

        public String getRedNewPersonStatus() {
            return redNewPersonStatus;
        }

        public void setRedNewPersonStatus(String redNewPersonStatus) {
            this.redNewPersonStatus = redNewPersonStatus;
        }

        public String getIsGetNewPersonRed() {
            return isGetNewPersonRed;
        }

        public void setIsGetNewPersonRed(String isGetNewPersonRed) {
            this.isGetNewPersonRed = isGetNewPersonRed;
        }
    }


    public static class GroupPayBean implements Serializable {
        private String payFee;
        private String isOpen;

        public String getPayFee() {
            return payFee;
        }

        public void setPayFee(String payFee) {
            this.payFee = payFee;
        }

        public String getIsOpen() {
            return isOpen;
        }

        public void setIsOpen(String isOpen) {
            this.isOpen = isOpen;
        }
    }

    public PermissionBean getGroupPermission() {
        return groupPermission;
    }

    public void setGroupPermission(PermissionBean groupPermission) {
        this.groupPermission = groupPermission;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(String maxNumber) {
        this.maxNumber = maxNumber;
    }

    public GroupInfoBean getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfoBean groupInfo) {
        this.groupInfo = groupInfo;
    }

    public List<CustomersBean> getCustomers() {
        return customers;
    }

    public void setCustomers(List<CustomersBean> customers) {
        this.customers = customers;
    }

    public ChatInfoBean getChatInfo() {
        return chatInfo;
    }

    public void setChatInfo(ChatInfoBean chatInfo) {
        this.chatInfo = chatInfo;
    }

    public static class PermissionBean implements Serializable {

        /**
         * id : 38
         * groupId : 278
         * customerId :
         * isDelete :
         * createTime :
         * updateTime :
         * openBanned : 0
         * openForbidden : 0
         * nick :
         * headPortrait :
         * openAudio : 0
         * openVideo : 0
         */

        private String id;
        private String groupId;
        private String customerId;
        private String isDelete;
        private String createTime;
        private String updateTime;
        private String openBanned;
        private String openForbidden;
        private String nick;
        private String headPortrait;
        private String openAudio;
        private String openVideo;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(String isDelete) {
            this.isDelete = isDelete;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getOpenBanned() {
            return openBanned;
        }

        public void setOpenBanned(String openBanned) {
            this.openBanned = openBanned;
        }

        public String getOpenForbidden() {
            return openForbidden;
        }

        public void setOpenForbidden(String openForbidden) {
            this.openForbidden = openForbidden;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public String getHeadPortrait() {
            return headPortrait;
        }

        public void setHeadPortrait(String headPortrait) {
            this.headPortrait = headPortrait;
        }

        public String getOpenAudio() {
            return openAudio;
        }

        public void setOpenAudio(String openAudio) {
            this.openAudio = openAudio;
        }

        public String getOpenVideo() {
            return openVideo;
        }

        public void setOpenVideo(String openVideo) {
            this.openVideo = openVideo;
        }
    }

    public static class ChatInfoBean implements Serializable {

        /**
         * type : group
         * targetId : 260
         * screenCapture : false
         * incinerationTime : -1
         */

        private String type;
        private String targetId;
        private int screenCapture;
        private int incinerationTime;

        public int getScreenCapture() {
            return screenCapture;
        }

        public void setScreenCapture(int screenCapture) {
            this.screenCapture = screenCapture;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }

        public int getIncinerationTime() {
            return incinerationTime;
        }

        public void setIncinerationTime(int incinerationTime) {
            this.incinerationTime = incinerationTime;
        }
    }

    public static class GroupInfoBean implements Serializable {
        /**
         * id : 71
         * groupType : 0
         * groupNikeName : Zhaochen_4的讨论组
         * groupHeadPortrait :
         * groupSign :
         * groupNotice :
         * groupOwnerId : 17
         * updateTime :
         * createTime : 1553581323053
         * isDelete : 0
         * isInviteConfirm : 0
         * headPortrait :
         */

        private String id;
        private String groupType;
        private String groupNikeName;
        private String groupHeadPortrait;
        private String groupSign;
        private String groupNotice;
        private String groupOwnerId;
        private String updateTime;
        private String createTime;
        private String isDelete;
        private String isInviteConfirm;
        private String headPortrait;
        private String systemPumpingRate;
        private String gameType;
        private String groupOwnerName;
        private String isBanned;
        private String banFriend;

        public String getIsBanned() {
            return isBanned;
        }

        public void setIsBanned(String isBanned) {
            this.isBanned = isBanned;
        }

        public String getBanFriend() {
            return banFriend;
        }

        public void setBanFriend(String banFriend) {
            this.banFriend = banFriend;
        }

        public String getGroupOwnerName() {
            return groupOwnerName;
        }

        public void setGroupOwnerName(String groupOwnerName) {
            this.groupOwnerName = groupOwnerName;
        }

        public String getGameType() {
            return gameType;
        }

        public void setGameType(String gameType) {
            this.gameType = gameType;
        }

        public String getSystemPumpingRate() {
            return systemPumpingRate;
        }

        public void setSystemPumpingRate(String systemPumpingRate) {
            this.systemPumpingRate = systemPumpingRate;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGroupType() {
            return groupType;
        }

        public void setGroupType(String groupType) {
            this.groupType = groupType;
        }

        public String getGroupNikeName() {
            return groupNikeName;
        }

        public void setGroupNikeName(String groupNikeName) {
            this.groupNikeName = groupNikeName;
        }

        public String getGroupHeadPortrait() {
            return groupHeadPortrait;
        }

        public void setGroupHeadPortrait(String groupHeadPortrait) {
            this.groupHeadPortrait = groupHeadPortrait;
        }

        public String getGroupSign() {
            return groupSign;
        }

        public void setGroupSign(String groupSign) {
            this.groupSign = groupSign;
        }

        public String getGroupNotice() {
            return groupNotice;
        }

        public void setGroupNotice(String groupNotice) {
            this.groupNotice = groupNotice;
        }

        public String getGroupOwnerId() {
            return groupOwnerId;
        }

        public void setGroupOwnerId(String groupOwnerId) {
            this.groupOwnerId = groupOwnerId;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(String isDelete) {
            this.isDelete = isDelete;
        }

        public String getIsInviteConfirm() {
            return isInviteConfirm;
        }

        public void setIsInviteConfirm(String isInviteConfirm) {
            this.isInviteConfirm = isInviteConfirm;
        }

        public String getHeadPortrait() {
            return headPortrait;
        }

        public void setHeadPortrait(String headPortrait) {
            this.headPortrait = headPortrait;
        }
    }

    public static class CustomersBean implements Serializable {
        /**
         * id : 17
         * duoduoId :
         * nick : Zhaochen_4
         * realname :
         * mobile :
         * password :
         * address :
         * email :
         * headPortrait : https://zhongxingjike.oss-cn-hongkong.aliyuncs.com/upload/A324124A-A577-4980-AF66-684D1CE44376.jpg
         * sex :
         * signature :
         * walletAddress :
         * idCard :
         * isShowRealname :
         * updateTime :
         * createTime :
         * isDelete :
         * token :
         * remark :
         * rongToken :
         * payPwd :
         * isFirstLogin :
         * renegeNumber :
         * isConfine :
         * status :
         * isAuthentication :
         * onlineService :
         */

        private String id;
        private String duoduoId;
        private String nick;
        private String realname;
        private String mobile;
        private String password;
        private String address;
        private String email;
        private String headPortrait;
        private String sex;
        private String signature;
        private String walletAddress;
        private String idCard;
        private String isShowRealname;
        private String updateTime;
        private String createTime;
        private String isDelete;
        private String token;
        private String remark;
        private String rongToken;
        private String payPwd;
        private String isFirstLogin;
        private String renegeNumber;
        private String isConfine;
        private String status;
        private String isAuthentication;
        private String onlineService;
        private boolean checked;
        private String firstLetter;

        public String getFirstLetter() {
            return firstLetter;
        }

        public void setFirstLetter(String firstLetter) {
            this.firstLetter = firstLetter;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDuoduoId() {
            return duoduoId;
        }

        public void setDuoduoId(String duoduoId) {
            this.duoduoId = duoduoId;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getHeadPortrait() {
            return headPortrait;
        }

        public void setHeadPortrait(String headPortrait) {
            this.headPortrait = headPortrait;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getWalletAddress() {
            return walletAddress;
        }

        public void setWalletAddress(String walletAddress) {
            this.walletAddress = walletAddress;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public String getIsShowRealname() {
            return isShowRealname;
        }

        public void setIsShowRealname(String isShowRealname) {
            this.isShowRealname = isShowRealname;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(String isDelete) {
            this.isDelete = isDelete;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getRongToken() {
            return rongToken;
        }

        public void setRongToken(String rongToken) {
            this.rongToken = rongToken;
        }

        public String getPayPwd() {
            return payPwd;
        }

        public void setPayPwd(String payPwd) {
            this.payPwd = payPwd;
        }

        public String getIsFirstLogin() {
            return isFirstLogin;
        }

        public void setIsFirstLogin(String isFirstLogin) {
            this.isFirstLogin = isFirstLogin;
        }

        public String getRenegeNumber() {
            return renegeNumber;
        }

        public void setRenegeNumber(String renegeNumber) {
            this.renegeNumber = renegeNumber;
        }

        public String getIsConfine() {
            return isConfine;
        }

        public void setIsConfine(String isConfine) {
            this.isConfine = isConfine;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIsAuthentication() {
            return isAuthentication;
        }

        public void setIsAuthentication(String isAuthentication) {
            this.isAuthentication = isAuthentication;
        }

        public String getOnlineService() {
            return onlineService;
        }

        public void setOnlineService(String onlineService) {
            this.onlineService = onlineService;
        }

    }
}
