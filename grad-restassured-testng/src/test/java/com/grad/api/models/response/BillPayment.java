package com.grad.api.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/** POST /bill-payments (201 və idempotent replay 200) cavabı (schemas/bill-payment.json). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillPayment {

    private String id;
    private String receiptNo;
    private String status;
    private BillerRef biller;
    private String subscriberNo;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal total;
    private String currency;
    private String cardId;
    private BigDecimal balanceAfter;
    private String createdAt;

    public String getId() {
        return id;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public String getStatus() {
        return status;
    }

    public BillerRef getBiller() {
        return biller;
    }

    public String getSubscriberNo() {
        return subscriberNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCardId() {
        return cardId;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BillerRef {

        private String id;
        private String name;
        private String category;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }
    }
}
