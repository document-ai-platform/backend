package com.samklas.document_ai_backend.repository;

import com.samklas.document_ai_backend.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * Find all documents by status
     * Used by scheduled task to find PENDING documents
     */
    List<Document> findByStatus(String status);

    /**
     * Find documents by document type
     * Useful for filtering/searching
     */
    List<Document> findByDocumentType(String documentType);
}
