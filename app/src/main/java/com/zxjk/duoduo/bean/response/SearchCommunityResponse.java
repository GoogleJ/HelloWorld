package com.zxjk.duoduo.bean.response;

import java.util.List;

public class SearchCommunityResponse {


    /**
     * total : 119
     * list : [{"ownerNick":"134****9876","groupId":"136","communityLogo":"https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/8C634871-7A42-4965-BDC1-FAEEC8304B95.jpg","communityName":"1","isPay":"0","members":"1","isInGroup":"","inGroup":"0","code":"1102390"},{"ownerNick":"155****3569","groupId":"184","communityLogo":"https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/8C634871-7A42-4965-BDC1-FAEEC8304B95.jpg","communityName":"1","isPay":"0","members":"1","isInGroup":"","inGroup":"0","code":"3149858"},{"ownerNick":"午饭吃什么午饭吃什么午饭吃什么","groupId":"82","communityLogo":"https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/8C634871-7A42-4965-BDC1-FAEEC8304B95.jpg","communityName":"1","isPay":"1","members":"1","isInGroup":"","inGroup":"0","code":"1259675"},{"ownerNick":"彭摆鱼","groupId":"305","communityLogo":"https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/7DEABC7F-824D-4E30-81E8-323EC2D35244.jpg","communityName":"1","isPay":"0","members":"1","isInGroup":"","inGroup":"0","code":"7879419"},{"ownerNick":"","groupId":"75","communityLogo":"https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/8C634871-7A42-4965-BDC1-FAEEC8304B95.jpg","communityName":"1","isPay":"0","members":"1","isInGroup":"","inGroup":"0","code":"1259668"},{"ownerNick":"","groupId":"76","communityLogo":"https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/8C634871-7A42-4965-BDC1-FAEEC8304B95.jpg","communityName":"1","isPay":"0","members":"1","isInGroup":"","inGroup":"0","code":"1102210"},{"ownerNick":"","groupId":"94","communityLogo":"https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/31684ABE-A197-4477-94FE-450698D26162.jpg","communityName":"1","isPay":"0","members":"1","isInGroup":"","inGroup":"0","code":"1102276"},{"ownerNick":"","groupId":"120","communityLogo":"https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/8C634871-7A42-4965-BDC1-FAEEC8304B95.jpg","communityName":"1","isPay":"0","members":"1","isInGroup":"","inGroup":"0","code":"1574821"},{"ownerNick":"","groupId":"122","communityLogo":"https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/8C634871-7A42-4965-BDC1-FAEEC8304B95.jpg","communityName":"1","isPay":"0","members":"1","isInGroup":"","inGroup":"0","code":"1259857"},{"ownerNick":"","groupId":"131","communityLogo":"https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/8C634871-7A42-4965-BDC1-FAEEC8304B95.jpg","communityName":"1","isPay":"0","members":"1","isInGroup":"","inGroup":"0","code":"7874216"}]
     * pageNum : 1
     * pageSize : 10
     * size : 10
     * startRow : 1
     * endRow : 10
     * pages : 12
     * prePage : 0
     * nextPage : 2
     * isFirstPage : true
     * isLastPage : false
     * hasPreviousPage : false
     * hasNextPage : true
     * navigatePages : 8
     * navigatepageNums : [1,2,3,4,5,6,7,8]
     * navigateFirstPage : 1
     * navigateLastPage : 8
     * lastPage : 8
     * firstPage : 1
     */

    private int total;
    private int pageNum;
    private int pageSize;
    private int size;
    private int startRow;
    private int endRow;
    private int pages;
    private int prePage;
    private int nextPage;
    private boolean isFirstPage;
    private boolean isLastPage;
    private boolean hasPreviousPage;
    private boolean hasNextPage;
    private int navigatePages;
    private int navigateFirstPage;
    private int navigateLastPage;
    private int lastPage;
    private int firstPage;
    private List<ListBean> list;
    private List<Integer> navigatepageNums;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPrePage() {
        return prePage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public boolean isIsFirstPage() {
        return isFirstPage;
    }

    public void setIsFirstPage(boolean isFirstPage) {
        this.isFirstPage = isFirstPage;
    }

    public boolean isIsLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public int getNavigatePages() {
        return navigatePages;
    }

    public void setNavigatePages(int navigatePages) {
        this.navigatePages = navigatePages;
    }

    public int getNavigateFirstPage() {
        return navigateFirstPage;
    }

    public void setNavigateFirstPage(int navigateFirstPage) {
        this.navigateFirstPage = navigateFirstPage;
    }

    public int getNavigateLastPage() {
        return navigateLastPage;
    }

    public void setNavigateLastPage(int navigateLastPage) {
        this.navigateLastPage = navigateLastPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public List<Integer> getNavigatepageNums() {
        return navigatepageNums;
    }

    public void setNavigatepageNums(List<Integer> navigatepageNums) {
        this.navigatepageNums = navigatepageNums;
    }

    public static class ListBean {
        /**
         * ownerNick : 134****9876
         * groupId : 136
         * communityLogo : https://zhongxingjike2.oss-cn-hongkong.aliyuncs.com/upload/8C634871-7A42-4965-BDC1-FAEEC8304B95.jpg
         * communityName : 1
         * isPay : 0
         * members : 1
         * isInGroup :
         * inGroup : 0
         * code : 1102390
         */

        private String ownerNick;
        private String groupId;
        private String communityLogo;
        private String communityName;
        private String isPay;
        private String members;
        private String isInGroup;
        private String inGroup;
        private String code;

        public String getOwnerNick() {
            return ownerNick;
        }

        public void setOwnerNick(String ownerNick) {
            this.ownerNick = ownerNick;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getCommunityLogo() {
            return communityLogo;
        }

        public void setCommunityLogo(String communityLogo) {
            this.communityLogo = communityLogo;
        }

        public String getCommunityName() {
            return communityName;
        }

        public void setCommunityName(String communityName) {
            this.communityName = communityName;
        }

        public String getIsPay() {
            return isPay;
        }

        public void setIsPay(String isPay) {
            this.isPay = isPay;
        }

        public String getMembers() {
            return members;
        }

        public void setMembers(String members) {
            this.members = members;
        }

        public String getIsInGroup() {
            return isInGroup;
        }

        public void setIsInGroup(String isInGroup) {
            this.isInGroup = isInGroup;
        }

        public String getInGroup() {
            return inGroup;
        }

        public void setInGroup(String inGroup) {
            this.inGroup = inGroup;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
