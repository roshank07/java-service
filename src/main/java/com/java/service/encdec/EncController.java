package com.java.service.encdec;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.service.encdec.exception.EncryptionException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/encrypt")
@Slf4j
public class EncController {

    @PostMapping
    public ResponseEntity<String> createEncryptionRequestV1(@RequestBody EncryptionRequest request) {
        String uuid = request.getUuid();
        log.info("Received Encryption request for uuid {} : {}", uuid, request);
        try {
            validateRequest(request);
            String encryptedResult = Encryptor.encryption(request.getFileName(), request.getData());
            String xmlResult = convertToXml(encryptedResult, uuid);
            log.debug("Encryption successful for uuid {} : {}", uuid, xmlResult);
            return ResponseEntity.ok(xmlResult);
        } catch (IllegalArgumentException e) {
            log.error("Validation error during Encryption for uuid {} : {}", uuid, e.getMessage());
            String errorMessage = String.format("{\"message\":\"Invalid input\", \"error\":\"%s\"}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        } catch (EncryptionException e) {
            log.error("Encryption error for uuid {} : {}", uuid, e.getMessage(), e);
            String errorMessage = String.format("{\"message\":\"Encryption failed\", \"error\":\"%s\"}",
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        } catch (Exception e) {
            log.error("Unexpected error occured during encryption for uuid {} : {}", uuid, e.getMessage(), e);
            String errorMessage = String.format("{\"message\":\"An unexpected error occurred\", \"error\":\"%s\"}",
                    e.getMessage() != null ? e.getMessage() : "Unknown error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    private void validateRequest(EncryptionRequest request) {
        if (request == null || request.getFileName() == null || request.getFileName().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty.");
        }
        if (request.getData() == null || request.getData().isEmpty()) {
            throw new IllegalArgumentException("Data to encrypt cannot be null or empty.");
        }
    }

    private String convertToXml(String encryptedResult, String uuid) {
        try {
            String[] parts = encryptedResult.split("\n");
            String encryptedData = parts[0].replace("Encrypted Data: ", "");
            String encryptedAESKey = parts[1].replace("Encrypted AES Key: ", "");

            log.info("Converted to XML during encryption for uuid {} : encryptedData = {} encryptedAESKey = {}", uuid,
                    encryptedData,
                    encryptedAESKey);

            return "<EncryptionResult>\n" +
                    "  <EncryptedData>" + encryptedData + "</EncryptedData>\n" +
                    "  <EncryptedAESKey>" + encryptedAESKey + "</EncryptedAESKey>\n" +
                    "</EncryptionResult>";
        } catch (Exception e) {
            log.error("Error converting to XML during encryption for uuid {} : {}", uuid, e.getMessage(), e);
            throw e;
        }
    }
}