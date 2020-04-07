package com.zxjk.moneyspace.bean.response;

import java.util.List;

public class CommunityFilesListResponse {

    /**
     * filesCreate : 4
     * files : [{"fileId":"1","createTime":"1574558707212","fileFormat":"pdf","fileName":"嗯哼","fileAddress":"xxx","fileSize":"20KB"},{"fileId":"2","createTime":"1574558707212","fileFormat":"ppt","fileName":"官方ppt","fileAddress":"xxx","fileSize":"21KB"}]
     */

    private String filesCreate;
    private List<EditListCommunityCultureResponse.FilesBean.FilesListBean> files;

    public String getFilesCreate() {
        return filesCreate;
    }

    public void setFilesCreate(String filesCreate) {
        this.filesCreate = filesCreate;
    }

    public List<EditListCommunityCultureResponse.FilesBean.FilesListBean> getFiles() {
        return files;
    }

    public void setFiles(List<EditListCommunityCultureResponse.FilesBean.FilesListBean> files) {
        this.files = files;
    }
}
