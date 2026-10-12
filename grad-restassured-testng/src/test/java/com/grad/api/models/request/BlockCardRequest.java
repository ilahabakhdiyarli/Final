package com.grad.api.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * POST /cards/{id}/block sorğu body-si (GRAD-203).
 * reason: TEMPORARY → FROZEN, LOST / STOLEN → BLOCKED. reason null olduqda body "{}" olur (AC-6).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockCardRequest {

    private String reason;

    public BlockCardRequest() {
    }

    public BlockCardRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
