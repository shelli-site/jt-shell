package com.jt.shell.entity

import cn.hutool.socket.aio.AioClient
import com.jt.shell.annotation.TableIgnore
import java.time.LocalDateTime

class SocketTable(val name: String, val host: String, val port: Int) {
    fun print(i: Int) {
        var index = i
        if (i < 0) {
            index = historyMsg.size + i
        }
        val msg = historyMsg.get(index)
        print(msg.toString())
    }

    @TableIgnore
    var client: AioClient? = null
    var active: Boolean = false
    var snapshot: String = ""
    var error: String? = null

    @TableIgnore
    val historyMsg: MutableList<HistoryMsg> = ArrayList()
}

class HistoryMsg(val type: MsgType, val collectTime: LocalDateTime, val hex: String) {
    override fun toString(): String {
        var sb = StringBuffer("[")
        sb.append(collectTime).append("]  ")
            .append(type.single).append(" ")
            .append(hex).append("\n")
        return sb.toString()
    }
}

enum class MsgType(val single: String) {
    发送("-->>>"),
    接收("<<<--")
}
