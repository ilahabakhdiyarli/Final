package com.grad.api.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * POST /beneficiaries sorğu body-si (GRAD-101).
 * nickname opsionaldır — null olduqda JSON-a ümumiyyətlə yazılmır (AC-3: "nickname göndərilmir").
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BeneficiaryRequest {

    private String fullName;
    private String iban;
    private String currency;
    private String nickname;

    public BeneficiaryRequest() {
    }

    public BeneficiaryRequest(String fullName, String iban, String currency, String nickname) {
        this.fullName = fullName;
        this.iban = iban;
        this.currency = currency;
        this.nickname = nickname;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
