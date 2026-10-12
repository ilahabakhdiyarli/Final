package com.grad.api.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** GET /beneficiaries cavabı (schemas/beneficiary-list.json). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BeneficiaryList {

    private int total;
    private List<Beneficiary> items;

    public int getTotal() {
        return total;
    }

    public List<Beneficiary> getItems() {
        return items;
    }
}
