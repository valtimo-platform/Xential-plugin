package com.ritense.valtimo.xential.service

import com.ritense.documentenapi.DocumentenApiPlugin
import com.ritense.documentenapi.client.CreateDocumentRequest
import com.ritense.documentenapi.client.DocumentStatusType
import com.ritense.documentenapi.client.DocumentenApiClient
import com.ritense.documentenapi.event.DocumentCreated
import com.ritense.plugin.service.PluginService
import com.ritense.valtimo.xential.domain.DocumentCreatedMessage
import com.ritense.valtimo.xential.domain.GenerateDocumentProperties
import com.ritense.valtimo.xential.domain.XentialToken
import com.ritense.valtimo.xential.plugin.XentialPlugin
import com.ritense.valtimo.xential.repository.XentialTokenRepository
import com.ritense.zakenapi.ZaakUrlProvider
import com.ritense.zakenapi.ZakenApiPlugin
import com.ritense.zakenapi.client.LinkDocumentRequest
import com.ritense.zakenapi.client.ZakenApiClient
import com.rotterdam.xential.api.DefaultApi
import com.rotterdam.xential.model.Sjabloondata
import org.camunda.bpm.engine.RuntimeService
import org.openapitools.client.infrastructure.ApiClient
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.context.SecurityContextHolder
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.util.Base64
import java.util.UUID

class DocumentGenerationService(
    val defaultApi: DefaultApi,
    val xentialTokenRepository: XentialTokenRepository,
    val pluginService: PluginService,
    val documentenApiClient: DocumentenApiClient,
    val applicationEventPublisher: ApplicationEventPublisher,
    val zaakUrlProvider: ZaakUrlProvider,
    val zakenApiClient: ZakenApiClient,
    val runtimeService: RuntimeService,
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
            messageName = generateDocumentProperties.messageName,
            resumeUrl = result.resumeUrl
        )

        xentialTokenRepository.save(xentialToken)
    }

    fun onDocumentGenerated(message: DocumentCreatedMessage) {
        val plugin = getXentialPlugin(message)
        val documentenApiPlugin = plugin.documentenApiPluginConfiguration
        val zakenApiPlugin = plugin.zakenApiPluginConfiguration

        val bytes = Base64.getDecoder().decode(message.data)

        val xentialToken = xentialTokenRepository.findById(UUID.fromString(message.documentCreatieSessieId))
            .orElseThrow { NoSuchElementException("Could not find Xential Token ${message.documentCreatieSessieId}") }

        ByteArrayInputStream(bytes).use { inputStream ->
            val createDocumentRequest = CreateDocumentRequest(
                bronorganisatie = documentenApiPlugin.bronorganisatie,
                creatiedatum = LocalDate.now(),
                titel = message.documentkenmerk,
                auteur = message.gebruiker,
                status = DocumentStatusType.DEFINITIEF,
                taal = "nld",
                bestandsnaam = message.documentkenmerk,
                bestandsomvang = bytes.size.toLong(),
                inhoud = inputStream,
                beschrijving = "",
                ontvangstdatum = LocalDate.now(),
                verzenddatum = LocalDate.now(),
                informatieobjecttype = null,
                formaat = message.formaat.name,
            )

            val documentCreateResult = documentenApiClient.storeDocument(
                documentenApiPlugin.authenticationPluginConfiguration,
                documentenApiPlugin.url,
                createDocumentRequest
            )
            val event = DocumentCreated(
                documentCreateResult.url,
                documentCreateResult.auteur,
                documentCreateResult.bestandsnaam,
                documentCreateResult.bestandsomvang,
                documentCreateResult.beginRegistratie
            )
            applicationEventPublisher.publishEvent(event)

            val zaakUrl = zaakUrlProvider.getZaakUrl(xentialToken.processId)
            zakenApiClient.linkDocument(
                zakenApiPlugin.authenticationPluginConfiguration,
                zakenApiPlugin.url,
                LinkDocumentRequest(
                    documentCreateResult.url,
                    zaakUrl.toString(),
                    message.documentkenmerk,
                    ""
                )
            )

            runtimeService.createMessageCorrelation(xentialToken.messageName)
                .processInstanceId(xentialToken.processId.toString())
                .correlate()
        }
    }

    private fun getXentialPlugin(message: DocumentCreatedMessage): XentialPlugin {
        //FIXME needs a way of determining the right plugin
        val pluginConfig = pluginService.findPluginConfiguration(XentialPlugin.PLUGIN_KEY) { _ -> true}
            ?: throw NoSuchElementException("Could not find Xential plugin")
        return pluginService.createInstance(pluginConfig) as XentialPlugin
    }
}
