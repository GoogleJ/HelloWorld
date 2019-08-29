package com.zxjk.duoduo.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtils {

    /**
     * 保留两位小数
     *
     * @param string 判断的string
     * @return 空:"0.00" 不为空:string
     */
    public static String getTwoDecimals(@Nullable String string) {
        if (string != null && !TextUtils.isEmpty(string) && !string.equalsIgnoreCase("null")
                && !("").equals(string)) {
            Double d = Double.parseDouble(string);
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            return decimalFormat.format(d);
        }
        return "0.00";
    }

    /**
     * 向上取整
     * 保留两位小数
     *
     * @param string 判断的string
     * @return 空:"0.00" 不为空:string
     */
    public static String getCeilDecimals(@Nullable String string) {
        if (string != null && !TextUtils.isEmpty(string) && !string.equalsIgnoreCase("null")
                && !("").equals(string)) {
            Double d = ArithUtils.mul(Double.parseDouble(string), Double.parseDouble("100"));
            Double s = ArithUtils.div(Math.ceil(d), Double.parseDouble("100"));
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            return decimalFormat.format(s);
        }
        return "0.00";
    }

    /**
     * 保留整数
     *
     *
     * @param string 判断的string
     * @return 空:"0" 不为空:string
     */
    public static String getInteger(@Nullable String string) {
        if (string != null && !TextUtils.isEmpty(string) && !string.equalsIgnoreCase("null")
                && !("").equals(string)) {
            Double d = Double.parseDouble(string);
            DecimalFormat decimalFormat = new DecimalFormat("0");
            return decimalFormat.format(d);
        }
        return "0";
    }

    /**
     * 13位时间戳转换日期格式字符串
     *
     * @param time
     * @param format
     * @return
     */
    public static String timeStamp2Date(long time, String format) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }


}
