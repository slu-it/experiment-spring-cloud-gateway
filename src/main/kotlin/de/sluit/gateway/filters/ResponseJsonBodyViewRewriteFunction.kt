package de.sluit.gateway.filters

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.sluit.gateway.filters.ResponseJsonBodyViewsFilterProperties.Mapping
import org.reactivestreams.Publisher
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

// TODO
// this is not streaming ready!
// definitely need a different approach for for big lists etc.

class ResponseJsonBodyViewRewriteFunction(
    private val properties: ResponseJsonBodyViewsFilterProperties
) : RewriteFunction<JsonNode, Any> {

    private val objectMapper = jacksonObjectMapper()
        .disable(FAIL_ON_UNKNOWN_PROPERTIES)

    override fun apply(exchange: ServerWebExchange, body: JsonNode?): Publisher<Any> {
        if (body == null) return Mono.empty()

        if (isNotError(exchange)) {
            val path = getPath(exchange)
            val mapping = getMapping(path)
            if (mapping != null) {
                return Mono.just(convertToView(body, mapping))
            }
        }

        return Mono.just(body)
    }

    private fun isNotError(exchange: ServerWebExchange): Boolean =
        exchange.response.statusCode?.isError == false

    private fun getPath(exchange: ServerWebExchange): String =
        exchange.request.path.pathWithinApplication().value()

    private fun getMapping(path: String): Mapping? =
        properties.pathToViewMapping.firstOrNull { path matches it.pathPattern }

    private fun convertToView(body: JsonNode, mapping: Mapping): Any =
        if (body.isArray) {
            body.elements().asSequence().map { convertObjectToView(it, mapping) }.toList()
        } else {
            convertObjectToView(body, mapping)
        }

    private fun convertObjectToView(it: JsonNode, mapping: Mapping): Any =
        objectMapper.treeToValue(it, mapping.viewClass)
}
