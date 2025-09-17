package com.tujulishanehub.backend.controllers;

import com.tujulishanehub.backend.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class SpaController {

    // This application is now API-only. Explicitly return a 404-like API response for root
    // and /test paths so clients get a clear message instead of an HTML SPA.
    @GetMapping({"/", "/test", "/test/**"})
    public ResponseEntity<ApiResponse<Object>> handleRoot() {
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "API-only backend. Static file serving has been disabled. Use /api/* endpoints.", null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
