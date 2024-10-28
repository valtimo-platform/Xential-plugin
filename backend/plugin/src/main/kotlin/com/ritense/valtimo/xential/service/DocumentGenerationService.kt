package com.ritense.valtimo.xential.service

import com.ritense.valtimo.xential.domain.GenerateDocumentProperties
import com.ritense.valtimo.xential.domain.XentialToken
import com.ritense.valtimo.xential.repository.XentialTokenRepository
import com.rotterdam.xential.api.DefaultApi
import com.rotterdam.xential.model.Sjabloondata
import org.openapitools.client.infrastructure.ApiClient
import java.util.UUID

class DocumentGenerationService(
    val defaultApi: DefaultApi,
    val xentialTokenRepository: XentialTokenRepository,
    ) {

    fun generateDocument(
        processId: UUID,
        generateDocumentProperties: GenerateDocumentProperties,
        clientId: String,
        clientPassword: String,
    ) {
        val sjabloonVulData = generateDocumentProperties.templateData.entries.map { "<${it.key}>${it.value}</${it.key}>" }.joinToString()

        ApiClient.username = clientId
        ApiClient.password = clientPassword
        val result = defaultApi.creeerDocument(
            gebruikersId = clientId,
            accepteerOnbekend = false,
            sjabloondata = Sjabloondata(
                sjabloonId = generateDocumentProperties.templateId.toString(),
                bestandsFormaat = Sjabloondata.BestandsFormaat.valueOf(generateDocumentProperties.fileFormat.name),
                documentkenmerk = generateDocumentProperties.documentId,
                sjabloonVulData = "<root>$sjabloonVulData</root>"
            )
        )

        val xentialToken = XentialToken(
            token = UUID.fromString(result.documentCreatieSessieId),
            processId = processId,
            resumeUrl = result.resumeUrl
        )

        xentialTokenRepository.save(xentialToken)
    }
}
