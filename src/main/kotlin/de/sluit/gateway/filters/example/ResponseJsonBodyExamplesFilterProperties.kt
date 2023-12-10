package de.sluit.gateway.filters.example

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.ClassPathResource
import kotlin.text.Charsets.UTF_8

private val simpleObjectMapper = jacksonObjectMapper()

@ConfigurationProperties("filters.response-body-examples")
data class ResponseJsonBodyExamplesFilterProperties(
    val pathToExampleMapping: List<Mapping>
) {
    data class Mapping(
        val pathPattern: Regex,
        val exampleFilePath: String
    ) {
        val example: JsonNode

        init {
            val resource = ClassPathResource(exampleFilePath)
            val json = resource.getContentAsString(UTF_8)
            example = simpleObjectMapper.readTree(json)
        }
    }
}
