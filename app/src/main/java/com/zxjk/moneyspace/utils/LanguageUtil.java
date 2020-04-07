package com.zxjk.moneyspace.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import androidx.annotation.StringDef;

import com.zxjk.moneyspace.Constant;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

public class LanguageUtil {
    public static final String ENGLISH = "english";
    public static final String CHINESE = "chinese";
    public static final String KOREAN = "korean";

    private final String SP_NAME = "language_setting";
    private final String TAG_LANGUAGE = "language_select";

    private final SharedPreferences mSharedPreferences;

    private static volatile LanguageUtil instance;

    private LanguageUtil(Context context) {
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static LanguageUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (LanguageUtil.class) {
                if (instance == null) {
                    instance = new LanguageUtil(context);
                }
            }
        }
        return instance;
    }

    @StringDef({ENGLISH, CHINESE, KOREAN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Language {
    }

    public void changeLanguage(@Language String language) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(TAG_LANGUAGE, language);
        edit.commit();
    }

    public String getCurrentLanguage() {
        return mSharedPreferences.getString(TAG_LANGUAGE, "phone");
    }

    public Context setLocal(Context context) {
        return updateResources(context, getCurrentLanguageLocale());
    }

    private Context updateResources(Context context, Locale locale) {
        Locale.setDefault(locale);
        Constant.language = locale.toString().replace("_", "-");
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        context = context.createConfigurationContext(config);
        return context;
    }

    private Locale getCurrentLanguageLocale() {
        String language = getCurrentLanguage();
        switch (language) {
            case "phone":
                return getSystemLocale();
            case "english":
                return Locale.ENGLISH;
            case "korean":
                return Locale.KOREAN;
            case "chinese":
            default:
                return Locale.SIMPLIFIED_CHINESE;
        }
    }

    private Locale getSystemLocale() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return locale;
    }

}
