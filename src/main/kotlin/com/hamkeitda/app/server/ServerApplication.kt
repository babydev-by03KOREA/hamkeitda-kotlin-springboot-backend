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

    runApplication<ServerApplication>(*args)
}
