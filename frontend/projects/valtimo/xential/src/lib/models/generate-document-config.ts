interface GenerateDocumentConfig {
    templateId: string;
    fileFormat: FileFormat;
    documentId: string;
    templateData: Array<{key: string; value: string}>
}

type FileFormat = 'WORD' | 'PDF';

export {GenerateDocumentConfig, FileFormat}
