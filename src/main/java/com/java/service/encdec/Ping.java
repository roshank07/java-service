package com.java.service.encdec;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ping")

public class Ping {

    @GetMapping
    public String ping() {
        try {
            return "{\"message\":\"Ping successful\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "Ping failed: " + e.getMessage();
        }

    }

}
