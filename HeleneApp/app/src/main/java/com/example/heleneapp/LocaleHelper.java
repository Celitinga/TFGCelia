package com.example.heleneapp;

import android.content.Context;
import android.content.res.Configuration;
import android.os.LocaleList;

import java.util.Locale;

public class LocaleHelper {

    private static final String PREF_LANG = "app_language";

    public static void setLocale(Context context, String langCode) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .edit().putString(PREF_LANG, langCode).apply();
    }

    public static String getSavedLocale(Context context) {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getString(PREF_LANG, "es");
    }

    public static Context applyLocale(Context context) {
        String lang = getSavedLocale(context);
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());

        config.setLocale(locale);
        config.setLocales(new LocaleList(locale));

        return context.createConfigurationContext(config);
    }
}