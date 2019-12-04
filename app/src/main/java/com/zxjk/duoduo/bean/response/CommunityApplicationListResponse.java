package com.zxjk.duoduo.bean.response;

import java.util.List;

public class CommunityApplicationListResponse {

    /**
     * application : [{"applicationId":"1","applicationName":"百度","applicationLogo":"http://img5.imgtn.bdimg.com/it/u=3092254507,1605408710&fm=26&gp=0.jpg","applicationAddress":"https://www.baidu.com/"},{"applicationId":"2","applicationName":"京东","applicationLogo":"https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1388647553,511134846&fm=26&gp=0.jpg","applicationAddress":"https://www.jd.com/"},{"applicationId":"3","applicationName":"网易云音乐","applicationLogo":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575186121&di=6c0bb35bf9a312f0232bbddfe71a9403&imgtype=jpg&er=1&src=http%3A%2F%2Fimg1.gtimg.com%2Ftech%2Fpics%2Fhv1%2F56%2F157%2F1275%2F82946966.png","applicationAddress":"https://music.163.com/"},{"applicationId":"4","applicationName":"QQ音乐","applicationLogo":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1574660733692&di=a50b609d71b0800e9d7feb9ecad44bda&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F031009157fcf46da84a0d304f579195.jpg","applicationAddress":"https://y.qq.com/"},{"applicationId":"5","applicationName":"keep","applicationLogo":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1574660772646&di=4f610f972c96e5f17b3965290d5da1e6&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20170308%2F99ef8eba95354f468a75eacdc1aa3f28_th.jpeg","applicationAddress":"https://www.gotokeep.com/"}]
     * permission : 1
     * appCreate : 8
     * officialApplication : [{"applicationId":"1","applicationName":"海浪官方应用(测试)","applicationLogo":"https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/4qnihrkux4nk4cqw3wq5.jpg","applicationAddress":"https://www.baidu.com","isOpen":"1"},{"applicationId":"2","applicationName":"奥嘞给","applicationLogo":"http://pics6.baidu.com/feed/78310a55b319ebc480b07f7c5d1865f91c1716ca.jpeg?token=51deb7b5572491e224d281838a80c84d&s=1F06D70448036ECC5A0601E90300A062","applicationAddress":"https://www.baidu.com","isOpen":"0"}]
     */

    private String permission;
    private String appCreate;
    private List<ApplicationBean> application;
    private List<ApplicationBean> officialApplication;

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getAppCreate() {
        return appCreate;
    }

    public void setAppCreate(String appCreate) {
        this.appCreate = appCreate;
    }

    public List<ApplicationBean> getApplication() {
        return application;
    }

    public void setApplication(List<ApplicationBean> application) {
        this.application = application;
    }

    public List<ApplicationBean> getOfficialApplication() {
        return officialApplication;
    }

    public void setOfficialApplication(List<ApplicationBean> officialApplication) {
        this.officialApplication = officialApplication;
    }

    public static class ApplicationBean {
        /**
         * applicationId : 1
         * applicationName : 海浪官方应用(测试)
         * applicationLogo : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/4qnihrkux4nk4cqw3wq5.jpg
         * applicationAddress : https://www.baidu.com
         * isOpen : 1
         */

        private String applicationId;
        private String applicationName;
        private String applicationLogo;
        private String applicationAddress;
        private String isOpen = "1";

        public String getApplicationId() {
            return applicationId;
        }

        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public String getApplicationName() {
            return applicationName;
        }

        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        public String getApplicationLogo() {
            return applicationLogo;
        }

        public void setApplicationLogo(String applicationLogo) {
            this.applicationLogo = applicationLogo;
        }

        public String getApplicationAddress() {
            return applicationAddress;
        }

        public void setApplicationAddress(String applicationAddress) {
            this.applicationAddress = applicationAddress;
        }

        public String getIsOpen() {
            return isOpen;
        }

        public void setIsOpen(String isOpen) {
            this.isOpen = isOpen;
        }
    }
}
