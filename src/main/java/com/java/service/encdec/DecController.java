package com.java.service.encdec;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.service.encdec.exception.DecryptionException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/decrypt")
@Slf4j

public class DecController {

    @PostMapping
    public ResponseEntity<String> createDecryptionRequestV1(@RequestBody DecryptionRequest request) {
        String uuid = request.getUuid();
        log.info("Received Decryption request for uuid {} : {}", uuid, request);
        try {
            validateRequest(request);
            String decryptedResult = Decryptor.decryption(request.getFileName(), request.getData());
            log.debug("Decryption successful for uuid {} : {}", uuid, decryptedResult);
            return ResponseEntity.ok(decryptedResult);
        } catch (IllegalArgumentException e) {
            log.error("Validation error in Decryption request for uuid {} : {}", uuid, e.getMessage());
            String errorMessage = String.format("{\"message\":\"Invalid input\", \"error\":\"%s\"}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        } catch (DecryptionException e) {
            log.error("Decryption error for uuid {} : {}", uuid, e.getMessage(), e);
            String errorMessage = String.format("{\"message\":\"Decryption failed\", \"error\":\"%s\"}",
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        } catch (Exception e) {
            log.error("Unexpected error occured during decryption for uuid {} : {}", uuid, e.getMessage(), e);
            String errorMessage = String.format(
                    "{\"message\":\"An unexpected error occurred during decryption\", \"error\":\"%s\"}",
                    e.getMessage() != null ? e.getMessage() : "Unknown error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }

    }

    private void validateRequest(DecryptionRequest request) {
        if (request == null || request.getFileName() == null || request.getFileName().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty.");
        }
        if (request.getData() == null || request.getData().isEmpty()) {
            throw new IllegalArgumentException("Data to decrypt cannot be null or empty.");
        }
    }

}
