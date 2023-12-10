package de.sluit.gateway.filters.example

import com.fasterxml.jackson.databind.JsonNode
import jakarta.annotation.PostConstruct
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory
import org.springframework.stereotype.Component

@Component
class ResponseJsonBodyExamplesGatewayFilterFactory(
    private val filterFactory: ModifyResponseBodyGatewayFilterFactory,
    private val properties: ResponseJsonBodyExamplesFilterProperties
) : AbstractGatewayFilterFactory<Any>(Any::class.java) {

    @PostConstruct
    fun init() {
        println(properties)
    }

    override fun apply(config: Any?): GatewayFilter =
        filterFactory.apply { c ->
            c.setRewriteFunction(
                JsonNode::class.java,
                JsonNode::class.java,
                ResponseJsonBodyExamplesRewriteFunction(properties)
            )
        }
}
