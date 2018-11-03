package com.jx.sleep_dg.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LanguageUtil {

    private static final String LANGUAGE = "prefs.language";
    private static final String COUNTRY = "prefs.country";

    /**
     * 更改应用语言
     *
     * @param context
     * @param locale      语言地区
     * @param persistence 是否持久化
     */
    public static void changeAppLanguage(Context context, Locale locale, boolean persistence) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, metrics);
        if (persistence) {
            PreferenceUtils.putString(LANGUAGE, locale.getLanguage());
        }
    }

    public static boolean isSameWithSetting(Context context) {
        String language = context.getResources().getConfiguration().locale.getLanguage();
        return language.equals(PreferenceUtils.getString(LANGUAGE));
    }

    public static Locale getAppLocale() {
        String languate = PreferenceUtils.getString(LANGUAGE);
        if (TextUtils.isEmpty(languate)) return null;
        return new Locale(languate);
    }
}
