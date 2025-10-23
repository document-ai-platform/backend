package com.samklas.document_ai_backend.service;

import com.samklas.document_ai_backend.dto.DocumentListResponse;
import com.samklas.document_ai_backend.dto.DocumentResponse;
import com.samklas.document_ai_backend.entity.Document;
import com.samklas.document_ai_backend.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;


    @Value("${document.storage.path:./uploads}")
    private String uploadPath;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;

    }

    /**
     * Process uploaded document - saves file and initiates async OCR processing
     */
    @Transactional
    public DocumentResponse processDocument(MultipartFile file) {
        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;

            // Save file to disk
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePath = uploadDir.resolve(storedFilename);
            Files.write(filePath, file.getBytes());

            // Create document entity
            Document document = new Document();
            document.setFilename(originalFilename);
            document.setFilepath(filePath.toString());
            document.setContentType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setStatus("PENDING");
            document.setCreatedAt(LocalDateTime.now());

            // Save to database
            document = documentRepository.save(document);

            logger.info("Document saved with ID: {}, initiating async processing", document.getId());

            // Return response immediately
            return mapToDocumentResponse(document);

        } catch (IOException e) {
            logger.error("Error saving document file", e);
            throw new RuntimeException("Failed to store document", e);
        }
    }

    /**
     * Scheduled task to process pending documents
     * Runs every 30 seconds
     */
    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void processPendingDocuments() {
        List<Document> pendingDocs = documentRepository.findByStatus("PENDING");

        if (!pendingDocs.isEmpty()) {
            logger.info("Found {} pending documents to process", pendingDocs.size());
        }

        for (Document doc : pendingDocs) {
            processDocumentAsync(doc.getId());
        }
    }

    /**
     * Async processing of a single document
     */
    @Async
    @Transactional
    public void processDocumentAsync(Long documentId) {
        Optional<Document> optionalDoc = documentRepository.findById(documentId);

        if (optionalDoc.isEmpty()) {
            logger.warn("Document with ID {} not found", documentId);
            return;
        }

        Document document = optionalDoc.get();

        // Prevent double processing
        if (!"PENDING".equals(document.getStatus())) {
            return;
        }

        try {
            // Update status to PROCESSING
            document.setStatus("PROCESSING");
            documentRepository.save(document);

            logger.info("Processing document ID: {}", documentId);

            // Call ML service for OCR and classification
            Path filePath = Paths.get(document.getFilepath());
            byte[] fileContent = Files.readAllBytes(filePath);

            /*
            MLServiceClient.MLResponse mlResponse = mlServiceClient.processDocument(
                    fileContent,
                    document.getContentType()
            );

             */

            // Update document with results
            /*
            document.setExtractedText(mlResponse.getExtractedText());
            document.setDocumentType(mlResponse.getDocumentType());
            document.setConfidenceScore(mlResponse.getConfidence());

             */
            document.setStatus("COMPLETED");
            document.setProcessedAt(LocalDateTime.now());

            documentRepository.save(document);

            /*
            logger.info("Document ID: {} processed successfully as type: {}",
                    documentId, mlResponse.getDocumentType());

             */

        } catch (Exception e) {
            logger.error("Error processing document ID: {}", documentId, e);
            document.setStatus("FAILED");
            document.setErrorMessage(e.getMessage());
            documentRepository.save(document);
        }
    }

    /**
     * Get document by ID
     */
    @Transactional(readOnly = true)
    public Optional<DocumentResponse> getDocumentById(Long id) {
        return documentRepository.findById(id)
                .map(this::mapToDocumentResponse);
    }

    /**
     * Get all documents
     */
    @Transactional(readOnly = true)
    public List<DocumentListResponse> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(this::mapToDocumentListResponse)
                .collect(Collectors.toList());
    }

    // Mapper methods
    private DocumentResponse mapToDocumentResponse(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getFilename(),
                document.getStatus(),
                document.getDocumentType(),
                document.getExtractedText(),
                document.getCreatedAt(),
                document.getProcessedAt()
        );
    }

    private DocumentListResponse mapToDocumentListResponse(Document document) {
        return new DocumentListResponse(
                document.getId(),
                document.getFilename(),
                document.getStatus(),
                document.getDocumentType(),
                document.getCreatedAt()
        );
    }
}
