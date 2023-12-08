package de.sluit.gateway

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.zalando.logbook.HttpLogFormatter
import org.zalando.logbook.logstash.LogstashLogbackSink

@Configuration
@EnableConfigurationProperties
class ApplicationConfiguration {

    @Bean
    fun httpMessageSink(httpLogFormatter: HttpLogFormatter) = LogstashLogbackSink(httpLogFormatter)
}
