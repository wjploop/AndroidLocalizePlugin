package com.wjp.actions

import com.wjp.TranslateService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.wjp.ConfigureDialog
import com.wjp.Language

class TranslateAction:AnAction("Translate to you want") {
    override fun actionPerformed(e: AnActionEvent) {
        val file=CommonDataKeys.VIRTUAL_FILE.getData(e.dataContext) as VirtualFile
//        val service= TranslateService(
//            file.path,
//            Language.ChineseSimplified, listOf(
//                Language.ChineseTraditional,
//                Language.English,
//                Language.Japanese,
//                Language.Korean
//            )
//        )
//        service.traslate()
        ConfigureDialog(e.project!!,file.path).show()
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        val file=CommonDataKeys.VIRTUAL_FILE.getData(e.dataContext) as VirtualFile
        val isStringXml= file.path.substringAfterLast("/")=="strings.xml"
        e.presentation.apply {
            isVisible=isStringXml
            isEnabled=isStringXml
        }

    }
}