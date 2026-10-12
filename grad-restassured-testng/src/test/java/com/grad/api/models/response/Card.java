package com.grad.api.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Virtual kart — POST /cards (201) və GET /cards/{id} (200) cavabı (schemas/card.json).
 * equals() bütün sahələri müqayisə edir — GRAD-202 AC-1 ("POST cavabı ilə eyni dəyərlər") üçün.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {

    private String id;
    private String accountId;
    private String label;
    private String maskedPan;
    private String expiry;
    private String currency;
    private String status;
    private BigDecimal availableBalance;
    private Limits limits;
    private String blockReason;
    private String blockedAt;
    private String createdAt;
    private BigDecimal spentToday;

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getLabel() {
        return label;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public String getExpiry() {
        return expiry;
    }

    public String getCurrency() {
        return currency;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public Limits getLimits() {
        return limits;
    }

    public String getBlockReason() {
        return blockReason;
    }

    public String getBlockedAt() {
        return blockedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public BigDecimal getSpentToday() {
        return spentToday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Card)) {
            return false;
        }
        Card card = (Card) o;
        return Objects.equals(id, card.id)
                && Objects.equals(accountId, card.accountId)
                && Objects.equals(label, card.label)
                && Objects.equals(maskedPan, card.maskedPan)
                && Objects.equals(expiry, card.expiry)
                && Objects.equals(currency, card.currency)
                && Objects.equals(status, card.status)
                && Objects.equals(availableBalance, card.availableBalance)
                && Objects.equals(limits, card.limits)
                && Objects.equals(blockReason, card.blockReason)
                && Objects.equals(blockedAt, card.blockedAt)
                && Objects.equals(createdAt, card.createdAt)
                && Objects.equals(spentToday, card.spentToday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountId, label, maskedPan, expiry, currency, status,
                availableBalance, limits, blockReason, blockedAt, createdAt, spentToday);
    }

    @Override
    public String toString() {
        return "Card{id=" + id + ", accountId=" + accountId + ", label=" + label + ", maskedPan=" + maskedPan
                + ", expiry=" + expiry + ", currency=" + currency + ", status=" + status
                + ", availableBalance=" + availableBalance + ", limits=" + limits + ", blockReason=" + blockReason
                + ", blockedAt=" + blockedAt + ", createdAt=" + createdAt + ", spentToday=" + spentToday + "}";
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Limits {

        private BigDecimal dailyOnline;
        private BigDecimal perTransaction;

        public BigDecimal getDailyOnline() {
            return dailyOnline;
        }

        public BigDecimal getPerTransaction() {
            return perTransaction;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Limits)) {
                return false;
            }
            Limits limits = (Limits) o;
            return Objects.equals(dailyOnline, limits.dailyOnline)
                    && Objects.equals(perTransaction, limits.perTransaction);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dailyOnline, perTransaction);
        }

        @Override
        public String toString() {
            return "Limits{dailyOnline=" + dailyOnline + ", perTransaction=" + perTransaction + "}";
        }
    }
}
