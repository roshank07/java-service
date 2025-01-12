package com.java.service.encdec;

import lombok.Data;

@Data
public class EncryptionRequest {
    private String fileName;
    private String data;
    private String uuid;
}
