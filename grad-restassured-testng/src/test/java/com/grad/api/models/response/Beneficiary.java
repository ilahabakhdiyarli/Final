package com.grad.api.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Alıcı obyekti — POST /beneficiaries cavabı və GET /beneficiaries items[] (schemas/beneficiary.json). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Beneficiary {

    private String id;
    private String fullName;
    private String iban;
    private String ibanMasked;
    private String currency;
    private String nickname;
    private String createdAt;

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getIban() {
        return iban;
    }

    public String getIbanMasked() {
        return ibanMasked;
    }

    public String getCurrency() {
        return currency;
    }

    public String getNickname() {
        return nickname;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
