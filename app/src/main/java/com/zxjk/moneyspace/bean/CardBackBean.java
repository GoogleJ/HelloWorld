package com.zxjk.moneyspace.bean;

public class CardBackBean {

    /**
     * config_str : {"side":"back"}
     * end_date : 20210830
     * issue : 岐山县公安局
     * request_id : 20190524154112_48e043aa13a6827882343e0962dd914c
     * start_date : 20110830
     * success : true
     */

    private String config_str;
    private String end_date;
    private String issue;
    private String request_id;
    private String start_date;
    private boolean success;

    public String getConfig_str() {
        return config_str;
    }

    public void setConfig_str(String config_str) {
        this.config_str = config_str;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
