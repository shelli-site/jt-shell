package com.jt.shell.command

import cn.hutool.core.util.StrUtil
import cn.hutool.socket.aio.AioClient
import cn.hutool.socket.aio.AioSession
import cn.hutool.socket.aio.SimpleIoAction
import com.jt.shell.entity.HistoryMsg
import com.jt.shell.entity.MsgType
import com.jt.shell.entity.SocketTable
import com.jt.shell.provider.ConnectProvider
import com.jt.shell.utils.NONE
import com.jt.shell.utils.designTableStyle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.shell.Availability
import org.springframework.shell.component.support.SelectorItem
import org.springframework.shell.standard.*
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank


@ShellComponent("socket")
@ShellCommandGroup("socket")
class SocketCommand(
    @Autowired val eventPublisher: ApplicationEventPublisher,
    @Autowired val provider: ConnectProvider
) : CustomShellComponent() {
    private val pool = mutableMapOf<String, SocketTable>()


    /**
     * 查看已连接的服务
     */
    @ShellMethod(key = ["socket ps", "ps"], value = "查看已连接的服务", prefix = "-")
    fun ps(a: Boolean): String? {
        var list = pool.values.toList()
        if (a) {
            return designTableStyle(list).render(50)
        }
        list = list.filter { it.active }
        if (list.isEmpty()) {
            return "无连接"
        }
        return designTableStyle(list).render(50)
    }


    /**
     * 加入一个连接
     */
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
                        val msg = HistoryMsg(MsgType.接收, LocalDateTime.now(), StrUtil.utf8Str(data))
                        socketTable.historyMsg.add(msg)
                        socketTable.snapshot = msg.toString()
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
            result = """连接失败 ${e.message}"""
        }
        return result
    }

    /**
     * 移除连接
     */
    @ShellMethod(key = arrayOf("socket rm", "rm"), value = "移除连接", prefix = "")
    fun rm(@ShellOption(value = [""], defaultValue = NONE) name: String): String {
        var poolName = name
        if (poolName == NONE) {
            provider.getCurrentConnect()?.let {
                poolName = it.name
            }
        }
        if (poolName in pool) {
            pool.remove(poolName)
            return poolName
        }
        return "无连接可移除"
    }

    /**
     * 查看消息历史记录
     */
    @ShellMethod(key = arrayOf("socket hs", "hs"), value = "查看消息历史记录", prefix = "-")
    fun hs(@ShellOption(value = [""], defaultValue = NONE) name: String): String {
        if (name == NONE) {
            return provider.getCurrentConnect()?.let {
                it.historyMsg.map { it.toString() }.joinToString("")
            } ?: "当前无连接"
        }
        return pool[name]?.let {
            it.historyMsg.map { it.toString() }.joinToString("")
        } ?: "无此连接"
    }

    /**
     * 发送消息
     */
    @ShellMethod(key = arrayOf("socket send", "send"), value = "发送消息", prefix = "-")
    fun send(
        @NotBlank @ShellOption(value = [""]) hex: String,
        @ShellOption(value = ["-W", "-w"], help = "阻塞等待") W: Boolean,
        @ShellOption(help = "格式化输出") F: Boolean
    ): String {
        val sendTime = LocalDateTime.now()
        val msg = HistoryMsg(MsgType.发送, sendTime, hex)
        val bytes = hex.toByteArray()
        return provider.getCurrentConnect()!!.run {
            if (!active || client == null) {
                return@run "连接异常"
            }
            historyMsg.add(msg)
            snapshot = msg.toString()
            client!!.write(ByteBuffer.wrap(bytes))
            this.print(-1)
            if (W) {
                while (true) {
                    if (historyMsg[historyMsg.size - 1].collectTime.isAfter(sendTime)) {
                        // 已收到消息
                        this.print(-1)
                        break
                    }
                }
            }
            return@run "发送完毕！"
        }
    }

    /**
     * 设置当前连接
     */
    @ShellMethod(key = arrayOf("socket use", "use"), value = "设置当前连接", prefix = "-")
    fun use(@ShellOption(value = [""], defaultValue = NONE) name: String): String {
        if (name != NONE && pool.keys.contains(name)) {
            eventPublisher.publishEvent(pool[name]!!)
            return "use ${name} success！"
        }
        val options = pool.entries.map { SelectorItem.of(it.value.showName(), it.value) }
        if (options.isEmpty()) {
            return "没有可用连接"
        }
        val value = this.singleSelect(options, "选择当前连接")
        eventPublisher.publishEvent(value)
        return "Got value ${value.name}"
    }

    /**
     * 连接检测
     */
    @ShellMethodAvailability(value = ["send"])
    fun connectCheck(): Availability {
        val currentConnect = provider.getCurrentConnect()
        if (currentConnect == null) {
            return Availability.unavailable("you are not connected")
        }
        return Availability.available()
    }

}