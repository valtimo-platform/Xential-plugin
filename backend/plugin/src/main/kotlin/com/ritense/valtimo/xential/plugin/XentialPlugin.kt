/*
 * Copyright 2015-2022 Ritense BV, the Netherlands.
 *
 * Licensed under EUPL, Version 1.2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ritense.valtimo.xential.plugin

import com.ritense.documentenapi.DocumentenApiPlugin
import com.ritense.plugin.annotation.Plugin
import com.ritense.plugin.annotation.PluginAction
import com.ritense.plugin.annotation.PluginActionProperty
import com.ritense.plugin.annotation.PluginProperty
import com.ritense.processlink.domain.ActivityTypeWithEventName
import com.ritense.valtimo.xential.domain.FileFormat
import com.ritense.valtimo.xential.domain.GenerateDocumentProperties
import com.ritense.valtimo.xential.plugin.XentialPlugin.Companion.PLUGIN_KEY
import com.ritense.valtimo.xential.service.DocumentGenerationService
import com.ritense.zakenapi.ZakenApiPlugin
import org.camunda.bpm.engine.delegate.DelegateExecution
import java.util.UUID

@Plugin(
    key = PLUGIN_KEY,
    title = "Xential Plugin",
    description = ""
)
class XentialPlugin(
    val documentGenerationService: DocumentGenerationService
) {

    @PluginProperty(key = "clientId", secret = false)
    private lateinit var clientId: String

    @PluginProperty(key = "clientPassword", secret = true)
    private lateinit var clientPassword: String

    @PluginProperty(key = "documentenApiPluginConfiguration", secret = false)
    lateinit var documentenApiPluginConfiguration: DocumentenApiPlugin

    @PluginProperty(key = "zakenApiPluginConfiguration", secret = false)
    lateinit var zakenApiPluginConfiguration: ZakenApiPlugin


    @PluginAction(
        key = "generate-document",
        title = "Generate document",
        description = "Generate a document using xential.",
        activityTypes = [ActivityTypeWithEventName.SERVICE_TASK_START]
    )
    fun generateDocument(
        @PluginActionProperty templateId: UUID,
        @PluginActionProperty fileFormat: FileFormat,
        @PluginActionProperty documentId: String,
        @PluginActionProperty messageName: String,
        @PluginActionProperty templateData: Array<TemplateDataEntry>,
        execution: DelegateExecution
    ) {
        val generateDocumentProperties = GenerateDocumentProperties(
            templateId,
            fileFormat,
            documentId,
            messageName,
            templateData
        )
        documentGenerationService.generateDocument(
            UUID.fromString(execution.processInstanceId),
            generateDocumentProperties,
            clientId,
            clientPassword,
            execution
        )
    }

    companion object {
        const val PLUGIN_KEY = "xential"
    }

}
