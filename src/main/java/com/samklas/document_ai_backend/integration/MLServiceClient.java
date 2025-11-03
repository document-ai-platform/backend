package com.samklas.document_ai_backend.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

@Service
public class MLServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(MLServiceClient.class);

    private final RestTemplate restTemplate;

    @Value("${ml.service.url:http://localhost:5000}")
    private String mlServiceUrl;

    public MLServiceClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Send document to ML service for OCR and classification
     */
    public MLResponse processDocument(byte[] fileContent, String contentType) {

        try {
            String endpoint = mlServiceUrl + "/api/process";

            logger.info("Calling ML service at: {}", endpoint);

            // Create multipart request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Wrap file content as resource
            ByteArrayResource fileResource = new ByteArrayResource(fileContent) {
                @Override
                public String getFilename() {
                    return "document"; // Filename for the multipart request
                }
            };

            // Build multipart body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Call ML service
            ResponseEntity<MLResponse> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    requestEntity,
                    MLResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("ML service returned: documentType={}, confidence={}",
                        response.getBody().getDocumentType(),
                        response.getBody().getConfidence());
                return response.getBody();
            } else {
                logger.error("ML service returned unexpected response: {}", response.getStatusCode());
                throw new MLServiceException("ML service returned unexpected response");
            }



        } catch (RestClientException e) {
            logger.error("Error calling ML service", e);
            throw new MLServiceException("Failed to communicate with ML service: " + e.getMessage(), e);
        }

        /*
        // mocking

        return new MLResponse(
                "Mock extracted text from document",
                "INVOICE",
                0.85
        );

         */
    }

    /**
     * Response object from ML service
     */
    public static class MLResponse {
        private String extractedText;
        private String documentType;
        private Double confidence;

        // Constructors
        public MLResponse() {
        }

        public MLResponse(String extractedText, String documentType, Double confidence) {
            this.extractedText = extractedText;
            this.documentType = documentType;
            this.confidence = confidence;
        }

        // Getters and Setters
        public String getExtractedText() {
            return extractedText;
        }

        public void setExtractedText(String extractedText) {
            this.extractedText = extractedText;
        }

        public String getDocumentType() {
            return documentType;
        }

        public void setDocumentType(String documentType) {
            this.documentType = documentType;
        }

        public Double getConfidence() {
            return confidence;
        }

        public void setConfidence(Double confidence) {
            this.confidence = confidence;
        }
    }

    /**
     * Custom exception for ML service errors
     */
    public static class MLServiceException extends RuntimeException {
        public MLServiceException(String message) {
            super(message);
        }

        public MLServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}