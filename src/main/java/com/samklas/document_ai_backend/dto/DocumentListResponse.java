package com.samklas.document_ai_backend.dto;

import java.time.LocalDateTime;

public class DocumentListResponse {

    private Long id;
    private String filename;
    private String status;
    private String documentType;
    private LocalDateTime createdAt;

    // Constructors
    public DocumentListResponse() {
    }

    public DocumentListResponse(Long id, String filename, String status,
                                String documentType, LocalDateTime createdAt) {
        this.id = id;
        this.filename = filename;
        this.status = status;
        this.documentType = documentType;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
