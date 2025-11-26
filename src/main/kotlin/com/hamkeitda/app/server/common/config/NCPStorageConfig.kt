package com.hamkeitda.app.server.common.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NCPStorageConfig(
    private val props: NcpStorageProperties
) {
    @Bean
    fun objectStorageClient(): AmazonS3 {
        return AmazonS3ClientBuilder.standard().withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(props.endpoint, props.region)
            ).withCredentials(
                AWSStaticCredentialsProvider(BasicAWSCredentials(props.accessKey, props.secretKey))
            ).build()
    }
}
