interface GenerateDocumentConfig {
    templateId: string;
    fileFormat: FileFormat;
    documentId: string;
    templateData: Array<{key: string; value: string}>
}

type FileFormat = 'DOCX' | 'PDF' | 'XML' | 'HTML';

export {GenerateDocumentConfig, FileFormat}
