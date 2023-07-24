package com.caowei.utils

import java.util.Locale

object LanguageUtils {
    public fun getSystemLanguage(): Language {
        val locale = Locale.getDefault()
        return Language(
            language = locale.language,
            country = locale.country,
            script = locale.script
        )
    }

    /**
     * 获取当前系统所支持的所有语言
     */
    public fun getSystemLanguages(): List<Language> {
        val locales = Locale.getAvailableLocales()
        val languages = mutableListOf<Language>()
        locales.forEach {
            val language = Language(it.language, it.country, it.script)
            languages.add(language)
        }
        return languages.toList()
    }
}

data class Language(
    val language: String,
    val country: String,
    val script: String
)