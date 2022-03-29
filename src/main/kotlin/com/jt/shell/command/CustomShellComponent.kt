package com.jt.shell.command

import org.springframework.shell.component.SingleItemSelector
import org.springframework.shell.component.support.SelectorItem
import org.springframework.shell.standard.AbstractShellComponent
import java.util.*

open class CustomShellComponent : AbstractShellComponent() {
    /**
     *单选
     */
    fun <T> singleSelect(options: List<SelectorItem<T>>, name: String): T {
        val component = SingleItemSelector(terminal, options, name, null)
        component.setResourceLoader(resourceLoader)
        component.setTemplateExecutor(templateExecutor)
        val context = component.run(SingleItemSelector.SingleItemSelectorContext.empty<T, SelectorItem<T>>())
        val result: T = context.resultItem.flatMap { Optional.ofNullable(it.item) }.get()
        return result
    }

}