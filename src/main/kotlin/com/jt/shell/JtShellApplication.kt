package com.jt.shell

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JtShellApplication

fun main(args: Array<String>) {
	runApplication<JtShellApplication>(*args)
}
