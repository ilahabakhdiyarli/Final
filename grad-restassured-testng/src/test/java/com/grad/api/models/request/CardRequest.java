package com.grad.api.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/**
 * POST /cards sorğu body-si (GRAD-201).
 * initialAmount BigDecimal-dır ki, "1.00" / "1000.00" kimi sərhəd dəyərləri JSON-a dəqiq yazılsın.
 * Tip xətası ssenariləri (məs. "50" mətn kimi) bu POJO ilə yox, xam JSON ilə göndərilir.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardRequest {

    private String accountId;
    private BigDecimal initialAmount;
    private String label;

    public CardRequest() {
    }

    public CardRequest(String accountId, BigDecimal initialAmount) {
        this(accountId, initialAmount, null);
    }

    public CardRequest(String accountId, BigDecimal initialAmount, String label) {
        this.accountId = accountId;
        this.initialAmount = initialAmount;
        this.label = label;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
