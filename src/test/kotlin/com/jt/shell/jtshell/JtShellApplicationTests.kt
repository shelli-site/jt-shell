package com.jt.shell.jtshell

import cn.hutool.socket.aio.AioServer

//@SpringBootTest
class JtShellApplicationTests {

    //	@Test
    fun contextLoads() {
    }

}

fun main() {
    val aioServer = AioServer(30523)
    aioServer.start(false)
}