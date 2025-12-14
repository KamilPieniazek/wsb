package com.example.wsb.administration.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminCancelVisitRequest(

        @NotBlank(message = "Cancel reason is required")
        @Size(max = 500, message = "Reason must be max 500 characters")
        String reason
) {}
