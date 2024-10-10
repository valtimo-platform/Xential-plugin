package com.ritense.valtimo.xential.model

data class GenerateDocumentProperties(
    val templateId: String,
    val fileFormat: FileFormat,
    val documentId: String,
    val templateData: Map<String, String>
)

enum class FileFormat {
    DOCX, HTML, PDF, XML
}
