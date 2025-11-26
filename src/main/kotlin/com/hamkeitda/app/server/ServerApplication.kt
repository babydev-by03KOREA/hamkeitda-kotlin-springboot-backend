package com.hamkeitda.app.server

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ServerApplication

fun main(args: Array<String>) {
    val dotenv = Dotenv.load()

    System.setProperty("DATABASE_URL", dotenv["DATABASE_URL"])
    System.setProperty("DATABASE_USERNAME", dotenv["DATABASE_USERNAME"])
    System.setProperty("DATABASE_PASSWORD", dotenv["DATABASE_PASSWORD"])
    System.setProperty("JWT_SECRET", dotenv["JWT_SECRET"])
    System.setProperty("NCP_ACCESS_KEY", dotenv["NCP_ACCESS_KEY"])
    System.setProperty("NCP_SECRET_KEY", dotenv["NCP_SECRET_KEY"])

    runApplication<ServerApplication>(*args)
}
