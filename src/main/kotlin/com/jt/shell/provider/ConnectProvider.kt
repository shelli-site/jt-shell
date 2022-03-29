package com.jt.shell.provider

import com.jt.shell.entity.SocketTable
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.springframework.context.event.EventListener
import org.springframework.shell.jline.PromptProvider
import org.springframework.stereotype.Component


@Component
class ConnectProvider : PromptProvider {

    private var currentConnect: SocketTable? = null

    fun getCurrentConnect():SocketTable?  = currentConnect

    override fun getPrompt(): AttributedString {
        return AttributedString(
            "server-${promptPrefix()}:>",
            AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)
        )
    }

    private fun promptPrefix(): String = currentConnect?.run { "$name[$host:$port]" } ?: "unknown"

    @EventListener
    fun handle(connect: SocketTable) {
        currentConnect = connect
    }
}