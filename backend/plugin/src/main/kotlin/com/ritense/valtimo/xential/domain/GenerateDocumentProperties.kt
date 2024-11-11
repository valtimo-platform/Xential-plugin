package com.ritense.valtimo.xential.domain

import com.ritense.valtimo.xential.plugin.TemplateDataEntry
import java.util.UUID

data class GenerateDocumentProperties(
    val templateId: UUID,
    val fileFormat: FileFormat,
    val documentId: String,
    val messageName: String,
    val templateData: Array<TemplateDataEntry>
)
