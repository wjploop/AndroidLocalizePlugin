package com.wjp

import java.lang.Exception

enum class Language(
    val englishName: String,
    val codeForApi: String,
    val suffixForFile: String
) {
    English("English", "en", "en"),
    ChineseSimplified("ChineseSimplified", "zh", "zh"),
    ChineseTraditional("ChineseTraditional", "cht", "zh-rTW"),
    Japanese("Japanese", "jp", "ja"),
    Korean("Korean", "kor", "ko"),
    Russian("Russian","ru","ru"),
    German ("German","de","de"),
    French("French","fra","fr"),
    Italian("Italian","it","it"),
    Thai("Thai","th","th")

}


fun fromCode(codeForApi: String): Language? {
    return Language.values().filter { it.codeForApi == codeForApi }.firstOrNull()
}


fun main() {
    println(Language.values().map { it.name })
}