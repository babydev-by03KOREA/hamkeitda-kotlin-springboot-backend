package com.hamkeitda.app.server.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "ncp.storage")
data class NcpStorageProperties(
    var region: String = "",
    var endpoint: String = "",
    var accessKey: String = "",
    var secretKey: String = ""
)
