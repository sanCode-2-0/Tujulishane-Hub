package com.tujulishanehub.backend.payload;

import jakarta.validation.constraints.NotBlank;

public class MessageRequest {
    @NotBlank(message = "Message cannot be blank")
    private String message;

    public MessageRequest() {}

    public MessageRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}