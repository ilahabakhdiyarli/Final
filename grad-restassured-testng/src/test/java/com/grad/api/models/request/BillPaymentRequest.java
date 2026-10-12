package com.grad.api.models.request;

import java.math.BigDecimal;

/**
 * POST /bill-payments sorğu body-si (GRAD-302). Idempotency-Key header-də göndərilir, body-də yox.
 * Tip xətası ssenariləri (məs. amount "10" mətn kimi) bu POJO ilə yox, xam JSON ilə göndərilir.
 */
public class BillPaymentRequest {

    private String cardId;
    private String billerId;
    private String subscriberNo;
    private BigDecimal amount;

    public BillPaymentRequest() {
    }

    public BillPaymentRequest(String cardId, String billerId, String subscriberNo, BigDecimal amount) {
        this.cardId = cardId;
        this.billerId = billerId;
        this.subscriberNo = subscriberNo;
        this.amount = amount;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getBillerId() {
        return billerId;
    }

    public void setBillerId(String billerId) {
        this.billerId = billerId;
    }

    public String getSubscriberNo() {
        return subscriberNo;
    }

    public void setSubscriberNo(String subscriberNo) {
        this.subscriberNo = subscriberNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
