package com.grad.api.util;

/**
 * src/test/resources/schemas altındakı JSON Schema (draft-04) fayllarının classpath yolları.
 * Schema-lar real API cavablarından çıxarılıb və AC-Reference-dəki formatlarla (id prefiksləri,
 * maskedPan, receiptNo, status/currency enum-ları) məhdudlaşdırılıb.
 */
public final class Schemas {

    private Schemas() {
    }

    public static final String LOGIN = "schemas/login-response.json";
    public static final String BENEFICIARY = "schemas/beneficiary.json";
    public static final String BENEFICIARY_LIST = "schemas/beneficiary-list.json";
    public static final String CARD = "schemas/card.json";
    public static final String CARD_STATUS = "schemas/card-status.json";
    public static final String BILLERS_LIST = "schemas/billers-list.json";
    public static final String BILL_PAYMENT = "schemas/bill-payment.json";
    public static final String BILL_PAYMENT_LIST = "schemas/bill-payment-list.json";
    public static final String ERROR = "schemas/error.json";
    public static final String VALIDATION_ERROR = "schemas/validation-error.json";
    public static final String UNAUTHORIZED = "schemas/unauthorized.json";
}
