package com.java.service.encdec;

import lombok.Data;

@Data
public class DecryptionRequest {
    private String fileName;
    private String data;
    private String uuid;
}
