package com.jt.shell.provider

import com.jt.shell.entity.SocketTable
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.springframework.context.event.EventListener
import org.springframework.shell.jline.PromptProvider
import org.springframework.stereotype.Component


@Component
class ConnectProvider : PromptProvider {

    /**
     * 当前连接（单例）
     */
    object Connect {
        var socket: SocketTable? = null
    }

    fun getCurrentConnect(): SocketTable? = Connect.socket

    override fun getPrompt(): AttributedString {
        return AttributedString(
            "server-${promptPrefix()}:>",
            AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)
        )
    }

    private fun promptPrefix(): String = Connect.socket?.run { "$name[$host:$port]" } ?: "unknown"

    @EventListener
    fun handle(event: SocketTable) {
        Connect.socket = event
    }
}