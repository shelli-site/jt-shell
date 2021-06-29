package com.jt.shell.command

import cn.hutool.core.util.StrUtil
import cn.hutool.socket.aio.AioClient
import cn.hutool.socket.aio.AioSession
import cn.hutool.socket.aio.SimpleIoAction
import com.jt.shell.entity.HistoryMsg
import com.jt.shell.entity.MsgType
import com.jt.shell.entity.SocketTable
import com.jt.shell.utils.TableBuilderHelper
import org.springframework.shell.standard.ShellCommandGroup
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.util.function.Consumer
import javax.validation.constraints.NotBlank


@ShellComponent("socket")
@ShellCommandGroup("socket")
class SocketCommand {
    private val pool: HashMap<String, SocketTable> = HashMap()

    @ShellMethod(key = ["socket ps", "ps"], value = "查看已连接的服务", prefix = "-")
    fun ps(a: Boolean): String? {
        var list = pool.keys.map { k -> pool.get(k) as SocketTable }
        if (a) {
            return TableBuilderHelper.designTableStyle(SocketTable::class.java, list).render(50)
        }
        list = list.filter { s -> s.active }
        return TableBuilderHelper.designTableStyle(SocketTable::class.java, list).render(50)
    }


    @ShellMethod(key = ["socket join", "join"], value = "加入到一个连接", prefix = "-")
    fun join(
        @ShellOption(value = ["-host", "-h"], defaultValue = "127.0.0.1") host: String,
        @ShellOption(value = ["-post", "-p"], defaultValue = "30523") port: Int,
        @ShellOption(value = ["-name", "-n"]) @NotBlank name: String
    ): String {
        if (StrUtil.isBlank(name)) {
            return "请输入名称"
        }
        val socketTable = SocketTable(name, host, port)
        pool.put(name, socketTable)
        var result: String
        try {
            print("连接中...\n")
            val client = AioClient(InetSocketAddress(host, port), object : SimpleIoAction() {
                override fun doAction(session: AioSession, data: ByteBuffer) {
                    if (data.hasRemaining()) {
                        socketTable.historyMsg.add(HistoryMsg(MsgType.接收, LocalDateTime.now(), StrUtil.utf8Str(data)))
                        session.read()
                    }
                }
            })

            socketTable.client = client
            client.read()
            socketTable.active = true
            result = "连接成功"
        } catch (e: Exception) {
            socketTable.error = e.message
            result = """连接失败 ${e.message.toString()}"""
        }
        return result
    }

    @ShellMethod(key = arrayOf("socket rm", "rm"), value = "移除连接", prefix = "")
    fun rm(@ShellOption(value = [""]) name: String): String {
        if (pool.containsKey(name)) {
            pool.remove(name)
            return name
        }
        return "无此连接"
    }

    @ShellMethod(key = arrayOf("socket hs", "hs"), value = "查看消息历史记录", prefix = "-")
    fun hs(@NotBlank name: String): String {
        val socketTable = pool.get(name)
        if (socketTable == null) {
            return "无此连接"
        }
        socketTable.historyMsg.forEach(Consumer { h -> print(h.toString()) })
        return ""
    }

    @ShellMethod(key = arrayOf("socket send", "send"), value = "发送消息", prefix = "-")
    fun send(
        @NotBlank name: String,
        @NotBlank hex: String,
        @ShellOption(value = ["-W", "-w"], help = "阻塞等待") W: Boolean,
        @ShellOption(help = "格式化输出") F: Boolean
    ): String {
        val socketTable = pool.get(name)
        if (socketTable == null) {
            return "无此连接"
        } else if (!socketTable.active || socketTable.client == null) {
            return "连接异常"
        }
        val sendTime = LocalDateTime.now()
        socketTable.historyMsg.add(HistoryMsg(MsgType.发送, sendTime, hex))
        val bytes = hex.toByteArray()
        socketTable.client!!.write(ByteBuffer.wrap(bytes))
        socketTable.print(-1)
        if (W) {
            while (true) {
                if (socketTable.historyMsg.get(socketTable.historyMsg.size - 1).collectTime.isAfter(sendTime)) {
                    // 已收到消息
                    socketTable.print(-1)
                    break
                }
            }
        }
        return "发送完毕！"
    }
}