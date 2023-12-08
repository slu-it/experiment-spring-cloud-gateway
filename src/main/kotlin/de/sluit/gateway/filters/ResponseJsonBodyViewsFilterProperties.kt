package de.sluit.gateway.filters

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("filters.response-body-views")
data class ResponseJsonBodyViewsFilterProperties(
    val pathToViewMapping: List<Mapping>
) {
    data class Mapping(
        val pathPattern: Regex,
        val viewClass: Class<*>
    )
}
