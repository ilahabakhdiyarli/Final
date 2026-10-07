package com.grad.api.util;

/** api.anarabbas.com endpoint path-ları (AC-Reference-ə uyğun). */
public final class Endpoints {

    private Endpoints() {
    }

    public static final String LOGIN = "/auth/login";
    public static final String BENEFICIARIES = "/beneficiaries";
    public static final String CARDS = "/cards";
    public static final String BILLERS = "/billers";
    public static final String BILL_PAYMENTS = "/bill-payments";

    public static String beneficiaryById(String id) {
        return BENEFICIARIES + "/" + id;
    }

    public static String cardById(String id) {
        return CARDS + "/" + id;
    }

    public static String cardBlock(String id) {
        return CARDS + "/" + id + "/block";
    }

    public static String cardUnblock(String id) {
        return CARDS + "/" + id + "/unblock";
    }
}
