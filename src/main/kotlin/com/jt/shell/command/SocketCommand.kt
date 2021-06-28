package com.jt.shell.command

import cn.hutool.core.util.StrUtil
import com.jt.shell.entity.SocketTable
import com.jt.shell.utils.TableBuilderHelper
import org.springframework.shell.standard.ShellCommandGroup
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import java.net.Socket
import javax.validation.constraints.NotBlank


@ShellComponent("socket")
@ShellCommandGroup("socket")
class SocketCommand {
    private val pool: HashMap<String, Socket> = HashMap()

    @ShellMethod(key = arrayOf("socket ps", "ps"), value = "查看已连接的服务", prefix = "-")
    fun ps(a: Boolean): String? {
        if (a) {
            return null
        }
        val list = pool.keys.map { k -> SocketTable(k, pool.get(k) as Socket) }
        return TableBuilderHelper.designTableStyle(SocketTable::class.java, list).render(50)
    }


    @ShellMethod(key = arrayOf("socket join", "join"), value = "加入到一个连接", prefix = "-")
    fun join(@ShellOption(value = arrayOf("-host", "-h"), defaultValue = "127.0.0.1") host: String,
             @ShellOption(value = arrayOf("-post", "-p"), defaultValue = "30523") port: Int,
             @ShellOption(value = arrayOf("-name", "-n")) @NotBlank name: String): String {
        if (StrUtil.isBlank(name)) {
            return "请输入名称"
        }
        Thread({
            try {
                val socket = Socket(host, port)
                pool.put(name, socket)
            } catch (e: InterruptedException) {
                e.message
            }
        }).start()

        return "连接中..."
    }
}