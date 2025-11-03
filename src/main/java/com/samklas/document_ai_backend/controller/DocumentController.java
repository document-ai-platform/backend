package com.samklas.document_ai_backend.controller;

import com.samklas.document_ai_backend.service.DocumentService;
import com.samklas.document_ai_backend.dto.DocumentListResponse;
import com.samklas.document_ai_backend.dto.DocumentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Upload document for OCR processing
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file) {

        // Validoi tiedosto
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Tarkista tiedostotyyppi (valinnainen)
        String contentType = file.getContentType();
        if (!isValidFileType(contentType)) {
            return ResponseEntity.badRequest().build();
        }

        DocumentResponse response = documentService.processDocument(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get document by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocument(@PathVariable Long id) {
        return documentService.getDocumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * List all documents
     */
    @GetMapping
    public ResponseEntity<List<DocumentListResponse>> listDocuments() {
        List<DocumentListResponse> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    private boolean isValidFileType(String contentType) {
        return contentType != null && (
                contentType.equals("image/png") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("application/pdf")
        );
    }
}
