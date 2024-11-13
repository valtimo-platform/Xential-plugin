package com.ritense.valtimo.xential.service

import com.ritense.valtimo.xential.domain.FileFormat
import com.ritense.valtimo.xential.domain.GenerateDocumentProperties
import com.ritense.valtimo.xential.domain.XentialToken
import com.ritense.valtimo.xential.repository.XentialTokenRepository
import com.rotterdam.xential.api.DefaultApi
import com.rotterdam.xential.model.DocumentCreatieResultaat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

class DocumentGenerationServiceTest {

    @Mock
    lateinit var defaultApi: DefaultApi

    @Mock
    lateinit var xentialTokenRepository: XentialTokenRepository

    @InjectMocks
    lateinit var documentGenerationService: DocumentGenerationService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun shouldGenerateDocument() {
        val executionId = UUID.randomUUID()

        val generateDocumentProperties = GenerateDocumentProperties(
            templateId = UUID.randomUUID(),
            fileFormat = FileFormat.PDF,
            documentId = "mijn-kenmerk",
            templateData = emptyMap()
        )

        val creatieResultaat = DocumentCreatieResultaat(
            documentCreatieSessieId = UUID.randomUUID().toString(),
            status = DocumentCreatieResultaat.Status.VOLTOOID,
            resumeUrl = null
        )
        whenever(defaultApi.creeerDocument(any(), any(), any())).thenReturn(creatieResultaat)

        documentGenerationService.generateDocument(
            executionId,
            generateDocumentProperties,
            "client-id",
            "client-password",
            execution,
        )

        verify(xentialTokenRepository).save(XentialToken(
            token = UUID.fromString(creatieResultaat.documentCreatieSessieId),
            processId = executionId,
            resumeUrl = null
        ))
    }

}
