package com.jarlingwar.adminapp.utils

import android.content.Context
import android.content.res.Resources
import java.util.Locale

object LocaleHelper {
    const val LANGUAGE_PREFS = "languageCodePrefs"
    const val LANGUAGE_PREFS_VALUE = "languageCodeValue"
    const val LANG_SYSTEM = "sys"
    const val LANG_ENGLISH = "en"
    const val LANG_RUSSIAN = "ru"
    fun setLocale(context: Context): Context? {
        var language = context.getPreferredLocale()
        if (language.isEmpty()) language = LANG_SYSTEM
        return updateResources(context, language)
    }

    fun getCurrentLocale(): Locale = Locale.getDefault()

    fun Context.getPreferredLocale() =
        getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE)
            .getString(LANGUAGE_PREFS_VALUE, LANG_SYSTEM) ?: LANG_SYSTEM

    private fun updateResources(context: Context, language: String): Context? {
        //russian is default language so leave empty
        val locale = when (language) {
            LANG_SYSTEM -> Resources.getSystem().configuration.locales[0]
            LANG_RUSSIAN -> Locale("")
            else -> Locale(language)
        }
        Locale.setDefault(locale)
        val configuration =  context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }
}