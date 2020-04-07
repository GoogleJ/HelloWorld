package com.zxjk.moneyspace.network.rx;

import android.accounts.NetworkErrorException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class RxException {
    private static final String CONNECT_NETWORK_EXCEPTION = "网络未连接,请先连接网络";
    private static final String CONNECTION_TIMEOUT = "网络连接超时，请检查您的网络状态，稍后重试";
    private static final String CONNECT_EXCEPTION = "网络连接异常，请检查您的网络状态";
    private static final String UNKNOWN_HOST_EXCEPTION = "网络异常，请检查您的网络状态";

    public static String getMessage(Throwable t) {
        if (t instanceof SocketTimeoutException) {
            return CONNECTION_TIMEOUT;
        } else if (t instanceof ConnectException) {
            return CONNECT_EXCEPTION;
        } else if (t instanceof UnknownHostException) {
            return UNKNOWN_HOST_EXCEPTION;
        } else if (t instanceof NetworkErrorException) {
            return CONNECT_NETWORK_EXCEPTION;
        } else {
            return t.getMessage();
        }
    }

    public static class ParamsException extends Exception {
        private String msg;
        private int code;

        public ParamsException() {
            super("");
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public ParamsException(String msg, int code) {
            super(msg);
            this.msg = msg;
            this.code = code;
        }

        @Override
        public String getMessage() {
            return msg;
        }

        public int getCode() {
            return code;
        }
    }

    public static class DuplicateLoginExcepiton extends Exception {
        private String msg;
        private int code;

        public DuplicateLoginExcepiton(String msg) {
            super(msg);
            this.msg = msg;
        }

        public DuplicateLoginExcepiton(String msg,int code) {
            super(msg);
            this.msg = msg;
            this.code = code;
        }

        @Override
        public String getMessage() {
            return msg;
        }

        public int getCode() {
            return code;
        }
    }

}
