package de.sluit.gateway.filters.example

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import de.sluit.gateway.filters.example.ResponseJsonBodyExamplesFilterProperties.Mapping
import org.reactivestreams.Publisher
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

// TODO
// this is not streaming ready!
// definitely need a different approach for for big lists etc.

class ResponseJsonBodyExamplesRewriteFunction(
    private val properties: ResponseJsonBodyExamplesFilterProperties
) : RewriteFunction<JsonNode, JsonNode> {

    override fun apply(exchange: ServerWebExchange, body: JsonNode?): Publisher<JsonNode> {
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
        properties.pathToExampleMapping.firstOrNull { path matches it.pathPattern }

    private fun convertToView(body: JsonNode, mapping: Mapping): JsonNode {
        filterByExample(body, mapping.example)
        return body
    }

    private fun filterByExample(input: JsonNode, example: JsonNode) {
        when (input) {
            is ObjectNode -> filterObject(input, example)
            is ArrayNode -> filterArray(input, example)
        }
    }

    private fun filterObject(input: ObjectNode, example: JsonNode) {
        if (example !is ObjectNode) return

        val allowedFieldsNames = example.fieldNames().asSequence().toSet()

        val fieldsToRemoveByName = mutableSetOf<String>()
        input.fields()
            .forEach { (name, node) ->
                if (name !in allowedFieldsNames) {
                    fieldsToRemoveByName.add(name)
                } else {
                    val exampleNode = example[name]
                    if (node::class != exampleNode::class) {
                        fieldsToRemoveByName.add(name)
                    }
                }
            }

        fieldsToRemoveByName.forEach(input::remove)

        input.fields().forEach { (name, node) ->
            filterByExample(node, example.get(name))
        }
    }

    private fun filterArray(input: ArrayNode, example: JsonNode) {
        if (example !is ArrayNode) return

        val e1 = example.first()
        input.forEach { filterByExample(it, e1) }
    }
}
