package com.tujulishanehub.backend.controllers;

import com.tujulishanehub.backend.payload.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class ProtectedController {

    @GetMapping("/protected")
    public ResponseEntity<ApiResponse<Object>> protectedEndpoint(Principal principal) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("user", principal != null ? principal.getName() : null);
        ApiResponse<Object> response = new ApiResponse<>(200, "Access granted to protected resource.", data);
        return ResponseEntity.ok(response);
    }
}
