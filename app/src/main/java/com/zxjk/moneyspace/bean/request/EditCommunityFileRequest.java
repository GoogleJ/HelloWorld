package com.zxjk.moneyspace.bean.request;

import java.util.List;

public class EditCommunityFileRequest {

    /**
     * type : add
     * filesList : [{"fileFormat":"fileFormat","fileName":"fileName","fileAddress":"fileAddress","fileSize":"5kb"},{"fileFormat":"fileFormat1","fileName":"fileName1","fileAddress":"fileAddress1","fileSize":"20kb"}]
     * groupId : 65
     */

    private String type;
    private String groupId;
    private List<FilesListBean> filesList;
    /**
     * fileName : 嗯哼
     * fileId : 1
     */

    private String fileName;
    private String fileId;
    /**
     * filesOpen : 0
     */

    private String filesOpen;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<FilesListBean> getFilesList() {
        return filesList;
    }

    public void setFilesList(List<FilesListBean> filesList) {
        this.filesList = filesList;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFilesOpen() {
        return filesOpen;
    }

    public void setFilesOpen(String filesOpen) {
        this.filesOpen = filesOpen;
    }

    public static class FilesListBean {
        /**
         * fileFormat : fileFormat
         * fileName : fileName
         * fileAddress : fileAddress
         * fileSize : 5kb
         */

        private String fileFormat;
        private String fileName;
        private String fileAddress;
        private String fileSize;

        public String getFileFormat() {
            return fileFormat;
        }

        public void setFileFormat(String fileFormat) {
            this.fileFormat = fileFormat;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileAddress() {
            return fileAddress;
        }

        public void setFileAddress(String fileAddress) {
            this.fileAddress = fileAddress;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }
    }
}
