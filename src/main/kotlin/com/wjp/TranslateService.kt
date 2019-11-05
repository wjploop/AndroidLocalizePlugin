package com.wjp

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.dom4j.Document
import org.dom4j.DocumentException
import org.dom4j.DocumentHelper
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import java.io.File
import java.net.URLEncoder
import org.dom4j.io.XMLWriter
import java.io.FileOutputStream
import kotlin.concurrent.thread


class TranslateService(val filePath: String,  val toLangs: List<Language>,val appId:String,val privateKey:String) {

    lateinit var fromDoc: Document
    var targetDocList = ArrayList<Document>(toLangs.size)
    var targetFilePathList=ArrayList<File>(toLangs.size)

    val fromString = LinkedHashMap<String, String>()

    lateinit var httpClient: OkHttpClient


    var xmlFormat = OutputFormat.createPrettyPrint()

    init {
        httpClient=OkHttpClient()
        readData()

        createFileIfNeed()
    }

    private fun readData() {
        fromDoc = readFromFile(filePath)
        fromDoc.rootElement.elements().forEach {
            fromString[it.attribute("name").value] = it.text
        }
    }

    fun traslate() {
        checkIsNeedToTranslate()
    }

    private fun checkIsNeedToTranslate() {
        for ((index, doc) in targetDocList.withIndex()) {
            var currentDocShouldByMotify=false
            for((key,value) in fromString){
                val containedThisKey =  //该文件是否已经有了该字段
                    doc.rootElement.elements().map {
                        it.attributeValue("name")
                    }.contains(key.toString())
                println("key=${key}\nvalue=${value}")

                if (!containedThisKey) {
                    currentDocShouldByMotify=true
                    val targetLangValue=translate(value.toString(),"auto",toLangs[index].codeForApi)
                    doc.rootElement.addElement("string").apply {
                        addAttribute("name",key.toString())
                        text=targetLangValue
                    }
                }
            }
            if (currentDocShouldByMotify) {
                doc.writeToFile(targetFilePathList[index].path)
            }


        }

    }


    private fun createFileIfNeed() {
        val resDirPath = File(filePath).parentFile.parentFile.path

        for (toLang in toLangs) {
            val needFileDir = File(resDirPath + "/" + "values-" + toLang.suffixForFile)
            if (!needFileDir.exists()) {
                needFileDir.mkdir()
            }
            val needFile = File(needFileDir.path + "/", "strings.xml")
            //若是有
            val doc: Document
            if (!needFile.exists()) {
                needFile.createNewFile()
                doc = createEmptyDoc()
            } else {
                doc = readFromFile(needFile.path)
            }
            targetDocList.add(doc)
            targetFilePathList.add(needFile)
        }

    }

    private fun createEmptyDoc(): Document {
        return DocumentHelper.createDocument().apply {
            addElement("resources")
        }
    }

    fun Document.writeToFile(path: String) {
        thread {
            val writer=XMLWriter(FileOutputStream(File(path)),xmlFormat)
            writer.write(this)
            writer.flush()
            writer.close()
        }

    }

    fun readFromFile(filePath: String): Document {
        var doc: Document
        val reader = SAXReader()
        try {
            doc = reader.read(File(filePath))
        } catch (ex: DocumentException) {
            //已经创建了文件，但是该文件不符合xml文件格式要求
            doc=createEmptyDoc()
        }

        return doc

    }

    fun translate(source: String, fromLang: String, toLang: String):String {
        val sign =
            md5(appId + source + SALT + privateKey)
        val url = URL + "q=${URLEncoder.encode(
            source,
            "utf-8"
        )}&from=${fromLang}&to=${toLang}&appid=$appId&salt=$SALT&sign=${sign}"
        val request = Request.Builder().url(url).addHeader(
            "User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)"
        ).build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                println("failed:${response.body}")
            }
            println(response.body.toString())
            val result = Gson().fromJson(response.body?.charStream(), Result::class.java)
            println(result)
            return result.trans_result.first().dst
        }

    }

    companion object {
//        const val APPID = "20191026000344502"
//        const val PRIVATEKEY = "uH5a9XtbyYCJ4BeKkZUF"
        const val SALT = "qwer1234"
        const val URL = "http://api.fanyi.baidu.com/api/trans/vip/translate?"
    }


    data class Result(
        val form: String,
        val to: String,
        val trans_result: List<TransResult>
    ) {
        data class TransResult(
            val src: String,
            val dst: String
        ) {

        }
    }

}

fun main() {
//    val service = TranslateService(
//        "res/values/strings.xml", Language.English, listOf(
//            Language.ChineseTraditional,
//            Language.English,
//            Language.Japanese,
//            Language.Korean
//        )
//    )

//    service.traslate()
}