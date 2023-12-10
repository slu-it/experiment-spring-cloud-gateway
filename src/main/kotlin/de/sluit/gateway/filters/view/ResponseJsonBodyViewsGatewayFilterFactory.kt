package de.sluit.gateway.filters.view

import com.fasterxml.jackson.databind.JsonNode
import jakarta.annotation.PostConstruct
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory
import org.springframework.stereotype.Component

@Component
class ResponseJsonBodyViewsGatewayFilterFactory(
    private val filterFactory: ModifyResponseBodyGatewayFilterFactory,
    private val properties: ResponseJsonBodyViewsFilterProperties
) : AbstractGatewayFilterFactory<Any>(Any::class.java) {

    @PostConstruct
    fun init() {
        println(properties)
    }

    override fun apply(config: Any?): GatewayFilter =
        filterFactory.apply { c ->
            c.setRewriteFunction(JsonNode::class.java, Any::class.java, ResponseJsonBodyViewsRewriteFunction(properties))
        }
}
