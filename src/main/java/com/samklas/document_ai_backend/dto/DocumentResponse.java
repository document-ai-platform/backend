package com.samklas.document_ai_backend.dto;

import java.time.LocalDateTime;

public class DocumentResponse {

    private Long id;
    private String filename;
    private String status;
    private String documentType;
    private String extractedText;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public DocumentResponse() {
    }

    public DocumentResponse(Long id, String filename, String status, String documentType,
                            String extractedText, LocalDateTime createdAt, LocalDateTime processedAt) {
        this.id = id;
        this.filename = filename;
        this.status = status;
        this.documentType = documentType;
        this.extractedText = extractedText;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}