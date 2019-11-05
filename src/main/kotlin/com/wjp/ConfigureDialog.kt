package com.wjp

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import java.awt.BorderLayout
import java.awt.Container
import java.awt.GridLayout
import java.awt.TextField
import java.awt.event.ItemEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class ConfigureDialog(val project: Project,val filePath:String) : DialogWrapper(project, true) {

    lateinit var input_app_id:JTextField
    lateinit var input_private_key:JTextField

    private var appId=""
    private var privateKey=""

    private lateinit var properties: PropertiesComponent

    val selectedLang = mutableListOf<Language>()

    init {
        title = "Configure and Convert"
        init()
    }


    override fun createCenterPanel(): JComponent? {
        val panel = JPanel(BorderLayout(16, 6))
        val container = Container()
        properties = PropertiesComponent.getInstance(project)
        val toLangsString = properties.getValue("key_to_languages", "")

        appId=properties.getValue("key_app_id","")
        privateKey=properties.getValue("key_private_key","")


        val supportLangs = Language.values()

        val toLangs = toLangsString.split(", ").map {
            fromCode(it)
        }


        container.layout = GridLayout(supportLangs.size / 4, 4)
        supportLangs.forEach { lang ->
            container.add(JCheckBox(lang.englishName).apply {
                addItemListener {
                    if (it.stateChange == ItemEvent.SELECTED) {
                        selectedLang.add(lang)
                    } else {
                        selectedLang.remove(lang)
                    }
                }
                if (toLangs.isNotEmpty() && toLangs.contains(lang)) {
                    isSelected = true
                }
            })
        }
        panel.add(container, BorderLayout.CENTER)
        val tokenInput=Container().apply {
            layout=GridLayout(6,2)
            val baiduStr="http://api.fanyi.baidu.com/api/trans/product/index"
            add(JLabel("<html>请进入<a href='$baiduStr'>百度翻译开放平台</a>，获取输入以下信息(默认是本人账号，次数可能有限)</html>").apply {
                addMouseListener(object:MouseAdapter(){
                    override fun mouseClicked(e: MouseEvent?) {
                        try{
                            Runtime.getRuntime().exec("cmd.exe /c start $baiduStr")
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                })
            })
            add(JLabel())

            add(JLabel("APP ID："))
            add(JTextField().apply {
                input_app_id=this
                text=appId

            })
            add(JLabel("密钥："))
            add(JTextField().apply {
                input_private_key=this
                text=privateKey
            })

            add(JLabel())
            add(JLabel())

            add(JLabel("勾选要转换的目标语言："))


        }
        panel.add(tokenInput,BorderLayout.NORTH)

        return panel
    }

    override fun doOKAction() {
        super.doOKAction()
        if (selectedLang.isEmpty()) {
            Messages.showErrorDialog("Please select the target languages you want to convert to ", "Error")
        }
        val selectedLanguagesString = selectedLang.map { it.codeForApi }.joinToString()
        properties.setValue("key_to_languages", selectedLanguagesString)

        val appId=input_app_id.text.toString().trim()
        val privateKey=input_private_key.text.toString().trim()

        properties.setValue("key_app_id",appId)
        properties.setValue("key_private_key",privateKey)

        TranslateService(filePath,selectedLang,appId,privateKey).traslate()

    }



}

