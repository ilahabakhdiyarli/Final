package com.grad.api.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** POST /cards/{id}/block və /unblock (200) cavabı (schemas/card-status.json). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardStatus {

    private String id;
    private String status;
    private String blockReason;
    private String blockedAt;

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getBlockReason() {
        return blockReason;
    }

    public String getBlockedAt() {
        return blockedAt;
    }
}
